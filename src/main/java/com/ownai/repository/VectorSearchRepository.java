package com.ownai.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
@Repository
@RequiredArgsConstructor
public class VectorSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insert(String title, String content, String embedding) {
        jdbcTemplate.update(
                "INSERT INTO document_chunks (title, content, embedding) VALUES (?, ?, CAST(? AS vector))",
                title, content, embedding
        );
    }

    public List<Map<String, Object>> search(String embedding, int k) {
        // ← embedding already [..] format mein aata hai EmbeddingService se
        // lekin agar nahi aaya toh wrap karo
        String vector = embedding.startsWith("[") ? embedding : "[" + embedding + "]";

        return jdbcTemplate.queryForList(
                """
                SELECT id, title, content,
                       embedding <=> CAST(? AS vector) AS distance
                FROM document_chunks
                ORDER BY distance
                LIMIT ?
                """,
                vector, k
        );
    }
}