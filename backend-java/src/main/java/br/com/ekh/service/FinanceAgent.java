package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceAgent {
    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String FINANCE_PROMPT = """
            Você é um Agente Financeiro. Você lida com despesas, orçamentos, centros de custo e indicadores.
            Foque em dados numéricos e conformidade financeira.

            DADOS FINANCEIROS:
            {context}

            PERGUNTA:
            {question}
            """;

    public String ask(String question) {
        List<Document> docs = searchService.search(question, "FINANCEIRO", 5);
        String context = docs.stream().map(Document::getContent).collect(Collectors.joining("\n\n"));

        return chatClientBuilder.build().prompt()
                .user(u -> u.text(FINANCE_PROMPT).param("context", context).param("question", question))
                .call().content();
    }
}
