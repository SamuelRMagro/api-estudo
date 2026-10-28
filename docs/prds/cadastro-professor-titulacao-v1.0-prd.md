# Vínculo de Titulação a Professor - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint para registrar que um professor possui uma titulação, com o período correspondente.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter o currículo acadêmico dos professores.
- **Proposta de Valor**: Vincular uma titulação do catálogo a um professor, com validação de datas, de status e de duplicidade.

### Feature Overview
- **Funcionalidades Principais**: Endpoint que cria um registro em `tb_professor_titulacao` para o professor informado na rota.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Não cria titulações — a titulação deve existir no catálogo
  - Um vínculo por requisição
- **Cenários de Uso Típicos**:
  - Front-end vincula "Mestrado em Computação" a um professor, com início em 2020 e término em 2022
  - Front-end vincula um doutorado em andamento, sem `dataTermino`
  - Front-end tenta vincular uma titulação inativa e recebe erro de validação

### Detailed Requirements

#### Input/Output

**Parâmetro de rota:**
- `id` do professor (`/professores/{id}/titulacoes`)

**Payload de entrada:**

| Campo | Obrigatório | Regras |
|---|---|---|
| `titulacaoId` | Sim | Deve existir em `tb_titulacoes` e estar ativa |
| `dataInicio` | Sim | Não pode ser data futura |
| `dataTermino` | Não | Se informada, não pode ser anterior à `dataInicio`; pode ser futura (titulação em andamento) |

**Campos não aceitos no payload de entrada (ignorados ou rejeitados):**
- `id`, `professorId` (vem da rota), `ativo` (default `true`), `dataCriacao`, `dataAtualizacao`

**Resposta de sucesso:**
- O vínculo criado, com todos os seus campos: `id`, `professorId`, `titulacaoId`, `nome` e `area` (da titulação), `dataInicio`, `dataTermino`, `ativo`, `dataCriacao`, `dataAtualizacao`

#### Data Requirements
- O professor da rota deve existir e estar **ativo**
- A titulação deve existir e estar **ativa**
- Não pode existir outro vínculo **ativo** com o mesmo professor, a mesma titulação e a mesma `dataInicio`
- O mesmo par professor + titulação com `dataInicio` diferente é permitido, sem checagem de sobreposição de períodos
- `ativo` é gravado como `true`; `dataCriacao` e `dataAtualizacao` são preenchidas automaticamente

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Professor da rota inexistente | Erro 404 com mensagem adequada |
| `titulacaoId` inexistente | Erro 404 com mensagem indicando a titulação |
| Professor inativo | Erro de validação |
| Titulação inativa | Erro de validação |
| `dataInicio` futura | Erro de validação |
| `dataTermino` anterior à `dataInicio` | Erro de validação |
| `dataTermino` igual à `dataInicio` | Permitido |
| `dataTermino` futura | Permitido |
| Sem `dataTermino` | Permitido — titulação em andamento |
| Vínculo **ativo** com mesmo professor + titulação + `dataInicio` já existe | Erro de validação |
| Vínculo **inativo** com mesmo professor + titulação + `dataInicio` já existe | Permitido — cria novo vínculo |
| Mesmo professor + titulação com períodos sobrepostos e `dataInicio` diferente | Permitido |
| Payload sem `titulacaoId` ou `dataInicio` | Erro de validação (campo obrigatório ausente) |
| Payload com `id`, `professorId`, `ativo`, `dataCriacao` ou `dataAtualizacao` | Ignorados ou rejeitados — não influenciam o registro |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Mesmo padrão dos cadastros existentes (CreateDTO validado → service → repository).
- **Componentes Principais**: Endpoint de vínculo, validação de datas (Bean Validation + regra entre campos), checagens de existência/status de professor e titulação, checagem de duplicidade entre ativos.
- **Armazenamento de Dados**: `tb_professor_titulacoes`.
- **Design de Interface**: `POST /professores/{id}/titulacoes` com o payload no corpo; resposta JSON com o vínculo criado.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Depende da tabela v1.1.
- **Segurança**: Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Duplicidade validada na aplicação permite condição de corrida; aceitável neste projeto.
- **Riscos de Dependência**: `tabela-tb-professor-titulacao-v1.1-prd.md`, recursos de professor e titulação.
- **Riscos de Cronograma**: Nenhum identificado.

## Acceptance Criteria

### Functional Acceptance
- [ ] Cria o vínculo para o professor da rota e responde com o vínculo completo, incluindo `nome` e `area` da titulação
- [ ] Vínculo criado tem `ativo = true` e datas de auditoria preenchidas
- [ ] Professor inexistente retorna 404; `titulacaoId` inexistente retorna 404
- [ ] Professor inativo ou titulação inativa retorna erro de validação
- [ ] `dataInicio` futura retorna erro de validação
- [ ] `dataTermino` anterior à `dataInicio` retorna erro de validação; igual ou futura é aceita
- [ ] Vínculo sem `dataTermino` é aceito
- [ ] Duplicar professor + titulação + `dataInicio` de um vínculo ativo retorna erro de validação
- [ ] A mesma combinação é aceita quando o vínculo anterior está inativo
- [ ] Payload sem campo obrigatório retorna erro de validação
- [ ] `id`, `professorId`, `ativo`, `dataCriacao` e `dataAtualizacao` do payload não influenciam o registro

---

**Document Version**: 1.0
**Created**: 2026-10-22
**Clarification Rounds**: 3
**Quality Score**: 93/100
