# Atualização de Professor - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita atualizar os dados de um professor já cadastrado, considerando que esses dados estão distribuídos entre `usuarios` e `tb_professores`.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter os dados de professores atualizados.
- **Proposta de Valor**: Permitir a atualização completa e validada dos dados editáveis de um professor, mantendo a consistência entre `usuarios` e `tb_professores`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização (completa) de professor, recebendo `nome`, `login`, `idade`, `email`, `cpf` (de `usuarios`) e `dataAdmissao` (de `tb_professores`), com as mesmas validações do cadastro.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - `senha` não é editável por este endpoint — fica fora de escopo (tratada em endpoint próprio, se necessário, futuramente)
  - `ativo` não é editável por este endpoint
  - `id`, `usuarioId` e `dataCriacao` não podem ser alterados
  - Atualização é completa — não há suporte a atualização parcial (PATCH) nesta versão
- **Cenários de Uso Típicos**:
  - Front-end envia todos os campos editáveis válidos para um professor existente e recebe a entidade atualizada
  - Front-end tenta atualizar um professor com `id` inexistente e recebe erro 404
  - Front-end envia um valor fora das regras de validação e recebe erro correspondente

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do professor a ser atualizado (via path)

**Payload de entrada (atualização completa):**

| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `login` | Sim | Entre 3 e 50 caracteres; único (exceto para o próprio registro) |
| `idade` | Sim | Entre 18 e 80 |
| `email` | Sim | Formato de e-mail válido; único (exceto para o próprio registro) |
| `cpf` | Sim | CPF válido (formato e dígito verificador); único (exceto para o próprio registro) |
| `dataAdmissao` | Sim | Não pode ser data futura |

**Campos não editáveis por este endpoint:**
- `id` (usuário e professor) — não podem ser alterados
- `senha` — fora de escopo deste endpoint
- `ativo` — não editável por este endpoint
- `dataCriacao` — imutável
- `dataAtualizacao` — preenchido automaticamente pelo sistema/banco no momento da atualização

**Resposta de sucesso:**
- A entidade completa atualizada, combinando dados de `tb_professores` e `usuarios`: `id`, `usuarioId`, `nome`, `email`, `cpf` (mascarado), `ativo`, `dataAdmissao`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `nome`, `login`, `idade`, `email`, `cpf`, `dataAdmissao` são obrigatórios no payload (atualização completa)
- Validações seguem as mesmas regras definidas no cadastro
- `login`, `email` e `cpf` devem continuar únicos, considerando os demais registros (não conflitar consigo mesmo)
- A operação deve atualizar os dois registros relacionados (`usuarios` e `tb_professores`) de forma consistente
- `dataAtualizacao` (de `usuarios`) deve ser atualizado automaticamente para o momento da alteração
- `dataCriacao` permanece inalterado

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum professor existente | Erro 404 com mensagem adequada |
| `nome` fora do intervalo 5–255 caracteres | Erro de validação |
| `login` fora do intervalo 3–50 caracteres, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `idade` fora do intervalo 18–80 | Erro de validação |
| `email` com formato inválido, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `cpf` inválido, ou já usado por outro registro | Erro de validação / erro de unicidade |
| `dataAdmissao` no futuro | Erro de validação |
| Payload sem algum campo obrigatório | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id`, `senha`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro atualizado |
| Atualização com sucesso | Retorna a entidade completa atualizada, com `dataAtualizacao` refletindo o momento da alteração |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de atualização, camada de validação de entrada (reaproveitando as regras do cadastro), persistência transacional em `usuarios` e `tb_professores`.
- **Armazenamento de Dados**: Tabelas `usuarios` e `tb_professores`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (PUT) recebendo o `id` via path e os campos editáveis no corpo da requisição, respondendo com a entidade atualizada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: CPF mascarado na resposta. Senha não é alterada por este endpoint. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Operação atômica entre duas tabelas exige controle transacional, assim como no cadastro.
- **Riscos de Dependência**: Depende das tabelas `usuarios` e `tb_professores` já existirem, e do endpoint de cadastro já ter definido as regras de validação reaproveitadas aqui.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome`, `login`, `idade`, `email`, `cpf`, `dataAdmissao` no payload de entrada (atualização completa)
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `login` fora do intervalo 3–50 caracteres ou duplicado (em outro registro) retorna erro
- [ ] `idade` fora do intervalo 18–80 retorna erro de validação
- [ ] `email` com formato inválido ou duplicado (em outro registro) retorna erro
- [ ] `cpf` inválido ou duplicado (em outro registro) retorna erro
- [ ] `dataAdmissao` no futuro retorna erro de validação
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Campos `id`, `senha`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos/alterados via este endpoint
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da alteração
- [ ] `dataCriacao` permanece inalterado após a atualização
- [ ] Resposta de sucesso retorna a entidade completa atualizada, sem o campo `senha`

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 92/100
