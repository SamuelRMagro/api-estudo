# Busca de Professor por ID - Product Requirements Document (PRD)

> **SUBSTITUÍDO** por [`busca-professor-por-id-v1.1-prd.md`](busca-professor-por-id-v1.1-prd.md) (resposta passa a incluir os vínculos de titulação do professor).

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que retorne um único professor a partir do seu identificador.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam exibir os detalhes de um professor específico.
- **Proposta de Valor**: Fornecer acesso direto e previsível aos dados completos de um professor, dado seu `id`, combinando dados de `tb_professores` e `usuarios`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de busca de professor por `id`, retornando todos os campos do registro (combinando `tb_professores` e `usuarios`).
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não inclui edição ou remoção — apenas consulta
  - Não inclui as titulações do professor — tratado em endpoint/recurso separado
- **Cenários de Uso Típicos**:
  - Front-end solicita os detalhes de um professor específico a partir do `id`
  - Front-end tenta buscar um `id` que não existe e recebe um erro claro
  - Front-end busca um professor inativo e recebe os dados normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do professor (via path)

**Campos de saída:**
| Campo | Origem |
|---|---|
| `id` | `tb_professores` |
| `usuarioId` | `tb_professores` |
| `nome` | `usuarios` |
| `email` | `usuarios` |
| `cpf` | `usuarios` (mascarado) |
| `ativo` | `usuarios` |
| `dataAdmissao` | `tb_professores` |
| `dataCriacao` | `usuarios` |
| `dataAtualizacao` | `usuarios` |

#### Data Requirements
- A busca deve realizar o relacionamento (join) entre `tb_professores` e `usuarios` através de `usuario_id`
- Professor inativo (`ativo = false` em `usuarios`) é retornado normalmente, sem tratamento especial
- Nenhum campo é omitido na resposta

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum professor existente | Erro 404 com mensagem adequada |
| Professor encontrado, mas com `ativo = false` | Retornado normalmente, sem tratamento especial |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de busca por `id`, com join entre `tb_professores` e `usuarios`.
- **Armazenamento de Dados**: Tabelas `tb_professores` e `usuarios`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (GET) recebendo o `id` via path, respondendo com o objeto do professor em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: CPF mascarado, conforme já definido para `usuarios`. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável — consulta simples por chave primária com join.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação simples de busca por chave primária.
- **Riscos de Dependência**: Depende das tabelas `tb_professores` e `usuarios` já existirem.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna todos os campos do professor ao buscar por `id`, combinando dados de `tb_professores` e `usuarios`
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Professor inativo é retornado normalmente, sem tratamento especial

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 1
**Quality Score**: 96/100
