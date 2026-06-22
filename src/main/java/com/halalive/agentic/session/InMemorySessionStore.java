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

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory session store for development and testing.
 */
@Slf4j
public class InMemorySessionStore implements SessionStore {

    private final Map<SessionId, Session> sessions = new ConcurrentHashMap<>();
    private final Map<SessionId, List<Checkpoint>> checkpoints = new ConcurrentHashMap<>();

    @Override
    public Session load(SessionId id) {
        return sessions.get(id);
    }

    @Override
    public void save(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void delete(SessionId id) {
        sessions.remove(id);
        checkpoints.remove(id);
    }

    @Override
    public boolean exists(SessionId id) {
        return sessions.containsKey(id);
    }

    @Override
    public Checkpoint checkpoint(SessionId id, int stepIndex, String stepName, String stateJson) {
        Checkpoint cp = Checkpoint.builder()
                .checkpointId(UUID.randomUUID().toString())
                .sessionId(id)
                .stepIndex(stepIndex)
                .stepName(stepName)
                .stateJson(stateJson)
                .createdAt(Instant.now())
                .build();
        checkpoints.computeIfAbsent(id, k -> new ArrayList<>()).add(cp);
        log.debug("Checkpoint created: session={}, step={}", id, stepIndex);
        return cp;
    }

    @Override
    public Optional<Session> restore(SessionId id, String checkpointId) {
        Session session = sessions.get(id);
        if (session != null) {
            session.setUpdatedAt(Instant.now());
            return Optional.of(session);
        }
        return Optional.empty();
    }

    @Override
    public List<Checkpoint> listCheckpoints(SessionId id) {
        return checkpoints.getOrDefault(id, List.of());
    }
}
