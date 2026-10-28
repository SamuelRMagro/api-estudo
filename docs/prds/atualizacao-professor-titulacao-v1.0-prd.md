# Atualização de Vínculo Professor-Titulação - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint para corrigir o período de um vínculo entre professor e titulação, por exemplo para informar a `dataTermino` quando a titulação é concluída.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter o currículo acadêmico dos professores.
- **Proposta de Valor**: Atualizar as datas de um vínculo com as mesmas validações do vínculo inicial.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização completa das datas de um vínculo.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Só as datas são editáveis; para trocar a titulação, remove-se o vínculo e cria-se outro
  - `id`, `professorId`, `titulacaoId`, `ativo` e `dataCriacao` não são editáveis
  - Atualização completa (PUT); sem atualização parcial
- **Cenários de Uso Típicos**:
  - Front-end informa a `dataTermino` de um doutorado concluído
  - Front-end corrige a `dataInicio` registrada errado
  - Front-end tenta atualizar um vínculo de outro professor e recebe 404

### Detailed Requirements

#### Input/Output

**Parâmetros de rota:**
- `id` do professor e `vinculoId` (`/professores/{id}/titulacoes/{vinculoId}`)

**Payload de entrada (atualização completa):**

| Campo | Obrigatório | Regras |
|---|---|---|
| `dataInicio` | Sim | Não pode ser data futura |
| `dataTermino` | Não | Se informada, não pode ser anterior à `dataInicio`; pode ser futura. Se omitida, o vínculo passa a ficar sem término (em andamento) |

**Campos não editáveis (ignorados ou rejeitados):**
- `id`, `professorId`, `titulacaoId`, `ativo`, `dataCriacao`, `dataAtualizacao`

**Resposta de sucesso:**
- O vínculo atualizado, com os mesmos campos da resposta do vínculo (`cadastro-professor-titulacao-v1.0-prd.md`)

#### Data Requirements
- O vínculo deve existir e pertencer ao professor da rota
- `dataAtualizacao` é preenchida automaticamente; `dataCriacao` não muda
- Se o vínculo estiver **ativo**, a nova `dataInicio` não pode coincidir com outro vínculo ativo do mesmo professor e da mesma titulação
- Vínculo inativo pode ser atualizado; `ativo` não muda
- Atualizar vínculo de professor inativo ou de titulação inativa é permitido — a restrição de status vale só para **novos** vínculos

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Professor da rota inexistente | Erro 404 com mensagem adequada |
| `vinculoId` inexistente | Erro 404 com mensagem adequada |
| `vinculoId` existe, mas pertence a outro professor | Erro 404 |
| `dataInicio` futura | Erro de validação |
| `dataTermino` anterior à `dataInicio` | Erro de validação |
| Payload sem `dataTermino` | Permitido — `dataTermino` passa a ficar vazia |
| Payload sem `dataInicio` | Erro de validação (campo obrigatório ausente) |
| Nova `dataInicio` coincide com outro vínculo ativo do mesmo professor + titulação | Erro de validação |
| Vínculo inativo | Atualização permitida; `ativo` permanece `false` |
| Professor ou titulação inativos | Atualização permitida |
| Payload com campos não editáveis | Ignorados ou rejeitados — não alteram o registro |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Mesmo padrão das atualizações existentes (UpdateDTO validado → service → repository).
- **Componentes Principais**: Endpoint de atualização, validação de datas reaproveitada do vínculo, checagem de duplicidade entre ativos excluindo o próprio registro.
- **Armazenamento de Dados**: `tb_professor_titulacoes`.
- **Design de Interface**: `PUT /professores/{id}/titulacoes/{vinculoId}`; resposta JSON com o vínculo atualizado.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Depende da tabela v1.1.
- **Segurança**: Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: A checagem de duplicidade deve desconsiderar o próprio vínculo, senão a atualização sem mudar `dataInicio` falharia.
- **Riscos de Dependência**: `cadastro-professor-titulacao-v1.0-prd.md` (regras de datas reaproveitadas).
- **Riscos de Cronograma**: Nenhum identificado.

## Acceptance Criteria

### Functional Acceptance
- [ ] Atualiza `dataInicio` e `dataTermino` e responde com o vínculo completo atualizado
- [ ] Professor inexistente, `vinculoId` inexistente ou vínculo de outro professor retornam 404
- [ ] `dataInicio` futura ou ausente retorna erro de validação
- [ ] `dataTermino` anterior à `dataInicio` retorna erro de validação
- [ ] Omitir `dataTermino` deixa o vínculo sem término
- [ ] Nova `dataInicio` que duplica outro vínculo ativo do mesmo professor + titulação retorna erro de validação
- [ ] Atualizar sem mudar `dataInicio` não acusa duplicidade com o próprio vínculo
- [ ] Vínculo inativo pode ser atualizado e continua inativo
- [ ] `dataAtualizacao` é atualizada; `dataCriacao` permanece inalterada
- [ ] `titulacaoId`, `ativo` e demais campos não editáveis não são alterados

---

**Document Version**: 1.0
**Created**: 2026-10-22
**Clarification Rounds**: 3
**Quality Score**: 92/100
