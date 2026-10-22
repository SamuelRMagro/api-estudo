# Listagem de Titulações - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que exponha, de forma paginada, ordenada e filtrável, o catálogo de titulações cadastradas no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam listar titulações, por exemplo para vinculá-las a professores.
- **Proposta de Valor**: Fornecer uma listagem confiável do catálogo de titulações, seguindo o mesmo padrão já estabelecido para os demais recursos.

### Feature Overview
- **Funcionalidades Principais**:
  - Endpoint de listagem de titulações com paginação
  - Ordenação configurável (campo e direção)
  - Filtros parciais por `nome` e `area`
  - Metadados de paginação prontos para uso pelo front-end
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Sem filtro por `ativo` nesta versão — a listagem retorna titulações ativas e inativas
  - Sem filtro para `id`
  - Não inclui os professores vinculados a cada titulação — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end solicita a primeira página com tamanho padrão (10 itens), ordenada por nome
  - Front-end filtra titulações por trecho do nome ou da área
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
| `area` | Parcial (contém) |

**Campos sem filtro:**
- `id`, `ativo`

**Campos de saída (por titulação):**
| Campo | Descrição |
|---|---|
| `id` | Identificador único da titulação |
| `nome` | Nome do curso/titulação |
| `area` | Área de especialização |
| `ativo` | Status (true/false) |

Todos os campos acima aparecem na resposta.

**Estrutura da resposta:**
- Lista de titulações (campos acima)
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
| Nenhuma titulação cadastrada ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |
| Nenhum filtro informado | Retorna todas as titulações (ativas e inativas), respeitando paginação e ordenação padrão |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — a tecnologia de implementação é livre.
- **Componentes Principais**: Endpoint de listagem, camada de filtros parciais, camada de paginação/ordenação no backend.
- **Armazenamento de Dados**: Tabela `tb_titulacoes` (PRD v1.1, já com o campo `ativo`).
- **Design de Interface**: Endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Ausência de autenticação expõe o endpoint publicamente — risco a ser endereçado em versão futura. Sem filtro por `ativo`, o front-end precisa tratar titulações inativas por conta própria.
- **Riscos de Dependência**: Depende da tabela `tb_titulacoes` (v1.1) já existir.
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna lista de titulações com todos os campos: id, nome, area, ativo
- [ ] Paginação funciona com tamanho de página default 10 e máximo 100
- [ ] Página/tamanho ≤ 0 retornam erro de validação
- [ ] Tamanho de página > 100 retorna erro de validação
- [ ] Ordenação padrão é por `nome`, ascendente
- [ ] Direção de ordenação (asc/desc) é configurável via parâmetro
- [ ] Solicitar uma página além do total existente retorna a última página válida
- [ ] Filtro parcial (contém) funciona para `nome` e `area`
- [ ] `id` e `ativo` não possuem filtro disponível
- [ ] Titulações ativas e inativas aparecem na listagem
- [ ] Resposta inclui total de registros, total de páginas e página atual

---

**Document Version**: 1.0
**Created**: 2026-10-19
**Clarification Rounds**: 1
**Quality Score**: 93/100
