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


package com.halalive.agentic.human;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Human-in-the-loop interface. Modeled after Google ADK's LongRunningEvent + LangGraph's interrupt().
 */
public interface HumanInTheLoop {

    /** Request human approval. Returns a future that completes when the human responds. */
    CompletableFuture<HumanResponse> requestApproval(HumanRequest request);

    /** Check if there's a pending request. */
    boolean hasPending(String sessionId);

    @Data
    @Builder
    class HumanRequest {
        private String requestId;
        private String agentId;
        private String sessionId;
        private String title;
        private String description;
        private Map<String, Object> context;
        private List<String> availableActions;
        private Duration timeout;
    }

    @Data
    @Builder
    class HumanResponse {
        private String requestId;
        private String action;          // "approved", "rejected", "modified", "escalated"
        private Map<String, Object> modifications;
        private String comment;
        private String respondedBy;
    }
}
