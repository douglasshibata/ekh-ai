# Enterprise Knowledge Hub (EKH AI)

O **Enterprise Knowledge Hub** é uma plataforma de Inteligência Artificial Corporativa que centraliza todo o conhecimento de uma organização, permitindo que colaboradores encontrem respostas em linguagem natural, gerem documentos, automatizem tarefas e consultem informações distribuídas em diversos sistemas internos.

## 🚀 Visão Geral

Em vez de pesquisar manualmente em SharePoint, Confluence, e-mails, PDFs, ERPs e CRMs, o usuário faz uma pergunta ao sistema, que consulta múltiplas fontes, combina os resultados com busca híbrida e utiliza um LLM para gerar uma resposta fundamentada nas informações da empresa.

## ✨ Principais Funcionalidades

- **Busca Híbrida Inteligente:** Combinação de busca vetorial (semântica) e busca textual (lexical) para máxima precisão.
- **Motor RAG (Retrieval-Augmented Generation):** Respostas baseadas exclusivamente em dados corporativos com citações das fontes.
- **Agentes Especializados:** Agentes com contexto específico para RH, Jurídico, Financeiro, Comercial e TI.
- **Conectores de Dados:** Integração com SharePoint, Google Drive, Salesforce, SAP, bancos de dados SQL e muito mais.
- **OCR Inteligente:** Extração de texto e dados de documentos digitalizados e imagens.
- **Workflows Inteligentes:** Execução de ações automáticas após a geração de respostas.
- **Painel Administrativo:** Monitoramento de custos, tokens, qualidade de respostas e engajamento.

## 🛠️ Stack Tecnológica

### Backend
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.3.0
- **IA:** Spring AI, LangChain4j
- **Processamento:** Spring Batch, RabbitMQ, Apache Tika

### Frontend
- **Framework:** Angular 20+
- **Estado:** Signals
- **Estilização:** Tailwind CSS
- **Visualização:** Highcharts

### Infraestrutura & Dados
- **Bancos de Dados:** PostgreSQL (Relacional), Qdrant (Vetorial), OpenSearch (Textual), Redis (Cache)
- **Orquestração:** Docker, Kubernetes

## 📁 Estrutura do Projeto

```text
ekh-ai/
├── backend-java/       # API Spring Boot e Motor de IA
├── frontend-angular/   # Portal Administrativo e Interface de Chat
└── infra/
    └── docker/         # Configurações de containers e orquestração
```

## 🚥 Como Iniciar (Desenvolvimento)

1. **Configuração de Infra:**
   ```bash
   cd infra/docker
   docker-compose up -d
   ```

2. **Backend:**
   ```bash
   cd backend-java
   ./mvnw spring-boot:run
   ```

3. **Frontend:**
   ```bash
   cd frontend-angular
   npm install && npm start
   ```
