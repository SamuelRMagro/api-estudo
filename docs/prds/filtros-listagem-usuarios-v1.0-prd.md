# Filtros na Listagem de Usuários - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Business Problem**: O endpoint de listagem de usuários (`GET /usuarios`) hoje retorna todos os usuários de forma paginada, sem possibilidade de filtragem. Isso dificulta a busca de registros específicos em bases de usuários grandes.
- **Target Users**: Consumidores (clientes/frontends) do endpoint `/usuarios` que precisam localizar usuários por critérios específicos.
- **Value Proposition**: Permite localizar usuários rapidamente por nome, status, e-mail e período de cadastro, sem quebrar a paginação já existente.

### Feature Overview
- **Core Features**: Adição de filtros opcionais e combináveis ao endpoint existente `GET /usuarios`, mantendo a estrutura de paginação atual.
- **Feature Boundaries**:
  - Inclui: adição dos parâmetros de filtro à query existente.
  - Não inclui: alteração do mecanismo de paginação (parâmetros, defaults, ordenação), alteração do cálculo/mascaramento do CPF, criação de autenticação/autorização.
- **User Scenarios**:
  - Buscar usuários ativos cujo nome contenha "joao".
  - Buscar usuários cadastrados em um intervalo de datas específico.
  - Combinar múltiplos filtros simultaneamente (ex: nome + isAtivo + intervalo de datas).
  - Listar todos os usuários sem informar nenhum filtro (comportamento atual preservado).

### Detailed Requirements

#### Endpoint
- **Path**: `GET /usuarios`
- **Autenticação**: Não exigida (endpoint público, comportamento mantido)
- **Paginação**: Mecanismo já existente no endpoint deve ser preservado sem alterações (parâmetros, defaults e ordenação atuais continuam valendo).

#### Campos de Retorno (por item da listagem)
| Campo | Tipo |
|---|---|
| id | Long |
| nome | String |
| isAtivo | Boolean |
| idade | int |
| email | String |
| login | String |
| cpfMascarado | String |
| dataCadastro | LocalDateTime |

> `cpfMascarado`: reutilizar o padrão de mascaramento de CPF já existente no sistema. Nenhuma nova lógica de mascaramento deve ser criada.

#### Filtros (todos opcionais)

| Filtro | Tipo | Comportamento |
|---|---|---|
| `nome` | String | Busca parcial (`LIKE %valor%`), case-insensitive |
| `isAtivo` | Boolean | Igualdade exata. Se não informado, retorna usuários ativos e inativos |
| `email` | String | Busca parcial (`LIKE %valor%`), case-insensitive |
| `dataCadastroInicio` | Data | Filtra `dataCadastro >= dataCadastroInicio` (inclusivo). Opcional e independente de `dataCadastroFim` |
| `dataCadastroFim` | Data | Filtra `dataCadastro <= dataCadastroFim` (inclusivo). Opcional e independente de `dataCadastroInicio` |

- **Combinação de filtros**: todos os filtros informados se combinam com **AND**. Filtros não informados são ignorados na composição da query.
- A escolha da estratégia de implementação da query (Specification, Criteria API, JPQL, `ILIKE` vs `UPPER()/LOWER()`, etc.) fica a critério do back-end — este PRD define apenas o comportamento observável, não a implementação.

#### Regras de Validação

1. **Intervalo de datas invertido**: se **ambas** `dataCadastroInicio` e `dataCadastroFim` forem informadas, `dataCadastroFim` deve ser posterior a `dataCadastroInicio`. Se essa condição não for atendida → **HTTP 400 Bad Request**.
   - Essa validação só se aplica quando as duas datas são informadas simultaneamente. Se apenas uma for informada (ou nenhuma), a validação de intervalo não se aplica.

2. **Limites de data permitidos**: `dataCadastroInicio` e `dataCadastroFim`, quando informadas, devem estar dentro do intervalo `01-01-1900` a `31-12-2100` (ambos inclusive). Datas fora desse intervalo → **HTTP 400 Bad Request**.

3. **Formato da resposta de erro**: seguir o padrão de resposta de erro já existente no projeto para validações HTTP 400. O texto exato da(s) mensagem(ns) de erro fica a critério do back-end.

#### Edge Cases
- Nenhum filtro informado → comportamento idêntico ao atual (lista completa paginada).
- Apenas `dataCadastroInicio` informada → filtra a partir dessa data, sem limite superior (exceto o limite máximo de `31-12-2100`).
- Apenas `dataCadastroFim` informada → filtra até essa data, sem limite inferior (exceto o limite mínimo de `01-01-1900`).
- `dataCadastroInicio` igual a `dataCadastroFim` → intervalo de um único dia, válido (extremos inclusos), não deve ser tratado como erro de "invertido".
- Filtros de texto vazios (string vazia) — comportamento a critério do back-end, mas recomenda-se tratar como "sem filtro" para consistência com o padrão de filtros opcionais.

## Design Decisions

### Technical Approach
- **Architecture Choice**: Ajuste incremental no endpoint REST existente `GET /usuarios`, adicionando parâmetros de filtro opcionais à camada de controller/service/repository, sem alterar o contrato de paginação.
- **Key Components**: Controller (parâmetros de filtro), camada de serviço (composição das regras de validação de data), camada de persistência (composição da query com AND condicional).
- **Data Storage**: PostgreSQL (conforme stack já utilizada no projeto). Nenhuma alteração de schema é esperada, apenas na query de leitura.
- **Interface Design**: Parâmetros de filtro adicionados como query params do `GET /usuarios` existente, junto aos parâmetros de paginação já em uso.

### Constraints
- **Performance Requirements**: Query deve continuar paginada; filtros `LIKE`/`ILIKE` devem considerar necessidade de índice em `nome`/`email` caso a tabela seja grande (decisão de implementação).
- **Compatibility**: Deve manter compatibilidade retroativa — chamadas sem filtros continuam funcionando como hoje.
- **Security**: Endpoint permanece sem autenticação, conforme decisão de negócio já vigente.
- **Scalability**: N/A para este escopo.

### Risk Assessment
- **Technical Risks**: Case-insensitive matching pode ter impacto de performance dependendo da collation do banco; mitigação fica a critério do back-end (ex: uso de `ILIKE` nativo do PostgreSQL).
- **Dependency Risks**: Depende do padrão de mascaramento de CPF já existente estar corretamente reutilizável na query atual.
- **Schedule Risks**: Baixo risco — ajuste incremental em endpoint já existente.

## Acceptance Criteria

### Functional Acceptance
- [ ] `GET /usuarios` sem nenhum filtro retorna o comportamento atual (paginado, todos os usuários).
- [ ] Filtro `nome` retorna usuários cujo nome contenha o valor informado, de forma case-insensitive.
- [ ] Filtro `email` retorna usuários cujo email contenha o valor informado, de forma case-insensitive.
- [ ] Filtro `isAtivo` retorna apenas usuários com o status exato informado; se omitido, retorna ativos e inativos.
- [ ] Filtro `dataCadastroInicio` isolado retorna usuários com `dataCadastro >= valor`.
- [ ] Filtro `dataCadastroFim` isolado retorna usuários com `dataCadastro <= valor`.
- [ ] Filtros `dataCadastroInicio` e `dataCadastroFim` combinados retornam usuários com `dataCadastro` dentro do intervalo, extremos inclusos.
- [ ] Múltiplos filtros informados juntos combinam-se com AND.
- [ ] `dataCadastroFim` anterior ou igual a `dataCadastroInicio` (quando ambas informadas) retorna HTTP 400.
- [ ] Data de filtro fora do intervalo `01-01-1900` a `31-12-2100` retorna HTTP 400.
- [ ] Resposta contém exatamente os campos: `id, nome, isAtivo, idade, email, login, cpfMascarado, dataCadastro`.
- [ ] `cpfMascarado` segue o padrão de mascaramento já existente no sistema.
- [ ] Estrutura/contrato de paginação permanece inalterado.

### Quality Standards
- [ ] Code Quality: seguir os padrões arquiteturais já utilizados no projeto (camadas controller/service/repository).
- [ ] Test Coverage: testes cobrindo cada filtro isoladamente, combinações de filtros, e os dois cenários de erro 400.
- [ ] Performance Metrics: N/A definido neste PRD (sem meta explícita de SLA).
- [ ] Security Review: N/A (endpoint já público, sem alteração de superfície de exposição de dados sensíveis além do já existente).

### User Acceptance
- [ ] User Experience: consumidores do endpoint conseguem combinar filtros livremente sem impacto na paginação.
- [ ] Documentation: atualizar documentação do endpoint (ex: OpenAPI/Swagger, se existente) com os novos parâmetros.
- [ ] Training Materials: não aplicável.

## Execution Phases

### Phase 1: Preparação
**Goal**: Validar contrato atual e preparar terreno para o ajuste
- [ ] Levantar a implementação atual do endpoint `GET /usuarios` (controller, service, repository/query)
- [ ] Confirmar padrão de mascaramento de CPF já existente e como reutilizá-lo
- **Deliverables**: Mapeamento técnico do código atual
- **Time**: A definir pelo time

### Phase 2: Desenvolvimento Core
**Goal**: Implementar os filtros e validações
- [ ] Adicionar parâmetros de filtro opcionais ao controller (`nome`, `isAtivo`, `email`, `dataCadastroInicio`, `dataCadastroFim`)
- [ ] Implementar composição de query com AND condicional entre filtros informados
- [ ] Implementar matching case-insensitive para `nome` e `email`
- [ ] Implementar validações de data (intervalo invertido e limites 1900–2100) com retorno HTTP 400
- **Deliverables**: Endpoint ajustado com filtros funcionais
- **Time**: A definir pelo time

### Phase 3: Integração e Testes
**Goal**: Garantir qualidade e não regressão
- [ ] Testes unitários/integração para cada filtro isolado e combinações
- [ ] Testes para os dois cenários de erro 400 (intervalo invertido, fora do range)
- [ ] Teste de regressão da paginação (garantir que não foi alterada)
- **Deliverables**: Suíte de testes cobrindo o novo comportamento
- **Time**: A definir pelo time

### Phase 4: Deploy
**Goal**: Liberação e acompanhamento
- [ ] Atualizar documentação do endpoint (Swagger/OpenAPI, se aplicável)
- [ ] Deploy em ambiente de homologação e validação funcional
- [ ] Deploy em produção
- **Deliverables**: Endpoint em produção com filtros disponíveis
- **Time**: A definir pelo time

---

**Document Version**: 1.0
**Created**: 2026-09-21
**Clarification Rounds**: 5
**Quality Score**: 97/100
