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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Runtime tool discovery and resolution.
 * Three-layer discovery: annotation scanning + programmatic registration + MCP remote.
 */
public interface ToolRegistry {

    /** Register a tool programmatically. */
    void register(Tool tool);

    /** Register a batch of tools. */
    void registerAll(Collection<Tool> tools);

    /** Unregister a tool by name. */
    void unregister(String toolName);

    /** Get a tool by name. */
    Optional<Tool> get(String toolName);

    /** List all registered tools. */
    List<Tool> listAll();

    /** Find tools by tags. */
    List<Tool> findByTags(String... tags);

    /** Find tools whose names are in the given set. */
    List<Tool> findByName(Set<String> names);

    /** Get count of registered tools. */
    int size();
}
