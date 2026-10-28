# Contexto da sessão de especificação

Resumo da conversa no claude.ai (17/09/2026 a 22/10/2026) que produziu os PRDs deste projeto. Serve para retomar o trabalho sem reler a conversa.

> **Atenção:** a tabela "Status por recurso" abaixo indica só se o **PRD** existe, não se há código. O estado da implementação e a stack (já definida: Spring Boot + PostgreSQL) estão no `CLAUDE.md` e em `docs/prds/INDEX.md`. Alguns PRDs marcados "ok" aqui (tabela e endpoints de usuário, exclusão de professor, `tb_disciplinas`, `tb_titulacoes` v1.1) não foram trazidos para o repositório e ficam de fora por ora: para esses casos o código é a referência.

## Quem e como

- Usuário: Jailson. Conduz o trabalho como analista/arquiteto; prefere decidir por etapas curtas.
- Método: skill **requirements-clarity** — rodadas de 2–3 perguntas, pontuação de clareza de 0 a 100, e geração do PRD quando atinge ≥ 90.
- O projeto é de **estudo**: o domínio acadêmico foi escolhido para praticar relacionamentos (1:N, N:N com atributos), regras de integridade e filtros.

## Preferências de trabalho observadas

- **Um PRD por endpoint** de cada recurso, sempre como documento independente.
- PRDs **agnósticos de tecnologia**. Nomes de parâmetros de paginação e de campos de metadados ficam livres para quem implementar; os PRDs só exigem que a informação exista na resposta.
- Os PRDs não levam as seções "Quality Standards", "User Acceptance" e "Execution Phases" (o usuário pediu para remover). Só Requirements Description, Design Decisions e Acceptance Criteria (funcional).
- Validações de formato/faixa **não** entram nos PRDs de tabela; ficam no PRD de cadastro do recurso.
- Respostas objetivas; o usuário costuma responder só "próximo" para seguir a sequência proposta.

## Decisões de modelagem

1. Aluno e professor **herdam de usuário** por FK única (`usuario_id`). Nome, e-mail, CPF, `ativo` e datas vêm de `usuarios`.
2. A estrutura original de `tb_alunos` (com nome/e-mail/CPF próprios) foi **substituída** pela v2.0.
3. Titulação é um **catálogo reutilizável**; o vínculo com professor é N:N em `tb_professor_titulacao`, com `dataInicio` obrigatória e `dataTermino` opcional. O mesmo par pode se repetir (períodos diferentes).
4. `tb_titulacoes` ganhou `ativo` (v1.1) para permitir exclusão lógica.
5. `tb_disciplinas` foi revisada e **mantida sem alterações**: id, nome, cargaHoraria, ementa, ativo, dataCriacao, dataAtualizacao. O vínculo com curso fica na futura `tb_curso_disciplina`.
6. Curso: `duracao` é em **semestres** (5 a 10).
7. Exclusão é sempre **lógica**; listagem não esconde inativos (nenhum recurso tem filtro automático por `ativo`).

## Status por recurso

| Recurso | Tabela | Listagem | Busca | Cadastro | Atualização | Exclusão |
|---|---|---|---|---|---|---|
| Usuário | ok | ok | ok | ok | ok | ok (inativação) |
| Curso | ok | ok | ok | ok | ok | ok (ponto a confirmar) |
| Aluno | ok (v2.0) | ok (v2.0) | ok | ok | ok | ok |
| Professor | ok | ok | ok | ok | ok | ok |
| Disciplina | ok | ok | ok | ok | ok | ok |
| Titulação | ok (v1.1) | ok | ok | ok | ok | ok |
| Professor-titulação | ok (v1.1) | — (vínculos na busca de professor v1.1) | — | ok (vincular) | ok | ok (remover vínculo) |
| Grade curricular (curso-disciplina) | — | — | — | — | — | — |
| Turma | — | — | — | — | — | — |
| Matrícula (aluno-curso) | — | — | — | — | — | — |
| Matrícula em turma (nota/frequência) | — | — | — | — | — | — |

Detalhe arquivo a arquivo em `docs/prds/INDEX.md`.

## Pendências e decisões em aberto

**Professor-titulação: decisões fechadas em 22/10/2026** (PRDs gerados pela linha de comando; ver `docs/prds/INDEX.md`):

| Decisão | Definido |
|---|---|
| Endpoints | Vincular, atualizar vínculo (datas) e remover vínculo. **Sem listagem própria**: os vínculos aparecem na busca de professor por id (v1.1), por simplicidade |
| Remoção do vínculo | Exclusão **lógica**: a tabela ganha `ativo`, `dataCriacao` e `dataAtualizacao` (v1.1); idempotente (204) |
| Status | Titulação inativa **e** professor inativo não recebem novos vínculos (erro de validação); vínculos existentes são preservados |
| Datas | `dataTermino` não pode ser anterior à `dataInicio`; `dataInicio` não pode ser futura; `dataTermino` pode ser futura (titulação em andamento) |
| Unicidade | Professor + titulação + `dataInicio` único só entre vínculos ativos (validado na aplicação) |
| Atualização | Só as datas; para trocar a titulação, remove-se e cria-se outro vínculo |
| Titulação inexistente no vínculo | 404 |

Outras pendências:
- Conferir os **pontos de atenção** do `CLAUDE.md` (regras de cadastro divergentes entre usuário e professor/aluno, `dataInativacao`, `idade` derivada).
- Confirmar a idempotência da exclusão de curso.
- Criar PRDs de: grade curricular, turmas, matrículas, matrículas em turma. Sequência acordada: `tb_curso_disciplina` → `tb_turmas` → `tb_matriculas` → `tb_matriculas_turma`.
- ~~Definir a stack e só então começar a implementação.~~ Feito: Spring Boot + PostgreSQL (ver `CLAUDE.md`).

## Sugestão de roteiro para o Claude Code

1. Abrir o projeto, ler `CLAUDE.md` e `docs/prds/INDEX.md`.
2. Fechar os pontos de atenção com o usuário e registrar as decisões no `CLAUDE.md`.
3. ~~Definir a stack e criar o esqueleto do projeto.~~ Feito: o projeto já existe e tudo até professor-titulação está implementado (ISSUE-01 a 47, ver `CHANGELOG.md`; a 47 foi resolvida junto com a 46 e não consta lá).
4. Implementar recurso a recurso, na ordem das dependências: usuário → curso → disciplina → titulação → professor → aluno → professor-titulação.
5. Para cada endpoint: seguir o passo 5 de "Como trabalhar" no `CLAUDE.md`, com testes cobrindo os critérios de aceite.
