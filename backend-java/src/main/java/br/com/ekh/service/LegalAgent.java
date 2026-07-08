package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LegalAgent {
    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String LEGAL_PROMPT = """
            Você é um Agente Jurídico corporativo. Sua função é analisar contratos, normas, legislação interna e pareceres.
            Seja formal, preciso e cite as cláusulas ou documentos quando possível.

            CONTRATOS E NORMAS:
            {context}

            CONSULTA JURÍDICA:
            {question}
            """;

    public String ask(String question) {
        List<Document> docs = searchService.search(question, "JURIDICO", 5);
        String context = docs.stream().map(Document::getContent).collect(Collectors.joining("\n\n"));

        return chatClientBuilder.build().prompt()
                .user(u -> u.text(LEGAL_PROMPT).param("context", context).param("question", question))
                .call().content();
    }
}
