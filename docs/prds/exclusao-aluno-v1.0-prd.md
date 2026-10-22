# Exclusão Lógica de Aluno - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita desativar um aluno cadastrado no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por gerenciar o ciclo de vida dos alunos.
- **Proposta de Valor**: Permitir a desativação de um aluno, preservando o histórico do registro no banco, seguindo o mesmo padrão já definido para curso, professor e disciplina.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de exclusão lógica de aluno, a partir do `id` — marca o usuário vinculado como inativo (`ativo = false` em `usuarios`).
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Exclusão é lógica — nenhum registro é removido de `usuarios` ou `tb_alunos`
  - Operação é idempotente: repetir a exclusão em um aluno já inativo não gera erro
  - Não há, até o momento, validação de vínculo com matrículas, pois essa entidade ainda não existe no sistema — se for criada futuramente, essa restrição deverá ser revista
- **Cenários de Uso Típicos**:
  - Front-end solicita a exclusão (lógica) de um aluno existente e recebe confirmação (204)
  - Front-end tenta excluir um aluno com `id` inexistente e recebe erro 404
  - Front-end tenta excluir um aluno já inativo e recebe 204 normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do aluno a ser excluído (via path)

**Resposta de sucesso:**
- Status 204 (sem corpo)

#### Data Requirements
- A exclusão é lógica — o campo `ativo` em `usuarios` (vinculado via `usuario_id`) é definido como `false`
- `dataAtualizacao` (de `usuarios`) deve ser atualizado automaticamente no momento da desativação
- `dataCriacao` permanece inalterado
- Nenhum registro é removido de `usuarios` ou `tb_alunos`

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum aluno existente | Erro 404 com mensagem adequada |
| `id` existente e aluno ativo | Usuário vinculado marcado como `ativo = false`; resposta 204 sem corpo |
| Aluno já está inativo (`ativo = false`) | Operação idempotente — retorna 204 normalmente, sem erro |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de exclusão lógica, atualização do campo `ativo` em `usuarios` a partir do `id` de `tb_alunos`.
- **Armazenamento de Dados**: Tabelas `usuarios` e `tb_alunos`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (DELETE) recebendo o `id` do aluno via path, respondendo com status 204 em caso de sucesso.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — exclusão lógica é reversível (pode ser revertida reativando o usuário, se um endpoint de reativação existir futuramente).
- **Riscos de Dependência**: Depende das tabelas `usuarios` e `tb_alunos` já existirem.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint marca o usuário vinculado ao aluno como `ativo = false` (exclusão lógica) a partir do `id` do aluno
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Exclusão bem-sucedida retorna status 204, sem corpo
- [ ] Excluir um aluno já inativo também retorna 204, sem erro (idempotente)
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da desativação
- [ ] Nenhum registro é removido de `usuarios` ou `tb_alunos`

---

**Document Version**: 1.0
**Created**: 2026-10-16
**Clarification Rounds**: 1
**Quality Score**: 94/100
