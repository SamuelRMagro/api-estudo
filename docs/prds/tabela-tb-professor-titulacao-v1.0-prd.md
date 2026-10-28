# Tabela tb_professor_titulacao - Product Requirements Document (PRD)

> **SUBSTITUÍDO** por [`tabela-tb-professor-titulacao-v1.1-prd.md`](tabela-tb-professor-titulacao-v1.1-prd.md) (adiciona `ativo` e datas de auditoria; unicidade só entre vínculos ativos).

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje a estrutura de dados que relacione professores às suas titulações, considerando que um professor pode ter várias titulações e uma titulação (catálogo) pode ser reaproveitada por vários professores.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem e criação do banco de dados.
- **Proposta de Valor**: Estrutura de relacionamento N:N entre `tb_professores` e `tb_titulacoes`, registrando o período em que cada professor cursou/possui cada titulação.

### Feature Overview
- **Funcionalidades Principais**: Criar a tabela associativa `tb_professor_titulacao` com os campos: `id`, `professor_id`, `titulacao_id`, `dataInicio`, `dataTermino`.
- **Limites da Feature**:
  - Não inclui a criação de endpoints (vincular, listar, remover vínculo) — apenas a estrutura da tabela
  - Não inclui validações de formato/faixa de datas
  - Não define o SGBD a ser utilizado
- **Cenários de Uso Típicos**: Tarefa de infraestrutura de dados — a tabela permite consultar quais titulações um professor possui, e quais professores possuem determinada titulação.

### Detailed Requirements

#### Campos da tabela

| Campo | Obrigatório | Único | Preenchimento | Observações |
|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | Automático | Identificador único do vínculo |
| `professor_id` | Sim | Não | Informado pela aplicação | Chave estrangeira para `tb_professores` |
| `titulacao_id` | Sim | Não | Informado pela aplicação | Chave estrangeira para `tb_titulacoes` |
| `dataInicio` | Sim | Não | Informado pelo usuário/aplicação | Início da titulação |
| `dataTermino` | Não | Não | Informado pelo usuário/aplicação | Opcional — ausência indica titulação em andamento |

#### Data Requirements
- `professor_id`, `titulacao_id`, `dataInicio` são obrigatórios (not null)
- `dataTermino` é opcional
- `professor_id` deve referenciar um registro existente em `tb_professores` (integridade referencial)
- `titulacao_id` deve referenciar um registro existente em `tb_titulacoes` (integridade referencial)
- O par `professor_id` + `titulacao_id` pode se repetir — não há restrição de unicidade sobre essa combinação (ex: professor pode ter cursado a mesma titulação em períodos diferentes)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Mesmo par `professor_id` + `titulacao_id` cadastrado mais de uma vez | Permitido — sem restrição de unicidade |
| Vínculo sem `dataTermino` | Permitido — indica titulação em andamento |
| Tentativa de vincular `professor_id` inexistente | Deve ser rejeitado pela integridade referencial |
| Tentativa de vincular `titulacao_id` inexistente | Deve ser rejeitado pela integridade referencial |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Script/migration de criação da tabela `tb_professor_titulacao`.
- **Armazenamento de Dados**: Nova tabela associativa, relacionando `tb_professores` e `tb_titulacoes`.
- **Design de Interface**: Não aplicável — esta tarefa não expõe interface/endpoint.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não aplicável diretamente nesta tarefa.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — criação de tabela nova, sem dados existentes a migrar.
- **Riscos de Dependência**: Depende de `tb_professores` e `tb_titulacoes` já existirem.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Tabela `tb_professor_titulacao` criada com os campos: id, professor_id, titulacao_id, dataInicio, dataTermino
- [ ] `professor_id`, `titulacao_id`, `dataInicio` são obrigatórios
- [ ] `dataTermino` é opcional
- [ ] `professor_id` e `titulacao_id` respeitam integridade referencial
- [ ] O mesmo par `professor_id` + `titulacao_id` pode se repetir

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 3
**Quality Score**: 92/100
