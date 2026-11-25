package BDI.SistemaAvaliacao.controller;

import BDI.SistemaAvaliacao.dao.AvaliacaoDAO;
import BDI.SistemaAvaliacao.model.Avaliacao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/avaliacoes")
@CrossOrigin(origins = "http://localhost:5173")
public class AvaliacaoController{
    // ATRIBUTOS
    private final AvaliacaoDAO avaliacaoDAO;



    // CONSTRUTOR
    public AvaliacaoController(AvaliacaoDAO avaliacaoDAO) {this.avaliacaoDAO = avaliacaoDAO;}



    // MÉTODOS
    // Método 1: Criar uma nova avaliação completa (incluindo questões e alunos vinculados)
    @PostMapping
    public ResponseEntity<Integer> criar(@RequestBody Avaliacao avaliacao){
        // 1: Chama o método criarAvaliacaoCompleta() do DAO para inserir a avaliação no banco de dados
        Integer id = avaliacaoDAO.criarAvaliacaoCompleta(avaliacao);

        // 2: Retorna o código da avaliação criada com status HTTP 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // Método 2: Listar todas as avaliações de um aluno específico
    @GetMapping("/aluno/{matricula}")
    public List<Map<String, Object>> listarPorAluno(@PathVariable String matricula){
        // 1: Chama o método listarPorAluno() do DAO para obter a lista de avaliações do aluno
        return avaliacaoDAO.listarPorAluno(matricula);
    }

    // Método 3: Buscar todas as questões de uma avaliação específica, incluindo alternativas para questões objetivas
    @GetMapping("/{codigo}/questoes")
    public List<Map<String, Object>> buscarQuestoes(@PathVariable Integer codigo){
        // 1: Chama o método buscarQuestoesDaAvaliacao() do DAO para obter a lista de questões da avaliação
        return avaliacaoDAO.buscarQuestoesDaAvaliacao(codigo);
    }
}