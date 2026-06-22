# Agentic Framework · 智能体框架

[English](#english) | [中文](#中文)

A lightweight, Spring-native AI agent framework for building LLM-powered agents in Java.

一款轻量级、Spring 原生的 AI 智能体框架，用于在 Java 中构建 LLM 驱动的智能体。

---

## <a id="english"></a>Features

- **Agent abstraction** — compose agents with pluggable tools, middleware, memory, and execution engines
- **Tool system** — auto-discover and register tools via Spring component scanning or programmatic API
- **ReAct loop** — built-in reasoning-and-acting execution loop with configurable iteration limits
- **Session & memory** — in-memory and Redis-backed session stores with checkpoint support
- **Middleware pipeline** — intercept agent execution at multiple lifecycle points
- **Event bus** — publish/subscribe for cross-agent communication
- **Human-in-the-loop** — hook into agent decisions for approval workflows

## Quick Start

### Maven

```xml
<dependency>
    <groupId>com.halalive</groupId>
    <artifactId>halalive-agentic-framework</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Define a Tool

```java
@Component
@AgenticTool(name = "search_web", description = "Search the web for information")
public class SearchWebTool implements Tool {

    @Override
    public ToolDefinition getDefinition() {
        return ToolDefinition.builder()
            .name("search_web")
            .description("Search the web")
            .parameters(List.of(
                ParameterDef.builder().name("query").type("string")
                    .description("Search query").required(true).build()
            ))
            .build();
    }

    @Override
    public CompletableFuture<ToolResult> execute(ToolInput input) {
        String query = (String) input.getArguments().get("query");
        // ... perform search ...
        return CompletableFuture.completedFuture(
            ToolResult.builder().success(true)
                .data(Map.of("results", results)).build()
        );
    }
}
```

### Build an Agent

```java
Agent agent = AgentBuilder.builder()
    .id(AgentId.of("my-agent", "v1"))
    .card(AgentCard.builder()
        .name("MyAgent")
        .description("An example agent")
        .version("1.0.0")
        .build())
    .model("deepseek-v4-flash")
    .instructions("You are a helpful assistant.")
    .toolRegistry(toolRegistry)
    .executionEngine(executionEngine)
    .build();

AgentContext ctx = AgentContext.builder()
    .input(Map.of("task", "Find the latest news about AI"))
    .toolRegistry(toolRegistry)
    .build();

AgentResult result = agent.execute(ctx).get();
```

## Architecture

```
com.halalive.agentic
├── agent/          # Agent, AgentBuilder, AgentContext, AgentResult
├── capability/     # Tool, ToolRegistry, ToolDefinition, @AgenticTool
├── event/          # AgentEvent, AgentEventBus
├── human/          # HumanInTheLoop
├── middleware/      # Middleware, MiddlewarePipeline
├── orchestration/  # AgentEngine (execution engine interface)
└── session/        # Session, SessionStore, MemoryStrategy
```

## Requirements

- Java 17+
- Spring Boot 2.7+ (optional — core interfaces work standalone)
- Redis (optional — for RedisSessionStore)

## License

MIT License — see [LICENSE](LICENSE)

---

## <a id="中文"></a>中文

### 特性

- **Agent 抽象** — 通过可插拔的工具、中间件、记忆和执行引擎组合智能体
- **工具系统** — 通过 Spring 组件扫描或编程式 API 自动发现和注册工具
- **ReAct 循环** — 内置推理-行动执行循环，支持可配置的迭代次数限制
- **会话与记忆** — 基于内存和 Redis 的会话存储，支持检查点
- **中间件管道** — 在多个生命周期节点拦截智能体执行
- **事件总线** — 发布/订阅模式，支持跨智能体通信
- **人机协同** — 在智能体决策流程中接入人工审批

### 快速开始

#### Maven

```xml
<dependency>
    <groupId>com.halalive</groupId>
    <artifactId>halalive-agentic-framework</artifactId>
    <version>0.1.0</version>
</dependency>
```

#### 定义一个工具

```java
@Component
@AgenticTool(name = "search_web", description = "搜索网络获取信息")
public class SearchWebTool implements Tool {

    @Override
    public ToolDefinition getDefinition() {
        return ToolDefinition.builder()
            .name("search_web")
            .description("搜索网络")
            .parameters(List.of(
                ParameterDef.builder().name("query").type("string")
                    .description("搜索关键词").required(true).build()
            ))
            .build();
    }

    @Override
    public CompletableFuture<ToolResult> execute(ToolInput input) {
        String query = (String) input.getArguments().get("query");
        // ... 执行搜索 ...
        return CompletableFuture.completedFuture(
            ToolResult.builder().success(true)
                .data(Map.of("results", results)).build()
        );
    }
}
```

#### 构建一个智能体

```java
Agent agent = AgentBuilder.builder()
    .id(AgentId.of("my-agent", "v1"))
    .card(AgentCard.builder()
        .name("MyAgent")
        .description("示例智能体")
        .version("1.0.0")
        .build())
    .model("deepseek-v4-flash")
    .instructions("你是一个乐于助人的助手。")
    .toolRegistry(toolRegistry)
    .executionEngine(executionEngine)
    .build();

AgentContext ctx = AgentContext.builder()
    .input(Map.of("task", "查找关于AI的最新新闻"))
    .toolRegistry(toolRegistry)
    .build();

AgentResult result = agent.execute(ctx).get();
```

### 架构

```
com.halalive.agentic
├── agent/          # Agent、AgentBuilder、AgentContext、AgentResult
├── capability/     # Tool、ToolRegistry、ToolDefinition、@AgenticTool
├── event/          # AgentEvent、AgentEventBus
├── human/          # HumanInTheLoop
├── middleware/      # Middleware、MiddlewarePipeline
├── orchestration/  # AgentEngine（执行引擎接口）
└── session/        # Session、SessionStore、MemoryStrategy
```

### 环境要求

- Java 17+
- Spring Boot 2.7+（可选 —— 核心接口可独立运行）
- Redis（可选 —— 用于 RedisSessionStore）

### 许可证

MIT License — 详见 [LICENSE](LICENSE)
