# Tabela tb_alunos - Product Requirements Document (PRD)

> **SUBSTITUÍDO** por [`estrutura-tb-alunos-v2.0-prd.md`](estrutura-tb-alunos-v2.0-prd.md). Mantido só como histórico; não implemente a partir deste documento.

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje a estrutura de dados para armazenar alunos, necessária para suportar o sistema de matrículas em cursos.
- **Usuários-Alvo**: Equipe de backend responsável pela modelagem e criação do banco de dados.
- **Proposta de Valor**: Estrutura de dados consistente para `tb_alunos`, com regras de obrigatoriedade, unicidade e preenchimento automático bem definidas, servindo de base para os próximos endpoints (cadastro, atualização, listagem, matrícula).

### Feature Overview
- **Funcionalidades Principais**: Criar a tabela `tb_alunos` com os campos: `id`, `nome`, `email`, `cpf`, `telefone`, `dataNascimento`, `ativo`, `dataCriacao`, `dataAtualizacao`.
- **Limites da Feature**:
  - Não inclui a criação de endpoints (cadastro, atualização, listagem) — apenas a estrutura da tabela
  - Não inclui validações de formato (ex: email válido, CPF válido, telefone válido) — ficam para a demanda do endpoint de cadastro
  - Não define o SGBD a ser utilizado
- **Cenários de Uso Típicos**: Esta é uma tarefa de infraestrutura de dados — não há interação direta do usuário final; o resultado esperado é a tabela pronta para ser usada pelos próximos endpoints.

### Detailed Requirements

#### Campos da tabela

| Campo | Obrigatório | Único | Default | Preenchimento | Observações |
|---|---|---|---|---|---|
| `id` | Sim (chave primária) | Sim | — | Automático | Identificador único |
| `nome` | Sim | Não | — | Informado pelo usuário/aplicação | — |
| `email` | Sim | Sim | — | Informado pelo usuário/aplicação | Sem validação de formato nesta tarefa |
| `cpf` | Sim | Sim | — | Informado pelo usuário/aplicação | Sem validação de formato nesta tarefa |
| `telefone` | Não | Não | — | Informado pelo usuário/aplicação | Opcional |
| `dataNascimento` | Sim | Não | — | Informado pelo usuário/aplicação | Sem validação de formato/faixa nesta tarefa |
| `ativo` | Sim | Não | `true` | Informado pelo usuário/aplicação ou default | Default `true` na criação |
| `dataCriacao` | Sim | Não | — | Automático — sistema/banco define, nunca informado pelo usuário | Imutável após a criação do registro |
| `dataAtualizacao` | Não | Não | — | Automático — sistema/banco define, nunca informado pelo usuário | Atualizado automaticamente a cada alteração do registro; endpoint de atualização ainda não existe, será tratado em demanda futura |

#### Data Requirements
- `nome`, `email`, `cpf`, `dataNascimento`, `ativo`, `dataCriacao` são obrigatórios (not null)
- `telefone` é opcional
- `email` e `cpf` possuem restrição de unicidade
- `ativo` assume `true` como valor default quando não informado na criação
- `dataCriacao` não pode ser alterado após a criação do registro
- `dataCriacao` e `dataAtualizacao` nunca devem aceitar valor vindo do usuário/aplicação — o preenchimento é de responsabilidade do sistema/banco
- Validações de formato (email válido, CPF válido, telefone válido, data de nascimento válida) ficam fora de escopo desta tarefa

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Tentativa de inserir `email` ou `cpf` duplicado | Deve ser rejeitado pela restrição de unicidade (comportamento de erro será detalhado no endpoint de cadastro) |
| Tentativa de informar `dataCriacao` ou `dataAtualizacao` manualmente | Não deve ser aceito — valor sempre definido automaticamente |
| Tentativa de alterar `dataCriacao` após criação do registro | Não deve ser permitido |
| Aluno criado sem informar `ativo` | Assume `true` automaticamente |
| Aluno criado sem informar `telefone` | Permitido — campo opcional |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — SGBD e estratégia de migration ficam a critério de quem implementar.
- **Componentes Principais**: Script/migration de criação da tabela `tb_alunos`.
- **Armazenamento de Dados**: Nova tabela `tb_alunos`.
- **Design de Interface**: Não aplicável — esta tarefa não expõe interface/endpoint.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não aplicável diretamente nesta tarefa.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — criação de tabela nova, sem dados existentes a migrar.
- **Riscos de Dependência**: Os futuros endpoints de cadastro, atualização, listagem e matrícula dependem desta estrutura estar pronta antes.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Tabela `tb_alunos` criada com os campos: id, nome, email, cpf, telefone, dataNascimento, ativo, dataCriacao, dataAtualizacao
- [ ] `nome`, `email`, `cpf`, `dataNascimento`, `ativo`, `dataCriacao` são obrigatórios
- [ ] `telefone` é opcional
- [ ] `dataAtualizacao` não é obrigatório
- [ ] `ativo` possui default `true`
- [ ] `email` e `cpf` possuem restrição de unicidade
- [ ] `dataCriacao` é imutável após a criação do registro
- [ ] `dataCriacao` e `dataAtualizacao` não aceitam valor informado pelo usuário/aplicação

---

**Document Version**: 1.0
**Created**: 2026-09-25
**Clarification Rounds**: 2
**Quality Score**: 90/100
