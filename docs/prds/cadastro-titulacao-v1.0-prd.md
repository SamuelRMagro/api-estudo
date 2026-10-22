# Cadastro de Titulação - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita criar novas titulações no catálogo do sistema.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por cadastrar novas titulações.
- **Proposta de Valor**: Permitir a criação de titulações de forma validada e consistente, garantindo que os dados obedeçam às regras da tabela `tb_titulacoes` já definidas.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de cadastro (criação) de titulação, recebendo `nome` e `area`, com validação de tamanho.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não permite informar `id` ou `ativo` — esses campos não fazem parte do payload de entrada
  - Não inclui o vínculo da titulação com professores — tratado em recurso separado
- **Cenários de Uso Típicos**:
  - Front-end envia `nome` e `area` válidos e recebe a titulação criada como resposta
  - Front-end envia um valor fora das faixas permitidas e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Payload de entrada:**
| Campo | Obrigatório | Regras |
|---|---|---|
| `nome` | Sim | Entre 5 e 255 caracteres |
| `area` | Sim | Entre 3 e 100 caracteres |

**Campos não aceitos no payload de entrada:**
- `id` — gerado automaticamente
- `ativo` — assume `true` por default, conforme definido na estrutura da tabela

**Resposta de sucesso:**
- A entidade completa salva no banco, com todos os campos: `id`, `nome`, `area`, `ativo`

#### Data Requirements
- `nome` e `area` são obrigatórios no payload
- Valores fora das faixas definidas devem ser rejeitados com erro de validação
- Não há restrição de unicidade: titulações com o mesmo `nome` e `area` podem ser cadastradas mais de uma vez
- Segue as regras já definidas para a tabela `tb_titulacoes` (obrigatoriedade, default de `ativo`)

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `nome` com menos de 5 ou mais de 255 caracteres | Erro de validação |
| `area` com menos de 3 ou mais de 100 caracteres | Erro de validação |
| Payload sem `nome` ou `area` | Erro de validação (campo obrigatório ausente) |
| Titulação com `nome` e `area` iguais aos de outra já cadastrada | Permitido — não há restrição de unicidade |
| Payload contendo `id` ou `ativo` | Esses campos devem ser ignorados ou rejeitados — não podem influenciar o registro criado |
| Cadastro com sucesso | Retorna a entidade completa salva, incluindo `id` e `ativo` (`true`) |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia de implementação livre.
- **Componentes Principais**: Endpoint de cadastro, camada de validação de entrada, persistência na tabela `tb_titulacoes`.
- **Armazenamento de Dados**: Tabela `tb_titulacoes` (PRD v1.1, já com o campo `ativo`).
- **Design de Interface**: Endpoint HTTP (POST) recebendo `nome` e `area` no corpo da requisição, respondendo com a entidade criada em JSON.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Não especificada nesta versão (autenticação a ser tratada futuramente).
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação simples de criação com validação de tamanho. Sem unicidade, o catálogo pode acumular titulações duplicadas.
- **Riscos de Dependência**: Depende da tabela `tb_titulacoes` (v1.1) já existir com as regras definidas.
- **Riscos de Cronograma**: Nenhum identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint aceita `nome` e `area` no payload de entrada
- [ ] `nome` fora do intervalo 5–255 caracteres retorna erro de validação
- [ ] `area` fora do intervalo 3–100 caracteres retorna erro de validação
- [ ] Payload sem algum campo obrigatório retorna erro de validação
- [ ] Titulações com o mesmo `nome` e `area` podem ser cadastradas
- [ ] Campos `id` e `ativo` não são aceitos como entrada
- [ ] Resposta de sucesso retorna a entidade completa salva, com todos os campos

---

**Document Version**: 1.0
**Created**: 2026-10-19
**Clarification Rounds**: 1
**Quality Score**: 90/100
