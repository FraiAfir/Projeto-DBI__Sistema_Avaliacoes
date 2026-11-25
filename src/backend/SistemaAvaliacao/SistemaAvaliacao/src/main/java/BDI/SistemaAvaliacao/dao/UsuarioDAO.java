package BDI.SistemaAvaliacao.dao;

import BDI.SistemaAvaliacao.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO{
    // ATRIBUTOS
    private final JdbcTemplate jdbc;



    // CONSTRUTOR
    public UsuarioDAO(JdbcTemplate jdbc) {this.jdbc = jdbc;}



    // MÉTODOS
    // Método 1: Autenticar usuário por matrícula (professor ou aluno)
    public Optional<Usuario> autenticarPorMatricula(String matricula){
        // 1: Tentar buscar o usuário na tabela "professor"
        String sqlProf = "SELECT matricula, cpf, nome, data_nascimento FROM professor WHERE matricula = ?";

        // 2: Se não encontrar, tentar buscar na tabela "aluno"
        List<Usuario> profs = jdbc.query(sqlProf, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setMatricula(rs.getString("matricula"));
            u.setCpf(rs.getString("cpf"));
            u.setNome(rs.getString("nome"));
            u.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
            u.setPerfil("PROFESSOR");
            return u;
        }, matricula);

        // 3: Retornar o primeiro professor encontrado, se houver; caso contrário, buscar na tabela "aluno"
        if(!profs.isEmpty()) {return Optional.of(profs.get(0));}

        // 4: Buscar na tabela "aluno"
        String sqlAluno = "SELECT matricula, cpf, nome, data_nascimento FROM aluno WHERE matricula = ?";
        List<Usuario> alunos = jdbc.query(sqlAluno, (rs, rowNum) -> {
            Usuario u = new Usuario();
            u.setMatricula(rs.getString("matricula"));
            u.setCpf(rs.getString("cpf"));
            u.setNome(rs.getString("nome"));
            u.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
            u.setPerfil("ALUNO");
            return u;
        }, matricula);

        // 5: Retornar o primeiro aluno encontrado, se houver; caso contrário, retornar Optional.empty()
        return alunos.stream().findFirst();
    }

    // Método 2: Listar todos os alunos cadastrados no sistema
    public List<Usuario> listarAlunos(){
        // 1: Buscar todos os alunos na tabela "aluno" e mapear para objetos Usuario
        return jdbc.query(
            "SELECT matricula, cpf, nome, data_nascimento FROM aluno ORDER BY nome",
            (rs, rowNum) -> {
                Usuario u = new Usuario();
                u.setMatricula(rs.getString("matricula"));
                u.setCpf(rs.getString("cpf"));
                u.setNome(rs.getString("nome"));
                u.setDataNascimento(rs.getObject("data_nascimento", LocalDate.class));
                u.setPerfil("ALUNO");
                return u;
            }
        );
    }
}