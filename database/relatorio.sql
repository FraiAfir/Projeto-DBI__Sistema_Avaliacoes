-- RELATÓRIO 1: Média e desempenho por avaliação
SELECT
    av.codigo AS codigo_avaliacao,
    av.titulo,
    av.data,
    prof.nome AS professor_responsavel,
    COUNT(f.matricula) AS total_designados,
    COUNT(f.nota_final) AS total_avaliados,
    ROUND(AVG(f.nota_final), 2) AS media_nota,
    MIN(f.nota_final) AS menor_nota,
    MAX(f.nota_final) AS maior_nota
FROM avaliacao av
INNER JOIN professor prof
    ON prof.matricula = av.matricula_professor
INNER JOIN faz f
    ON f.codigo_avaliacao = av.codigo
WHERE av.data BETWEEN DATE '2026-06-01' AND DATE '2026-12-31'
  AND prof.matricula IN ('20260020', '20260021', '20260022')
GROUP BY
    av.codigo,
    av.titulo,
    av.data,
    prof.nome
HAVING COUNT(f.nota_final) > 0
ORDER BY av.data ASC, av.codigo ASC;


-- RELATÓRIO 2: Desempenho de um aluno comparado com a média da turma
SELECT
    al.matricula,
    al.nome AS nome_aluno,
    av.codigo AS codigo_avaliacao,
    av.titulo AS avaliacao,
    av.data,
    f.nota_final AS nota_aluno,
    (
        SELECT ROUND(AVG(f2.nota_final), 2)
        FROM faz f2
        WHERE f2.codigo_avaliacao = av.codigo
          AND f2.nota_final IS NOT NULL
    ) AS media_turma,
    ROUND(
        f.nota_final - (
            SELECT AVG(f2.nota_final)
            FROM faz f2
            WHERE f2.codigo_avaliacao = av.codigo
              AND f2.nota_final IS NOT NULL
        ),
        2
    ) AS diferenca_vs_media
FROM aluno al
INNER JOIN faz f
    ON f.matricula = al.matricula
INNER JOIN avaliacao av
    ON av.codigo = f.codigo_avaliacao
WHERE (al.matricula = '20260001' OR al.nome ILIKE '%Ana%')
  AND f.nota_final IS NOT NULL
ORDER BY av.data ASC, av.codigo ASC;


-- RELATÓRIO 3: Ranking das questões com maior índice de erro / perda de pontos
SELECT
    q.codigo AS codigo_questao,
    q.pergunta,
    COUNT(r.matricula) AS total_respostas,
    ROUND(SUM(c.valor), 2) AS pontos_possiveis_total,
    ROUND(SUM(r.nota_obtida), 2) AS pontos_obtidos_total,
    ROUND(SUM(c.valor - r.nota_obtida), 2) AS pontos_perdidos_total,
    ROUND(
        CASE
            WHEN SUM(c.valor) > 0
                THEN ((SUM(c.valor) - SUM(r.nota_obtida)) / SUM(c.valor)) * 100
            ELSE 0
        END,
        2
    ) AS percentual_perda_pontos,
    RANK() OVER (
        ORDER BY SUM(c.valor - r.nota_obtida) DESC, q.codigo ASC
    ) AS ranking_perda
FROM questao q
INNER JOIN compoe c
    ON c.codigo_questao = q.codigo
INNER JOIN responde r
    ON r.codigo_questao = c.codigo_questao
   AND r.codigo_avaliacao = c.codigo_avaliacao
WHERE r.nota_obtida IS NOT NULL
GROUP BY
    q.codigo,
    q.pergunta
HAVING COUNT(r.matricula) > 0
ORDER BY
    pontos_perdidos_total DESC,
    percentual_perda_pontos DESC,
    q.codigo ASC
FETCH FIRST 10 ROWS ONLY;


-- RELATÓRIO 4: Avaliações sem nota final pois tem questão não corrigida
SELECT
    av.codigo AS codigo_avaliacao,
    av.titulo AS avaliacao,
    al.matricula,
    al.nome AS nome_aluno,
    f.nota_final,
    COUNT(r.codigo_questao) AS qtd_respostas_enviadas,
    COUNT(r.nota_obtida) AS qtd_respostas_corrigidas
FROM faz f
INNER JOIN avaliacao av
    ON av.codigo = f.codigo_avaliacao
INNER JOIN aluno al
    ON al.matricula = f.matricula
LEFT JOIN responde r
    ON r.codigo_avaliacao = f.codigo_avaliacao
   AND r.matricula = f.matricula
WHERE f.nota_final IS NULL
   OR r.nota_obtida IS NULL
GROUP BY
    av.codigo,
    av.titulo,
    al.matricula,
    al.nome,
    f.nota_final
ORDER BY av.codigo ASC, al.nome ASC;