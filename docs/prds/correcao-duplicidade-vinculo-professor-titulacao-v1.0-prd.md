# Correção da Verificação de Duplicidade de Vínculo Professor-Titulação (v1.0) - Product Requirements Document (PRD)

> **Tipo:** correção de aderência. O comportamento esperado já está definido em `tabela-tb-professor-titulacao-v1.1-prd.md`, `cadastro-professor-titulacao-v1.0-prd.md`, `atualizacao-professor-titulacao-v1.0-prd.md` e `exclusao-professor-titulacao-v1.0-prd.md`. Este documento descreve onde o código implementado nas ISSUE-42, 44 e 45 se afasta desses PRDs. Não cria regra nova.
>
> **Exceção à regra "um PRD por endpoint":** os três problemas abaixo estão na mesma verificação compartilhada por vincular e atualizar (e na mesma comparação de ids usada por atualizar e remover). Corrigir separadamente obrigaria a mexer duas vezes no mesmo método.

## Requirements Description

### Background
- **Problema de Negócio**: A regra de unicidade do vínculo diz que **professor + titulação + `dataInicio`** não pode se repetir entre vínculos **ativos**. O código atual aplica uma regra mais restritiva e, em alguns casos, impede operações legítimas.
- **Usuários-Alvo**: Estagiário responsável pela correção; revisor (analista).
- **Proposta de Valor**: Fazer vincular, atualizar e remover vínculo se comportarem como os PRDs descrevem.

### Situação identificada

Referências no código atual: `ProfessorTitulacaoRepository.quantVinculos`, `ProfessorService.verificarVinculosProfTitu`, `ProfessorService.criaRelacionamentoProfTitu`, `ProfessorService.atualizarDatas` e `ProfessorService.inativarVinculo`.

#### Problema 1 — A duplicidade ignora a titulação
- **O que acontece hoje**: a query `quantVinculos` conta os vínculos ativos com o mesmo **professor** e a mesma **`dataInicio`**. A titulação não participa da consulta (o método nem recebe o `titulacaoId`).
- **Exemplo**: o professor 10 já tem o vínculo ativo "Mestrado em Física", iniciado em 2020-03-01. Ao vincular "Especialização em Ensino" com `dataInicio` 2020-03-01, a API responde 409 "Vínculo já existente".
- **Por que é um problema**: são titulações diferentes, então não é duplicidade. O PRD define a chave como professor + **titulação** + `dataInicio`. Na prática, um professor não consegue registrar duas titulações que começaram no mesmo dia.
- **Solução proposta**: a consulta de duplicidade passa a filtrar também pela titulação. O vincular usa o `titulacaoId` do payload. O atualizar usa a titulação do próprio vínculo que está sendo editado (a titulação não muda na atualização).

#### Problema 2 — Atualizar acusa duplicidade com o próprio vínculo
- **O que acontece hoje**: `atualizarDatas` chama a mesma verificação, sem excluir o vínculo que está sendo editado. O próprio vínculo (ativo, com a mesma `dataInicio`) é contado como duplicado.
- **Exemplo**: o vínculo 5 tem `dataInicio` 2020-03-01 e não tem `dataTermino`. O front-end envia PUT com `dataInicio` 2020-03-01 e `dataTermino` 2024-12-15 (caso típico: informar a conclusão). A API responde 409, e o vínculo nunca consegue receber a `dataTermino`.
- **Por que é um problema**: contraria o critério do PRD "atualizar sem mudar `dataInicio` não acusa duplicidade com o próprio vínculo" e inutiliza o principal caso de uso do endpoint.
- **Solução proposta**: a verificação de duplicidade na atualização desconsidera o registro de `vinculoId` (por exemplo, "mesmo professor + mesma titulação + mesma `dataInicio` + ativo + `id` diferente do vínculo editado"). Além disso, conforme o PRD de atualização, a verificação só vale quando o **vínculo editado está ativo**; vínculo inativo pode ser atualizado sem checar duplicidade.

#### Problema 3 — Comparação de `Long` com `==`
- **O que acontece hoje**: em `atualizarDatas` e `inativarVinculo`, o código compara `vinculo.getProfessor().getId() == id`, ou seja, dois objetos `Long` (não o tipo primitivo `long`).
- **Por que é um problema**: em Java, `==` entre objetos `Long` compara **referências**. Isso só dá `true` por acaso para valores de -128 a 127, porque a JVM reaproveita esses objetos. Para ids maiores (ex.: professor de id 200), a comparação dá `false` mesmo com ids iguais. Resultado: atualizar e remover vínculo rejeitam vínculos legítimos assim que o banco passa de 127 professores. Em desenvolvimento, com poucos dados, o erro não aparece.
- **Solução proposta**: comparar com `.equals` (ou `Objects.equals`), em todos os pontos em que ids `Long` são comparados.

### Feature Overview
- **Funcionalidades Principais**: corrigir a verificação de duplicidade (vincular e atualizar) e a comparação de ids (atualizar e remover).
- **Limites da Feature**:
  - Não altera contratos de rota, payloads ou respostas
  - Não altera mensagens e status de erro (ver `ajuste-erros-vinculo-professor-titulacao-v1.0-prd.md`)
  - Não inclui a remoção da constraint do banco (ver `remocao-constraint-unicidade-professor-titulacao-v1.0-prd.md`)

### Detailed Requirements

#### Regra de duplicidade (valor final esperado)
| Operação | Há duplicidade quando existe outro vínculo que seja... |
|---|---|
| Vincular | do mesmo professor, da mesma titulação, com a mesma `dataInicio` e **ativo** |
| Atualizar (vínculo ativo) | do mesmo professor, da mesma titulação do vínculo editado, com a nova `dataInicio`, **ativo** e **com `id` diferente** do vínculo editado |
| Atualizar (vínculo inativo) | Não há verificação de duplicidade |

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Mesmo professor, titulações diferentes, mesma `dataInicio` | Permitido |
| Mesmo professor, mesma titulação, `dataInicio` diferente | Permitido |
| Mesmo professor, mesma titulação, mesma `dataInicio`, vínculo existente **ativo** | Rejeitado (409) |
| Mesma combinação, vínculo existente **inativo** | Permitido |
| Atualizar mantendo a `dataInicio` e mudando só a `dataTermino` | Sucesso |
| Atualizar para uma `dataInicio` já usada por **outro** vínculo ativo da mesma titulação | Rejeitado (409) |
| Atualizar ou remover vínculo em professor com id maior que 127 | Funciona normalmente |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: ajuste localizado no repository e no service existentes; sem novas classes.
- **Componentes Principais**: query de contagem/existência de vínculos ativos (recebe professor, titulação, `dataInicio` e, opcionalmente, o id a ser ignorado); `ProfessorService` (vincular, atualizar, remover).
- **Armazenamento de Dados**: `tb_professor_titulacoes` (sem alteração de schema).
- **Design de Interface**: sem mudança.

### Constraints
- **Compatibilidade**: depende das ISSUE-41, 42, 44 e 45 já entregues.
- **Segurança**: não aplicável.

### Risk Assessment
- **Riscos Técnicos**: ao excluir o próprio vínculo da checagem, cuidar para não "abrir" a duplicidade com outros vínculos. O teste de "atualizar para `dataInicio` de outro vínculo ativo" cobre isso.
- **Riscos de Dependência**: nenhum.

## Acceptance Criteria

### Functional Acceptance
- [ ] Vincular duas titulações diferentes ao mesmo professor com a mesma `dataInicio` funciona
- [ ] Vincular a mesma titulação, com a mesma `dataInicio`, a um professor que já tem esse vínculo **ativo** retorna 409
- [ ] Vincular a mesma combinação quando o vínculo anterior está **inativo** funciona
- [ ] Atualizar um vínculo ativo mantendo a `dataInicio` e informando a `dataTermino` retorna 200 com o vínculo atualizado
- [ ] Atualizar para uma `dataInicio` que coincide com **outro** vínculo ativo da mesma titulação do mesmo professor retorna 409
- [ ] Atualizar um vínculo inativo não faz verificação de duplicidade e mantém `ativo = false`
- [ ] Atualizar e remover vínculo funcionam para professores e vínculos com id maior que 127 (testar com ids acima de 127)
- [ ] Nenhum `==` entre objetos `Long` permanece no fluxo de vínculos

---

**Document Version**: 1.0
**Created**: 2026-10-27
**Clarification Rounds**: 0 (correção de aderência a PRDs já aprovados; sem regra nova)
**Quality Score**: n/a
