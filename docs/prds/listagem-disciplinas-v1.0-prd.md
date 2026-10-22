# Listagem de Disciplinas - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que exponha, de forma paginada, ordenada e filtrável, as disciplinas cadastradas no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam listar disciplinas para exibição em telas administrativas ou de gestão acadêmica.
- **Proposta de Valor**: Fornecer uma listagem confiável e flexível de disciplinas, seguindo o mesmo padrão já estabelecido para cursos.

### Feature Overview
- **Funcionalidades Principais**:
  - Endpoint de listagem de disciplinas com paginação
  - Ordenação configurável (campo e direção)
  - Filtros por `nome` e `ementa` (parcial), `ativo` (exato) e `cargaHoraria` (between)
  - Metadados de paginação prontos para uso pelo front-end
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Sem filtros para `id`, `dataCriacao`, `dataAtualizacao`
  - Não inclui informações de grade curricular (relação com cursos) — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end solicita a primeira página com tamanho padrão (10 itens), ordenada por nome
  - Front-end filtra disciplinas por nome ou trecho da ementa, status ativo, ou intervalo de carga horária
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
| `ementa` | Parcial (contém) |
| `ativo` | Exato (true/false) |
| `cargaHoraria` | Intervalo (between) |

**Campos sem filtro:**
- `id`, `dataCriacao`, `dataAtualizacao`

**Campos de saída (por disciplina):**
| Campo | Descrição |
|---|---|
| `id` | Identificador único da disciplina |
| `nome` | Nome da disciplina |
| `cargaHoraria` | Carga horária da disciplina |
| `ementa` | Descrição do conteúdo da disciplina |
| `ativo` | Status (true/false) |
| `dataCriacao` | Data de criação do registro |
| `dataAtualizacao` | Data da última atualização do registro |

Todos os campos acima aparecem na resposta, inclusive `ementa`.

**Estrutura da resposta:**
- Lista de disciplinas (campos acima)
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
| Filtro de `cargaHoraria` com valor inicial maior que o valor final | Erro de validação (requisição rejeitada) |
| Nenhuma disciplina cadastrada ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |
| Nenhum filtro informado | Retorna todas as disciplinas (respeitando paginação e ordenação padrão) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — a tecnologia de implementação é livre.
- **Componentes Principais**: Endpoint de listagem, camada de filtros (parcial, exato, between), camada de paginação/ordenação no backend.
- **Armazenamento de Dados**: Tabela `tb_disciplinas` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados. Atenção ao filtro parcial em `ementa`, que pode ser um texto longo.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Ausência de autenticação expõe o endpoint publicamente — risco a ser endereçado em versão futura. Busca parcial em `ementa` pode impactar performance em volumes grandes.
- **Riscos de Dependência**: Depende da tabela `tb_disciplinas` já existir.
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna lista de disciplinas com todos os campos: id, nome, cargaHoraria, ementa, ativo, dataCriacao, dataAtualizacao
- [ ] Paginação funciona com tamanho de página default 10 e máximo 100
- [ ] Página/tamanho ≤ 0 retornam erro de validação
- [ ] Tamanho de página > 100 retorna erro de validação
- [ ] Ordenação padrão é por `nome`, ascendente
- [ ] Direção de ordenação (asc/desc) é configurável via parâmetro
- [ ] Solicitar uma página além do total existente retorna a última página válida
- [ ] Filtro parcial (contém) funciona para `nome` e `ementa`
- [ ] Filtro exato funciona para `ativo`
- [ ] Filtro *between* funciona para `cargaHoraria`
- [ ] `id`, `dataCriacao`, `dataAtualizacao` não possuem filtro disponível
- [ ] Resposta inclui total de registros, total de páginas e página atual

---

**Document Version**: 1.0
**Created**: 2026-10-13
**Clarification Rounds**: 1
**Quality Score**: 94/100
