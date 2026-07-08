package br.com.ekh.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    @GetMapping("/metrics")
    public Map<String, Object> getMetrics() {
        return Map.of(
            "totalDocuments", 1250,
            "queriesToday", 450,
            "averageResponseTime", "1.2s",
            "costThisMonth", "$12.45",
            "activeConnectors", 8,
            "tokenUsage", Map.of(
                "input", 45000,
                "output", 12000
            )
        );
    }
}
