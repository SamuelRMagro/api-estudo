# Atualização de Curso - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita atualizar os dados de um curso já cadastrado.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter os dados de cursos atualizados.
- **Proposta de Valor**: Permitir a atualização completa e validada dos dados editáveis de um curso, mantendo a consistência das regras já definidas para `tb_cursos`.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização (completa) de curso, recebendo `nome`, `cargaHoraria` e `duracao`, com as mesmas validações do cadastro.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - `ativo` não é editável por este endpoint
  - `id` e `dataCriacao` não podem ser alterados
  - Atualização é completa — não há suporte a atualização parcial (PATCH) nesta versão
- **Cenários de Uso Típicos**:
  - Front-end envia `nome`, `cargaHoraria` e `duracao` válidos para um curso existente e recebe a entidade atualizada
  - Front-end tenta atualizar um curso com `id` inexistente e recebe erro 404
  - Front-end envia um valor fora das faixas permitidas e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do curso a ser atualizado (via path)

**Payload de entrada (atualização completa):**
| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `cargaHoraria` | Sim | Mínimo 10, máximo 10.000 |
| `duracao` | Sim | Entre 5 e 10 (quantidade de semestres) |

**Campos não editáveis por este endpoint:**
- `id` — não pode ser alterado
- `ativo` — não editável por este endpoint
- `dataCriacao` — imutável
- `dataAtualizacao` — preenchido automaticamente pelo sistema/banco no momento da atualização

**Resposta de sucesso:**
- A entidade completa atualizada, com todos os campos: `id`, `nome`, `cargaHoraria`, `duracao`, `ativo`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `nome`, `cargaHoraria` e `duracao` são obrigatórios no payload (atualização completa)
- Validações seguem as mesmas faixas definidas no cadastro
- `dataAtualizacao` deve ser atualizado automaticamente para o momento da alteração
- `dataCriacao` permanece inalterado

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum curso existente | Erro 404 com mensagem adequada |
| `nome` com menos de 5 ou mais de 255 caracteres | Erro de validação |
| `cargaHoraria` menor que 10 ou maior que 10.000 | Erro de validação |
| `duracao` fora do intervalo 5–10 | Erro de validação |
| Payload sem `nome`, `cargaHoraria` ou `duracao` | Erro de validação (campo obrigatório ausente) |
| Payload contendo `id`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro atualizado |
| Atualização com sucesso | Retorna a entidade completa atualizada, com `dataAtualizacao` refletindo o momento da alteração |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de atualização, camada de validação de entrada (reaproveitando as regras do cadastro), persistência na tabela `tb_cursos`.
- **Armazenamento de Dados**: Tabela `tb_cursos` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP (PUT) recebendo o `id` via path e `nome`, `cargaHoraria`, `duracao` no corpo da requisição, respondendo com a entidade atualizada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação de atualização simples, reaproveitando validações já definidas no cadastro.
- **Riscos de Dependência**: Depende da tabela `tb_cursos` e do endpoint de cadastro já existirem com as regras definidas.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome`, `cargaHoraria` e `duracao` no payload de entrada (atualização completa)
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `cargaHoraria` fora do intervalo 10–10.000 retorna erro de validação
- [ ] `duracao` fora do intervalo 5–10 retorna erro de validação
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Campos `id`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos/alterados via este endpoint
- [ ] `dataAtualizacao` é atualizado automaticamente no momento da alteração
- [ ] `dataCriacao` permanece inalterado após a atualização
- [ ] Resposta de sucesso retorna a entidade completa atualizada

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 3
**Quality Score**: 96/100
