package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesAgent {
    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String SALES_PROMPT = """
            Você é um Agente Comercial. Sua função é consultar propostas, CRM, metas e contratos com clientes.
            Foque em ajudar a equipe de vendas a fechar negócios e entender o histórico dos clientes.

            DADOS COMERCIAIS:
            {context}

            PERGUNTA:
            {question}
            """;

    public String ask(String question) {
        List<Document> docs = searchService.search(question, "COMERCIAL", 5);
        String context = docs.stream().map(Document::getContent).collect(Collectors.joining("\n\n"));

        return chatClientBuilder.build().prompt()
                .user(u -> u.text(SALES_PROMPT).param("context", context).param("question", question))
                .call().content();
    }
}
