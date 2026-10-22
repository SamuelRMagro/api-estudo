# Atualização da Estrutura de tb_alunos - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: A tabela `tb_alunos`, especificada anteriormente com campos duplicados de `usuarios` (nome, email, cpf, ativo, dataCriacao, dataAtualizacao), precisa ser redesenhada para se relacionar com `usuarios` via chave estrangeira, evitando duplicação de dados.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem e evolução do banco de dados.
- **Proposta de Valor**: Eliminar duplicação de dados entre `usuarios` e `tb_alunos`, garantindo que um usuário possa se tornar aluno sem repetir informações já existentes.

### Feature Overview
- **Funcionalidades Principais**: Redefinir a tabela `tb_alunos` para conter apenas os dados específicos de aluno, referenciando `usuarios` por chave estrangeira.
- **Limites da Feature**:
  - Não inclui a criação de endpoints — apenas a estrutura da tabela
  - Não inclui validações de formato (ex: data de nascimento válida, telefone válido)
  - Não define o SGBD a ser utilizado
  - Substitui a estrutura de `tb_alunos` definida em PRD anterior
- **Cenários de Uso Típicos**: Tarefa de infraestrutura de dados — o resultado esperado é a tabela pronta para suportar os próximos endpoints (cadastro, atualização, listagem, matrícula).

### Detailed Requirements

#### Campos da tabela (nova estrutura)

| Campo | Obrigatório | Único | Preenchimento | Observações |
|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | Automático | Identificador único do aluno |
| `usuario_id` | Sim | Sim | Informado pela aplicação | Chave estrangeira para `usuarios`; único — um usuário só pode ser aluno uma vez |
| `telefone` | Não | Não | Informado pelo usuário/aplicação | Opcional |
| `dataNascimento` | Sim | Não | Informado pelo usuário/aplicação | Sem validação de formato/faixa nesta tarefa |

#### Campos removidos desta tabela (passam a vir de `usuarios` via relacionamento)
- `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `usuario_id`, `dataNascimento` são obrigatórios (not null)
- `usuario_id` possui restrição de unicidade — um mesmo usuário não pode ter mais de um registro em `tb_alunos`
- `usuario_id` deve referenciar um registro existente em `usuarios` (integridade referencial)
- `telefone` é opcional
- `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao` do aluno são obtidos via join com `usuarios` — não são armazenados em `tb_alunos`

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Tentativa de vincular um `usuario_id` que já possui registro em `tb_alunos` | Deve ser rejeitado pela restrição de unicidade |
| Tentativa de vincular um `usuario_id` inexistente | Deve ser rejeitado pela integridade referencial |
| Aluno criado sem informar `telefone` | Permitido — campo opcional |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Script/migration de alteração da tabela `tb_alunos` (remoção dos campos duplicados, adição de `usuario_id` como FK).
- **Armazenamento de Dados**: Tabela `tb_alunos`, relacionada a `usuarios` via `usuario_id`.
- **Design de Interface**: Não aplicável — esta tarefa não expõe interface/endpoint.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Esta estrutura substitui a definida no PRD anterior de `tb_alunos` — qualquer implementação já iniciada com base na estrutura antiga deve ser revista.
- **Segurança**: Não aplicável diretamente nesta tarefa.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo, caso a tabela ainda esteja vazia. Caso já existam dados com a estrutura antiga, será necessária migração.
- **Riscos de Dependência**: Depende da tabela `usuarios` já existir e estar populada corretamente.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Tabela `tb_alunos` contém os campos: id, usuario_id, telefone, dataNascimento
- [ ] Campos `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao` não existem mais em `tb_alunos`
- [ ] `usuario_id` é obrigatório, único e referencia `usuarios` (FK)
- [ ] `dataNascimento` é obrigatório
- [ ] `telefone` é opcional
- [ ] Tentativa de vincular `usuario_id` duplicado é rejeitada
- [ ] Tentativa de vincular `usuario_id` inexistente é rejeitada

---

**Document Version**: 2.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 90/100
