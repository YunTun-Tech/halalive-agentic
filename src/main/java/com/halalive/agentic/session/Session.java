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


package com.halalive.agentic.session;

import com.halalive.agentic.agent.AgentId;
import com.halalive.agentic.agent.AgentState;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Typed session state with an escape hatch (extensions map) for flexibility.
 * Inspired by: LangGraph (typedState + reducer), OpenAI SDK (typed RunContext).
 */
@Data
@Builder
public class Session {
    private SessionId id;
    private AgentId agentId;
    private AgentState state;
    @Builder.Default
    private List<Message> history = new ArrayList<>();
    @Builder.Default
    private Map<String, Object> typedState = new HashMap<>();
    @Builder.Default
    private Map<String, Object> extensions = new HashMap<>();
    private Instant createdAt;
    private Instant updatedAt;

    public void addMessage(Message message) {
        if (history == null) history = new ArrayList<>();
        history.add(message);
        updatedAt = Instant.now();
    }

    @Data
    @Builder
    public static class Message {
        private String role;       // "system", "user", "assistant", "tool"
        private String content;
        private String toolName;
        private String toolCallId;
        private Map<String, Object> metadata;
        private Instant timestamp;
    }
}
