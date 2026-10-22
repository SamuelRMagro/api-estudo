# Atualização de Titulação - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita atualizar os dados de uma titulação já cadastrada no catálogo.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter o catálogo de titulações atualizado.
- **Proposta de Valor**: Permitir a atualização completa e validada dos dados editáveis de uma titulação, mantendo a consistência das regras já definidas para `tb_titulacoes`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização (completa) de titulação, recebendo `nome` e `area`, com as mesmas validações do cadastro.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - `ativo` não é editável por este endpoint
  - `id` não pode ser alterado
  - Atualização é completa — não há suporte a atualização parcial (PATCH) nesta versão
  - A alteração vale para todos os professores vinculados à titulação, já que ela é um catálogo compartilhado
- **Cenários de Uso Típicos**:
  - Front-end envia `nome` e `area` válidos para uma titulação existente e recebe a entidade atualizada
  - Front-end tenta atualizar uma titulação com `id` inexistente e recebe erro 404
  - Front-end envia um valor fora das faixas permitidas e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` da titulação a ser atualizada (via path)

**Payload de entrada (atualização completa):**
| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `area` | Sim | Entre 3 e 100 caracteres |

**Campos não editáveis por este endpoint:**
- `id` — não pode ser alterado
- `ativo` — não editável por este endpoint

**Resposta de sucesso:**
- A entidade completa atualizada, com todos os campos: `id`, `nome`, `area`, `ativo`

#### Data Requirements
- `nome` e `area` são obrigatórios no payload (atualização completa)
- Validações seguem as mesmas faixas definidas no cadastro
- Não há restrição de unicidade em `nome` ou `area`
- `tb_titulacoes` não possui campos de data de criação/atualização, portanto nenhum campo de data é alterado por este endpoint

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhuma titulação existente | Erro 404 com mensagem adequada |
| `nome` com menos de 5 ou mais de 255 caracteres | Erro de validação |
| `area` com menos de 3 ou mais de 100 caracteres | Erro de validação |
| Payload sem `nome` ou `area` | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id` ou `ativo` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro atualizado |
| Atualização de titulação inativa (`ativo = false`) | Permitida normalmente; `ativo` permanece inalterado |
| Atualização com sucesso | Retorna a entidade completa atualizada |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de atualização, camada de validação de entrada (reaproveitando as regras do cadastro), persistência na tabela `tb_titulacoes`.
- **Armazenamento de Dados**: Tabela `tb_titulacoes` (PRD v1.1).
- **Design de Interface**: Endpoint HTTP (PUT) recebendo o `id` via path e `nome`, `area` no corpo da requisição, respondendo com a entidade atualizada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo. Como a titulação é compartilhada, alterar `nome` ou `area` muda o que todos os professores vinculados veem.
- **Riscos de Dependência**: Depende da tabela `tb_titulacoes` (v1.1) e do endpoint de cadastro já terem as regras definidas.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome` e `area` no payload de entrada (atualização completa)
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `area` fora do intervalo 3–100 caracteres retorna erro de validação
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Campos `id` e `ativo` não são aceitos/alterados via este endpoint
- [ ] Resposta de sucesso retorna a entidade completa atualizada

---

**Document Version**: 1.0
**Created**: 2026-10-19
**Clarification Rounds**: 1
**Quality Score**: 91/100
