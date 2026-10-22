# Cursos (Tabela + Listagem) - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje a estrutura de dados para armazenar cursos, nem um endpoint que exponha, de forma paginada, ordenada e filtrável, os cursos cadastrados no sistema.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem do banco de dados e consumidores internos do sistema (front-end/outros serviços) que precisam listar cursos para exibição em telas administrativas ou de gestão acadêmica.
- **Proposta de Valor**: Estrutura de dados consistente para `tb_cursos`, somada a uma listagem confiável e flexível, com filtros, ordenação e paginação padronizados, pronta para ser consumida diretamente pelo front-end.

### Feature Overview
- **Funcionalidades Principais**:
  - Criação da tabela `tb_cursos` com regras de obrigatoriedade, unicidade e preenchimento automático
  - Endpoint de listagem de cursos com paginação
  - Ordenação configurável (campo e direção)
  - Filtros por diversos campos, incluindo filtros de intervalo (*between*)
  - Metadados de paginação prontos para uso pelo front-end
- **Limites da Feature**:
  - Não inclui a criação de outros endpoints (cadastro, atualização) — apenas a estrutura da tabela e a listagem
  - Não inclui validações de formato/faixa (ex: valor mínimo de carga horária) — ficam para a demanda do endpoint de cadastro, se necessário
  - Não define o SGBD a ser utilizado
  - Sem autenticação/autorização nesta versão
- **Cenários de Uso Típicos**:
  - Front-end solicita a primeira página com tamanho padrão (10 itens), ordenada por nome
  - Front-end filtra cursos por nome (parcial), por status ativo, ou por intervalo de carga horária/data de criação
  - Front-end solicita uma página além do total existente e recebe a última página válida

### Detailed Requirements

#### Campos da tabela `tb_cursos`

| Campo | Obrigatório | Único | Default | Preenchimento | Observações |
|---|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | — | Automático | Identificador único |
| `nome` | Sim | Não | — | Informado pelo usuário/aplicação | Sem restrição de unicidade |
| `cargaHoraria` | Sim | Não | — | Informado pelo usuário/aplicação | — |
| `duracao` | Sim | Não | — | Informado pelo usuário/aplicação | — |
| `ativo` | Sim | Não | `true` | Informado pelo usuário/aplicação ou default | Default `true` na criação |
| `dataCriacao` | Sim | Não | — | Automático — sistema/banco define, nunca informado pelo usuário | Imutável após a criação do registro |
| `dataAtualizacao` | Não | Não | — | Automático — sistema/banco define, nunca informado pelo usuário | Atualizado automaticamente a cada alteração do registro; endpoint de atualização ainda não existe, será tratado em demanda futura |

**Data Requirements (tabela):**
- `nome`, `cargaHoraria`, `duracao`, `ativo`, `dataCriacao` são obrigatórios (not null)
- Nenhum campo possui restrição de unicidade, exceto `id`
- `ativo` assume `true` como valor default quando não informado na criação
- `dataCriacao` não pode ser alterado após a criação do registro
- `dataCriacao` e `dataAtualizacao` nunca devem aceitar valor vindo do usuário/aplicação
- Validações de formato/faixa (ex: carga horária mínima, duração válida) ficam fora de escopo

#### Input/Output do endpoint de listagem

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
| `ativo` | Exato (true/false) |
| `cargaHoraria` | Intervalo (between) |
| `duracao` | Intervalo (between) |
| `dataCriacao` | Intervalo (between) |

**Campos sem filtro:**
- `id`
- `dataAtualizacao`

**Campos de saída (por curso):**
| Campo | Descrição |
|---|---|
| `id` | Identificador único do curso |
| `nome` | Nome do curso |
| `cargaHoraria` | Carga horária total do curso |
| `duracao` | Duração do curso |
| `ativo` | Se o curso está sendo ofertado atualmente |
| `dataCriacao` | Data de criação do registro |
| `dataAtualizacao` | Data da última atualização do registro |

Todos os campos acima aparecem na resposta — não há campo oculto/excluído nesta listagem.

**Estrutura da resposta:**
- Lista de cursos (campos acima)
- Metadados de paginação — a resposta deve conter campos que informem:
  - o total de registros existentes
  - o total de páginas existentes
  - a página atual retornada

#### User Interaction
- Requisição HTTP (GET) com parâmetros opcionais de paginação, ordenação e filtros
- Resposta contém a lista paginada + metadados, prontos para o front-end montar a paginação sem cálculo adicional

#### Edge Cases

**Tabela:**
| Cenário | Comportamento esperado |
|---|---|
| Tentativa de informar `dataCriacao` ou `dataAtualizacao` manualmente | Não deve ser aceito — valor sempre definido automaticamente |
| Tentativa de alterar `dataCriacao` após criação do registro | Não deve ser permitido |
| Curso criado sem informar `ativo` | Assume `true` automaticamente |
| Dois cursos com o mesmo `nome` | Permitido — não há restrição de unicidade em `nome` |

**Listagem:**
| Cenário | Comportamento esperado |
|---|---|
| Página solicitada maior que o total de páginas existentes | Retorna a última página existente (não vazio, não erro) |
| Número da página ou tamanho da página ≤ 0 | Erro de validação (requisição rejeitada) |
| Tamanho da página > 100 | Erro de validação (requisição rejeitada) |
| Campo/direção de ordenação com valor não suportado | Erro de validação (requisição rejeitada) |
| Filtro *between* com valor inicial maior que o valor final | Erro de validação (requisição rejeitada) |
| Nenhum curso cadastrado ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |
| Nenhum filtro informado | Retorna todos os cursos (respeitando paginação e ordenação padrão) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — SGBD, estratégia de migration e tecnologia de implementação ficam a critério de quem implementar.
- **Componentes Principais**: Script/migration de criação da tabela `tb_cursos`; endpoint de listagem; camada de filtros (incluindo *between*); camada de paginação/ordenação no backend.
- **Armazenamento de Dados**: Nova tabela `tb_cursos`.
- **Design de Interface**: Endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados nesta rodada.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Baixo — criação de tabela nova, sem dados existentes a migrar. Ausência de autenticação expõe o endpoint publicamente — risco a ser endereçado em versão futura.
- **Riscos de Dependência**: Os futuros endpoints de cadastro/atualização dependem desta estrutura de tabela estar pronta antes.
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance

**Tabela:**
- [ ] Tabela `tb_cursos` criada com os campos: id, nome, cargaHoraria, duracao, ativo, dataCriacao, dataAtualizacao
- [ ] `nome`, `cargaHoraria`, `duracao`, `ativo`, `dataCriacao` são obrigatórios
- [ ] `dataAtualizacao` não é obrigatório
- [ ] `ativo` possui default `true`
- [ ] `nome` não possui restrição de unicidade
- [ ] `dataCriacao` é imutável após a criação do registro
- [ ] `dataCriacao` e `dataAtualizacao` não aceitam valor informado pelo usuário/aplicação

**Listagem:**
- [ ] Endpoint retorna lista de cursos com todos os campos: id, nome, cargaHoraria, duracao, ativo, dataCriacao, dataAtualizacao
- [ ] Paginação funciona com tamanho de página default 10 e máximo 100
- [ ] Página/tamanho ≤ 0 retornam erro de validação
- [ ] Tamanho de página > 100 retorna erro de validação
- [ ] Ordenação padrão é por `nome`, ascendente
- [ ] Direção de ordenação (asc/desc) é configurável via parâmetro
- [ ] Solicitar uma página além do total existente retorna a última página válida
- [ ] Filtro parcial (contém) funciona para `nome`
- [ ] Filtro exato funciona para `ativo`
- [ ] Filtro *between* funciona para `cargaHoraria`, `duracao` e `dataCriacao`
- [ ] `id` e `dataAtualizacao` não possuem filtro disponível
- [ ] Resposta inclui total de registros, total de páginas e página atual

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Quality Score**: 90/100
