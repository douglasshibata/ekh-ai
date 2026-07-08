# EKH AI - Arquitetura Técnica

Este documento detalha a arquitetura do **Enterprise Knowledge Hub (EKH AI)**, projetada para escalabilidade, segurança e precisão na recuperação de informações.

## 1. Visão Geral da Arquitetura

O sistema segue uma arquitetura orientada a serviços e eventos para suportar o processamento intensivo de documentos e consultas de IA.

### Fluxo de Ingestão de Dados
1. **Conector:** Monitora fontes externas (SharePoint, APIs, DBs).
2. **Extração (Apache Tika):** Extrai texto e metadados de arquivos (PDF, DOCX, etc).
3. **Chunking:** Divide o texto em fragmentos semânticos.
4. **Embeddings:** Gera vetores numéricos para cada chunk.
5. **Indexação Dupla:**
   - **Qdrant:** Armazena vetores para busca semântica.
   - **OpenSearch:** Armazena texto para busca lexical (palavras-chave).

### Fluxo de Consulta (RAG)
1. **Pergunta:** Recebida via WebSocket/REST.
2. **Busca Híbrida:** Consulta simultânea ao Qdrant e OpenSearch.
3. **Reranking:** Reordena os resultados mais relevantes.
4. **Context Construction:** Monta o prompt com os chunks recuperados.
5. **LLM:** Gera a resposta final.
6. **Resposta:** Enviada ao usuário com citações das fontes.

## 2. Componentes de Software

### Backend (Spring Boot)
- **Spring AI:** Abstração para integração com LLMs (Gemini/OpenAI).
- **Spring Data JPA:** Gestão de metadados e histórico em PostgreSQL.
- **RabbitMQ:** Desacoplamento do pipeline de ingestão.
- **Security (OAuth2):** Controle de acesso RBAC e auditoria.

### Frontend (Angular 20+)
- **Signals:** Gestão de estado reativa para o chat.
- **Tailwind:** Interface moderna e responsiva.
- **WebSocket:** Comunicação em tempo real para streaming de respostas.

## 3. Estratégia de Dados

| Componente | Função |
|------------|--------|
| **PostgreSQL** | Metadados de documentos, usuários, permissões e histórico de chat. |
| **Qdrant** | Busca por similaridade vetorial. |
| **OpenSearch** | Busca textual de alta performance (BM25). |
| **Redis** | Cache de embeddings e sessões de usuário. |

## 4. Segurança e Governança
- **Isolamento Multi-tenant:** Filtros de metadados em todas as buscas garantem que usuários vejam apenas o que têm permissão.
- **Trilha de Auditoria:** Registro de cada pergunta e as fontes utilizadas para a resposta.
- **Filtro de PII:** Detecção e anonimização de dados sensíveis antes do envio ao LLM.
