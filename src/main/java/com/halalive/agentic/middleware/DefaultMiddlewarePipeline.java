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


package com.halalive.agentic.middleware;

import com.halalive.agentic.agent.AgentContext;
import com.halalive.agentic.agent.AgentResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Default ordered middleware pipeline.
 */
@Slf4j
public class DefaultMiddlewarePipeline implements MiddlewarePipeline {

    private final List<Middleware> chain = new ArrayList<>();

    public DefaultMiddlewarePipeline() {}

    public DefaultMiddlewarePipeline(List<Middleware> middlewares) {
        if (middlewares != null) {
            this.chain.addAll(middlewares);
            this.chain.sort(Comparator.comparingInt(Middleware::order));
        }
    }

    @Override
    public void add(Middleware middleware) {
        chain.add(middleware);
        chain.sort(Comparator.comparingInt(Middleware::order));
    }

    @Override
    public void remove(Middleware middleware) {
        chain.remove(middleware);
    }

    @Override
    public int size() {
        return chain.size();
    }

    @Override
    public AgentResult beforeExecute(AgentContext ctx) {
        for (Middleware mw : chain) {
            AgentResult result = mw.beforeExecute(ctx);
            if (result != null) {
                log.debug("Middleware[{}] short-circuited execution", mw.getClass().getSimpleName());
                return result;
            }
        }
        return null;
    }

    @Override
    public AgentResult afterExecute(AgentContext ctx, AgentResult result) {
        AgentResult current = result;
        for (Middleware mw : chain) {
            current = mw.afterExecute(ctx, current);
        }
        return current;
    }

    @Override
    public AgentResult onError(AgentContext ctx, Exception e) throws Exception {
        Exception current = e;
        for (Middleware mw : chain) {
            try {
                return mw.onError(ctx, current);
            } catch (Exception ex) {
                current = ex;
            }
        }
        throw current;
    }
}
