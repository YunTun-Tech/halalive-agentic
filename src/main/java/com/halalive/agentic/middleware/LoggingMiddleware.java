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

/**
 * Built-in middleware that logs agent execution lifecycle.
 */
@Slf4j
public class LoggingMiddleware implements Middleware {

    @Override
    public AgentResult beforeExecute(AgentContext ctx) {
        log.info("Agent execution starting: taskId={}", ctx.getTaskId());
        return null;
    }

    @Override
    public AgentResult afterExecute(AgentContext ctx, AgentResult result) {
        log.info("Agent execution completed: taskId={}, success={}, durationMs={}",
                ctx.getTaskId(), result.isSuccess(), result.getDurationMs());
        return result;
    }

    @Override
    public AgentResult onError(AgentContext ctx, Exception e) throws Exception {
        log.error("Agent execution failed: taskId={}, error={}", ctx.getTaskId(), e.getMessage());
        throw e;
    }

    @Override
    public int order() {
        return 0; // execute first
    }
}
