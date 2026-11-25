package BDI.SistemaAvaliacao.dao;

import BDI.SistemaAvaliacao.model.RespostaItem;
import BDI.SistemaAvaliacao.model.SubmissaoAvaliacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Repository
public class RespostaDAO{
    // ATRIBUTOS
    private final JdbcTemplate jdbc;



    // CONSTRUTOR
    public RespostaDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}



    // MÉTODOS
    // Método 1: Registrar respostas do aluno na tabela "responde" e recalcular a nota final na tabela "faz"
    @Transactional
    public void registrarRespostasAluno(SubmissaoAvaliacao sub){
        // 1: Registrar respostas do aluno na tabela "responde"
        String sqlInsert = """
            INSERT INTO responde (codigo_avaliacao, codigo_questao, matricula, resposta, nota_obtida)
            VALUES (?, ?, ?, ?, ?)
        """;

        // 2: Para cada resposta do aluno
        for(RespostaItem item : sub.getRespostas()){
            // 2.1: Calcular a nota obtida se a questão for objetiva; caso contrário, nota obtida será null
            BigDecimal notaObtida = calcularNotaSeObjetiva(
                sub.getCodigoAvaliacao(),
                item.getCodigoQuestao(),
                item.getResposta()
            );

            // 2.2: Inserir a resposta do aluno na tabela "responde"
            jdbc.update(
                sqlInsert,
                sub.getCodigoAvaliacao(),
                item.getCodigoQuestao(),
                sub.getMatricula(),
                item.getResposta(),
                notaObtida
            );
        }

        // 3: Recalcular a nota final do aluno na tabela "faz"
        recalcularNotaFinal(sub.getCodigoAvaliacao(), sub.getMatricula());
    }

    // Método 2: Atribuir nota a uma questão de resposta livre e recalcular a nota final
    @Transactional
    public void atribuirNotaQuestaoLivre(Integer codAvaliacao, Integer codQuestao, String matricula, BigDecimal nota){
        // 1: Atualizar a nota obtida na tabela "responde"
        jdbc.update(
            "UPDATE responde SET nota_obtida = ? WHERE codigo_avaliacao = ? AND codigo_questao = ? AND matricula = ?",
            nota, codAvaliacao, codQuestao, matricula
        );

        // 2: Recalcular a nota final do aluno na tabela "faz"
        recalcularNotaFinal(codAvaliacao, matricula);
    }


    // Método auxiliar para calcular a nota obtida se a questão for objetiva
    private BigDecimal calcularNotaSeObjetiva(Integer codAvaliacao, Integer codQuestao, String resposta){
        // 1: Verificar se a questão é objetiva (existe na tabela "objetiva")
        Integer ehObjetiva = jdbc.queryForObject(
            "SELECT COUNT(*) FROM objetiva WHERE codigo_questao = ?",
            Integer.class, codQuestao
        );

        // 2: Se a questão não for objetiva, retorna null (aguarda correção do professor)
        if (ehObjetiva == null || ehObjetiva == 0) {return null;}

        // 3: Se a questão for objetiva, buscar o valor da questão na tabela "compoe" e verificar se a resposta está correta
        // Converte alternativa.codigo (1, 2, 3, 4) para ('A', 'B', 'C', 'D') via CHR(64 + codigo)
        String sqlCorrecao = """
            SELECT c.valor
            FROM compoe c
            JOIN alternativa a ON a.codigo_questao = c.codigo_questao
            
            WHERE c.codigo_avaliacao = ?    AND
                  c.codigo_questao   = ?    AND
                  a.status           = TRUE AND
                  CHR(64 + a.codigo) = UPPER(?)
        """;

        // 4: Retorna o valor da questão se a resposta estiver correta; caso contrário, retorna BigDecimal.ZERO
        return jdbc.query(
            sqlCorrecao,
            rs -> rs.next() ? rs.getBigDecimal("valor") : BigDecimal.ZERO,
            codAvaliacao, codQuestao, resposta.trim()
        );
    }

    // Método auxiliar para recalcular a nota final do aluno na tabela "faz"
    private void recalcularNotaFinal(Integer codAvaliacao, String matricula){
        // 1: Calcular a nota final do aluno somando as notas obtidas na tabela "responde"
        String sqlTotal = """
            UPDATE faz
            SET nota_final = COALESCE((
                SELECT SUM(nota_obtida)
                FROM responde
                WHERE codigo_avaliacao = ? AND matricula = ?
            ), 0)
            WHERE codigo_avaliacao = ? AND matricula = ?
        """;

        // 2: Atualizar a nota final do aluno na tabela "faz"
        jdbc.update(sqlTotal, codAvaliacao, matricula, codAvaliacao, matricula);
    }
}