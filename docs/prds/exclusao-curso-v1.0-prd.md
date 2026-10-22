# Exclusão Lógica de Curso - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita remover um curso cadastrado no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por gerenciar o ciclo de vida dos cursos.
- **Proposta de Valor**: Permitir a desativação de um curso, preservando o histórico do registro no banco.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de exclusão lógica de curso, a partir do `id` — marca o curso como inativo (`ativo = false`) em vez de removê-lo do banco.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Exclusão é lógica (marca `ativo = false`), não física — o registro permanece na tabela
  - Não há, até o momento, validação de vínculo com outras entidades (ex: turmas, disciplinas), pois essas entidades ainda não existem no sistema — se forem criadas futuramente, essa restrição deverá ser revista
- **Cenários de Uso Típicos**:
  - Front-end solicita a exclusão (lógica) de um curso existente e recebe confirmação (204)
  - Front-end tenta excluir um curso com `id` inexistente e recebe erro 404

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do curso a ser excluído (via path)

**Resposta de sucesso:**
- Status 204 (sem corpo)

#### Data Requirements
- A exclusão é lógica — o curso é marcado com `ativo = false`, permanecendo na tabela `tb_cursos`
- `dataAtualizacao` deve ser atualizado automaticamente no momento da desativação
- `dataCriacao` permanece inalterado

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum curso existente | Erro 404 com mensagem adequada |
| `id` existente | Curso marcado como `ativo = false`; resposta 204 sem corpo |
| Curso já está inativo (`ativo = false`) | A confirmar: operação idempotente (retorna 204 normalmente) ou erro |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de exclusão lógica, atualização do campo `ativo` na tabela `tb_cursos`.
- **Armazenamento de Dados**: Tabela `tb_cursos` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP (DELETE) recebendo o `id` via path, respondendo com status 204 em caso de sucesso.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — exclusão lógica é reversível (pode ser revertida reativando o curso, se um endpoint de reativação existir futuramente).
- **Riscos de Dependência**: Depende da tabela `tb_cursos` já existir. Nenhuma dependência de outras entidades no momento.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint marca o curso como `ativo = false` (exclusão lógica) a partir do `id`, sem remover o registro do banco
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Exclusão bem-sucedida retorna status 204, sem corpo
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da desativação

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 1
**Quality Score**: 92/100
