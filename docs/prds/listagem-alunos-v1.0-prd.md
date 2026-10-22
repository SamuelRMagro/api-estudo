# Listagem de Alunos - Product Requirements Document (PRD)

> **SUBSTITUÍDO (campos da resposta)** por [`entidade-listagem-aluno-v2.0-prd.md`](entidade-listagem-aluno-v2.0-prd.md). Paginação, ordenação e filtros deste documento seguem valendo onde a v2.0 não os redefine.

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que exponha, de forma paginada, ordenada e filtrável, os alunos cadastrados no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam listar alunos para exibição em telas administrativas ou de gestão acadêmica.
- **Proposta de Valor**: Fornecer uma listagem confiável e flexível de alunos, com filtros, ordenação e paginação padronizados, seguindo o mesmo padrão já estabelecido para cursos.

### Feature Overview
- **Funcionalidades Principais**:
  - Endpoint de listagem de alunos com paginação
  - Ordenação configurável (campo e direção)
  - Filtros por `nome`, `email`, `cpf` (parcial) e `ativo` (exato)
  - Metadados de paginação prontos para uso pelo front-end
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Sem filtros para `telefone`, `dataNascimento`, `dataCriacao`, `id`, `dataAtualizacao`
- **Cenários de Uso Típicos**:
  - Front-end solicita a primeira página com tamanho padrão (10 itens), ordenada por nome
  - Front-end filtra alunos por nome/email/cpf (parcial) ou por status ativo
  - Front-end solicita uma página além do total existente e recebe a última página válida

### Detailed Requirements

#### Input/Output

**Parâmetros de entrada:**

Os nomes dos parâmetros ficam a critério do backend. A entrada deve permitir controlar:

| Controle | Obrigatório | Default | Regras |
|---|---|---|---|
| Número da página | Não | 1ª página | Deve ser > 0. Se além do total de páginas, retorna a última página existente |
| Tamanho da página | Não | 10 | Deve ser > 0 e ≤ 100 |
| Campo de ordenação | Não | `nome` | — |
| Direção da ordenação | Não | ascendente | Configurável pelo cliente (ascendente/descendente) |

**Filtros disponíveis:**

| Campo | Tipo de filtro |
|---|---|
| `nome` | Parcial (contém) |
| `email` | Parcial (contém) |
| `cpf` | Parcial (contém) |
| `ativo` | Exato (true/false) |

**Campos sem filtro:**
- `id`, `telefone`, `dataNascimento`, `dataCriacao`, `dataAtualizacao`

**Campos de saída (por aluno):**
| Campo | Descrição |
|---|---|
| `id` | Identificador único do aluno |
| `nome` | Nome do aluno |
| `email` | E-mail do aluno |
| `cpf` | CPF do aluno |
| `telefone` | Telefone do aluno |
| `dataNascimento` | Data de nascimento do aluno |
| `ativo` | Status (true/false) |
| `dataCriacao` | Data de criação do registro |
| `dataAtualizacao` | Data da última atualização do registro |

Todos os campos acima aparecem na resposta.

**Estrutura da resposta:**
- Lista de alunos (campos acima)
- Metadados de paginação — a resposta deve conter campos que informem:
  - o total de registros existentes
  - o total de páginas existentes
  - a página atual retornada

#### User Interaction
- Requisição HTTP (GET) com parâmetros opcionais de paginação, ordenação e filtros
- Resposta contém a lista paginada + metadados, prontos para o front-end montar a paginação sem cálculo adicional

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Página solicitada maior que o total de páginas existentes | Retorna a última página existente (não vazio, não erro) |
| Número da página ou tamanho da página ≤ 0 | Erro de validação (requisição rejeitada) |
| Tamanho da página > 100 | Erro de validação (requisição rejeitada) |
| Campo/direção de ordenação com valor não suportado | Erro de validação (requisição rejeitada) |
| Nenhum aluno cadastrado ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |
| Nenhum filtro informado | Retorna todos os alunos (respeitando paginação e ordenação padrão) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — a tecnologia de implementação é livre.
- **Componentes Principais**: Endpoint de listagem, camada de filtros parciais/exatos, camada de paginação/ordenação no backend.
- **Armazenamento de Dados**: Tabela `tb_alunos` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados nesta rodada.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Ausência de autenticação neste momento expõe o endpoint publicamente — risco a ser endereçado em versão futura.
- **Riscos de Dependência**: Depende da tabela `tb_alunos` já existir.
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna lista de alunos com todos os campos: id, nome, email, cpf, telefone, dataNascimento, ativo, dataCriacao, dataAtualizacao
- [ ] Paginação funciona com tamanho de página default 10 e máximo 100
- [ ] Página/tamanho ≤ 0 retornam erro de validação
- [ ] Tamanho de página > 100 retorna erro de validação
- [ ] Ordenação padrão é por `nome`, ascendente
- [ ] Direção de ordenação (asc/desc) é configurável via parâmetro
- [ ] Solicitar uma página além do total existente retorna a última página válida
- [ ] Filtro parcial (contém) funciona para `nome`, `email` e `cpf`
- [ ] Filtro exato funciona para `ativo`
- [ ] `id`, `telefone`, `dataNascimento`, `dataCriacao`, `dataAtualizacao` não possuem filtro disponível
- [ ] Resposta inclui total de registros, total de páginas e página atual

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 92/100
