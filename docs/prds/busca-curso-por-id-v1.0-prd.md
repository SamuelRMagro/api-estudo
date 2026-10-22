# Busca de Curso por ID - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que retorne um único curso a partir do seu identificador.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam exibir os detalhes de um curso específico.
- **Proposta de Valor**: Fornecer acesso direto e previsível aos dados completos de um curso, dado seu `id`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de busca de curso por `id`, retornando todos os campos do registro.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não inclui edição ou remoção — apenas consulta
- **Cenários de Uso Típicos**:
  - Front-end solicita os detalhes de um curso específico a partir do `id`
  - Front-end tenta buscar um `id` que não existe e recebe um erro claro
  - Front-end busca um curso inativo e recebe os dados normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do curso (via path)

**Campos de saída:**
- Todos os campos do curso: id, nome, cargaHoraria, duracao, ativo, dataCriacao, dataAtualizacao

#### Data Requirements
- Curso inativo (`ativo = false`) é retornado normalmente, sem tratamento especial
- Nenhum campo é omitido na resposta

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum curso existente | Erro 404 com mensagem adequada |
| Curso encontrado, mas com `ativo = false` | Retornado normalmente, sem tratamento especial |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de busca por `id` sobre a tabela `tb_cursos`.
- **Armazenamento de Dados**: Tabela `tb_cursos` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP (GET) recebendo o `id` via path, respondendo com o objeto do curso em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável — consulta simples por chave primária.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação simples de busca por chave primária.
- **Riscos de Dependência**: Depende da tabela `tb_cursos` já existir.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna todos os campos do curso ao buscar por `id`
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Curso inativo é retornado normalmente, sem tratamento especial

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 1
**Quality Score**: 95/100
