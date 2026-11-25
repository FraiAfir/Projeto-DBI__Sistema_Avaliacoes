-- RELATÓRIO 1: Desempenho Geral por Avaliação (Média, Valor Total e Adesão)
-- Gráfico sugerido: Barras agrupadas (Valor Total da Prova vs. Média Obtida pela Turma)
-- Recursos SQL: JOIN, LEFT JOIN, Subconsulta, Agregação (SUM, AVG, MIN, MAX, COUNT), GROUP BY, HAVING e Filtros (BETWEEN para datas e ILIKE para título).
SELECT
    a.codigo                                    AS codigo_avaliacao,
    a.titulo                                    AS titulo_avaliacao,
    a.data                                      AS data_avaliacao,
    p.nome                                      AS nome_professor,
    COALESCE((
        SELECT SUM(c.valor)
        FROM compoe c
        WHERE c.codigo_avaliacao = a.codigo
    ), 0.00)                                    AS valor_total_prova,
    COUNT(f.matricula)                          AS alunos_designados,
    COUNT(f.nota_final)                         AS alunos_responderam,
    ROUND(COALESCE(AVG(f.nota_final), 0), 2)    AS media_turma,
    ROUND(COALESCE(MIN(f.nota_final), 0), 2)    AS menor_nota,
    ROUND(COALESCE(MAX(f.nota_final), 0), 2)    AS maior_nota

FROM        avaliacao a
JOIN        professor p ON p.matricula = a.matricula_professor
LEFT JOIN   faz f       ON f.codigo_avaliacao = a.codigo

WHERE
    a.data BETWEEN '2026-01-01' AND '2026-12-31' AND 
    a.titulo ILIKE '%%'

GROUP BY
    a.codigo,
    a.titulo,
    a.data,
    p.nome
HAVING
    COUNT(f.matricula) > 0
ORDER BY
    a.data   DESC,
    a.codigo DESC;










-- RELATÓRIO 2: Evolução Individual do Aluno vs. Média da Turma
-- Gráfico sugerido: Linhas ou Barras lado a lado (Nota do Aluno vs. Média da Turma)
-- Recursos SQL: JOIN, Subconsulta correlacionada, Filtro por matrícula e ORDER BY.
SELECT
    al.matricula                                        AS matricula_aluno,
    al.nome                                             AS nome_aluno,
    a.codigo                                            AS codigo_avaliacao,
    a.titulo                                            AS titulo_avaliacao,
    a.data                                              AS data_avaliacao,
    f.nota_final                                        AS nota_aluno,
    ROUND((
        SELECT AVG(f2.nota_final)
        FROM faz f2
        WHERE f2.codigo_avaliacao = f.codigo_avaliacao
          AND f2.nota_final IS NOT NULL
    ), 2)                                               AS media_turma,
    ROUND(
        f.nota_final - (
            SELECT AVG(f2.nota_final)
            FROM faz f2
            WHERE f2.codigo_avaliacao = f.codigo_avaliacao
              AND f2.nota_final IS NOT NULL
        ),
        2
    )                                                   AS diferenca_para_media

FROM faz f
JOIN aluno al    ON al.matricula = f.matricula
JOIN avaliacao a ON a.codigo = f.codigo_avaliacao

WHERE
    al.matricula = '20260001'
    AND f.nota_final IS NOT NULL

ORDER BY
    a.data ASC,
    a.horario_inicio ASC;










-- RELATÓRIO 3: Ranking das 10 Questões com Maior Taxa de Erro
-- Gráfico sugerido: Barras horizontais (Percentual de Erro por Questão)
-- Recursos SQL: JOIN, Agregação (COUNT, AVG, GROUP BY, HAVING), Filtro com operador IN, Window Function RANK(), ORDER BY e FETCH FIRST.
SELECT
    RANK() OVER(
        ORDER BY
            (COUNT(CASE WHEN r.nota_obtida < c.valor THEN 1 END)::numeric / COUNT(r.nota_obtida)) DESC,
            COUNT(r.nota_obtida) DESC
    )                                                                           AS posicao_ranking,
    q.codigo                                                                    AS codigo_questao,
    LEFT(q.pergunta, 80)                                                        AS resumo_pergunta,
    COUNT(r.nota_obtida)                                                        AS total_respostas_corrigidas,
    COUNT(CASE WHEN r.nota_obtida < c.valor THEN 1 END)                         AS total_erros_ou_parciais,
    ROUND(
        (COUNT(CASE WHEN r.nota_obtida < c.valor THEN 1 END)::numeric * 100.0)
        / COUNT(r.nota_obtida),
        2
    )                                                                           AS percentual_erro,
    ROUND(AVG(r.nota_obtida), 2)                                                AS media_nota_obtida

FROM questao q
JOIN compoe c   ON c.codigo_questao = q.codigo
JOIN responde r ON r.codigo_avaliacao = c.codigo_avaliacao AND r.codigo_questao = c.codigo_questao

WHERE
    r.nota_obtida IS NOT NULL AND
    c.codigo_avaliacao IN (1, 2, 3, 4, 5)

GROUP BY
    q.codigo,
    q.pergunta
HAVING
    COUNT(r.nota_obtida) >= 1
ORDER BY
    percentual_erro DESC,
    total_respostas_corrigidas DESC
FETCH FIRST 10 ROWS ONLY;










-- RELATÓRIO 4: Alunos Pendentes por Avaliação (Quem Ainda Não Respondeu)
-- Gráfico sugerido: Barras empilhadas (Respondentes vs. Pendentes por Avaliação)
-- Recursos SQL: JOIN, LEFT JOIN para detectar ausência de respostas, COUNT, GROUP BY, HAVING e ORDER BY.
SELECT
    a.codigo                                            AS codigo_avaliacao,
    a.titulo                                            AS titulo_avaliacao,
    a.data                                              AS data_avaliacao,
    al.matricula                                        AS matricula_aluno,
    al.nome                                             AS nome_aluno,
    CASE
        WHEN COUNT(r.codigo_questao) = 0 THEN 'Pendente'
        ELSE 'Respondida'
    END                                                 AS situacao

FROM        avaliacao a
JOIN        faz f      ON f.codigo_avaliacao = a.codigo
JOIN        aluno al   ON al.matricula = f.matricula
LEFT JOIN   responde r ON r.codigo_avaliacao = f.codigo_avaliacao AND r.matricula = f.matricula

GROUP BY
    a.codigo,
    a.titulo,
    a.data,
    al.matricula,
    al.nome
HAVING
    COUNT(r.codigo_questao) = 0
ORDER BY
    a.data ASC,
    al.nome ASC;