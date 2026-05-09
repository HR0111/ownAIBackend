package com.ownai.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChunkService {

    public List<String> chunk(String text) {

        List<String> chunks = new ArrayList<>();

        int chunkSize = 700;

        for (int i = 0; i < text.length(); i += chunkSize) {

            int end = Math.min(i + chunkSize, text.length());

            chunks.add(text.substring(i, end));
        }

        return chunks;
    }
}