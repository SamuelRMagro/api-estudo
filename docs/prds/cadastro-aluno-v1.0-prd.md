# Cadastro de Aluno - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita criar um aluno no sistema. Como aluno é uma extensão de usuário, o cadastro precisa criar o usuário e o registro de aluno juntos, de forma atômica.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por cadastrar novos alunos.
- **Proposta de Valor**: Permitir a criação de um aluno em uma única operação, garantindo consistência entre `usuarios` e `tb_alunos` e respeitando as regras de unicidade e validação de ambos.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de cadastro que cria simultaneamente um registro em `usuarios` e um registro em `tb_alunos` vinculado a ele.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não permite informar `id`, `ativo`, `dataCriacao`, `dataAtualizacao` nem `idade` — `idade` é calculada pelo sistema a partir de `dataNascimento`
  - Não trata matrícula em curso neste cadastro — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end envia os dados de usuário, a data de nascimento e, opcionalmente, o telefone, e recebe o aluno criado
  - Front-end envia `email`, `cpf` ou `login` já existentes e recebe erro de unicidade
  - Front-end envia uma data de nascimento que resulta em idade fora da faixa permitida e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Payload de entrada:**

| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `login` | Sim | Entre 3 e 50 caracteres; único |
| `senha` | Sim | Mínimo 8 caracteres |
| `email` | Sim | Formato de e-mail válido; único |
| `cpf` | Sim | CPF válido (formato e dígito verificador); único |
| `dataNascimento` | Sim | Não pode ser data futura; a idade resultante deve estar entre 17 e 80 anos |
| `telefone` | Não | Se informado, somente dígitos, com 10 ou 11 dígitos (DDD + número) |

**Campos não aceitos no payload de entrada:**
- `id` (usuário e aluno) — gerados automaticamente
- `idade` — calculada pelo sistema a partir de `dataNascimento` e gravada em `usuarios`
- `ativo` — assume default `true`
- `dataCriacao` / `dataAtualizacao` — preenchidos automaticamente pelo sistema/banco

**Resposta de sucesso:**
- A entidade completa do aluno criado, combinando dados de `tb_alunos` e `usuarios`: `id`, `usuarioId`, `nome`, `email`, `cpf` (mascarado), `ativo`, `telefone`, `dataNascimento`, `dataCriacao`, `dataAtualizacao`
- `senha` nunca é retornada

#### Data Requirements
- A operação deve ser atômica: se a criação do usuário ou do aluno falhar, nenhum dos dois deve ser persistido
- `login`, `email` e `cpf` devem ser únicos em `usuarios`
- A idade é calculada em anos completos, com base na data do cadastro, e gravada no campo `idade` de `usuarios`
- Segue as regras já definidas para `usuarios` e `tb_alunos` (obrigatoriedade, unicidade, preenchimento automático de datas)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `nome` fora do intervalo 5–255 caracteres | Erro de validação |
| `login` fora do intervalo 3–50 caracteres, ou já existente | Erro de validação / erro de unicidade |
| `senha` com menos de 8 caracteres | Erro de validação |
| `email` com formato inválido, ou já existente | Erro de validação / erro de unicidade |
| `cpf` inválido (formato/dígito verificador), ou já existente | Erro de validação / erro de unicidade |
| `dataNascimento` no futuro | Erro de validação |
| `dataNascimento` que resulta em idade menor que 17 ou maior que 80 | Erro de validação |
| `telefone` informado com letras, símbolos ou quantidade de dígitos diferente de 10 ou 11 | Erro de validação |
| Cadastro sem `telefone` | Permitido — campo opcional |
| Payload sem algum campo obrigatório | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id`, `idade`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro criado |
| Cadastro com sucesso | Retorna a entidade completa do aluno, incluindo `id`, `usuarioId` e datas geradas automaticamente; `senha` nunca é retornada |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de cadastro, camada de validação de entrada, cálculo de idade, persistência transacional em `usuarios` e `tb_alunos`.
- **Armazenamento de Dados**: Tabelas `usuarios` e `tb_alunos`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (POST) recebendo os campos de usuário + `dataNascimento` e `telefone` no corpo da requisição, respondendo com a entidade aluno criada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Senha nunca exposta; CPF mascarado na resposta. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Operação atômica entre duas tabelas exige controle transacional. Além disso, `idade` é gravada no momento do cadastro e fica desatualizada com o tempo, já que `dataNascimento` é a fonte da verdade.
- **Riscos de Dependência**: Depende das tabelas `usuarios` e `tb_alunos` (estrutura v2.0) já existirem.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint cria simultaneamente um registro em `usuarios` e um em `tb_alunos`, vinculados
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `login` fora do intervalo 3–50 caracteres ou duplicado retorna erro
- [ ] `senha` com menos de 8 caracteres retorna erro de validação
- [ ] `email` com formato inválido ou duplicado retorna erro
- [ ] `cpf` inválido ou duplicado retorna erro
- [ ] `dataNascimento` no futuro retorna erro de validação
- [ ] `dataNascimento` que resulta em idade fora de 17–80 retorna erro de validação
- [ ] `telefone` informado fora do formato (somente dígitos, 10 ou 11) retorna erro de validação
- [ ] Cadastro sem `telefone` é aceito
- [ ] `idade` é calculada a partir de `dataNascimento` e gravada em `usuarios`
- [ ] Payload sem campo obrigatório retorna erro de validação
- [ ] Campos `id`, `idade`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos como entrada
- [ ] Resposta de sucesso retorna a entidade completa do aluno, sem o campo `senha`
- [ ] Falha na criação de qualquer uma das duas tabelas não persiste dados parciais

---

**Document Version**: 1.0
**Created**: 2026-10-16
**Clarification Rounds**: 2
**Quality Score**: 95/100
