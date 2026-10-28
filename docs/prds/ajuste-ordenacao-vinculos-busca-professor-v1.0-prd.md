# Ajuste da Ordenação dos Vínculos na Busca de Professor por Id (v1.0) - Product Requirements Document (PRD)

> **Tipo:** correção de aderência a `busca-professor-por-id-v1.1-prd.md` (ISSUE-43). Não cria regra nova.

## Requirements Description

### Background
- **Problema de Negócio**: a busca de professor por id devolve a lista `titulacoes` com os vínculos do professor. O PRD v1.1 define que essa lista vem ordenada por `dataInicio` **descendente** (mais recente primeiro).
- **Usuários-Alvo**: Estagiário responsável pela correção; consumidores da API.
- **Proposta de Valor**: ordem previsível dos vínculos, sem depender da ordem física do banco.

### Situação identificada
- **O que acontece hoje**: a query nativa `TitulacaoRepository.buscaTitulacoesProfessorId` não tem cláusula de ordenação.
- **Por que é um problema**: sem `ORDER BY`, o PostgreSQL não garante nenhuma ordem. Costuma devolver pela ordem de inserção, e isso muda depois de atualizações no registro (`UPDATE` reposiciona linhas) ou conforme o plano da consulta. Ao atualizar as datas de um vínculo, a ordem dos itens na resposta pode mudar sem motivo aparente. O critério do PRD ("lista ordenada por `dataInicio` descendente") não é atendido de forma confiável.
- **Solução proposta**: adicionar à query a ordenação por `data_inicio` descendente. Para desempate estável entre vínculos com a mesma `dataInicio`, ordenar também pelo id do vínculo descendente (o mais novo primeiro). Esse desempate é uma escolha técnica deste documento, não do PRD de origem.

### Feature Overview
- **Funcionalidades Principais**: ordenar a lista `titulacoes` da busca de professor.
- **Limites da Feature**:
  - Não altera os campos da resposta nem a rota
  - Vínculos inativos continuam aparecendo (com `ativo = false`), na mesma ordenação
  - Professor sem vínculos continua retornando lista vazia

### Detailed Requirements

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Vínculos com `dataInicio` diferentes | Do mais recente para o mais antigo |
| Vínculos com a mesma `dataInicio` (titulações diferentes) | Mais novo (maior id) primeiro |
| Vínculo inativo | Aparece na posição que sua `dataInicio` determina |
| Professor sem vínculos | Lista vazia |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: ajuste de uma linha na query existente do repository.
- **Componentes Principais**: `TitulacaoRepository.buscaTitulacoesProfessorId`.
- **Armazenamento de Dados**: sem alteração de schema.

### Constraints
- **Compatibilidade**: depende da ISSUE-43.
- **Segurança**: não aplicável.

### Risk Assessment
- **Riscos Técnicos**: baixo. O teste deve criar vínculos em ordem **diferente** da cronológica, para provar que a ordenação vem da query e não da ordem de inserção.

## Acceptance Criteria

### Functional Acceptance
- [ ] Com vínculos criados fora de ordem cronológica, a resposta traz `titulacoes` ordenada por `dataInicio` descendente
- [ ] Após atualizar as datas de um vínculo, a ordem da lista respeita as novas datas
- [ ] Vínculos com a mesma `dataInicio` aparecem com o de maior id primeiro
- [ ] Vínculos inativos aparecem na lista com `ativo = false`
- [ ] Professor sem vínculos retorna `titulacoes` vazia

---

**Document Version**: 1.0
**Created**: 2026-10-27
**Clarification Rounds**: 0 (correção de aderência a PRD já aprovado; sem regra nova)
**Quality Score**: n/a
