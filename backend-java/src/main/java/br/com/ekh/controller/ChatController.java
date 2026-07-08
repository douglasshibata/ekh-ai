package br.com.ekh.controller;

import br.com.ekh.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class ChatController {

    private final RagService ragService;
    private final HrAgent hrAgent;
    private final LegalAgent legalAgent;
    private final FinanceAgent financeAgent;
    private final TiAgent tiAgent;
    private final SalesAgent salesAgent;

    @PostMapping("/ask")
    public Map<String, Object> ask(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        String department = request.get("department");

        log.info("Request received - Dept: {}, Question: {}", department, question);

        String answer;
        if (department == null || department.isEmpty()) {
            answer = ragService.generateResponse(question, null);
        } else {
            answer = switch (department.toUpperCase()) {
                case "RH" -> hrAgent.ask(question);
                case "JURIDICO" -> legalAgent.ask(question);
                case "FINANCEIRO" -> financeAgent.ask(question);
                case "TI" -> tiAgent.ask(question);
                case "COMERCIAL" -> salesAgent.ask(question);
                default -> ragService.generateResponse(question, department);
            };
        }

        List<Document> sources = ragService.getSources(question, department);

        return Map.of(
            "answer", answer,
            "sources", sources.stream().map(d -> Map.of(
                "title", d.getMetadata().getOrDefault("title", "Documento Interno"),
                "id", d.getMetadata().getOrDefault("document_id", "0")
            )).toList()
        );
    }
}
