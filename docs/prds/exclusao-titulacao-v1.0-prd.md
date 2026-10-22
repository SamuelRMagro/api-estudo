# Exclusão Lógica de Titulação - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita desativar uma titulação do catálogo.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por gerenciar o catálogo de titulações.
- **Proposta de Valor**: Permitir a desativação de uma titulação, preservando o registro e os vínculos já existentes com professores, seguindo o mesmo padrão de curso, professor, aluno e disciplina.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de exclusão lógica de titulação, a partir do `id` — marca a titulação como inativa (`ativo = false`) em vez de removê-la do banco.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Exclusão é lógica (marca `ativo = false`), não física — o registro permanece na tabela
  - Operação é idempotente: repetir a exclusão em uma titulação já inativa não gera erro
  - Os vínculos existentes em `tb_professor_titulacao` não são alterados nem removidos
- **Cenários de Uso Típicos**:
  - Front-end solicita a exclusão (lógica) de uma titulação existente e recebe confirmação (204)
  - Front-end tenta excluir uma titulação com `id` inexistente e recebe erro 404
  - Front-end tenta excluir uma titulação já inativa e recebe 204 normalmente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` da titulação a ser excluída (via path)

**Resposta de sucesso:**
- Status 204 (sem corpo)

#### Data Requirements
- A exclusão é lógica — a titulação é marcada com `ativo = false`, permanecendo na tabela `tb_titulacoes`
- Nenhum registro é removido de `tb_titulacoes` nem de `tb_professor_titulacao`
- Professores já vinculados à titulação mantêm o vínculo e o histórico

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhuma titulação existente | Erro 404 com mensagem adequada |
| `id` existente e titulação ativa | Titulação marcada como `ativo = false`; resposta 204 sem corpo |
| Titulação já está inativa (`ativo = false`) | Operação idempotente — retorna 204 normalmente, sem erro |
| Titulação com professores vinculados | Exclusão permitida; vínculos preservados |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de exclusão lógica, atualização do campo `ativo` na tabela `tb_titulacoes`.
- **Armazenamento de Dados**: Tabela `tb_titulacoes` (PRD v1.1).
- **Design de Interface**: Endpoint HTTP (DELETE) recebendo o `id` via path, respondendo com status 204 em caso de sucesso.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — exclusão lógica é reversível. Como o vínculo com professores é preservado, o endpoint de vínculo precisará definir se aceita ou não titulações inativas em novos vínculos.
- **Riscos de Dependência**: Depende da tabela `tb_titulacoes` (v1.1) já existir.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint marca a titulação como `ativo = false` (exclusão lógica) a partir do `id`, sem remover o registro do banco
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Exclusão bem-sucedida retorna status 204, sem corpo
- [ ] Excluir uma titulação já inativa também retorna 204, sem erro (idempotente)
- [ ] Vínculos existentes em `tb_professor_titulacao` permanecem inalterados

---

**Document Version**: 1.0
**Created**: 2026-10-19
**Clarification Rounds**: 1
**Quality Score**: 92/100
