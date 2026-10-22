# Tabela tb_professores - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje a estrutura de dados para armazenar professores, necessária para suportar o sistema de turmas e titulações.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem e criação do banco de dados.
- **Proposta de Valor**: Estrutura de dados consistente para `tb_professores`, seguindo o mesmo padrão já estabelecido para `tb_alunos` — vinculada a `usuarios` via chave estrangeira, sem duplicar dados.

### Feature Overview
- **Funcionalidades Principais**: Criar a tabela `tb_professores` com os campos: `id`, `usuario_id`, `dataAdmissao`.
- **Limites da Feature**:
  - Não inclui a criação de endpoints (cadastro, atualização, listagem) — apenas a estrutura da tabela
  - Não inclui validações de formato (ex: data de admissão válida)
  - Não define o SGBD a ser utilizado
  - `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao` do professor vêm de `usuarios` via relacionamento — não são armazenados aqui, seguindo o mesmo padrão de `tb_alunos`
- **Cenários de Uso Típicos**: Tarefa de infraestrutura de dados — o resultado esperado é a tabela pronta para suportar os próximos endpoints (cadastro, atualização, listagem, turmas, titulação).

### Detailed Requirements

#### Campos da tabela

| Campo | Obrigatório | Único | Preenchimento | Observações |
|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | Automático | Identificador único do professor |
| `usuario_id` | Sim | Sim | Informado pela aplicação | Chave estrangeira para `usuarios`; único — um usuário só pode ser professor uma vez |
| `dataAdmissao` | Sim | Não | Informado pelo usuário/aplicação | Sem validação de formato/faixa nesta tarefa |

#### Campos que NÃO existem nesta tabela (vêm de `usuarios` via relacionamento)
- `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `usuario_id`, `dataAdmissao` são obrigatórios (not null)
- `usuario_id` possui restrição de unicidade — um mesmo usuário não pode ter mais de um registro em `tb_professores`
- `usuario_id` deve referenciar um registro existente em `usuarios` (integridade referencial)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Tentativa de vincular um `usuario_id` que já possui registro em `tb_professores` | Deve ser rejeitado pela restrição de unicidade |
| Tentativa de vincular um `usuario_id` inexistente | Deve ser rejeitado pela integridade referencial |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Script/migration de criação da tabela `tb_professores`.
- **Armazenamento de Dados**: Nova tabela `tb_professores`, relacionada a `usuarios` via `usuario_id`.
- **Design de Interface**: Não aplicável — esta tarefa não expõe interface/endpoint.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não aplicável diretamente nesta tarefa.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — criação de tabela nova, sem dados existentes a migrar.
- **Riscos de Dependência**: Depende da tabela `usuarios` já existir. Os futuros endpoints de cadastro, atualização, listagem, turmas e titulação dependem desta estrutura estar pronta antes.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Tabela `tb_professores` criada com os campos: id, usuario_id, dataAdmissao
- [ ] `usuario_id` é obrigatório, único e referencia `usuarios` (FK)
- [ ] `dataAdmissao` é obrigatório
- [ ] Tentativa de vincular `usuario_id` duplicado é rejeitada
- [ ] Tentativa de vincular `usuario_id` inexistente é rejeitada

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 3
**Quality Score**: 92/100
