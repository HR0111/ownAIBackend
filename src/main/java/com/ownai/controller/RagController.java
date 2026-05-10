package com.ownai.controller;

import com.ownai.dto.AskRequest;
import com.ownai.dto.InsertRequest;
import com.ownai.repository.VectorSearchRepository;
import com.ownai.services.ChunkService;
import com.ownai.services.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doc")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RagController {

    private final VectorSearchRepository vectorRepository;
    private final EmbeddingService       embeddingService;
    private final ChunkService           chunkService;
    private final ChatClient.Builder     chatClientBuilder;
    private final JdbcTemplate           jdbcTemplate;

    // ── POST /doc/insert ─────────────────────────────────────
    @PostMapping("/insert")
    public Map<String, Object> insert(@RequestBody InsertRequest request) {
        List<String> chunks = chunkService.chunk(request.getText());

        for (String chunk : chunks) {
            String embedding = embeddingService.embedAsVector(chunk);
            vectorRepository.insert(request.getTitle(), chunk, embedding);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("chunks", chunks.size());
        res.put("dims",   1536);
        return res;
    }

    // ── GET /doc/list ────────────────────────────────────────
    @GetMapping("/list")
    public List<Map<String, Object>> list() {
        return jdbcTemplate.queryForList(
                """
                SELECT id, title,
                       LEFT(content, 120) AS preview,
                       array_length(string_to_array(content, ' '), 1) AS words
                FROM document_chunks
                ORDER BY id
                """
        );
    }

    // ── DELETE /doc/delete/{id} ──────────────────────────────
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteDoc(@PathVariable Long id) {
        jdbcTemplate.update("DELETE FROM document_chunks WHERE id = ?", id);
        return Map.of("deleted", id);
    }

    // ── POST /doc/search ─────────────────────────────────────
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody AskRequest request) {
        String vector = embeddingService.embedAsVector(request.getQuestion());
        List<Map<String, Object>> rawContexts = vectorRepository.search(vector, request.getK());

        List<Map<String, Object>> contexts = rawContexts.stream().map(c -> {
            Map<String, Object> ctx = new HashMap<>();
            ctx.put("title",    c.get("title"));
            ctx.put("text",     c.get("content"));
            ctx.put("distance", c.get("distance"));
            return ctx;
        }).collect(Collectors.toList());

        return Map.of("contexts", contexts);
    }

    // ── POST /doc/ask ────────────────────────────────────────
    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody AskRequest request) {
        try {
            String vector = embeddingService.embedAsVector(request.getQuestion());

            List<Map<String, Object>> rawContexts =
                    vectorRepository.search(vector, request.getK());

            List<Map<String, Object>> contexts = rawContexts.stream().map(c -> {
                Map<String, Object> ctx = new HashMap<>();
                ctx.put("title",    c.get("title"));
                ctx.put("text",     c.get("content"));
                ctx.put("distance", c.get("distance"));
                return ctx;
            }).collect(Collectors.toList());

            String contextText = contexts.stream()
                    .map(c -> c.get("text").toString())
                    .collect(Collectors.joining("\n\n"));

            String prompt = """
                    You are a helpful AI assistant.
                    Use ONLY the context below to answer.
                    If the answer is not in the context, say "I don't know."

                    Context:
                    %s

                    Question:
                    %s
                    """.formatted(contextText, request.getQuestion());

            String answer = chatClientBuilder.build()
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            Map<String, Object> response = new HashMap<>();
            response.put("answer",   answer);
            response.put("contexts", contexts);
            response.put("model",    "meta-llama/llama-3.2-3b-instruct:free");
            return response;

        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("error",    "LLM Error: " + e.getMessage());
            err.put("contexts", List.of());
            return err;
        }
    }
}