# Cadastro de Disciplina - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita criar novas disciplinas no sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por cadastrar novas disciplinas.
- **Proposta de Valor**: Permitir a criação de disciplinas de forma validada e consistente, garantindo que os dados obedeçam às regras da tabela `tb_disciplinas` já definidas.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de cadastro (criação) de disciplina, recebendo `nome`, `cargaHoraria` e `ementa`, com validação de formato/faixa.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não permite informar `id`, `ativo`, `dataCriacao` ou `dataAtualizacao` — esses campos não fazem parte do payload de entrada
  - Não inclui vínculo com cursos (grade curricular) — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end envia `nome`, `cargaHoraria` e `ementa` válidos e recebe a disciplina criada como resposta
  - Front-end envia um valor fora das faixas permitidas e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Payload de entrada:**
| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `cargaHoraria` | Sim | Mínimo 10, máximo 10.000 |
| `ementa` | Sim | Entre 20 e 5.000 caracteres |

**Campos não aceitos no payload de entrada:**
- `id` — gerado automaticamente
- `ativo` — assume `true` por default, conforme definido na estrutura da tabela
- `dataCriacao` / `dataAtualizacao` — preenchidos automaticamente pelo sistema/banco

**Resposta de sucesso:**
- A entidade completa salva no banco, com todos os campos: `id`, `nome`, `cargaHoraria`, `ementa`, `ativo`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- `nome`, `cargaHoraria` e `ementa` são obrigatórios no payload
- Valores fora das faixas definidas devem ser rejeitados com erro de validação
- `nome` não possui restrição de unicidade — disciplinas com o mesmo nome podem ser cadastradas
- Segue as regras já definidas para a tabela `tb_disciplinas` (obrigatoriedade, default de `ativo`, preenchimento automático de datas, imutabilidade de `dataCriacao`)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `nome` com menos de 5 ou mais de 255 caracteres | Erro de validação |
| `cargaHoraria` menor que 10 ou maior que 10.000 | Erro de validação |
| `ementa` com menos de 20 ou mais de 5.000 caracteres | Erro de validação |
| Payload sem `nome`, `cargaHoraria` ou `ementa` | Erro de validação (campo obrigatório ausente) |
| Disciplina com `nome` igual ao de outra já cadastrada | Permitido — não há restrição de unicidade |
| Payload contendo `id`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro criado |
| Cadastro com sucesso | Retorna a entidade completa salva, incluindo `id`, `ativo` (`true`) e datas geradas automaticamente |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de cadastro, camada de validação de entrada, persistência na tabela `tb_disciplinas`.
- **Armazenamento de Dados**: Tabela `tb_disciplinas` (já especificada em PRD anterior).
- **Design de Interface**: Endpoint HTTP (POST) recebendo `nome`, `cargaHoraria` e `ementa` no corpo da requisição, respondendo com a entidade criada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação simples de criação com validação de faixa.
- **Riscos de Dependência**: Depende da tabela `tb_disciplinas` já existir com as regras definidas.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome`, `cargaHoraria` e `ementa` no payload de entrada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `cargaHoraria` fora do intervalo 10–10.000 retorna erro de validação
- [ ] `ementa` fora do intervalo 20–5.000 caracteres retorna erro de validação
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Disciplinas com o mesmo `nome` podem ser cadastradas
- [ ] Campos `id`, `ativo`, `dataCriacao`, `dataAtualizacao` não são aceitos como entrada
- [ ] Resposta de sucesso retorna a entidade completa salva, com todos os campos

---

**Document Version**: 1.0
**Created**: 2026-10-13
**Clarification Rounds**: 2
**Quality Score**: 95/100
