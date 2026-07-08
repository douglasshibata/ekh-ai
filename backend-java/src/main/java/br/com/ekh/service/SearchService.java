package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final VectorStore vectorStore;

    /**
     * Executes a hybrid search combining Semantic (Vector) and Lexical results.
     */
    public List<Document> search(String query, String department, int topK) {
        log.info("Hybrid Search: '{}' in {}", query, department);

        // 1. Vector Search
        SearchRequest vectorRequest = SearchRequest.query(query)
                .withTopK(topK * 2)
                .withSimilarityThreshold(0.6);

        if (department != null && !department.isEmpty()) {
            vectorRequest = vectorRequest.withFilterExpression("department == '" + department + "'");
        }
        List<Document> vectorDocs = vectorStore.similaritySearch(vectorRequest);

        // 2. Lexical Search (Simulated BM25 Ranking for POC)
        // In a real environment, this calls OpenSearch directly
        List<Document> lexicalDocs = simulateLexicalSearch(query, vectorDocs);

        // 3. Reciprocal Rank Fusion (RRF) Logic
        return reciprocalRankFusion(vectorDocs, lexicalDocs, topK);
    }

    private List<Document> reciprocalRankFusion(List<Document> vectorDocs, List<Document> lexicalDocs, int limit) {
        Map<String, Double> scores = new HashMap<>();

        // RRF Constant
        final int K = 60;

        for (int i = 0; i < vectorDocs.size(); i++) {
            String id = vectorDocs.get(i).getId();
            scores.put(id, scores.getOrDefault(id, 0.0) + 1.0 / (K + i + 1));
        }

        for (int i = 0; i < lexicalDocs.size(); i++) {
            String id = lexicalDocs.get(i).getId();
            scores.put(id, scores.getOrDefault(id, 0.0) + 1.0 / (K + i + 1));
        }

        // Combine and Sort
        Map<String, Document> allDocs = new HashMap<>();
        vectorDocs.forEach(d -> allDocs.put(d.getId(), d));
        lexicalDocs.forEach(d -> allDocs.put(d.getId(), d));

        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(e -> allDocs.get(e.getKey()))
                .collect(Collectors.toList());
    }

    private List<Document> simulateLexicalSearch(String query, List<Document> pool) {
        // Simple word match ranking simulation for POC
        String[] keywords = query.toLowerCase().split("\\s+");
        return pool.stream()
                .filter(d -> {
                    String content = d.getContent().toLowerCase();
                    return Arrays.stream(keywords).anyMatch(content::contains);
                })
                .sorted((a, b) -> {
                    long countA = Arrays.stream(keywords).filter(a.getContent().toLowerCase()::contains).count();
                    long countB = Arrays.stream(keywords).filter(b.getContent().toLowerCase()::contains).count();
                    return Long.compare(countB, countA);
                })
                .collect(Collectors.toList());
    }
}
