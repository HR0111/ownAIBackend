package com.ownai.controller;


import com.ownai.dto.AskRequest;
import com.ownai.dto.InsertRequest;
import com.ownai.model.DocumentChunk;
import com.ownai.repository.DocumentRepository;
import com.ownai.repository.VectorSearchRepository;
import com.ownai.services.ChunkService;
import com.ownai.services.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doc")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RagController {


    private final DocumentRepository repository;
    private final VectorSearchRepository vectorRepository;
    private final EmbeddingService embeddingService;
    private final ChunkService chunkService;
    private final ChatClient.Builder chatClientBuilder;

    @PostMapping("/insert")
    public String insert(
            @RequestBody InsertRequest request
    ) {

        List<String> chunks =
                chunkService.chunk(request.getText());

        for (String chunk : chunks) {

            List<Double> embedding =
                    embeddingService.embed(chunk);

            DocumentChunk doc = new DocumentChunk();

            doc.setTitle(request.getTitle());
            doc.setContent(chunk);
            doc.setEmbedding(embedding.toString());

            repository.save(doc);
        }

        return "Inserted Successfully";

    }


    @PostMapping("/ask")
    public Map<String, Object> ask(
            @RequestBody AskRequest request
    ) {

        List<Double> questionEmbedding =
                embeddingService.embed(
                        request.getQuestion()
                );

        List<Map<String, Object>> contexts =
                vectorRepository.search(
                        questionEmbedding.toString(),
                        request.getK()
                );

        String contextText = contexts.stream()
                .map(c -> c.get("content").toString())
                .collect(Collectors.joining("\n\n"));

        String prompt = """
                You are a helpful AI assistant.

                Use ONLY the context below.

                Context:
                %s

                Question:
                %s
                """.formatted(
                contextText,
                request.getQuestion()
        );

        String answer = chatClientBuilder
                .build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        Map<String, Object> response =
                new HashMap<>();

        response.put("answer", answer);
        response.put("contexts", contexts);

        return response;
    }
}