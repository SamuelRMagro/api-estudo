# Índice de PRDs

Um PRD por endpoint. A coluna **Issue** liga o PRD à entrega registrada no `CHANGELOG.md`; "—" indica que ainda não foi implementado.

Legenda de status: **implementado**, **em andamento** (branch aberta), **pendente** (PRD pronto, sem código), **substituído** (mantido só como histórico), **sem PRD (código é a referência)** (implementado antes da documentação; ficou de fora por decisão).

## Usuário

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| _Tabela `usuarios`_ | — | — | — | ISSUE-01 | implementado, sem PRD (código é a referência) |
| _Listagem de usuários_ | — | — | — | ISSUE-02 | implementado, sem PRD (código é a referência) |
| _Busca de usuário por id_ | — | — | — | ISSUE-03 | implementado, sem PRD (código é a referência) |
| _Inativação de usuário_ | — | — | — | ISSUE-04 | implementado, sem PRD (código é a referência) |
| _Cadastro de usuário_ | — | — | — | ISSUE-05 | implementado, sem PRD (código é a referência) |
| [atualizacao-usuario-v1.0-prd.md](atualizacao-usuario-v1.0-prd.md) | 1.0 | 95 | 2026-09-17 | ISSUE-06 | implementado |
| [filtros-listagem-usuarios-v1.0-prd.md](filtros-listagem-usuarios-v1.0-prd.md) | 1.0 | 97 | 2026-09-21 | ISSUE-07 | implementado |

## Curso

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| [cursos-v1.0-prd.md](cursos-v1.0-prd.md) (tabela + listagem) | 1.0 | 90 | 2026-09-25 | ISSUE-08 | implementado |
| [busca-curso-por-id-v1.0-prd.md](busca-curso-por-id-v1.0-prd.md) | 1.0 | 95 | 2026-09-25 | ISSUE-09 | implementado |
| [cadastro-curso-v1.0-prd.md](cadastro-curso-v1.0-prd.md) | 1.0 | 95 | 2026-09-25 | ISSUE-10 | implementado |
| [atualizacao-curso-v1.0-prd.md](atualizacao-curso-v1.0-prd.md) | 1.0 | 96 | 2026-09-25 | ISSUE-11 | implementado |
| [exclusao-curso-v1.0-prd.md](exclusao-curso-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-12 | implementado (idempotência "a confirmar" no PRD) |

## Aluno

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| [tabela-tb-alunos-v1.0-prd.md](tabela-tb-alunos-v1.0-prd.md) | 1.0 | 90 | 2026-09-25 | ISSUE-13 | substituído pela v2.0 |
| [listagem-alunos-v1.0-prd.md](listagem-alunos-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-14 | campos substituídos pela v2.0; paginação/filtros valem |
| [estrutura-tb-alunos-v2.0-prd.md](estrutura-tb-alunos-v2.0-prd.md) | 2.0 | 90 | 2026-09-25 | ISSUE-15 | implementado |
| [entidade-listagem-aluno-v2.0-prd.md](entidade-listagem-aluno-v2.0-prd.md) | 2.0 | **88** | 2026-09-25 | ISSUE-16 | implementado (score abaixo de 90) |
| [busca-aluno-por-id-v1.0-prd.md](busca-aluno-por-id-v1.0-prd.md) | 1.0 | 96 | 2026-10-16 | ISSUE-32 | implementado |
| [cadastro-aluno-v1.0-prd.md](cadastro-aluno-v1.0-prd.md) | 1.0 | 95 | 2026-10-16 | ISSUE-33 | implementado |
| [atualizacao-aluno-v1.0-prd.md](atualizacao-aluno-v1.0-prd.md) | 1.0 | 90 | 2026-10-16 | ISSUE-34 | em andamento |
| [exclusao-aluno-v1.0-prd.md](exclusao-aluno-v1.0-prd.md) | 1.0 | 94 | 2026-10-16 | — | pendente |

## Professor

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| [tabela-tb-professores-v1.0-prd.md](tabela-tb-professores-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-18 | implementado |
| [listagem-professores-v1.0-prd.md](listagem-professores-v1.0-prd.md) | 1.0 | 95 | 2026-09-25 | ISSUE-21 | implementado |
| [busca-professor-por-id-v1.0-prd.md](busca-professor-por-id-v1.0-prd.md) | 1.0 | 96 | 2026-09-25 | ISSUE-22 | implementado |
| [cadastro-professor-v1.0-prd.md](cadastro-professor-v1.0-prd.md) | 1.0 | 96 | 2026-09-25 | ISSUE-23 | implementado |
| [atualizacao-professor-v1.0-prd.md](atualizacao-professor-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-24 | implementado |
| _Exclusão de professor_ | — | — | — | ISSUE-26 | implementado, sem PRD (código é a referência) |

## Disciplina

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| _Tabela `tb_disciplinas`_ | — | — | — | — | entidade existe, sem PRD (código é a referência) |
| [listagem-disciplinas-v1.0-prd.md](listagem-disciplinas-v1.0-prd.md) | 1.0 | 94 | 2026-10-13 | ISSUE-27 | implementado |
| [busca-disciplina-por-id-v1.0-prd.md](busca-disciplina-por-id-v1.0-prd.md) | 1.0 | 96 | 2026-10-13 | ISSUE-28 | implementado |
| [cadastro-disciplina-v1.0-prd.md](cadastro-disciplina-v1.0-prd.md) | 1.0 | 95 | 2026-10-13 | ISSUE-29 | implementado |
| [atualizacao-disciplina-v1.0-prd.md](atualizacao-disciplina-v1.0-prd.md) | 1.0 | 94 | 2026-10-13 | ISSUE-30 | implementado |
| [exclusao-disciplina-v1.0-prd.md](exclusao-disciplina-v1.0-prd.md) | 1.0 | 92 | 2026-10-13 | ISSUE-31 | implementado |

## Titulação

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| [tabela-tb-titulacoes-v1.0-prd.md](tabela-tb-titulacoes-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-19 | implementado (o `CLAUDE.md` cita uma v1.1 com `ativo`, que não está na pasta) |
| [listagem-titulacoes-v1.0-prd.md](listagem-titulacoes-v1.0-prd.md) | 1.0 | 93 | 2026-10-19 | — | pendente |
| [busca-titulacao-por-id-v1.0-prd.md](busca-titulacao-por-id-v1.0-prd.md) | 1.0 | 96 | 2026-10-19 | — | pendente |
| [cadastro-titulacao-v1.0-prd.md](cadastro-titulacao-v1.0-prd.md) | 1.0 | 90 | 2026-10-19 | — | pendente |
| [atualizacao-titulacao-v1.0-prd.md](atualizacao-titulacao-v1.0-prd.md) | 1.0 | 91 | 2026-10-19 | — | pendente |
| [exclusao-titulacao-v1.0-prd.md](exclusao-titulacao-v1.0-prd.md) | 1.0 | 92 | 2026-10-19 | — | pendente |

## Professor-titulação

| PRD | Versão | Score | Criado | Issue | Status |
|---|---|---|---|---|---|
| [tabela-tb-professor-titulacao-v1.0-prd.md](tabela-tb-professor-titulacao-v1.0-prd.md) | 1.0 | 92 | 2026-09-25 | ISSUE-20 | implementado |
| _Endpoints (listar, vincular, atualizar, remover vínculo)_ | — | — | — | — | sem PRD; decisões abertas em `docs/CONTEXTO-SESSAO.md` |

## Ainda sem PRD

Grade curricular (`tb_curso_disciplina`) → turmas (`tb_turmas`) → matrículas (`tb_matriculas`) → matrículas em turma (`tb_matriculas_turma`).
