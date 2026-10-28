# Remoção Lógica de Vínculo Professor-Titulação - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint para desfazer o vínculo entre um professor e uma titulação registrado por engano ou que deixou de valer.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter o currículo acadêmico dos professores.
- **Proposta de Valor**: Remover o vínculo preservando o histórico, seguindo o padrão de exclusão lógica dos demais recursos.

### Feature Overview
- **Funcionalidades Principais**: Endpoint que marca o vínculo como inativo (`ativo = false`).
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Exclusão lógica — nenhum registro é removido; professor e titulação não são alterados
  - Operação idempotente
  - Não há reativação de vínculo nesta versão; para restaurar, cria-se um novo vínculo
- **Cenários de Uso Típicos**:
  - Front-end remove um vínculo e recebe 204
  - Front-end repete a remoção e recebe 204 novamente
  - Front-end tenta remover um vínculo de outro professor e recebe 404

### Detailed Requirements

#### Input/Output

**Parâmetros de rota:**
- `id` do professor e `vinculoId` (`/professores/{id}/titulacoes/{vinculoId}`)

**Resposta de sucesso:**
- Status 204, sem corpo

#### Data Requirements
- O vínculo deve existir e pertencer ao professor da rota
- `ativo` do vínculo passa a `false` e `dataAtualizacao` é atualizada; `dataCriacao` não muda
- Um vínculo inativo libera a combinação professor + titulação + `dataInicio` para um novo vínculo
- Remover vínculo de professor inativo ou de titulação inativa é permitido

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Professor da rota inexistente | Erro 404 com mensagem adequada |
| `vinculoId` inexistente | Erro 404 com mensagem adequada |
| `vinculoId` existe, mas pertence a outro professor | Erro 404 |
| Vínculo ativo | Marcado como `ativo = false`; resposta 204 |
| Vínculo já inativo | Idempotente — resposta 204, sem erro e sem alterar `dataAtualizacao` |
| Professor ou titulação inativos | Remoção permitida |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Mesmo padrão das inativações existentes (service busca, marca inativo e salva).
- **Componentes Principais**: Endpoint de remoção lógica, busca do vínculo filtrando pelo professor da rota.
- **Armazenamento de Dados**: `tb_professor_titulacoes`.
- **Design de Interface**: Seguir o verbo usado nas inativações existentes do projeto (ex.: `PATCH /professores/{id}/titulacoes/{vinculoId}/inativar`), respondendo 204.

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Depende da tabela v1.1.
- **Segurança**: Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Baixo — operação reversível por novo vínculo.
- **Riscos de Dependência**: `tabela-tb-professor-titulacao-v1.1-prd.md`.
- **Riscos de Cronograma**: Nenhum identificado.

## Acceptance Criteria

### Functional Acceptance
- [ ] Marca o vínculo como `ativo = false` e responde 204 sem corpo
- [ ] Professor inexistente, `vinculoId` inexistente ou vínculo de outro professor retornam 404
- [ ] Remover vínculo já inativo retorna 204 (idempotente)
- [ ] `dataAtualizacao` é atualizada na desativação; `dataCriacao` permanece inalterada
- [ ] Nenhum registro é apagado; professor e titulação não são alterados
- [ ] Depois da remoção, a mesma combinação professor + titulação + `dataInicio` pode ser vinculada de novo

---

**Document Version**: 1.0
**Created**: 2026-10-22
**Clarification Rounds**: 3
**Quality Score**: 94/100
