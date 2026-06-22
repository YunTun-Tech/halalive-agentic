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


package com.halalive.agentic.capability;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Default in-memory tool registry backed by ConcurrentHashMap.
 */
@Slf4j
public class DefaultToolRegistry implements ToolRegistry {

    private final Map<String, Tool> tools = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> tagIndex = new ConcurrentHashMap<>();

    @Override
    public void register(Tool tool) {
        String name = tool.getDefinition().getName();
        tools.put(name, tool);
        Set<String> tags = tool.getDefinition().getTags();
        if (tags != null) {
            for (String tag : tags) {
                tagIndex.computeIfAbsent(tag, k -> ConcurrentHashMap.newKeySet()).add(name);
            }
        }
        log.info("Tool registered: {}", name);
    }

    @Override
    public void registerAll(Collection<Tool> toolList) {
        toolList.forEach(this::register);
    }

    @Override
    public void unregister(String toolName) {
        Tool removed = tools.remove(toolName);
        if (removed != null && removed.getDefinition().getTags() != null) {
            for (String tag : removed.getDefinition().getTags()) {
                Set<String> names = tagIndex.get(tag);
                if (names != null) names.remove(toolName);
            }
        }
    }

    @Override
    public Optional<Tool> get(String toolName) {
        return Optional.ofNullable(tools.get(toolName));
    }

    @Override
    public List<Tool> listAll() {
        return List.copyOf(tools.values());
    }

    @Override
    public List<Tool> findByTags(String... tags) {
        Set<String> matchingNames = new HashSet<>();
        for (String tag : tags) {
            Set<String> names = tagIndex.get(tag);
            if (names != null) matchingNames.addAll(names);
        }
        if (matchingNames.isEmpty()) return List.of();
        return matchingNames.stream()
                .map(tools::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tool> findByName(Set<String> names) {
        return names.stream()
                .map(tools::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int size() {
        return tools.size();
    }
}
