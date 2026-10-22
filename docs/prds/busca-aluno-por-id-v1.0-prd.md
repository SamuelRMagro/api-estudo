# Busca de Aluno por ID - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que retorne um único aluno a partir do seu identificador.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam exibir os detalhes de um aluno específico.
- **Proposta de Valor**: Fornecer acesso direto e previsível aos dados completos de um aluno, dado seu `id`, combinando dados de `tb_alunos` e `usuarios`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de busca de aluno por `id`, retornando todos os campos do registro (combinando `tb_alunos` e `usuarios`).
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não inclui edição ou remoção — apenas consulta
  - Não inclui matrículas do aluno — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end solicita os detalhes de um aluno específico a partir do `id`
  - Front-end tenta buscar um `id` que não existe e recebe um erro claro
  - Front-end busca um aluno inativo e recebe os dados normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do aluno (via path)

**Campos de saída:**
| Campo | Origem |
|---|---|
| `id` | `tb_alunos` |
| `usuarioId` | `tb_alunos` |
| `nome` | `usuarios` |
| `email` | `usuarios` |
| `cpf` | `usuarios` (mascarado) |
| `ativo` | `usuarios` |
| `telefone` | `tb_alunos` |
| `dataNascimento` | `tb_alunos` |
| `dataCriacao` | `usuarios` |
| `dataAtualizacao` | `usuarios` |

#### Data Requirements
- A busca deve realizar o relacionamento (join) entre `tb_alunos` e `usuarios` através de `usuario_id`
- Aluno inativo (`ativo = false` em `usuarios`) é retornado normalmente, sem tratamento especial
- `telefone` é opcional — quando não informado no cadastro, retorna vazio/nulo
- Nenhum campo é omitido na resposta; `senha` nunca é retornada

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum aluno existente | Erro 404 com mensagem adequada |
| Aluno encontrado, mas com `ativo = false` | Retornado normalmente, sem tratamento especial |
| Aluno sem `telefone` cadastrado | Retornado normalmente, com `telefone` vazio/nulo |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de busca por `id`, com join entre `tb_alunos` e `usuarios`.
- **Armazenamento de Dados**: Tabelas `tb_alunos` e `usuarios`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (GET) recebendo o `id` via path, respondendo com o objeto do aluno em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: CPF mascarado, conforme já definido para `usuarios`. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável — consulta simples por chave primária com join.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação simples de busca por chave primária.
- **Riscos de Dependência**: Depende das tabelas `tb_alunos` (estrutura v2.0) e `usuarios` já existirem.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna todos os campos do aluno ao buscar por `id`, combinando dados de `tb_alunos` e `usuarios`
- [ ] `cpf` é retornado mascarado
- [ ] `senha` nunca é retornada
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Aluno inativo é retornado normalmente, sem tratamento especial

---

**Document Version**: 1.0
**Created**: 2026-10-16
**Clarification Rounds**: 1
**Quality Score**: 96/100
