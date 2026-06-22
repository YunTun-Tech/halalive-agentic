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

import java.util.concurrent.CompletableFuture;

/**
 * A self-describing capability that an Agent can use.
 * Definition and execution are separated — definition is pure JSON Schema (Anthropic pattern).
 */
public interface Tool {

    /** Declarative definition (JSON Schema). Immutable within a session — enables prompt caching. */
    ToolDefinition getDefinition();

    /** Execute the tool with the given input. */
    CompletableFuture<ToolResult> execute(ToolInput input);

    /** Eligibility check for this tool in the current context. */
    default ToolEligibility getEligibility() {
        return ctx -> true;
    }

    /**
     * Returns the effective definition, merging {@link AgenticTool} annotation values
     * for displayName/displayDescription when the definition doesn't already provide them.
     */
    default ToolDefinition getResolvedDefinition() {
        ToolDefinition def = getDefinition();
        AgenticTool ann = this.getClass().getAnnotation(AgenticTool.class);
        if (ann == null) {
            return def;
        }
        boolean hasDisplayName = def.getDisplayName() != null && !def.getDisplayName().isBlank();
        boolean hasDisplayDesc = def.getDisplayDescription() != null && !def.getDisplayDescription().isBlank();
        if (!hasDisplayName || !hasDisplayDesc) {
            return ToolDefinition.builder()
                    .name(def.getName())
                    .description(def.getDescription())
                    .displayName(hasDisplayName ? def.getDisplayName() : ann.displayName())
                    .displayDescription(hasDisplayDesc ? def.getDisplayDescription() : ann.displayDescription())
                    .inputSchemaJson(def.getInputSchemaJson())
                    .parameters(def.getParameters())
                    .tags(def.getTags())
                    .requiresApproval(def.isRequiresApproval())
                    .metadata(def.getMetadata())
                    .build();
        }
        return def;
    }
}
