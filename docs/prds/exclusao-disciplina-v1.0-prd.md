# Exclusão Lógica de Disciplina - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita desativar uma disciplina cadastrada no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por gerenciar o ciclo de vida das disciplinas.
- **Proposta de Valor**: Permitir a desativação de uma disciplina, preservando o histórico do registro no banco, seguindo o mesmo padrão já definido para curso e professor.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de exclusão lógica de disciplina, a partir do `id` — marca a disciplina como inativa (`ativo = false`) em vez de removê-la do banco.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Exclusão é lógica (marca `ativo = false`), não física — o registro permanece na tabela
  - Operação é idempotente: repetir a exclusão em uma disciplina já inativa não gera erro
  - Não há, até o momento, validação de vínculo com outras entidades (ex: grade curricular, turmas), pois essas entidades ainda não existem no sistema — se forem criadas futuramente, essa restrição deverá ser revista
- **Cenários de Uso Típicos**:
  - Front-end solicita a exclusão (lógica) de uma disciplina existente e recebe confirmação (204)
  - Front-end tenta excluir uma disciplina com `id` inexistente e recebe erro 404
  - Front-end tenta excluir uma disciplina já inativa e recebe 204 normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` da disciplina a ser excluída (via path)

**Resposta de sucesso:**
- Status 204 (sem corpo)

#### Data Requirements
- A exclusão é lógica — a disciplina é marcada com `ativo = false`, permanecendo na tabela `tb_disciplinas`
- `dataAtualizacao` deve ser atualizado automaticamente no momento da desativação
- `dataCriacao` permanece inalterado

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhuma disciplina existente | Erro 404 com mensagem adequada |
| `id` existente e disciplina ativa | Disciplina marcada como `ativo = false`; resposta 204 sem corpo |
| Disciplina já está inativa (`ativo = false`) | Operação idempotente — retorna 204 normalmente, sem erro |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de exclusão lógica, atualização do campo `ativo` na tabela `tb_disciplinas`.
- **Armazenamento de Dados**: Tabela `tb_disciplinas` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP (DELETE) recebendo o `id` via path, respondendo com status 204 em caso de sucesso.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — exclusão lógica é reversível (pode ser revertida reativando a disciplina, se um endpoint de reativação existir futuramente).
- **Riscos de Dependência**: Depende da tabela `tb_disciplinas` já existir. Nenhuma dependência de outras entidades no momento.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint marca a disciplina como `ativo = false` (exclusão lógica) a partir do `id`, sem remover o registro do banco
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Exclusão bem-sucedida retorna status 204, sem corpo
- [ ] Excluir uma disciplina já inativa também retorna 204, sem erro (idempotente)
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da desativação

---

**Document Version**: 1.0
**Created**: 2026-10-13
**Clarification Rounds**: 1
**Quality Score**: 92/100
