# Atualização de Usuário - Product Requirements Document (PRD)

## Requirements Description

### Background
- **Problema de Negócio**: Não existe hoje um endpoint que permita atualizar os dados de um usuário já cadastrado. É necessário um endpoint de atualização que preserve as regras já definidas para os demais endpoints (validações do cadastro, proteção de dados sensíveis) e respeite os campos que não podem ser alterados por esta via.
- **Usuários-Alvo**: Consumidores internos do sistema (front-end/outros serviços) responsáveis por manter os dados dos usuários atualizados.
- **Proposta de Valor**: Permitir a atualização consistente e validada dos dados de um usuário, sem permitir alteração de campos sensíveis/estruturais (login, senha, datas de controle, status ativo), que possuem fluxos próprios já definidos.

### Feature Overview
- **Funcionalidades Principais**: Endpoint de atualização total dos dados editáveis de um usuário existente, reaproveitando as validações já definidas no cadastro.
- **Limites da Feature**:
  - Não permite atualizar `login` e `senha`
  - Não permite atualizar `dataCriacao` e a data de inativação
  - Não permite atualizar `ativo` — alteração de status é exclusiva do endpoint de inativação (e futuro de reativação)
  - Não é atualização parcial — todos os campos editáveis devem ser enviados na requisição, mesmo os que não mudam
  - Não permite atualizar usuários inativos
- **Cenários de Uso Típicos**:
  - Atualização de um usuário ativo com todos os campos válidos → dados atualizados com sucesso
  - Atualização com campos fora das regras de validação → erro 400 com lista dos campos inválidos
  - Atualização com email/cpf já usado por outro usuário → erro específico de duplicidade
  - Atualização de um usuário inativo → erro informando que não é possível atualizar
  - Atualização de um `id` inexistente → erro 404 padronizado

### Detailed Requirements

#### Campos editáveis e regras de validação
(mesmas regras já definidas no cadastro de usuário)

| Campo | Editável | Regras |
|---|---|---|
| `nome` | Sim | Obrigatório, mínimo 5 e máximo 250 caracteres |
| `idade` | Sim | Obrigatório, mínimo 15 e máximo 130 |
| `email` | Sim | Obrigatório, formato válido, mínimo 5 e máximo 250 caracteres, único (comparado apenas com outros usuários, excluindo o próprio registro) |
| `cpf` | Sim | Obrigatório, deve ser um CPF válido, único (comparado apenas com outros usuários, excluindo o próprio registro), salvo sem caracteres especiais |

#### Campos não editáveis por este endpoint
- `login`
- `senha`
- `dataCriacao`
- Data de inativação (ex: `dataInativacao`)
- `ativo`

Esses campos devem permanecer inalterados independentemente do que for enviado na requisição (ou a requisição deve ignorá-los/rejeitá-los, a critério técnico da implementação).

#### Campo automático
- `dataAtualizacao`: atualizado automaticamente ao efetivar a atualização, nunca informado pelo usuário

#### User Interaction
- Requisição HTTP de atualização (total — todos os campos editáveis devem ser reenviados) para um `id` de usuário existente
- Resposta de sucesso: objeto atualizado, sem dados sensíveis (`senha` nunca exposta), com `cpf` mascarado
- Resposta de erro de validação: lista organizada indicando cada campo que falhou
- Resposta de erro de duplicidade: mensagem específica por campo (`email` ou `cpf`)
- Resposta de erro para usuário não encontrado: mensagem já padronizada no sistema (mesmo padrão dos demais endpoints)
- Resposta de erro para usuário inativo: mensagem organizada informando que não é possível atualizar um usuário inativo

#### Data Requirements
- Atualização é total — todos os campos editáveis (`nome`, `idade`, `email`, `cpf`) devem ser enviados na requisição, mesmo que não tenham mudado
- Unicidade de `email` e `cpf` deve ser verificada excluindo o próprio registro sendo atualizado (o usuário pode reenviar seu próprio email/cpf atual sem gerar erro de duplicidade)
- `cpf` deve continuar sendo salvo sem caracteres especiais
- `cpf` deve ser retornado mascarado na resposta, seguindo o mesmo formato já usado nos demais endpoints

#### Edge Cases
| Cenário | Comportamento esperado |
|---|---|
| Campos fora das regras de validação | Erro 400 com lista organizada de todos os campos inválidos |
| `email` já usado por outro usuário | Erro específico informando duplicidade de email |
| `cpf` já usado por outro usuário | Erro específico informando duplicidade de cpf |
| Usuário reenvia seu próprio `email`/`cpf` atual | Não deve gerar erro de duplicidade |
| `id` inexistente | Erro 404, com a mensagem já padronizada no sistema |
| Usuário existente, porém inativo | Não realiza a atualização; retorna erro organizado informando que usuários inativos não podem ser atualizados |
| Tentativa de alterar `login`, `senha`, `dataCriacao`, data de inativação ou `ativo` | Esses campos permanecem inalterados, independentemente do que for enviado |

## Design Decisions

### Technical Approach
- **Escolha de Arquitetura**: Não definida nesta fase — tecnologia, método HTTP e forma de rejeitar/ignorar campos não editáveis ficam a critério de quem implementar.
- **Componentes Principais**: Endpoint de atualização, reaproveitando a camada de validação já criada no cadastro, camada de checagem de unicidade (excluindo o próprio registro), camada de mascaramento de CPF já usada nos demais endpoints.
- **Armazenamento de Dados**: Atualização de registro existente na tabela de usuários já estruturada.
- **Design de Interface**: Endpoint HTTP recebendo o `id` do usuário e o corpo com os campos editáveis, todos obrigatórios (atualização total).

### Constraints
- **Requisitos de Performance**: Não especificados.
- **Compatibilidade**: Não especificada.
- **Segurança**: Senha nunca exposta; CPF sempre mascarado na resposta. Sem autenticação nesta versão.
- **Escalabilidade**: Não aplicável — operação sobre um único registro.

### Risk Assessment
- **Riscos Técnicos**: Nenhum risco técnico adicional identificado além dos já conhecidos (ausência de autenticação).
- **Riscos de Dependência**: Depende das regras de validação já definidas no cadastro e da estrutura de dados já criada (tabela de usuários, inativação).
- **Riscos de Cronograma**: Nenhum risco de prazo identificado nesta fase.

## Acceptance Criteria

### Functional Acceptance
- [ ] Endpoint atualiza `nome`, `idade`, `email` e `cpf` de um usuário existente e ativo
- [ ] `login`, `senha`, `dataCriacao`, data de inativação e `ativo` permanecem inalterados, independentemente do que for enviado
- [ ] `dataAtualizacao` é atualizada automaticamente ao efetivar a atualização
- [ ] Validações seguem as mesmas regras do cadastro (tamanhos, formato de email, CPF válido)
- [ ] Requisição exige todos os campos editáveis, mesmo os que não mudaram (atualização total)
- [ ] Campos inválidos retornam HTTP 400 com lista organizada de todos os erros encontrados
- [ ] Duplicidade de `email` ou `cpf` com outro usuário retorna mensagem específica por campo
- [ ] Reenvio do próprio `email`/`cpf` atual não gera erro de duplicidade
- [ ] `id` inexistente retorna a mensagem de erro já padronizada no sistema
- [ ] Usuário inativo não pode ser atualizado; retorna mensagem de erro organizada
- [ ] Resposta de sucesso retorna o objeto atualizado, sem `senha`, com `cpf` mascarado

---

**Document Version**: 1.0
**Created**: 2026-09-17
**Clarification Rounds**: 2
**Quality Score**: 95/100
