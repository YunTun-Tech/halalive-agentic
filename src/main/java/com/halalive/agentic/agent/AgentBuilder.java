/*
 * MIT License
 *
 * Copyright (c) 2026 halalive.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.halalive.agentic.agent;

import com.halalive.agentic.capability.Tool;
import com.halalive.agentic.capability.ToolRegistry;
import com.halalive.agentic.human.HumanInTheLoop;
import com.halalive.agentic.middleware.Middleware;
import com.halalive.agentic.middleware.MiddlewarePipeline;
import com.halalive.agentic.middleware.DefaultMiddlewarePipeline;
import com.halalive.agentic.orchestration.AgentEngine;
import com.halalive.agentic.session.SessionStore;
import com.halalive.agentic.session.MemoryStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Fluent Builder for composing Agent instances.
 * Every capability is a pluggable component — composition over inheritance.
 */
@Slf4j
public class AgentBuilder {

    public static AgentBuilder builder() {
        return new AgentBuilder();
    }
    private AgentId id;
    private AgentCard card;
    private String model;
    private String instructions;
    private final List<Tool> tools = new ArrayList<>();
    private final List<Agent> subAgents = new ArrayList<>();
    private final List<Middleware> middlewares = new ArrayList<>();
    private SessionStore sessionStore;
    private MemoryStrategy memoryStrategy;
    private HumanInTheLoop humanHandler;
    private ToolRegistry toolRegistry;
    private AgentEngine executionEngine;

    public AgentBuilder id(AgentId id) { this.id = id; return this; }
    public AgentBuilder card(AgentCard card) { this.card = card; return this; }
    public AgentBuilder model(String model) { this.model = model; return this; }
    public AgentBuilder instructions(String instructions) { this.instructions = instructions; return this; }

    public AgentBuilder tool(Tool tool) { this.tools.add(tool); return this; }
    public AgentBuilder tools(Tool... tools) { for (Tool t : tools) this.tools.add(t); return this; }
    public AgentBuilder subAgent(Agent agent) { this.subAgents.add(agent); return this; }
    public AgentBuilder middleware(Middleware mw) { this.middlewares.add(mw); return this; }
    public AgentBuilder memory(SessionStore store) { this.sessionStore = store; return this; }
    public AgentBuilder memoryStrategy(MemoryStrategy strategy) { this.memoryStrategy = strategy; return this; }
    public AgentBuilder humanHandler(HumanInTheLoop handler) { this.humanHandler = handler; return this; }
    public AgentBuilder toolRegistry(ToolRegistry registry) { this.toolRegistry = registry; return this; }
    public AgentBuilder executionEngine(AgentEngine engine) { this.executionEngine = engine; return this; }

    public Agent build() {
        if (id == null) {
            id = AgentId.of(card != null ? card.getName() : "agent", UUID.randomUUID().toString().substring(0, 8));
        }
        if (card == null) {
            card = AgentCard.builder().name(id.getType()).version("1.0.0").build();
        }
        MiddlewarePipeline pipeline = new DefaultMiddlewarePipeline(middlewares);

        DefaultAgent agent = new DefaultAgent(id, card, model, instructions, tools, subAgents,
                pipeline, sessionStore, memoryStrategy, humanHandler, toolRegistry, executionEngine);
        agent.setState(AgentState.IDLE);
        return agent;
    }

    @Getter
    static class DefaultAgent implements Agent {
        private final AgentId id;
        private final AgentCard card;
        private final String model;
        private final String instructions;
        private final List<Tool> tools;
        private final List<Agent> subAgents;
        private final MiddlewarePipeline middlewarePipeline;
        private final SessionStore sessionStore;
        private final MemoryStrategy memoryStrategy;
        private final HumanInTheLoop humanHandler;
        private final ToolRegistry toolRegistry;
        private final AgentEngine executionEngine;
        private volatile AgentState state = AgentState.CREATED;
        private volatile boolean pauseRequested;

        DefaultAgent(AgentId id, AgentCard card, String model, String instructions,
                     List<Tool> tools, List<Agent> subAgents,
                     MiddlewarePipeline middlewarePipeline, SessionStore sessionStore,
                     MemoryStrategy memoryStrategy, HumanInTheLoop humanHandler,
                     ToolRegistry toolRegistry, AgentEngine executionEngine) {
            this.id = id;
            this.card = card;
            this.model = model;
            this.instructions = instructions;
            this.tools = List.copyOf(tools);
            this.subAgents = List.copyOf(subAgents);
            this.middlewarePipeline = middlewarePipeline;
            this.sessionStore = sessionStore;
            this.memoryStrategy = memoryStrategy;
            this.humanHandler = humanHandler;
            this.toolRegistry = toolRegistry;
            this.executionEngine = executionEngine;
        }

        void setState(AgentState s) { this.state = s; }

        @Override
        public CompletableFuture<AgentResult> execute(AgentContext ctx) {
            if (!state.canTransitionTo(AgentState.RUNNING)) {
                return CompletableFuture.failedFuture(
                        new IllegalStateException("Cannot execute from state " + state));
            }
            state = AgentState.RUNNING;
            pauseRequested = false;

            return CompletableFuture.supplyAsync(() -> {
                try {
                    AgentResult shortcut = middlewarePipeline.beforeExecute(ctx);
                    if (shortcut != null) return shortcut;

                    AgentResult result = doExecute(ctx);

                    return middlewarePipeline.afterExecute(ctx, result);
                } catch (Exception e) {
                    try {
                        return middlewarePipeline.onError(ctx, e);
                    } catch (Exception ex) {
                        state = AgentState.ERROR;
                        return AgentResult.builder().success(false).message(ex.getMessage()).build();
                    }
                }
            });
        }

        private AgentResult doExecute(AgentContext ctx) {
            if (executionEngine != null) {
                return executionEngine.execute(this, ctx).join();
            }
            return AgentResult.builder()
                    .success(true)
                    .message("Agent " + id + " executed successfully (no execution engine)")
                    .output(ctx.getInput())
                    .build();
        }

        @Override
        public void pause() {
            if (state == AgentState.RUNNING) {
                pauseRequested = true;
                state = AgentState.PAUSED;
                log.info("Agent[{}] paused", id);
            }
        }

        @Override
        public void resume(String fromCheckpointId) {
            if (state == AgentState.PAUSED) {
                pauseRequested = false;
                state = AgentState.RUNNING;
                log.info("Agent[{}] resumed from checkpoint {}", id, fromCheckpointId);
            }
        }

        @Override
        public void stop() {
            state = AgentState.COMPLETED;
            log.info("Agent[{}] stopped", id);
        }
    }
}
