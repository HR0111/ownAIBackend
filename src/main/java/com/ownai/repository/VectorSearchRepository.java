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

    public List<Map<String, Object>> search(
            String embedding,
            int k
    ) {

        String sql = """
            SELECT id, title, content,
            embedding <=> CAST(? AS vector) AS distance
            FROM document_chunks
            ORDER BY distance
            LIMIT ?
        """;

        return jdbcTemplate.queryForList(
                sql,
                embedding,
                k
        );
    }
}
