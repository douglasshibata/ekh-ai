package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HrAgent {
    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String HR_PROMPT = """
            Você é um Agente de RH especializado. Sua função é responder sobre benefícios, férias, políticas de treinamento e admissões.
            Utilize os documentos de RH fornecidos para fundamentar sua resposta.

            CONTEXTO DE RH:
            {context}

            PERGUNTA DO COLABORADOR:
            {question}
            """;

    public String ask(String question) {
        List<Document> docs = searchService.search(question, "RH", 5);
        String context = docs.stream().map(Document::getContent).collect(Collectors.joining("\n\n"));

        return chatClientBuilder.build().prompt()
                .user(u -> u.text(HR_PROMPT).param("context", context).param("question", question))
                .call().content();
    }
}
