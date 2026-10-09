# Benchmark & Condensação Estratégica: Apps de Escrita para Autores

Este documento reúne a análise aprofundada, recursos essenciais, deficiências de mercado e arquitetura funcional para o desenvolvimento de um aplicativo de escrita moderno, ágil e focado em autores de literatura, ensaios e obras de fôlego.

---

## 1. Mapeamento dos Principais Concorrentes do Mercado

### 1.1 Microsoft Word / Google Docs (Editores Tradicionais)
* **Perfil:** Processadores de texto lineares de uso geral.
* **Pontos Fortes:**
  - Onipresença e padrão de mercado para revisão editorial (.docx).
  - Colaboração em tempo real e controle de alterações (Track Changes) altamente maduros.
  - Correção gramatical e formatação padrão ABNT / Chicago.
* **Pontos Fracos para Autores:**
  - Não lidam bem com obras longas (arquivos pesados com centenas de páginas travam a navegação).
  - Ausência de divisão modular por capítulos e cenas.
  - Sem suporte nativo para bíblia de personagens, fichas de lore, mapas mentais ou linhas temporais.
  - Distrações visuais constantes com dezenas de barras de ferramentas.

### 1.2 Scrivener (Literature & Latte)
* **Perfil:** O padrão da indústria tradicional para autores profissionais.
* **Pontos Fortes:**
  - Sistema de "Binder" (árvore hierárquica de pastas, capítulos, cenas e notas de pesquisa).
  - Modo Cortiça (Corkboard / Index Cards): sinopses de cenas dispostas em cartões móveis.
  - Modo Outliner: visão em tabela com metas de palavras por capítulo e status de rascunho.
  - Compilador extremamente poderoso (exporta para EPUB, MOBI, PDF tipográfico e DOCX).
  - Metas diárias de escrita e estatísticas de progresso.
* **Pontos Fracos:**
  - Interface defasada e curva de aprendizado íngreme.
  - Sincronização em nuvem rudimentar (depende de pastas Dropbox e gera conflitos com facilidade).
  - Sem versão oficial para Android ou suporte web em tempo real.

### 1.3 Ulysses (Mac / iOS)
* **Perfil:** Editor minimalista baseado em Markdown voltado para o ecossistema Apple.
* **Pontos Fortes:**
  - Interface limpa, tipografia impecável e modo de foco sem distrações.
  - Organização unificada de biblioteca (todas as histórias em um só lugar).
  - Sincronização contínua e confiável via iCloud.
  - Verificação de estilo, repetições de palavras e gramática integrada.
* **Pontos Fracos:**
  - Exclusivo do ecossistema Apple (sem suporte a Windows ou Linux).
  - Modelo de assinatura recorrente caro.
  - Poucas ferramentas para enredos complexos ou fichas profundas de personagens.

### 1.4 Dabble Writer
* **Perfil:** Concorrente moderno em nuvem inspirado no Scrivener.
* **Pontos Fortes:**
  - Funciona direto no navegador e com apps desktop multiplataforma.
  - Matriz de Enredo (Plot Grid): visualização de arcos de personagens contra capítulos.
  - Foco em simplicidade com interface limpa e sincronização automática.
* **Pontos Fracos:**
  - Requer assinatura mensal/anual relativamente alta.
  - Menos opções avançadas de formatação e compilação do que o Scrivener.

### 1.5 Novelcrafter & Sudowrite (Nova Geração / IA-Assisted)
* **Perfil:** Softwares orientados a planejamento narrativo avançado e assistência de IA contextual.
* **Pontos Fortes:**
  - "Codex" integrado: enciclopédia dinâmica da história (personagens, locais, itens, magias).
  - Detecção automática de menções do Codex dentro do texto.
  - Controle granular de contexto (envia apenas informações pertinentes de lore para a IA).
* **Pontos Fracos:**
  - Dependência de créditos de IA ou APIs externas.
  - Complexidade inicial de configuração.

### 1.6 Obsidian / Notion (Editores de Notas & Conhecimento Pessoal)
* **Perfil:** Bases de conhecimento locais ou em nuvem com hiperlinks bidirecionais.
* **Pontos Fortes:**
  - Obsidian opera em Markdown local puro (o autor é dono dos seus arquivos .md).
  - Links bidirecionais (`[[Nome do Personagem]]`) para navegação instantânea.
  - Ecossistema gigante de plugins.
* **Pontos Fracos:**
  - Não são desenhados para o fluxo contínuo de manuscritos nem compilação de livros.
  - Exigem muito tempo de configuração e customização para ficarem úteis para ficção.

---

## 2. Dores e Necessidades Críticas do Escritor Moderno

1. **Bloqueio Criativo e Distrações:** Excesso de botões, menus e notificações rouba o foco do estado de fluxo (Flow State).
2. **Perda de Coerência Narrativa:** Em livros com mais de 50.000 palavras, é fácil esquecer a cor dos olhos de um personagem secundário, datas ou regras do universo.
3. **Falta de Ritmo e Metas:** Dificuldade em rastrear consistência diária, velocidade de escrita (palavras/hora) e metas por cena.
4. **Exportação Frustrante:** Transformar rascunhos em arquivos prontos para publicação (Amazon KDP, PDF com sangria, EPUB sem falhas de formatação).
5. **Segurança e Propriedade dos Dados:** Medo de perder milhares de palavras por falhas de nuvem ou travamentos do programa.

---

## 3. Pilares Fundamentais do Nosso Aplicativo

### Pilar 1: O Núcleo de Escrita (Editor & Modo Foco)
* **Editor Distraction-Free:** Modo Zen (tela cheia, apenas o parágrafo em edição iluminado - Typewriter Scrolling).
* **Estrutura Modular:** Livro dividido em Partes > Capítulos > Cenas com drag-and-drop rápido.
* **Metas Dinâmicas:** Contador de palavras da sessão, tempo dedicado e estimativa de tempo de leitura.

### Pilar 2: A Bíblia Narrativa (Worldbuilding & Codex)
* **Fichas Vivas:** Entidades para Personagens, Cenários, Itens e Linha do Tempo.
* **Menções Inteligentes:** Digitar `@` ou marcar uma palavra abre a ficha do personagem na lateral sem tirar o autor da página.
* **Relações de Personagens:** Grafo ou árvore simples de laços (aliados, rivais, família).

### Pilar 3: O Planejador de Enredo (Plotting & Arcos)
* **Visão Cartões (Kanban Literário):** Arrastar cenas entre capítulos ou atos da história (Jornada do Herói, Método dos 3 Atos, Save the Cat).
* **Tags de Ritmo:** Marcar cenas por Ponto de Vista (POV), tom emocional ou subtramas.

### Pilar 4: Segurança, Performance e Propriedade
* **Offline-First:** O autor pode escrever no avião ou sem internet; sincronização silenciosa ao reconectar.
* **Histórico de Versões (Snapshots):** Capacidade de salvar versões anteriores de um capítulo com um clique antes de reescrever.
* **Exportação Universal:** Exportar com fidelidade para Markdown, .docx, PDF e EPUB.

---

## 4. Oportunidades Tecnológicas (Kotlin / Multiplatform)
* **Kotlin Multiplatform (KMP):** Compartilhar 100% da lógica de manipulação de manuscritos, parsers de texto, controle de versões e banco SQLite/Room entre Desktop, Web e Mobile.
* **Jetpack Compose / Compose Multiplatform:** UI declarativa de altíssimo desempenho, fluida e com suporte nativo a temas escuros profundos e tipografia editorial.
