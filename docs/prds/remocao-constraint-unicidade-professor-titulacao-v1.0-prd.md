# Remoção da Constraint de Unicidade do Banco — Professor-Titulação (v1.0) - Product Requirements Document (PRD)

> **Tipo:** passo manual de banco de dados, exigido por `tabela-tb-professor-titulacao-v1.1-prd.md` (ISSUE-41). Não envolve alteração de código.

## Requirements Description

### Background
- **Problema de Negócio**: na v1.0 da tabela, a combinação `professor_id` + `titulacao_id` + `data_inicio` era única no banco. Na v1.1 a unicidade vale **só entre vínculos ativos** e passou a ser validada pela aplicação, para que um vínculo removido (inativo) não impeça recriar a mesma combinação.
- **Usuários-Alvo**: Estagiário (executa), analista (confere).
- **Proposta de Valor**: o banco deixa de bloquear a recriação de vínculos após a remoção lógica.

### Situação identificada
- **O que acontece hoje**: a entidade `ProfessorTitulacao` já não declara a constraint (removida na ISSUE-41). Porém o projeto usa `spring.jpa.hibernate.ddl-auto=update`, que **só cria e altera, e nunca remove** constraints. Em qualquer banco criado antes da ISSUE-41 (inclusive o de desenvolvimento do estagiário), a constraint antiga continua existindo.
- **Por que é um problema**: com a constraint ainda ativa, o fluxo "remover vínculo → vincular de novo a mesma combinação" falha no banco com violação de unicidade (erro 500), mesmo que a aplicação o permita. Isso contraria o critério do PRD: "a mesma combinação professor + titulação + `dataInicio` pode ser recriada quando o vínculo anterior está inativo".
- **Solução proposta**: localizar e remover a constraint manualmente em cada banco existente. Bancos criados do zero depois da ISSUE-41 não têm a constraint e não precisam do passo.

### Feature Overview
- **Funcionalidades Principais**: remover a constraint de unicidade de `tb_professor_titulacoes` nos bancos existentes e validar.
- **Limites da Feature**:
  - Não altera dados nem outras tabelas
  - Não cria migração automatizada (o projeto não usa Flyway nem Liquibase)

### Detailed Requirements

#### Passo a passo
1. **Localizar** o nome da constraint (o Hibernate gera um nome aleatório, por isso é preciso consultar):
   ```sql
   SELECT conname, pg_get_constraintdef(oid)
   FROM pg_constraint
   WHERE conrelid = 'tb_professor_titulacoes'::regclass
     AND contype = 'u';
   ```
   O resultado esperado é uma linha cuja definição é `UNIQUE (professor_id, titulacao_id, data_inicio)`. Se não houver linhas, a constraint já foi removida: pular para o passo 3.
2. **Remover**, usando o nome retornado no passo 1:
   ```sql
   ALTER TABLE tb_professor_titulacoes DROP CONSTRAINT <nome_retornado>;
   ```
3. **Conferir** repetindo a consulta do passo 1: não deve retornar nenhuma linha.
4. **Testar pela API**: vincular uma titulação, remover o vínculo, vincular a **mesma** combinação (mesmo professor, mesma titulação, mesma `dataInicio`). O segundo vínculo deve ser criado com sucesso.

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Banco criado antes da ISSUE-41 | Executar os passos 1 a 3 |
| Banco criado do zero depois da ISSUE-41 | Passo 1 não retorna linhas; nenhum ajuste necessário |
| Existem dados duplicados antigos | Não é problema; a remoção da constraint nunca falha por causa de dados |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: script SQL manual, executado uma vez por banco. Sem migração versionada, por decisão do projeto de estudo.
- **Armazenamento de Dados**: apenas a constraint de `tb_professor_titulacoes` é afetada.

### Constraints
- **Compatibilidade**: depende da ISSUE-41. Cada pessoa que rodar o projeto com um banco antigo precisa executar o passo.
- **Segurança**: executar só no banco de estudo (`demo`, local).

### Risk Assessment
- **Riscos Técnicos**: remover a constraint errada. Conferir que a definição retornada é exatamente `UNIQUE (professor_id, titulacao_id, data_inicio)` antes do `DROP`.
- **Riscos de Dependência**: nenhum.

## Acceptance Criteria

### Functional Acceptance
- [ ] A consulta de constraints únicas de `tb_professor_titulacoes` não retorna nenhuma linha
- [ ] Vincular, remover e vincular novamente a mesma combinação professor + titulação + `dataInicio` cria o segundo vínculo com sucesso
- [ ] O passo a passo (consulta, `DROP`, conferência) está registrado na descrição do pull request

---

**Document Version**: 1.0
**Created**: 2026-10-27
**Clarification Rounds**: 0 (passo exigido pelo PRD `tabela-tb-professor-titulacao-v1.1`; sem regra nova)
**Quality Score**: n/a
