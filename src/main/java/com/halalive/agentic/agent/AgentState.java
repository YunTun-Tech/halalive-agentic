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

/**
 * Agent lifecycle states with validated transitions.
 *
 * <pre>
 * CREATED -> IDLE -> RUNNING -> PAUSED / ERROR / COMPLETED
 * PAUSED  -> RUNNING
 * ERROR   -> IDLE
 * RUNNING -> COMPLETED
 * PAUSED  -> COMPLETED (forced stop)
 * </pre>
 */
public enum AgentState {
    CREATED,
    IDLE,
    RUNNING,
    PAUSED,
    ERROR,
    COMPLETED;

    public boolean canTransitionTo(AgentState target) {
        switch (this) {
            case CREATED:   return target == IDLE;
            case IDLE:      return target == RUNNING || target == COMPLETED;
            case RUNNING:   return target == PAUSED || target == ERROR || target == COMPLETED;
            case PAUSED:    return target == RUNNING || target == COMPLETED;
            case ERROR:     return target == IDLE || target == COMPLETED;
            case COMPLETED: return false;
            default:        return false;
        }
    }
}
