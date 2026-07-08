package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TiAgent {
    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String TI_PROMPT = """
            Você é um Agente de TI especializado em infraestrutura e documentação técnica.
            Ajude com manuais, APIs, procedimentos de segurança e incidentes.

            DOCUMENTAÇÃO TÉCNICA:
            {context}

            SOLICITAÇÃO:
            {question}
            """;

    public String ask(String question) {
        List<Document> docs = searchService.search(question, "TI", 5);
        String context = docs.stream().map(Document::getContent).collect(Collectors.joining("\n\n"));

        return chatClientBuilder.build().prompt()
                .user(u -> u.text(TI_PROMPT).param("context", context).param("question", question))
                .call().content();
    }
}
