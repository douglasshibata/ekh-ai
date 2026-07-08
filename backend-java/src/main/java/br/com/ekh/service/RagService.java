package br.com.ekh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final SearchService searchService;
    private final ChatClient.Builder chatClientBuilder;

    private static final String RAG_PROMPT = """
            Você é um assistente virtual corporativo inteligente do Enterprise Knowledge Hub.
            Use as seguintes informações do contexto para responder à pergunta do usuário.
            Se você não sabe a resposta, diga que não sabe, não tente inventar uma resposta.

            CONTEXTO:
            {context}

            PERGUNTA:
            {question}

            RESPOSTA:
            """;

    public String generateResponse(String question, String department) {
        log.info("Generating RAG response for question: {}", question);

        // 1. Retrieve relevant documents
        List<Document> docs = searchService.search(question, department, 5);

        String context = docs.stream()
                .map(Document::getContent)
                .collect(Collectors.joining("\n---\n"));

        // 2. Augment prompt and call LLM
        ChatClient chatClient = chatClientBuilder.build();

        return chatClient.prompt()
                .user(u -> u.text(RAG_PROMPT)
                        .param("context", context)
                        .param("question", question))
                .call()
                .content();
    }

    public List<Document> getSources(String question, String department) {
        return searchService.search(question, department, 5);
    }
}
