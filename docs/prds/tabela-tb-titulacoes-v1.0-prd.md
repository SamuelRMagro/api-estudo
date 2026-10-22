# Tabela tb_titulacoes - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje a estrutura de dados para armazenar titulações acadêmicas (ex: mestrado, doutorado em determinada área), necessária para vincular professores às suas qualificações.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem e criação do banco de dados.
- **Proposta de Valor**: Estrutura de dados que funciona como um catálogo reutilizável de titulações, podendo ser associada a múltiplos professores.

### Feature Overview
- **Funcionalidades Principais**: Criar a tabela `tb_titulacoes` com os campos: `id`, `nome`, `area`.
- **Limites da Feature**:
  - Não inclui a criação de endpoints (cadastro, atualização, listagem) — apenas a estrutura da tabela
  - Não inclui validações de formato/tamanho
  - Não define o SGBD a ser utilizado
  - Não inclui a tabela associativa entre professor e titulação (tratada em PRD separado)
- **Cenários de Uso Típicos**: Tarefa de infraestrutura de dados — a tabela funciona como catálogo, reutilizável por múltiplos professores via a tabela associativa `tb_professor_titulacao`.

### Detailed Requirements

#### Campos da tabela

| Campo | Obrigatório | Único | Preenchimento | Observações |
|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | Automático | Identificador único da titulação |
| `nome` | Sim | Não | Informado pelo usuário/aplicação | Nome do curso/titulação (ex: "Doutorado em Ciência da Computação") — sem restrição de unicidade |
| `area` | Sim | Não | Informado pelo usuário/aplicação | Área de especialização — sem restrição de unicidade |

#### Data Requirements
- `nome` e `area` são obrigatórios (not null)
- Nenhum campo possui restrição de unicidade, exceto `id`
- A tabela funciona como catálogo — a mesma titulação (mesmo `nome`/`area`) pode ser cadastrada mais de uma vez, se necessário

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Duas titulações com o mesmo `nome` e `area` | Permitido — não há restrição de unicidade |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Script/migration de criação da tabela `tb_titulacoes`.
- **Armazenamento de Dados**: Nova tabela `tb_titulacoes`.
- **Design de Interface**: Não aplicável — esta tarefa não expõe interface/endpoint.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não aplicável diretamente nesta tarefa.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — criação de tabela nova, sem dados existentes a migrar.
- **Riscos de Dependência**: A tabela associativa `tb_professor_titulacao` depende desta estrutura estar pronta antes.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Tabela `tb_titulacoes` criada com os campos: id, nome, area
- [ ] `nome` e `area` são obrigatórios
- [ ] Nenhuma restrição de unicidade além de `id`

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 3
**Quality Score**: 92/100
