# 🖥️ Projeto-DBI__Sistema_Avaliacoes
> Projeto de Banco de Dados I - Sistema de avaliações (PostgreSQL | Back-End - Java | Sem JPA/Hibernate)

**MEMBROS:**
* André Felipe Ijiri Ribeiro;
* Marcela de Oliveira Dorigao;

**DESCRIÇÃO:** Um sistema web de avaliações, com banco de dados PostgreSQL, em que é possível cadastrar questões, montar avaliações com essas questões, indicar quem deve responder cada avaliação, registrar as respostas e, no fim, ver relatórios com tabelas e gráficos sobre os resultados. O objetivo é praticar, num sistema completo, tudo o que vimos na disciplina: modelagem, mapeamento, normalização, DDL e consultas SQL, ligando o banco a uma aplicação organizada em camadas.

---

## 🌳 Natureza do Sistema
> Avaliação de aprendizagem (provas, simulados, quizzes)

Cada `questão` tem um `valor` na `avaliação`. O sistema registra a `nota` obtida em cada `questão`
* Automática nas objetivas;
* Dada pelo professor nas de texto livre;

E calcula a `nota total` de cada aluno.

---
