# Listagem de Professores - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que exponha, de forma paginada, ordenada e filtrável, os professores cadastrados no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam listar professores para exibição em telas administrativas ou de gestão acadêmica.
- **Proposta de Valor**: Fornecer uma listagem confiável e flexível de professores, combinando dados de `tb_professores` e `usuarios`, seguindo o mesmo padrão já estabelecido para cursos e alunos.

### Feature Overview
- **Funcionalidades Principais**:
  - Endpoint de listagem de professores com paginação
  - Ordenação configurável (campo e direção)
  - Filtros por `nome`, `email`, `cpf` (parcial), `ativo` (exato) e `dataAdmissao` (between)
  - Metadados de paginação prontos para uso pelo front-end
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Sem filtros para `id`, `usuarioId`, `dataCriacao`, `dataAtualizacao`
  - Não inclui as titulações do professor nesta listagem — tratado em endpoint/recurso separado
- **Cenários de Uso Típicos**:
  - Front-end solicita a primeira página com tamanho padrão (10 itens), ordenada por nome
  - Front-end filtra professores por nome/email/cpf (parcial), status ativo, ou intervalo de data de admissão
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

| Campo | Tipo de filtro | Origem |
|---|---|---|
| `nome` | Parcial (contém) | `usuarios` |
| `email` | Parcial (contém) | `usuarios` |
| `cpf` | Parcial (contém) | `usuarios` |
| `ativo` | Exato (true/false) | `usuarios` |
| `dataAdmissao` | Intervalo (between) | `tb_professores` |

**Campos sem filtro:**
- `id`, `usuarioId`, `dataCriacao`, `dataAtualizacao`

**Campos de saída (por professor):**
| Campo | Origem | Descrição |
|---|---|---|
| `id` | `tb_professores` | Identificador único do registro de professor |
| `usuarioId` | `tb_professores` | Identificador do usuário vinculado |
| `nome` | `usuarios` | Nome do usuário/professor |
| `email` | `usuarios` | E-mail do usuário/professor |
| `cpf` | `usuarios` | CPF do usuário/professor (mascarado, conforme regra já definida em usuários) |
| `ativo` | `usuarios` | Status do usuário/professor |
| `dataAdmissao` | `tb_professores` | Data de admissão do professor |
| `dataCriacao` | `usuarios` | Data de criação do usuário/professor |
| `dataAtualizacao` | `usuarios` | Data da última atualização do usuário/professor |

Todos os campos acima aparecem na resposta.

**Estrutura da resposta:**
- Lista de professores (campos acima)
- Metadados de paginação — a resposta deve conter campos que informem:
  - o total de registros existentes
  - o total de páginas existentes
  - a página atual retornada

#### User Interaction
- Requisição HTTP (GET) com parâmetros opcionais de paginação, ordenação e filtros
- Resposta contém a lista paginada + metadados, prontos para o front-end montar a paginação sem cálculo adicional

#### Data Requirements
- A listagem deve realizar o relacionamento (join) entre `tb_professores` e `usuarios` através de `usuario_id`

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Página solicitada maior que o total de páginas existentes | Retorna a última página existente (não vazio, não erro) |
| Número da página ou tamanho da página ≤ 0 | Erro de validação (requisição rejeitada) |
| Tamanho da página > 100 | Erro de validação (requisição rejeitada) |
| Campo/direção de ordenação com valor não suportado | Erro de validação (requisição rejeitada) |
| Filtro de `dataAdmissao` com valor inicial maior que o valor final | Erro de validação (requisição rejeitada) |
| Nenhum professor cadastrado ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |
| Nenhum filtro informado | Retorna todos os professores (respeitando paginação e ordenação padrão) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Consulta com join entre `tb_professores` e `usuarios`, camada de filtros (parcial, exato, between), camada de paginação/ordenação.
- **Armazenamento de Dados**: Tabelas `tb_professores` e `usuarios`, relacionadas via `usuario_id`.
- **Design de Interface**: Endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados. Atenção a performance do join conforme volume de dados crescer.
- **Compatibilidade**: Não especificada.
- **Segurança**: CPF mascarado, conforme já definido para `usuarios`. Sem autenticação nesta versão.
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Ausência de autenticação expõe o endpoint publicamente — risco a ser endereçado em versão futura. Join pode impactar performance em volumes grandes.
- **Riscos de Dependência**: Depende das tabelas `tb_professores` e `usuarios` já existirem.
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna lista de professores com todos os campos: id, usuarioId, nome, email, cpf, ativo, dataAdmissao, dataCriacao, dataAtualizacao
- [ ] Paginação funciona com tamanho de página default 10 e máximo 100
- [ ] Página/tamanho ≤ 0 retornam erro de validação
- [ ] Tamanho de página > 100 retorna erro de validação
- [ ] Ordenação padrão é por `nome`, ascendente
- [ ] Direção de ordenação (asc/desc) é configurável via parâmetro
- [ ] Solicitar uma página além do total existente retorna a última página válida
- [ ] Filtro parcial (contém) funciona para `nome`, `email` e `cpf`
- [ ] Filtro exato funciona para `ativo`
- [ ] Filtro *between* funciona para `dataAdmissao`
- [ ] `id`, `usuarioId`, `dataCriacao`, `dataAtualizacao` não possuem filtro disponível
- [ ] Resposta inclui total de registros, total de páginas e página atual

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 95/100
