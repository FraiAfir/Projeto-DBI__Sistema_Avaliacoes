package BDI.SistemaAvaliacao.dao;

import BDI.SistemaAvaliacao.model.RelatorioAvaliacaoDTO;
import BDI.SistemaAvaliacao.model.RelatorioDesempenhoAlunoDTO;
import BDI.SistemaAvaliacao.model.RelatorioPendenciaDTO;
import BDI.SistemaAvaliacao.model.RelatorioRankingQuestaoDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Repository
public class RelatorioDAO{
    // ATRIBUTOS
    private final JdbcTemplate jdbc;



    // CONSTRUTOR
    public RelatorioDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}



    // MÉTODOS
    // RELATÓRIO 1: Média da turma por avaliação com filtro dinâmico
    public List<RelatorioAvaliacaoDTO> buscarMediaPorAvaliacao(LocalDate dataInicio, LocalDate dataFim, String filtroTitulo){
        // 1: Definir valores padrão para os filtros de data e título
        LocalDate   inicio = (dataInicio != null) ? dataInicio : LocalDate.of(2000, 1, 1);
        LocalDate   fim    = (dataFim != null)    ? dataFim    : LocalDate.of(2099, 12, 31);
        String      termo  = (filtroTitulo != null && !filtroTitulo.isBlank())
            ?
            "%" + filtroTitulo.trim() + "%"
            :
            "%";

        // 2: Consulta SQL para buscar a média da turma por avaliação com filtros dinâmicos
        String sql = """
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
                a.data BETWEEN ? AND ? AND
                a.titulo ILIKE ?
            
            GROUP BY
                a.codigo, a.titulo, a.data, p.nome
            HAVING
                COUNT(f.matricula) > 0
            ORDER BY
                a.data DESC, a.codigo DESC
        """;

        // 3: Executar a consulta e mapear os resultados para objetos RelatorioAvaliacaoDTO
        return jdbc.query(sql, (rs, rowNum) -> {
            RelatorioAvaliacaoDTO dto = new RelatorioAvaliacaoDTO();
            dto.setCodigoAvaliacao  (rs.getInt       ("codigo_avaliacao"               ));
            dto.setTituloAvaliacao  (rs.getString    ("titulo_avaliacao"               ));
            dto.setDataAvaliacao    (rs.getObject    ("data_avaliacao", LocalDate.class));
            dto.setNomeProfessor    (rs.getString    ("nome_professor"                 ));
            dto.setValorTotalProva  (rs.getBigDecimal("valor_total_prova"              ));
            dto.setAlunosDesignados (rs.getInt       ("alunos_designados"              ));
            dto.setAlunosResponderam(rs.getInt       ("alunos_responderam"             ));
            dto.setMediaTurma       (rs.getBigDecimal("media_turma"                    ));
            dto.setMenorNota        (rs.getBigDecimal("menor_nota"                     ));
            dto.setMaiorNota        (rs.getBigDecimal("maior_nota"                     ));
            return dto;
        }, inicio, fim, termo);
    }

    // RELATÓRIO 2: Evolução do aluno comparada à média da turma (Subconsulta correlacionada)
    public List<RelatorioDesempenhoAlunoDTO> buscarEvolucaoAluno(String matricula){
        // 1: Consulta SQL para buscar a evolução do aluno comparada à média da turma
        String sql = """
            SELECT
                al.matricula                                            AS matricula_aluno,
                al.nome                                                 AS nome_aluno,
                a.codigo                                                AS codigo_avaliacao,
                a.titulo                                                AS titulo_avaliacao,
                a.data                                                  AS data_avaliacao,
                f.nota_final                                            AS nota_aluno,
                ROUND((
                    SELECT AVG(f2.nota_final)
                    FROM faz f2
                    WHERE f2.codigo_avaliacao = f.codigo_avaliacao
                      AND f2.nota_final IS NOT NULL
                ), 2)                                                   AS media_turma,
                ROUND(
                    f.nota_final - (
                        SELECT AVG(f2.nota_final)
                        FROM faz f2
                        WHERE f2.codigo_avaliacao = f.codigo_avaliacao
                          AND f2.nota_final IS NOT NULL
                    ),
                    2
                )                                                       AS diferenca_para_media

            FROM faz f
            JOIN aluno al    ON al.matricula = f.matricula
            JOIN avaliacao a ON a.codigo = f.codigo_avaliacao
            
            WHERE
                al.matricula = ? AND
                f.nota_final IS NOT NULL
            
            ORDER BY
                a.data ASC, a.horario_inicio ASC
        """;

        // 2: Executar a consulta e mapear os resultados para objetos RelatorioDesempenhoAlunoDTO
        return jdbc.query(sql, (rs, rowNum) -> {
            RelatorioDesempenhoAlunoDTO dto = new RelatorioDesempenhoAlunoDTO();
            dto.setMatriculaAluno       (rs.getString    ("matricula_aluno"                ));
            dto.setNomeAluno            (rs.getString    ("nome_aluno"                     ));
            dto.setCodigoAvaliacao      (rs.getInt       ("codigo_avaliacao"               ));
            dto.setTituloAvaliacao      (rs.getString    ("titulo_avaliacao"               ));
            dto.setDataAvaliacao        (rs.getObject    ("data_avaliacao", LocalDate.class));
            dto.setNotaAluno            (rs.getBigDecimal("nota_aluno"                     ));
            dto.setMediaTurma           (rs.getBigDecimal("media_turma"                    ));
            dto.setDiferencaParaMedia   (rs.getBigDecimal("diferenca_para_media"           ));
            return dto;
        }, matricula);
    }

    // RELATÓRIO 3: Ranking das 10 questões mais erradas com filtro dinâmico IN
    public List<RelatorioRankingQuestaoDTO> buscarRankingQuestoesMaisErradas(List<Integer> codigosAvaliacao){
        // 1: Verificar se há filtro de códigos de avaliação
        boolean temFiltroIn = codigosAvaliacao != null && !codigosAvaliacao.isEmpty();

        // 2: Construir a cláusula IN dinamicamente se houver filtro
        String clausulaIn = temFiltroIn
                ? 
                " AND c.codigo_avaliacao IN ("+ String.join(",", Collections.nCopies(codigosAvaliacao.size(), "?"))+ ") "
                : 
                "";

        // 3: Executar a consulta com o filtro dinâmico
        String sql = """
            SELECT
                RANK() OVER (
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
                r.nota_obtida IS NOT NULL
            """ 
            + clausulaIn + 
            """
            GROUP BY
                q.codigo, q.pergunta
            HAVING
                COUNT(r.nota_obtida) >= 1
            ORDER BY
                percentual_erro DESC, total_respostas_corrigidas DESC
            FETCH FIRST 10 ROWS ONLY
        """;

        // 4: Preparar os parâmetros para a consulta, caso haja filtro IN
        Object[] params = temFiltroIn ? codigosAvaliacao.toArray() : new Object[0];

        // 5: Executar a consulta e mapear os resultados para objetos RelatorioRankingQuestaoDTO
        return jdbc.query(sql, (rs, rowNum) -> {
            RelatorioRankingQuestaoDTO dto = new RelatorioRankingQuestaoDTO();
            dto.setPosicaoRanking           (rs.getInt       ("posicao_ranking"           ));
            dto.setCodigoQuestao            (rs.getInt       ("codigo_questao"            ));
            dto.setResumoPergunta           (rs.getString    ("resumo_pergunta"           ));
            dto.setTotalRespostasCorrigidas (rs.getInt       ("total_respostas_corrigidas"));
            dto.setTotalErrosOuParciais     (rs.getInt       ("total_erros_ou_parciais"   ));
            dto.setPercentualErro           (rs.getBigDecimal("percentual_erro"           ));
            dto.setMediaNotaObtida          (rs.getBigDecimal("media_nota_obtida"         ));
            return dto;
        }, params);
    }

    // RELATÓRIO 4: Alunos que ainda não responderam cada avaliação (LEFT JOIN)
    public List<RelatorioPendenciaDTO> buscarAlunosPendentes(Integer codigoAvaliacao){
        // 1: Construir a cláusula WHERE dinamicamente se houver filtro de código de avaliação
        String filtroAv = (codigoAvaliacao != null) ? " WHERE a.codigo = ? " : "";

        // 2: Consulta SQL para buscar os alunos que ainda não responderam cada avaliação
        String sql = """
            SELECT
                a.codigo                                                AS codigo_avaliacao,
                a.titulo                                                AS titulo_avaliacao,
                a.data                                                  AS data_avaliacao,
                al.matricula                                            AS matricula_aluno,
                al.nome                                                 AS nome_aluno,
                CASE
                    WHEN COUNT(r.codigo_questao) = 0 THEN 'Pendente'
                    ELSE 'Respondida'
                END                                                     AS situacao
            
            FROM        avaliacao a
            JOIN        faz f      ON f.codigo_avaliacao = a.codigo
            JOIN        aluno al   ON al.matricula = f.matricula
            LEFT JOIN   responde r ON r.codigo_avaliacao = f.codigo_avaliacao AND r.matricula = f.matricula
            """ 
            + filtroAv + 
            """
            GROUP BY
                a.codigo, a.titulo, a.data, al.matricula, al.nome
            HAVING
                COUNT(r.codigo_questao) = 0
            ORDER BY
                a.data ASC, al.nome ASC
        """;

        // 3: Preparar os parâmetros para a consulta, caso haja filtro de código de avaliação
        Object[] params = (codigoAvaliacao != null) ? new Object[]{codigoAvaliacao} : new Object[0];

        // 4: Executar a consulta e mapear os resultados para objetos RelatorioPendenciaDTO
        return jdbc.query(sql, (rs, rowNum) -> {
            RelatorioPendenciaDTO dto = new RelatorioPendenciaDTO();
            dto.setCodigoAvaliacao  (rs.getInt   ("codigo_avaliacao"               ));
            dto.setTituloAvaliacao  (rs.getString("titulo_avaliacao"               ));
            dto.setDataAvaliacao    (rs.getObject("data_avaliacao", LocalDate.class));
            dto.setMatriculaAluno   (rs.getString("matricula_aluno"                ));
            dto.setNomeAluno        (rs.getString("nome_aluno"                     ));
            dto.setSituacao         (rs.getString("situacao"                       ));
            return dto;
        }, params);
    }
}