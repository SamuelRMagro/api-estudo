# Ajuste de Erros e Regras de Status do Vínculo Professor-Titulação (v1.0) - Product Requirements Document (PRD)

> **Tipo:** correção de aderência. O comportamento esperado já está nos PRDs `cadastro-professor-titulacao-v1.0`, `atualizacao-professor-titulacao-v1.0` e `exclusao-professor-titulacao-v1.0`. Este documento descreve onde o código das ISSUE-42, 44 e 45 se afasta deles. Não cria regra nova.
>
> **Exceção à regra "um PRD por endpoint":** os desvios estão em três endpoints, mas todos na mesma camada (`ProfessorService`) e tratam do mesmo assunto (que erro devolver e quando). Manter junto evita decisões inconsistentes entre os endpoints.

## Requirements Description

### Background
- **Problema de Negócio**: o front-end depende do status e da mensagem de erro para saber o que corrigir. Hoje alguns erros de vínculo voltam com status diferente do PRD, e uma restrição foi aplicada onde o PRD a dispensa.
- **Usuários-Alvo**: Estagiário responsável pela correção; consumidores da API.
- **Proposta de Valor**: erros previsíveis e coerentes com os PRDs.

### Situação identificada

Referências no código atual: `ProfessorService.criaRelacionamentoProfTitu`, `atualizarDatas`, `inativarVinculo`, `buscarUsuarioAtivoOuFalhar`, `buscarTitulacaoAtivaOuFalhar`, e `GlobalExceptionHandler`.

#### Problema 1 — Vínculo de outro professor retorna 409 em vez de 404
- **O que acontece hoje**: nos endpoints de atualizar e remover, se o `vinculoId` existe mas pertence a outro professor, o service lança `DadosInconsistentesCustomException`, que o `GlobalExceptionHandler` converte em **409**.
- **Por que é um problema**: os PRDs de atualização e de remoção definem **404** para esse caso (cenário "`vinculoId` existe, mas pertence a outro professor"). Do ponto de vista da rota `/professores/{id}/titulacoes/{vinculoId}`, esse vínculo não existe para aquele professor. Devolver 404 também evita revelar a existência de vínculos de outros professores.
- **Solução proposta**: nesse caso, lançar `RegistroNaoEncontradoCustomException` (que já resulta em 404), com mensagem adequada, por exemplo informando que o vínculo não foi encontrado para o professor da rota. Se a `DadosInconsistentesCustomException` deixar de ser usada, avaliar sua remoção; se for usada por outro fluxo, mantê-la.

#### Problema 2 — Professor ou titulação inativos no vincular retornam 404
- **O que acontece hoje**: no vincular, `buscarUsuarioAtivoOuFalhar` e `buscarTitulacaoAtivaOuFalhar` lançam `RegistroNaoEncontradoCustomException` (404) quando o professor ou a titulação **existem, mas estão inativos** (a mensagem diz "não encontrado ou inativo").
- **Por que é um problema**: o PRD de vínculo separa os dois casos: id **inexistente** → 404; registro **inativo** → **erro de validação**. Tratar o inativo como 404 engana o cliente, porque o registro existe e é retornado normalmente nas buscas por id (convenção do projeto: "registro inativo é retornado normalmente").
- **Solução proposta**: no vincular, buscar o professor e a titulação por id (inexistente → 404) e, em seguida, validar o status (inativo → erro de validação). Para o "erro de validação", usar o mesmo mecanismo que o projeto já usa para regra de negócio violada (`CadastroInvalidoCustomException`, hoje 409), com mensagem que diga qual registro está inativo (professor ou titulação).
  - **Ponto a confirmar pelo analista:** o status HTTP do "erro de validação" por registro inativo (hoje as regras de negócio do projeto usam 409; erros de formato de payload usam 400). Este documento assume 409, por coerência com a duplicidade de vínculo.

#### Problema 3 — Atualizar exige professor e titulação ativos
- **O que acontece hoje**: `atualizarDatas` chama `buscarUsuarioAtivoOuFalhar` e `buscarTitulacaoAtivaOuFalhar`. Se o professor ou a titulação do vínculo estiverem inativos, a atualização é recusada com 404.
- **Por que é um problema**: o PRD de atualização diz expressamente "Atualizar vínculo de professor inativo ou de titulação inativa é permitido — a restrição de status vale só para **novos** vínculos". Um professor que se aposentou (inativo) não consegue ter a `dataTermino` de uma titulação corrigida, e vínculos de titulações descontinuadas ficam congelados.
- **Solução proposta**: na atualização, apenas verificar a **existência** do professor (inexistente → 404) e do vínculo. Remover a exigência de status ativo do professor e da titulação.

### Feature Overview
- **Funcionalidades Principais**: ajustar status e mensagens de erro e a regra de status em vincular, atualizar e remover.
- **Limites da Feature**:
  - Não altera contratos de rota, payload ou resposta de sucesso
  - Não altera a regra de duplicidade (ver `correcao-duplicidade-vinculo-professor-titulacao-v1.0-prd.md`)
  - Remover vínculo continua idempotente (204 para vínculo já inativo)

### Detailed Requirements

#### Matriz de erros (valor final esperado)
| Endpoint | Cenário | Resposta esperada |
|---|---|---|
| Vincular | Professor inexistente | 404 |
| Vincular | Titulação inexistente | 404 |
| Vincular | Professor **inativo** | Erro de validação (mensagem cita o professor) |
| Vincular | Titulação **inativa** | Erro de validação (mensagem cita a titulação) |
| Atualizar | Professor inexistente | 404 |
| Atualizar | `vinculoId` inexistente | 404 |
| Atualizar | `vinculoId` de outro professor | 404 |
| Atualizar | Professor inativo ou titulação inativa | Atualização permitida (200) |
| Remover | Professor inexistente | 404 |
| Remover | `vinculoId` inexistente | 404 |
| Remover | `vinculoId` de outro professor | 404 |
| Remover | Vínculo já inativo | 204 (idempotente) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: ajuste no `ProfessorService` e reuso das exceções e do `GlobalExceptionHandler` existentes; sem novas classes, salvo se a mensagem exigir.
- **Componentes Principais**: `ProfessorService` (métodos de vincular, atualizar e remover); métodos auxiliares de busca (um para existência, outro para validar status).
- **Armazenamento de Dados**: sem alteração de schema.
- **Design de Interface**: apenas status e mensagens de erro mudam.

### Constraints
- **Compatibilidade**: depende das ISSUE-42, 44 e 45.
- **Segurança**: não aplicável.

### Risk Assessment
- **Riscos Técnicos**: `buscarUsuarioAtivoOuFalhar` também é usado por `atualizarProfessor`. Ao ajustar o fluxo de vínculos, **não alterar** o comportamento dessa atualização de professor.
- **Riscos de Dependência**: nenhum.

## Acceptance Criteria

### Functional Acceptance
- [ ] Vincular com professor inexistente e com titulação inexistente retornam 404
- [ ] Vincular com professor inativo retorna erro de validação com mensagem que identifica o professor
- [ ] Vincular com titulação inativa retorna erro de validação com mensagem que identifica a titulação
- [ ] Atualizar e remover com `vinculoId` de outro professor retornam 404 (não 409)
- [ ] Atualizar o vínculo de um professor inativo retorna 200
- [ ] Atualizar o vínculo de uma titulação inativa retorna 200
- [ ] Remover vínculo já inativo continua retornando 204
- [ ] O fluxo de atualização de professor (`PUT /professores/{id}`) continua rejeitando professor inativo, como antes

---

**Document Version**: 1.0
**Created**: 2026-10-27
**Clarification Rounds**: 0 (correção de aderência a PRDs já aprovados; sem regra nova)
**Quality Score**: n/a
