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


package com.halalive.agentic.event;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Default in-memory event bus using CopyOnWriteArrayList for subscriber safety.
 */
@Slf4j
public class DefaultAgentEventBus implements AgentEventBus {

    private final ConcurrentHashMap<String, List<Consumer<AgentEvent>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public void publish(AgentEvent event) {
        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID().toString());
        }
        if (event.getTimestamp() == null) {
            event.setTimestamp(java.time.Instant.now());
        }
        List<Consumer<AgentEvent>> handlers = subscribers.get(event.getTopic());
        if (handlers != null) {
            for (Consumer<AgentEvent> handler : handlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    log.error("Event handler error for topic {}: {}", event.getTopic(), e.getMessage());
                }
            }
        }
    }

    @Override
    public void subscribe(String topic, Consumer<AgentEvent> handler) {
        subscribers.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public void unsubscribe(String topic, Consumer<AgentEvent> handler) {
        List<Consumer<AgentEvent>> handlers = subscribers.get(topic);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    @Override
    public int subscriberCount(String topic) {
        List<Consumer<AgentEvent>> handlers = subscribers.get(topic);
        return handlers != null ? handlers.size() : 0;
    }
}
