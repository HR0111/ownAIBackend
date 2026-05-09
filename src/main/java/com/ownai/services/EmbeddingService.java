package com.ownai.services;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public List<Double> embed(String text) {

        float[] embedding = embeddingModel.embed(text);

        List<Double> vector = new ArrayList<>();

        for (float value : embedding) {
            vector.add((double) value);
        }

        return vector;
    }
}
