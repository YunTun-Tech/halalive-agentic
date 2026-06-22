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

/**
 * Interceptor chain for cross-cutting concerns (inspired by Spring AI Advisor chain).
 * Each middleware can short-circuit, modify, or pass through execution.
 */
public interface Middleware {

    /** Before agent execution. Return non-null to short-circuit. */
    default AgentResult beforeExecute(AgentContext ctx) {
        return null;
    }

    /** After successful agent execution. Return modified or original result. */
    default AgentResult afterExecute(AgentContext ctx, AgentResult result) {
        return result;
    }

    /** On error during agent execution. Rethrow or return a fallback result. */
    default AgentResult onError(AgentContext ctx, Exception e) throws Exception {
        throw e;
    }

    /** Priority: lower = earlier in chain. */
    default int order() {
        return 100;
    }
}
