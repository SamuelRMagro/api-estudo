# Busca de Professor por ID (v1.1) - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Os endpoints de atualização e remoção de vínculo professor-titulação recebem o `id` do vínculo na rota, mas não há como o cliente consultar os vínculos de um professor. Um endpoint de listagem próprio seria complexidade desnecessária, já que um professor tem poucas titulações.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) que exibem os detalhes de um professor e mantêm suas titulações.
- **Proposta de Valor**: Retornar, na própria busca do professor, as titulações vinculadas a ele, com o `id` do vínculo e o período de cada uma.

### Feature Overview
- **Funcionalidades Principais**: A resposta da busca de professor por `id` passa a incluir a lista `titulacoes`, com os vínculos de `tb_professor_titulacao`.
- **Limites da Feature**:
  - Sem autenticação/autorização nesta versão
  - Apenas consulta — vincular, atualizar e remover vínculos têm PRDs próprios
  - Não há paginação nem filtros na lista de titulações
  - Só a busca por `id` traz as titulações; a listagem de professores não muda
- **Cenários de Uso Típicos**:
  - Front-end exibe os detalhes de um professor com suas titulações e períodos
  - Front-end obtém o `id` de um vínculo para atualizar a `dataTermino` ou removê-lo
  - Front-end busca um professor sem titulações e recebe a lista vazia

### Detailed Requirements

#### Input/Output

**Parâmetro de entrada:**
- `id` do professor (via path)

**Campos de saída:**

| Campo | Origem |
|---|---|
| `id` | `tb_professores` |
| `usuarioId` | `tb_professores` |
| `nome` | `usuarios` |
| `email` | `usuarios` |
| `cpf` | `usuarios` (mascarado) |
| `ativo` | `usuarios` |
| `dataAdmissao` | `tb_professores` |
| `dataCriacao` | `usuarios` |
| `dataAtualizacao` | `usuarios` |
| `titulacoes` | **Novo.** Lista de vínculos (ver abaixo) |

**Campos de cada item de `titulacoes`:**

| Campo | Origem | Observação |
|---|---|---|
| `id` | vínculo | Usado nas rotas de atualização e remoção do vínculo |
| `titulacaoId` | vínculo | `id` da titulação no catálogo |
| `nome` | titulação | |
| `area` | titulação | |
| `dataInicio` | vínculo | |
| `dataTermino` | vínculo | Nulo quando a titulação está em andamento |
| `ativo` | vínculo | `false` indica vínculo removido |

#### Data Requirements
- A lista traz todos os vínculos do professor, ativos e inativos, sem filtro automático (convenção do projeto); o cliente distingue pelo `ativo` do vínculo
- Ordenação da lista: `dataInicio` descendente
- O `ativo` do item é o do **vínculo**, não o da titulação no catálogo
- Professor inativo é retornado normalmente, com seus vínculos
- Titulação inativa no catálogo aparece normalmente nos vínculos existentes

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| `id` não corresponde a nenhum professor existente | Erro 404 com mensagem adequada |
| Professor inativo | Retornado normalmente, com seus vínculos |
| Professor sem vínculos | `titulacoes` é uma lista vazia (não nula) |
| Vínculo removido (inativo) | Aparece na lista com `ativo = false` |
| Vínculo sem `dataTermino` | Aparece com `dataTermino` nulo |
| Mesma titulação vinculada em períodos diferentes | Aparece uma vez por vínculo |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Mantém o endpoint existente; altera só o conteúdo da resposta.
- **Componentes Principais**: `ProfessorIdResponseDTO` (já possui a lista `titulacoes`) e a consulta que monta a lista, que hoje retorna apenas dados do catálogo (`TitulacaoResponseDTO`) e passa a retornar dados do vínculo.
- **Armazenamento de Dados**: `tb_professores`, `usuarios`, `tb_professor_titulacoes` e `tb_titulacoes`.
- **Design de Interface**: `GET /professores/{id}`, sem mudança de rota.

### Constraints
- **Requisitos de Performance**: Não especificados; a lista é pequena por professor.
- **Compatibilidade**: Substitui a v1.0. Os itens de `titulacoes` mudam de formato (hoje: `id` da titulação, `area`, `nome`, `isAtivo`): o `id` passa a ser o do vínculo e o `ativo` passa a ser o do vínculo.
- **Segurança**: CPF mascarado. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável.

### Risk Assessment
- **Riscos Técnicos**: Mudança de significado do `id` dos itens pode quebrar clientes que já o usavam como `id` da titulação; o `titulacaoId` passa a cumprir esse papel.
- **Riscos de Dependência**: `tabela-tb-professor-titulacao-v1.1-prd.md` (campo `ativo` do vínculo).
- **Riscos de Cronograma**: Nenhum identificado.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint retorna todos os campos do professor, combinando `tb_professores` e `usuarios`
- [ ] `cpf` é retornado mascarado; `senha` nunca é retornada
- [ ] `id` inexistente retorna erro 404 com mensagem adequada
- [ ] Professor inativo é retornado normalmente
- [ ] Resposta inclui `titulacoes` com `id` do vínculo, `titulacaoId`, `nome`, `area`, `dataInicio`, `dataTermino` e `ativo` do vínculo
- [ ] Lista ordenada por `dataInicio` descendente
- [ ] Vínculos inativos aparecem com `ativo = false`
- [ ] Professor sem vínculos retorna `titulacoes` como lista vazia

---

**Document Version**: 1.1
**Created**: 2026-10-22
**Clarification Rounds**: 1
**Quality Score**: 94/100
