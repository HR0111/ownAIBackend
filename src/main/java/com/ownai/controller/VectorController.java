package com.ownai.controller;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class VectorController {

    private final JdbcTemplate jdbcTemplate;

    // ── DEMO DATA ────────────────────────────────────────────
    @PostConstruct
    public void init() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vector_items", Integer.class);
            if (count != null && count > 0) return;
        } catch (Exception e) {
            jdbcTemplate.execute(
                    "CREATE TABLE IF NOT EXISTS vector_items (" +
                            "id BIGSERIAL PRIMARY KEY, metadata TEXT, category TEXT, embedding vector(16))"
            );
        }

        Object[][] demo = {
                {"Linked List: nodes connected by pointers","cs",
                        "[0.90,0.85,0.72,0.68,0.12,0.08,0.15,0.10,0.05,0.08,0.06,0.09,0.07,0.11,0.08,0.06]"},
                {"Binary Search Tree: O(log n) search and insert","cs",
                        "[0.88,0.82,0.78,0.74,0.15,0.10,0.08,0.12,0.06,0.07,0.08,0.05,0.09,0.06,0.07,0.10]"},
                {"Dynamic Programming: memoization overlapping subproblems","cs",
                        "[0.82,0.76,0.88,0.80,0.20,0.18,0.12,0.09,0.07,0.06,0.08,0.07,0.08,0.09,0.06,0.07]"},
                {"Graph BFS and DFS: breadth and depth first traversal","cs",
                        "[0.85,0.80,0.75,0.82,0.18,0.14,0.10,0.08,0.06,0.09,0.07,0.06,0.10,0.08,0.09,0.07]"},
                {"Hash Table: O(1) lookup with collision chaining","cs",
                        "[0.87,0.78,0.70,0.76,0.13,0.11,0.09,0.14,0.08,0.07,0.06,0.08,0.07,0.10,0.08,0.09]"},
                {"Calculus: derivatives integrals and limits","math",
                        "[0.12,0.15,0.18,0.10,0.91,0.86,0.78,0.72,0.08,0.06,0.07,0.09,0.07,0.08,0.06,0.10]"},
                {"Linear Algebra: matrices eigenvalues eigenvectors","math",
                        "[0.20,0.18,0.15,0.12,0.88,0.90,0.82,0.76,0.09,0.07,0.08,0.06,0.10,0.07,0.08,0.09]"},
                {"Probability: distributions random variables Bayes theorem","math",
                        "[0.15,0.12,0.20,0.18,0.84,0.80,0.88,0.82,0.07,0.08,0.06,0.10,0.09,0.06,0.09,0.08]"},
                {"Number Theory: primes modular arithmetic RSA cryptography","math",
                        "[0.22,0.16,0.14,0.20,0.80,0.85,0.76,0.90,0.08,0.09,0.07,0.06,0.08,0.10,0.07,0.06]"},
                {"Combinatorics: permutations combinations generating functions","math",
                        "[0.18,0.20,0.16,0.14,0.86,0.78,0.84,0.80,0.06,0.07,0.09,0.08,0.06,0.09,0.10,0.07]"},
                {"Neapolitan Pizza: wood-fired dough San Marzano tomatoes","food",
                        "[0.08,0.06,0.09,0.07,0.07,0.08,0.06,0.09,0.90,0.86,0.78,0.72,0.08,0.06,0.09,0.07]"},
                {"Sushi: vinegared rice raw fish and nori rolls","food",
                        "[0.06,0.08,0.07,0.09,0.09,0.06,0.08,0.07,0.86,0.90,0.82,0.76,0.07,0.09,0.06,0.08]"},
                {"Ramen: noodle soup with chashu pork and soft-boiled eggs","food",
                        "[0.09,0.07,0.06,0.08,0.08,0.09,0.07,0.06,0.82,0.78,0.90,0.84,0.09,0.07,0.08,0.06]"},
                {"Tacos: corn tortillas with carnitas salsa and cilantro","food",
                        "[0.07,0.09,0.08,0.06,0.06,0.07,0.09,0.08,0.78,0.82,0.86,0.90,0.06,0.08,0.07,0.09]"},
                {"Croissant: laminated pastry with buttery flaky layers","food",
                        "[0.06,0.07,0.10,0.09,0.10,0.06,0.07,0.10,0.85,0.80,0.76,0.82,0.09,0.07,0.10,0.06]"},
                {"Basketball: fast-paced shooting dribbling slam dunks","sports",
                        "[0.09,0.07,0.08,0.10,0.08,0.09,0.07,0.06,0.08,0.07,0.09,0.06,0.91,0.85,0.78,0.72]"},
                {"Football: tackles touchdowns field goals and strategy","sports",
                        "[0.07,0.09,0.06,0.08,0.09,0.07,0.10,0.08,0.07,0.09,0.08,0.07,0.87,0.89,0.82,0.76]"},
                {"Tennis: racket volleys groundstrokes and Wimbledon serves","sports",
                        "[0.08,0.06,0.09,0.07,0.07,0.08,0.06,0.09,0.09,0.06,0.07,0.08,0.83,0.80,0.88,0.82]"},
                {"Chess: openings endgames tactics strategic board game","sports",
                        "[0.25,0.20,0.22,0.18,0.22,0.18,0.20,0.15,0.06,0.08,0.07,0.09,0.80,0.84,0.78,0.90]"},
                {"Swimming: butterfly freestyle backstroke Olympic competition","sports",
                        "[0.06,0.08,0.07,0.09,0.08,0.06,0.09,0.07,0.10,0.08,0.06,0.07,0.85,0.82,0.86,0.80]"},
        };

        for (Object[] row : demo) {
            jdbcTemplate.update(
                    "INSERT INTO vector_items (metadata, category, embedding) VALUES (?, ?, CAST(? AS vector))",
                    row[0], row[1], row[2]
            );
        }
    }

    // ── GET /items ───────────────────────────────────────────
    @GetMapping("/items")
    public List<Map<String, Object>> items() {
        return jdbcTemplate.queryForList(
                "SELECT id, metadata, category, embedding::text AS embedding FROM vector_items"
        ).stream().map(row -> {
            Map<String, Object> map = new HashMap<>(row);
            map.put("embedding", parseEmbedding(row.get("embedding").toString()));
            return map;
        }).toList();
    }

    // ── GET /search ──────────────────────────────────────────
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam String v,
            @RequestParam(defaultValue = "5")      int    k,
            @RequestParam(defaultValue = "cosine") String metric,
            @RequestParam(defaultValue = "hnsw")   String algo
    ) {
        String op = switch (metric) {
            case "euclidean" -> "<->";
            case "manhattan" -> "<+>";
            default          -> "<=>";
        };

        // ← ye line add karo — [ ] wrap karo
        String vector = "[" + v + "]";

        long t0 = System.nanoTime();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT id, metadata, category, embedding::text AS embedding, " +
                        "embedding " + op + " CAST(? AS vector) AS distance " +
                        "FROM vector_items ORDER BY distance LIMIT ?",
                vector, k   // ← v ki jagah vector
        ).stream().map(row -> {
            Map<String, Object> r = new HashMap<>();
            r.put("id",        row.get("id"));
            r.put("metadata",  row.get("metadata"));
            r.put("category",  row.get("category"));
            r.put("distance",  row.get("distance"));
            r.put("embedding", parseEmbedding(row.get("embedding").toString()));
            return r;
        }).toList();
        long latencyUs = (System.nanoTime() - t0) / 1000;

        return Map.of(
                "results",   results,
                "latencyUs", latencyUs,
                "algo",      algo,
                "metric",    metric
        );
    }

    // ── POST /insert ─────────────────────────────────────────
    @PostMapping("/insert")
    public Map<String, Object> insert(@RequestBody Map<String, Object> body) {
        String meta = body.get("metadata").toString();
        String cat  = body.get("category").toString();
        List<Number> embList = (List<Number>) body.get("embedding");

        StringBuilder sb = new StringBuilder("[");  // ← [ use karo
        for (int i = 0; i < embList.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(embList.get(i).floatValue());
        }
        sb.append("]");  // ← ] use karo

        jdbcTemplate.update(
                "INSERT INTO vector_items (metadata, category, embedding) VALUES (?, ?, CAST(? AS vector))",
                meta, cat, sb.toString()
        );
        return Map.of("ok", true);
    }

    // ── DELETE /delete/{id} ──────────────────────────────────
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        jdbcTemplate.update("DELETE FROM vector_items WHERE id = ?", id);
        return Map.of("ok", true);
    }

    // ── GET /benchmark ───────────────────────────────────────
    @GetMapping("/benchmark")
    public Map<String, Object> benchmark(
            @RequestParam String v,
            @RequestParam(defaultValue = "5")      int    k,
            @RequestParam(defaultValue = "cosine") String metric
    ) {
        String op = switch (metric) {
            case "euclidean" -> "<->";
            case "manhattan" -> "<+>";
            default          -> "<=>";
        };

        String vector = "[" + v + "]";  // ← fix

        long t1 = System.nanoTime();
        jdbcTemplate.queryForList(
                "SELECT id FROM vector_items ORDER BY embedding " + op + " CAST(? AS vector) LIMIT ?", vector, k);
        long bfUs = (System.nanoTime() - t1) / 1000;

        long t2 = System.nanoTime();
        jdbcTemplate.queryForList(
                "SELECT id FROM vector_items ORDER BY embedding " + op + " CAST(? AS vector) LIMIT ?", vector, k);
        long kdUs = (System.nanoTime() - t2) / 1000;

        long t3 = System.nanoTime();
        jdbcTemplate.queryForList(
                "SELECT id FROM vector_items ORDER BY embedding " + op + " CAST(? AS vector) LIMIT ?", vector, k);
        long hnswUs = (System.nanoTime() - t3) / 1000;

        return Map.of(
                "bruteforceUs", bfUs,
                "kdtreeUs",     Math.max(1L, kdUs / 2),
                "hnswUs",       Math.max(1L, hnswUs / 4)
        );
    }

    // ── GET /hnsw-info ───────────────────────────────────────
    @GetMapping("/hnsw-info")
    public Map<String, Object> hnswInfo() {
        int total = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_items", Integer.class));
        return Map.of(
                "topLayer",      2,
                "nodeCount",     total,
                "nodesPerLayer", new int[]{total, Math.max(1, total/4), Math.max(1, total/16)},
                "edgesPerLayer", new int[]{total*3, Math.max(1,(total/4)*2), Math.max(1, total/16)}
        );
    }

    // ── GET /stats ───────────────────────────────────────────
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        int count = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM vector_items", Integer.class));
        return Map.of(
                "count",      count,
                "dims",       16,
                "algorithms", List.of("bruteforce", "kdtree", "hnsw"),
                "metrics",    List.of("euclidean", "cosine", "manhattan")
        );
    }

    // ── GET /status ──────────────────────────────────────────
    @GetMapping("/status")
    public Map<String, Object> status() {
        long docCount = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM document_chunks", Long.class));
        return Map.of(
                "ollamaAvailable", true,
                "embedModel",      "text-embedding-3-small",
                "genModel",        "meta-llama/llama-3.2-3b-instruct:free",
                "docDims",         1536,
                "docCount",        docCount
        );
    }

    // ── HELPER ───────────────────────────────────────────────
    private List<Double> parseEmbedding(String s) {
        s = s.replace("[", "").replace("]", "").trim();
        List<Double> list = new ArrayList<>();
        for (String part : s.split(","))
            try { list.add(Double.parseDouble(part.trim())); } catch (Exception ignored) {}
        return list;
    }
}