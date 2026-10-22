# Cadastro de Professor - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita criar um professor no sistema. Como professor é uma extensão de usuário, o cadastro precisa criar o usuário e o registro de professor juntos, de forma atômica.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por cadastrar novos professores.
- **Proposta de Valor**: Permitir a criação de um professor em uma única operação, garantindo consistência entre `usuarios` e `tb_professores` e respeitando as regras de unicidade e validação de ambos.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de cadastro que cria simultaneamente um registro em `usuarios` e um registro em `tb_professores` vinculado a ele.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não permite informar `id`, `ativo`, `dataCriacao`, `dataAtualizacao` (de usuário) nem `id` (de professor) — gerados automaticamente
  - Não trata titulações neste cadastro — tratado em endpoint/recurso separado
- **Cenários de Uso Típicos**:
  - Front-end envia os dados de usuário e a data de admissão, e recebe o professor criado (com usuário já vinculado)
  - Front-end envia `email`, `cpf` ou `login` já existentes e recebe erro de unicidade
  - Front-end envia algum campo fora das regras de validação e recebe erro correspondente

### Detailed Requirements

#### Input/Output

**Payload de entrada:**

| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `login` | Sim | Entre 3 e 50 caracteres; único |
| `senha` | Sim | Mínimo 8 caracteres |
| `idade` | Sim | Entre 18 e 80 |
| `email` | Sim | Formato de e-mail válido; único |
| `cpf` | Sim | CPF válido (formato e dígito verificador); único |
| `dataAdmissao` | Sim | Não pode ser data futura |

**Campos não aceitos no payload de entrada:**
- `id` (usuário e professor) — gerados automaticamente
- `ativo` — assume default `true`
- `dataCriacao` / `dataAtualizacao` — preenchidos automaticamente pelo sistema/banco

**Resposta de sucesso:**
- A entidade completa do professor criado, combinando dados de `tb_professores` e `usuarios`: `id`, `usuarioId`, `nome`, `email`, `cpf` (mascarado), `ativo`, `dataAdmissao`, `dataCriacao`, `dataAtualizacao`
- `senha` nunca é retornada

#### Data Requirements
- A operação deve ser atômica: se a criação do usuário ou do professor falhar, nenhum dos dois deve ser persistido
- `login`, `email` e `cpf` devem ser únicos em `usuarios`
- `usuario_id` gerado deve ser único em `tb_professores` (garantido pela própria criação em conjunto)
- Segue as regras já definidas para `usuarios` e `tb_professores` (obrigatoriedade, unicidade, preenchimento automático de datas)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `nome` fora do intervalo 5–255 caracteres | Erro de validação |
| `login` fora do intervalo 3–50 caracteres, ou já existente | Erro de validação / erro de unicidade |
| `senha` com menos de 8 caracteres | Erro de validação |
| `idade` fora do intervalo 18–80 | Erro de validação |
| `email` com formato inválido, ou já existente | Erro de validação / erro de unicidade |
| `cpf` inválido (formato/dígito verificador), ou já existente | Erro de validação / erro de unicidade |
| `dataAdmissao` no futuro | Erro de validação |
| Payload sem algum campo obrigatório | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro criado |
| Cadastro com sucesso | Retorna a entidade completa do professor, incluindo `id`, `usuarioId` e datas geradas automaticamente; `senha` nunca é retornada |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Endpoint de cadastro, camada de validação de entrada, persistência transacional em `usuarios` e `tb_professores`.
- **Armazenamento de Dados**: Tabelas `usuarios` e `tb_professores`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP (POST) recebendo os campos de usuário + `dataAdmissao` no corpo da requisição, respondendo com a entidade professor criada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Senha nunca exposta; CPF mascarado na resposta. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Operação atômica entre duas tabelas exige controle transacional — se não implementado corretamente, pode gerar usuário órfão sem professor (ou vice-versa).
- **Riscos de Dependência**: Depende das tabelas `usuarios` e `tb_professores` já existirem com as regras definidas.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint cria simultaneamente um registro em `usuarios` e um em `tb_professores`, vinculados
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `login` fora do intervalo 3–50 caracteres ou duplicado retorna erro
- [ ] `senha` com menos de 8 caracteres retorna erro de validação
- [ ] `idade` fora do intervalo 18–80 retorna erro de validação
- [ ] `email` com formato inválido ou duplicado retorna erro
- [ ] `cpf` inválido ou duplicado retorna erro
- [ ] `dataAdmissao` no futuro retorna erro de validação
- [ ] Payload sem campo obrigatório retorna erro de validação
- [ ] Campos `id`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos como entrada
- [ ] Resposta de sucesso retorna a entidade completa do professor, sem o campo `senha`
- [ ] Falha na criação de qualquer uma das duas tabelas não persiste dados parciais

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 4
**Quality Score**: 96/100
