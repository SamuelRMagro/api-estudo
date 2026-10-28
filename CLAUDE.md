# Sistema de Cursos Superiores — API de estudo

Projeto de estudo: uma API de gestão acadêmica (usuários, alunos, professores, cursos, disciplinas, titulações, e depois turmas e matrículas). O objetivo é praticar modelagem, relacionamentos e regras de negócio reais.

Idioma do projeto: **português do Brasil** (PRDs, mensagens de erro, comentários e conversa).

## Estado atual

- **Em implementação.** Cada entrega é uma issue (`ISSUE-NN`), registrada no `CHANGELOG.md` e feita numa branch `ISSUE-NN_descricao`.
- **Implementado (até a ISSUE-47):** (a ISSUE-47 foi resolvida junto com a 46 e por isso não aparece no `CHANGELOG.md`) usuário, curso, professor, disciplina, aluno, titulação e professor-titulação (tabela v1.1, vincular, atualizar datas, remover vínculo, e busca de professor por id v1.1 com os vínculos). A atualização de aluno e professor v1.1 (sem `login`) também está no código (ISSUE-46).
- **Pendente (sem PRD):** grade curricular, turmas e matrículas (ver "Próximo passo"). A revisão das ISSUE-41 a 47 achou desvios do código em relação aos PRDs; as correções estão em PRDs próprios (ver `docs/prds/INDEX.md`).
- Os PRDs são agnósticos de tecnologia; a stack abaixo é a do código existente.
- Contexto da sessão que originou os PRDs (feita no claude.ai, antes do uso pela linha de comando): @docs/CONTEXTO-SESSAO.md
- Índice de todos os PRDs, com versão, score, issue e status: @docs/prds/INDEX.md

## Stack

- **Linguagem/framework:** Java 21, Spring Boot 4.1.1 (`spring-boot-starter-webmvc`, `data-jpa`, `validation`, `actuator`).
- **SGBD:** PostgreSQL em `localhost:5432/demo` (usuário/senha `postgres`), configurado em `src/main/resources/application.properties`.
- **Schema:** gerado pelo Hibernate (`spring.jpa.hibernate.ddl-auto=update`); não há Flyway nem Liquibase.
- **Bibliotecas:** Lombok, MapStruct 1.6.3 (mappers), springdoc-openapi 3.1.1 (Swagger), Hibernate Validator (inclui `@CPF`).
- **Rodar:** `./mvnw spring-boot:run` (context-path `/apiestudo`).
- **Testes:** `./mvnw test`. Hoje só existe `DemoApplicationTests.contextLoads`, que precisa do Postgres no ar.
- **Estrutura de pacotes** (`com.example.demo`): `controller`, `service`, `repository`, `model`, `dto/<recurso>` (`CreateDTO`, `UpdateDTO`, `ResponseDTO`, `FilterRequest`), `mappers`, `specifictions` (filtros via JPA Specification), `customValidations`, `exceptions` (`GlobalExceptionHandler`, `RegistroNaoEncontradoCustomException`, `CadastroInvalidoCustomException`).
- **Paginação:** Spring `Pageable` com parâmetros `page`, `size` e `sort`. Atenção: `default-page-size=20` no `application.properties`, mas a convenção abaixo pede 10.

## Modelo de domínio

```
usuarios ─┬─< tb_alunos          (usuario_id FK, único)
          └─< tb_professores     (usuario_id FK, único)

tb_professores ─< tb_professor_titulacao >─ tb_titulacoes     (N:N, com dataInicio/dataTermino)

tb_cursos ─< tb_curso_disciplina >─ tb_disciplinas            (grade curricular — AINDA SEM PRD)
tb_disciplinas ─< tb_turmas >─ tb_professores                 (AINDA SEM PRD)
tb_alunos ─< tb_matriculas >─ tb_cursos                       (AINDA SEM PRD)
tb_alunos ─< tb_matriculas_turma >─ tb_turmas                 (nota/frequência — AINDA SEM PRD)
```

Campos por tabela (detalhes, tamanhos e validações estão nos PRDs):

| Tabela | Campos |
|---|---|
| `usuarios` | id, nome, login (único), senha, idade, email (único), cpf (único), ativo, dataCriacao, dataAtualizacao (+ `dataInativacao`, sugerida) |
| `tb_cursos` | id, nome, cargaHoraria, duracao (em semestres), ativo (default true), dataCriacao, dataAtualizacao |
| `tb_disciplinas` | id, nome, cargaHoraria, ementa, ativo (default true), dataCriacao, dataAtualizacao |
| `tb_alunos` (v2) | id, usuario_id (FK único), telefone (opcional), dataNascimento |
| `tb_professores` | id, usuario_id (FK único), dataAdmissao |
| `tb_titulacoes` (v1.1) | id, nome, area, ativo (default true) |
| `tb_professor_titulacao` (v1.1; nome físico `tb_professor_titulacoes`) | id, professor_id, titulacao_id, dataInicio, dataTermino (opcional), ativo (default true), dataCriacao, dataAtualizacao; professor+titulação+dataInicio é único só entre vínculos ativos |

**Aluno e professor são extensões de usuário.** Nome, e-mail, CPF, `ativo` e datas de auditoria vêm de `usuarios` por join e **não** são duplicados nas tabelas de extensão. O cadastro de aluno/professor cria o usuário e a extensão **na mesma transação** (tudo ou nada).

## Convenções transversais (valem para todo endpoint)

**Listagem**
- Paginação: tamanho padrão **10**, máximo **100**. Página ou tamanho ≤ 0, ou tamanho > 100 → erro de validação. Página além do total → retorna a **última** página existente.
- Ordenação: padrão por `nome` ascendente; o cliente escolhe campo e direção.
- A resposta traz a lista e os metadados: total de registros, total de páginas e página atual.
- Os **nomes dos parâmetros e dos campos de metadados são livres** (não fixar `page`/`size` por imposição dos PRDs).
- Filtros por tipo: parcial ("contém"), exato (booleano) e intervalo (*between*; início > fim → erro de validação). Cada PRD lista quais campos têm filtro.

**Busca por id**: retorna todos os campos; registro inativo é retornado normalmente; id inexistente → **404** com mensagem adequada.

**Cadastro**: aceita só os campos do payload do PRD. `id`, `ativo` (exceto onde o PRD diz o contrário) e datas de auditoria **não** são aceitos como entrada (ignorar ou rejeitar). Responde com a entidade completa salva.

**Atualização**: completa (PUT), não parcial. `id`, `ativo` e `dataCriacao` não são editáveis. `dataAtualizacao` é preenchida automaticamente. Mesmas validações do cadastro. Id inexistente → 404. Responde com a entidade completa atualizada.

**Exclusão**: sempre **lógica** (`ativo = false`), resposta **204** sem corpo, id inexistente → 404. Exclusão de registro já inativo é idempotente (204), exceto onde o PRD marcar "a confirmar".

**Auditoria**: `dataCriacao` e `dataAtualizacao` nunca são informadas pelo cliente. `dataCriacao` é imutável.

**Segurança nesta versão**: sem autenticação/autorização. A `senha` **nunca** é retornada. O CPF é retornado **mascarado** (`***.***.**1-23`).

## Como trabalhar neste repositório

1. **Um PRD por endpoint.** Cada endpoint de cada recurso tem o seu documento em `docs/prds/`. Não misture endpoints num mesmo PRD.
2. **Leia o PRD antes de mexer em código.** O PRD é a fonte da verdade; os critérios de aceite viram testes.
3. **Não invente regra.** Se algo não está no PRD nem nas convenções acima, pergunte ao usuário antes de assumir. Ao assumir um padrão, diga qual foi.
4. **Novo PRD**: use a skill `/requirements-clarity` (rodadas de esclarecimento e pontuação de clareza). Siga o formato dos PRDs existentes em `docs/prds/` e só gere o PRD com clareza ≥ 90/100. Nome do arquivo: `<endpoint>-<recurso>-v<versão>-prd.md`.
5. **Implementar um PRD**: leia o PRD, implemente seguindo o padrão de pacotes da seção "Stack", cubra os critérios de aceite com testes e adicione a entrada no `CHANGELOG.md`.
6. **PRD substituído**: não apague; marque no topo com "SUBSTITUÍDO por …" e aponte para a nova versão.
7. Ao concluir ou alterar um PRD, ou ao entregar uma issue, atualize `docs/prds/INDEX.md`.

## Pontos de atenção (inconsistências entre PRDs — resolver antes de implementar)

1. **Regras de cadastro divergentes.** Usuário: nome 5–250, idade 15–130, login 5–250, senha 5–50. Professor/aluno (que criam um usuário): nome 5–255, login 3–50, senha ≥ 8, idade 18–80 (professor) ou 17–80 derivada de `dataNascimento` (aluno). Decidir qual regra vale quando o usuário nasce via professor/aluno.
2. **`ativo` no cadastro.** Em usuário ele é informado (default `true`); em professor e aluno não é aceito.
3. **Inativação.** A inativação de usuário grava `dataInativacao` e devolve o objeto atualizado, com 204 se já inativo. A exclusão de professor/aluno só marca `ativo = false` e atualiza `dataAtualizacao`, sem `dataInativacao`. Decidir se elas devem reutilizar a inativação de usuário.
4. **`idade` gravada vs `dataNascimento`.** No aluno, a idade é calculada no cadastro e gravada em `usuarios`, e fica desatualizada. Sugestão: calcular na leitura.
5. **Exclusão de curso**: idempotência para curso já inativo está "a confirmar" (em professor, aluno, disciplina e titulação ficou idempotente).
6. ~~Titulação inativa pode receber novos vínculos?~~ **Decidido (22/10/2026):** não; professor inativo também não. Vínculos existentes são preservados.
7. `tb_titulacoes` não tem `dataCriacao`/`dataAtualizacao`. (`tb_professor_titulacao` ganha as duas na v1.1.)
8. CPF: em `usuarios` é salvo só com números e exibido mascarado.
9. ~~`login` editável?~~ **Decidido (22/10/2026):** `login` **não** é editável em nenhuma atualização (usuário, aluno, professor). PRDs de atualização de aluno e professor foram para a v1.1. Implementado na ISSUE-46/47 (`login` saiu dos DTOs de atualização).
10. **Filtros de usuário** (`filtros-listagem-usuarios-v1.0-prd.md`) fixam nomes (`isAtivo`, `dataCadastroInicio`/`Fim`, `cpfMascarado`, `dataCadastro`), contrariando "nomes livres" e o campo `dataCriacao`. Também têm limites de data próprios (1900–2100) e se contradizem sobre início = fim (linhas 68 e 101).
11. **Tamanho de página**: convenção 10; o código usa 20 (`application.properties`); o PRD de filtros de usuário manda preservar o mecanismo existente.
12. **`tb_titulacoes` v1.1**: o campo `ativo` existe na entidade `Titulacao` (`isAtivo`), mas o PRD v1.1 não está na pasta (só a v1.0).
13. **`entidade-listagem-aluno-v2.0-prd.md`** tem score 88, abaixo do mínimo de 90.

## Próximo passo

1. **Implementar as correções da revisão das ISSUE-41 a 47** (feita em 27/10/2026), listadas em `docs/prds/INDEX.md` ("Correções da revisão"): duplicidade de vínculo, erros e status, ordenação da busca de professor e remoção da constraint antiga no banco (o `ddl-auto=update` não remove).
2. **Fechar os pontos de atenção** 1, 3, 4, 5, 10, 11, 12 e 13.
3. Depois: grade curricular (`tb_curso_disciplina`), turmas e matrículas, cada uma com `/requirements-clarity`.

**Sem PRD, por decisão:** tabela `usuarios`, listagem/busca/cadastro/inativação de usuário, exclusão de professor e tabela `tb_disciplinas`. Foram implementados antes da documentação e ficam sem PRD por ora: para eles, **o código é a referência**. Não gere PRD retroativo sem o usuário pedir. Se um desses endpoints precisar mudar, especifique a mudança com `/requirements-clarity` antes.
