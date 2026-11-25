package BDI.SistemaAvaliacao.dao;

import BDI.SistemaAvaliacao.model.Alternativa;
import BDI.SistemaAvaliacao.model.Questao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class QuestaoDAO{
    // ATRIBUTOS
    private final JdbcTemplate jdbc;



    // CONSTRUTOR
    public QuestaoDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}


    
    // MÉTODOS
    // Método 1: Cadastrar uma questão no banco de dados
    @Transactional
    public Integer cadastrar(Questao q){
        // 1: Insere a questão na tabela "questao" e obtem o código gerado
        Integer codigoQuestao = jdbc.queryForObject(
            "INSERT INTO questao (pergunta) VALUES (?) RETURNING codigo",
            Integer.class,
            q.getPergunta()
        );

        // 2: Insere na tabela específica com base no tipo da questão
        // 2.1: Se for do tipo "OBJETIVA", insere na tabela "objetiva" e nas alternativas
        if("OBJETIVA".equalsIgnoreCase(q.getTipo())){
            jdbc.update("INSERT INTO objetiva (codigo_questao) VALUES (?)", codigoQuestao);

            String sqlAlt = """
                INSERT INTO alternativa (codigo_questao, codigo, texto, status)
                VALUES (?, ?, ?, ?)
            """;

            int seq = 1;
            for(Alternativa alt : q.getAlternativas()){
                int codAlt = (alt.getCodigo() != null) ? alt.getCodigo() : seq++;
                jdbc.update(sqlAlt, codigoQuestao, codAlt, alt.getTexto(), alt.getStatus());
            }
        }

        // 2.2: Se for do tipo "LIVRE", insere na tabela "livre"
        else {jdbc.update("INSERT INTO livre (codigo_questao) VALUES (?)", codigoQuestao);}

        // 3: Retorna o código da questão cadastrada
        return codigoQuestao;
    }

    // Método 2: Listar todas as questões cadastradas no sistema
    public List<Map<String, Object>> listarTodas(){
        // 1: Consulta SQL para listar todas as questões com seus tipos
        String sql = """
            SELECT
                q.codigo,
                q.pergunta,
                CASE WHEN o.codigo_questao IS NOT NULL THEN 'OBJETIVA' ELSE 'LIVRE' END AS tipo
            FROM        questao q
            LEFT JOIN   objetiva o ON o.codigo_questao = q.codigo
            
            ORDER BY q.codigo DESC
        """;

        // 2: Retorna a lista de questões com seus tipos
        return jdbc.queryForList(sql);
    }
}