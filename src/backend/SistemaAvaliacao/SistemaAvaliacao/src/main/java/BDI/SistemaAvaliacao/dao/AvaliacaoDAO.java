package BDI.SistemaAvaliacao.dao;

import BDI.SistemaAvaliacao.model.Avaliacao;
import BDI.SistemaAvaliacao.model.ItemCompoe;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class AvaliacaoDAO{
    // ATRIBUTOS
    private final JdbcTemplate jdbc;



    // CONSTRUTOR
    public AvaliacaoDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}



    // MÉTODOS
    // Método 1: Cria uma avaliação completa, incluindo questões e alunos vinculados
    @Transactional
    public Integer criarAvaliacaoCompleta(Avaliacao av){
        // 1: Cria a avaliação em 'avaliacao' e obtém o código gerado
        String sqlAv = """
            INSERT INTO avaliacao (matricula_professor, titulo, descricao, data, horario_inicio, horario_fim)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING codigo
        """;

        // 2: Executa a inserção e obtém o código da avaliação criada
        Integer codAvaliacao = jdbc.queryForObject(
            sqlAv, Integer.class,
            av.getMatriculaProfessor(),
            av.getTitulo(),
            av.getDescricao(),
            av.getData(),
            av.getHorarioInicio(),
            av.getHorarioFim()
        );

        // 3: Vincula questões à avaliação em 'compoe'
        if(av.getQuestoes() != null){
            for(ItemCompoe item : av.getQuestoes()){
                jdbc.update(
                    "INSERT INTO compoe (codigo_avaliacao, codigo_questao, valor) VALUES (?, ?, ?)",
                    codAvaliacao, item.getCodigoQuestao(), item.getValor()
                );
            }
        }

        // 4: Vincula alunos à avaliação em 'faz'
        if(av.getMatriculasAlunos() != null){
            for(String matricula : av.getMatriculasAlunos()){
                jdbc.update(
                    "INSERT INTO faz (codigo_avaliacao, matricula, nota_final) VALUES (?, ?, NULL)",
                    codAvaliacao, matricula
                );
            }
        }

        // 5: Retorna o código da avaliação criada
        return codAvaliacao;
    }

    // Método 2: Listar todas as avaliações de um aluno, incluindo nota final e se já respondeu
    public List<Map<String, Object>> listarPorAluno(String matricula){
        // 1: Consulta SQL para buscar avaliações do aluno, incluindo nota final e status de resposta
        String sql = """
            SELECT
                a.codigo,
                a.titulo,
                a.descricao,
                a.data,
                a.horario_inicio,
                a.horario_fim,
                f.nota_final,
                EXISTS (
                    SELECT 1 FROM responde r
                    WHERE r.codigo_avaliacao = a.codigo AND r.matricula = f.matricula
                ) AS ja_respondeu
            FROM faz f
            JOIN avaliacao a ON a.codigo = f.codigo_avaliacao
            JOIN aluno al    ON al.matricula = f.matricula
            WHERE f.matricula = ?
            ORDER BY a.data DESC
        """;

        // 2: Executa a consulta e retorna a lista de avaliações do aluno
        return jdbc.queryForList(sql, matricula);
    }

    // Método 3: Listar todas as avaliações de um professor, incluindo quantidade de alunos e média de notas
    public List<Map<String, Object>> buscarQuestoesDaAvaliacao(Integer codigoAvaliacao){
        // 1: Consulta SQL para buscar questões da avaliação, incluindo tipo e alternativas se for objetiva
        String sql = """
            SELECT
                q.codigo AS codigo_questao,
                q.pergunta,
                c.valor,
                CASE WHEN o.codigo_questao IS NOT NULL THEN 'OBJETIVA' ELSE 'LIVRE' END AS tipo
            FROM compoe c
            JOIN questao q ON q.codigo = c.codigo_questao
            LEFT JOIN objetiva o ON o.codigo_questao = q.codigo
            WHERE c.codigo_avaliacao = ?
            ORDER BY q.codigo
        """;

        // 2: Executa a consulta e obtém a lista de questões da avaliação
        List<Map<String, Object>> questoes = jdbc.queryForList(sql, codigoAvaliacao);

        // 3: Para cada questão objetiva, buscar suas alternativas e adicioná-las ao mapa da questão
        for(Map<String, Object> q : questoes){
            if("OBJETIVA".equals(q.get("tipo"))){
                // Se a questão for objetiva, buscar suas alternativas
                List<Map<String, Object>> alts = jdbc.queryForList(
                    "SELECT codigo, CHR(64 + codigo) AS letra, texto FROM alternativa WHERE codigo_questao = ? ORDER BY codigo",
                    q.get("codigo_questao")
                );

                // Adicionar a lista de alternativas ao mapa da questão
                q.put("alternativas", alts);
            }
        }

        // 4: Retornar a lista de questões da avaliação, incluindo alternativas para questões objetivas
        return questoes;
    }

    // Método 4: Listar todas as avaliações de um professor, incluindo quantidade de alunos e média de notas
    public List<Map<String, Object>> listarRespostasLivresPendentes(){
        // 1: Consulta SQL para buscar respostas livres pendentes de correção
        String sql = """
            SELECT
                r.codigo_avaliacao,
                a.titulo AS titulo_avaliacao,
                r.codigo_questao,
                q.pergunta,
                r.matricula,
                al.nome AS nome_aluno,
                r.resposta,
                c.valor AS valor_maximo
            FROM responde r
            JOIN avaliacao a ON a.codigo = r.codigo_avaliacao
            JOIN questao q   ON q.codigo = r.codigo_questao
            JOIN aluno al    ON al.matricula = r.matricula
            JOIN compoe c    ON c.codigo_avaliacao = r.codigo_avaliacao AND c.codigo_questao = r.codigo_questao
            WHERE r.nota_obtida IS NULL
            ORDER BY a.data, al.nome
        """;

        // 2: Executa a consulta e retorna a lista de respostas livres pendentes
        return jdbc.queryForList(sql);
    }
}