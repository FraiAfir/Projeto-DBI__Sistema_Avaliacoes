package BDI.SistemaAvaliacao.controller;

import BDI.SistemaAvaliacao.dao.UsuarioDAO;
import BDI.SistemaAvaliacao.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController{
    // ATRIBUTOS
    private final UsuarioDAO usuarioDAO;



    // CONSTRUTOR
    public UsuarioController(UsuarioDAO usuarioDAO) {this.usuarioDAO = usuarioDAO;}



    // MÉTODOS
    // Método 1: Autenticar usuário por matrícula (professor ou aluno)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body){
        // 1: Extrair a matrícula do corpo da requisição
        String matricula = body.get("matricula");

        // 2: Autenticar o usuário usando o DAO e retornar a resposta apropriada
        return usuarioDAO.autenticarPorMatricula(matricula)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("erro", "Matrícula não encontrada")));
    }

    // Método 2: Listar todos os alunos cadastrados no sistema
    @GetMapping("/alunos")
    public List<Usuario> listarAlunos(){
        // 1: Retornar a lista de alunos usando o DAO
        return usuarioDAO.listarAlunos();
    }
}