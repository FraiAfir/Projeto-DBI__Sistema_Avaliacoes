package BDI.SistemaAvaliacao.controller;

import BDI.SistemaAvaliacao.dao.QuestaoDAO;
import BDI.SistemaAvaliacao.model.Questao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/questoes")
@CrossOrigin(origins = "http://localhost:5173")
public class QuestaoController{
    // ATRIBUTOS
    private final QuestaoDAO questaoDAO;



    // CONSTRUTOR
    public QuestaoController(QuestaoDAO questaoDAO) {this.questaoDAO = questaoDAO;}



    // MÉTODOS
    // Método 1: Cadastrar uma nova questão no sistema
    @PostMapping
    public ResponseEntity<Integer> criar(@RequestBody Questao questao){
        // 1: Chama o método cadastrar() do DAO para inserir a questão no banco de dados
        Integer id = questaoDAO.cadastrar(questao);

        // 2: Retorna o código da questão criada com status HTTP 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // Método 2: Listar todas as questões cadastradas no sistema
    @GetMapping
    public List<Map<String, Object>> listarTodas(){
        // 1: Chama o método listarTodas() do DAO para obter a lista de questões
        return questaoDAO.listarTodas();
    }
}