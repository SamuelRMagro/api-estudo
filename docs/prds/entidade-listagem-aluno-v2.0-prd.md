# Atualização da Entidade Aluno (Listagem) - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: A listagem de alunos, especificada anteriormente, assumia que `nome`, `email` e `cpf` estavam armazenados diretamente em `tb_alunos`. Com o novo modelo, esses dados vêm de `usuarios` via relacionamento (`usuario_id`), exigindo revisão da entidade exposta pelo endpoint.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que precisam listar alunos.
- **Proposta de Valor**: Manter a listagem de alunos funcional e completa, agora combinando dados de `tb_alunos` e `usuarios`, sem expor a complexidade do relacionamento ao consumidor da API.

### Feature Overview
- **Funcionalidades Principais**: Redefinir a entidade "Aluno" retornada pela API, unindo dados de `tb_alunos` (telefone, dataNascimento) e `usuarios` (nome, email, cpf, ativo, dataCriacao, dataAtualizacao).
- **Limites da Feature**:
  - Não altera o comportamento de paginação, ordenação e filtros já definidos na listagem de alunos — apenas a origem dos dados
  - Substitui a especificação de campos definida no PRD anterior de listagem de alunos
- **Cenários de Uso Típicos**:
  - Front-end lista alunos e recebe, para cada um, os dados combinados de `tb_alunos` e `usuarios`, como um único objeto

### Detailed Requirements

#### Composição da entidade "Aluno" (resposta da API)

| Campo | Origem | Descrição |
|---|---|---|
| `id` | `tb_alunos` | Identificador único do registro de aluno |
| `usuarioId` | `tb_alunos` | Identificador do usuário vinculado |
| `nome` | `usuarios` | Nome do usuário/aluno |
| `email` | `usuarios` | E-mail do usuário/aluno |
| `cpf` | `usuarios` | CPF do usuário/aluno (mascarado, conforme regra já definida em usuários) |
| `ativo` | `usuarios` | Status do usuário/aluno |
| `telefone` | `tb_alunos` | Telefone do aluno (opcional) |
| `dataNascimento` | `tb_alunos` | Data de nascimento do aluno |
| `dataCriacao` | `usuarios` | Data de criação do usuário/aluno |
| `dataAtualizacao` | `usuarios` | Data da última atualização do usuário/aluno |

#### Filtros (mantidos do PRD anterior, agora combinando fontes)

| Campo | Tipo de filtro | Origem |
|---|---|---|
| `nome` | Parcial (contém) | `usuarios` |
| `email` | Parcial (contém) | `usuarios` |
| `cpf` | Parcial (contém) | `usuarios` |
| `ativo` | Exato (true/false) | `usuarios` |

**Campos sem filtro**: `id`, `usuarioId`, `telefone`, `dataNascimento`, `dataCriacao`, `dataAtualizacao`

#### Paginação e ordenação
- Mantidos conforme já definido: paginação com tamanho default 10 e máximo 100, página além do limite retorna a última página existente, ordenação default por `nome` ascendente, direção configurável pelo cliente

#### Data Requirements
- A listagem deve realizar o relacionamento (join) entre `tb_alunos` e `usuarios` através de `usuario_id`
- Todos os campos expostos na resposta permanecem os mesmos definidos anteriormente, apenas mudando a origem interna dos dados (`tb_alunos` ou `usuarios`)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Registro em `tb_alunos` sem usuário correspondente (inconsistência de dados) | Não deve ocorrer, dado a integridade referencial definida na estrutura da tabela; fora de escopo tratar esse caso aqui |
| Nenhum aluno cadastrado ou nenhum resultado para os filtros aplicados | Lista vazia + metadados indicando zero registros e zero páginas |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase.
- **Componentes Principais**: Consulta com join entre `tb_alunos` e `usuarios`, camada de filtros, camada de paginação/ordenação.
- **Armazenamento de Dados**: Tabelas `tb_alunos` e `usuarios`, relacionadas via `usuario_id`.
- **Design de Interface**: Mantido — endpoint HTTP com parâmetros de paginação, ordenação e filtros; resposta em JSON contendo lista + metadados de paginação.

### Constraints
- **Requisitos de Performance**: Não especificados. Atenção a performance do join entre as duas tabelas conforme volume de dados crescer.
- **Compatibilidade**: Esta estrutura substitui a definida no PRD anterior de listagem de alunos.
- **Segurança**: CPF mascarado, conforme já definido para `usuarios`.
- **Escalabilidade**: Paginação limitada a tamanho de página máximo de 100.

### Risk Assessment
- **Riscos Técnicos**: Consulta com join pode impactar performance em volumes grandes — não tratado nesta fase.
- **Riscos de Dependência**: Depende da nova estrutura de `tb_alunos` (com `usuario_id`) já estar implementada.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna a entidade aluno combinando dados de `tb_alunos` e `usuarios`
- [ ] Campos `nome`, `email`, `cpf`, `ativo`, `dataCriacao`, `dataAtualizacao` vêm de `usuarios`
- [ ] Campos `telefone`, `dataNascimento`, `id`, `usuarioId` vêm de `tb_alunos`
- [ ] Filtros parciais (`nome`, `email`, `cpf`) e exato (`ativo`) continuam funcionando, agora sobre os dados de `usuarios`
- [ ] Paginação, ordenação e demais regras seguem inalteradas em relação ao PRD anterior

---

**Document Version**: 2.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 88/100
