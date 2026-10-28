# Tabela tb_professor_titulacao (v1.1) - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: A versão 1.0 da tabela associativa não tem campo `ativo` nem datas de auditoria. Com a decisão de que a remoção de vínculo é **lógica**, a tabela precisa registrar o status do vínculo e quando ele foi criado e alterado.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem do banco de dados.
- **Proposta de Valor**: Permitir a remoção lógica de vínculos professor-titulação, preservando o histórico, e alinhar a tabela à convenção de auditoria dos demais recursos.

### Feature Overview
- **Funcionalidades Principais**: Adicionar à tabela os campos `ativo`, `dataCriacao` e `dataAtualizacao`, e revisar a regra de unicidade.
- **Limites da Feature**:
  - Não inclui endpoints — eles têm PRDs próprios (vínculo, atualização e remoção de vínculo); a consulta dos vínculos é feita pela busca de professor por id (v1.1)
  - Não altera os campos existentes (`id`, `professor_id`, `titulacao_id`, `dataInicio`, `dataTermino`)
- **Cenários de Uso Típicos**: Tarefa de infraestrutura de dados — a tabela passa a distinguir vínculos ativos de vínculos removidos.

### Detailed Requirements

#### Campos da tabela

| Campo | Obrigatório | Único | Preenchimento | Observações |
|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | Automático | Identificador único do vínculo |
| `professor_id` | Sim | Não | Informado pela aplicação | Chave estrangeira para `tb_professores` |
| `titulacao_id` | Sim | Não | Informado pela aplicação | Chave estrangeira para `tb_titulacoes` |
| `dataInicio` | Sim | Não | Informado pelo usuário | Início da titulação |
| `dataTermino` | Não | Não | Informado pelo usuário | Opcional — ausência indica titulação em andamento |
| `ativo` | Sim | Não | Automático (default `true`) | **Novo.** `false` indica vínculo removido |
| `dataCriacao` | Sim | Não | Automático | **Novo.** Imutável |
| `dataAtualizacao` | Sim | Não | Automático | **Novo.** Atualizado a cada alteração, inclusive na remoção lógica |

#### Data Requirements
- Registros já existentes recebem `ativo = true`
- Unicidade: a combinação `professor_id` + `titulacao_id` + `dataInicio` não pode se repetir **entre vínculos ativos**. Um vínculo inativo não impede recriar a mesma combinação
- Como a regra depende de `ativo`, ela é validada pela aplicação. A constraint de unicidade atual do banco (`professor_id`, `titulacao_id`, `data_inicio`), que vale também para inativos, deve ser removida
- O par `professor_id` + `titulacao_id` continua podendo se repetir com `dataInicio` diferente
- Nome físico atual da tabela no código: `tb_professor_titulacoes`

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Mesmo par professor + titulação com `dataInicio` diferente | Permitido |
| Mesma combinação professor + titulação + `dataInicio` já existente e **ativa** | Rejeitado (validação na aplicação) |
| Mesma combinação já existente, mas **inativa** | Permitido — novo vínculo é criado |
| Registros criados antes desta versão | Passam a ter `ativo = true` |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Alteração da tabela existente; o schema é gerado pelo Hibernate (`ddl-auto=update`).
- **Componentes Principais**: Entidade `ProfessorTitulacao` (novos campos e remoção da constraint de unicidade).
- **Armazenamento de Dados**: Tabela `tb_professor_titulacoes`.
- **Design de Interface**: Não aplicável.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Substitui a v1.0. Com `ddl-auto=update`, a remoção da constraint de unicidade **não** é feita automaticamente pelo Hibernate e precisa ser executada manualmente no banco.
- **Segurança**: Não aplicável.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Unicidade validada na aplicação permite condição de corrida entre duas requisições simultâneas; aceitável neste projeto de estudo.
- **Riscos de Dependência**: Depende de `tb_professores` e `tb_titulacoes`.
- **Riscos de Cronograma**: Nenhum identificado.

## Acceptance Criteria

### Functional Acceptance
- [ ] A tabela possui os campos `ativo` (default `true`), `dataCriacao` e `dataAtualizacao`
- [ ] Registros existentes ficam com `ativo = true`
- [ ] `dataCriacao` e `dataAtualizacao` são preenchidos automaticamente
- [ ] A constraint de unicidade (`professor_id`, `titulacao_id`, `data_inicio`) não existe mais no banco
- [ ] A mesma combinação professor + titulação + `dataInicio` pode ser recriada quando o vínculo anterior está inativo

---

**Document Version**: 1.1
**Created**: 2026-10-22
**Clarification Rounds**: 3
**Quality Score**: 93/100
