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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * BeanPostProcessor that auto-registers any Spring bean implementing {@link Tool}
 * into the {@link ToolRegistry} after initialization.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ToolAutoRegistration implements BeanPostProcessor {

    private final ToolRegistry toolRegistry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Tool tool) {
            String toolName = tool.getDefinition().getName();
            // Avoid duplicate registration
            if (toolRegistry.get(toolName).isEmpty()) {
                toolRegistry.register(tool);
            } else {
                log.debug("Tool '{}' already registered, skipping bean '{}'", toolName, beanName);
            }
        }
        return bean;
    }
}
