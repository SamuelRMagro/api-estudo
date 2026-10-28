# Atualização de Aluno - Product Requirements Document (PRD)

> **SUBSTITUÍDO** por [`atualizacao-aluno-v1.1-prd.md`](atualizacao-aluno-v1.1-prd.md) (`login` deixa de ser editável).

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita atualizar os dados de um aluno já cadastrado, considerando que esses dados estão distribuídos entre `usuarios` e `tb_alunos`.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter os dados de alunos atualizados.
- **Proposta de Valor**: Permitir a atualização completa e validada dos dados editáveis de um aluno, mantendo a consistência entre `usuarios` e `tb_alunos`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização (completa) de aluno, recebendo `nome`, `login`, `email`, `cpf` (de `usuarios`), `dataNascimento` e `telefone` (de `tb_alunos`), com as mesmas validações do cadastro.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - `senha` não é editável por este endpoint — fica fora de escopo
  - `ativo` não é editável por este endpoint
  - `id`, `usuarioId` e `dataCriacao` não podem ser alterados
  - `idade` não é informada — é recalculada a partir de `dataNascimento`
  - Atualização é completa — não há suporte a atualização parcial (PATCH) nesta versão
- **Cenários de Uso Típicos**:
  - Front-end envia todos os campos editáveis válidos para um aluno existente e recebe a entidade atualizada
  - Front-end tenta atualizar um aluno com `id` inexistente e recebe erro 404
  - Front-end envia um valor fora das regras de validação e recebe erro correspondente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do aluno a ser atualizado (via path)

**Payload de entrada (atualização completa):**

| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `login` | Sim | Entre 3 e 50 caracteres; único (exceto para o próprio registro) |
| `email` | Sim | Formato de e-mail válido; único (exceto para o próprio registro) |
| `cpf` | Sim | CPF válido (formato e dígito verificador); único (exceto para o próprio registro) |
| `dataNascimento` | Sim | Não pode ser data futura; a idade resultante deve estar entre 17 e 80 anos |
| `telefone` | Não | Se informado, somente dígitos, com 10 ou 11 dígitos; se omitido, o telefone do aluno é removido (atualização completa) |

**Campos não editáveis por este endpoint:**
- `id` (usuário e aluno) — não podem ser alterados
- `senha` — fora de escopo deste endpoint
- `ativo` — não editável por este endpoint
- `idade` — recalculada pelo sistema a partir de `dataNascimento`
- `dataCriacao` — imutável
- `dataAtualizacao` — preenchido automaticamente pelo sistema/banco no momento da atualização

**Resposta de sucesso:**
- A entidade completa atualizada, combinando dados de `tb_alunos` e `usuarios`: `id`, `usuarioId`, `nome`, `email`, `cpf` (mascarado), `ativo`, `telefone`, `dataNascimento`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `nome`, `login`, `email`, `cpf`, `dataNascimento` são obrigatórios no payload (atualização completa); `telefone` é opcional
- Validações seguem as mesmas regras definidas no cadastro
- `login`, `email` e `cpf` devem continuar únicos, considerando os demais registros (não conflitar consigo mesmo)
- A operação deve atualizar os dois registros relacionados (`usuarios` e `tb_alunos`) de forma consistente
- `idade` (em `usuarios`) é recalculada a partir da nova `dataNascimento`
- `dataAtualizacao` (de `usuarios`) deve ser atualizado automaticamente para o momento da alteração
- `dataCriacao` permanece inalterado

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum aluno existente | Erro 404 com mensagem adequada |
| `nome` fora do intervalo 5–255 caracteres | Erro de validação |
| `login` fora do intervalo 3–50 caracteres, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `email` com formato inválido, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `cpf` inválido, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `dataNascimento` no futuro, ou que resulta em idade fora de 17–80 | Erro de validação |
| `telefone` informado fora do formato (somente dígitos, 10 ou 11) | Erro de validação |
| Payload sem `telefone` | Permitido — telefone do aluno passa a ficar vazio |
| Payload sem algum campo obrigatório | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id`, `senha`, `idade`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro atualizado |
| Atualização de aluno inativo (`ativo = false`) | Permitida normalmente; `ativo` permanece inalterado |
| Atualização com sucesso | Retorna a entidade completa atualizada, com `dataAtualizacao` refletindo o momento da alteração |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de atualização, camada de validação de entrada (reaproveitando as regras do cadastro), cálculo de idade, persistência transacional em `usuarios` e `tb_alunos`.
- **Armazenamento de Dados**: Tabelas `usuarios` e `tb_alunos`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (PUT) recebendo o `id` via path e os campos editáveis no corpo da requisição, respondendo com a entidade atualizada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: CPF mascarado na resposta. Senha não é alterada por este endpoint. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Operação atômica entre duas tabelas exige controle transacional, assim como no cadastro.
- **Riscos de Dependência**: Depende das tabelas `usuarios` e `tb_alunos` e do endpoint de cadastro já terem definido as regras reaproveitadas aqui.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome`, `login`, `email`, `cpf`, `dataNascimento` e `telefone` no payload de entrada (atualização completa)
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `login` fora do intervalo 3–50 caracteres ou duplicado (em outro registro) retorna erro
- [ ] `email` com formato inválido ou duplicado (em outro registro) retorna erro
- [ ] `cpf` inválido ou duplicado (em outro registro) retorna erro
- [ ] `dataNascimento` no futuro, ou que resulta em idade fora de 17–80, retorna erro de validação
- [ ] `telefone` fora do formato retorna erro de validação; omitido, o telefone é removido
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Campos `id`, `senha`, `idade`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos/alterados via este endpoint
- [ ] `idade` é recalculada a partir da nova `dataNascimento`
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da alteração
- [ ] `dataCriacao` permanece inalterado após a atualização
- [ ] Resposta de sucesso retorna a entidade completa atualizada, sem o campo `senha`

---

**Document Version**: 1.0
**Created**: 2026-10-16
**Clarification Rounds**: 2
**Quality Score**: 90/100
