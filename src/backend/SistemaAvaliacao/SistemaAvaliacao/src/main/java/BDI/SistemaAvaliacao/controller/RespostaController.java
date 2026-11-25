package BDI.SistemaAvaliacao.controller;

import BDI.SistemaAvaliacao.dao.AvaliacaoDAO;
import BDI.SistemaAvaliacao.dao.RespostaDAO;
import BDI.SistemaAvaliacao.model.SubmissaoAvaliacao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/respostas")
@CrossOrigin(origins = "http://localhost:5173")
public class RespostaController{
    // ATRIBUTOS
    private final RespostaDAO respostaDAO;
    private final AvaliacaoDAO avaliacaoDAO;



    // CONSTRUTOR
    public RespostaController(RespostaDAO respostaDAO, AvaliacaoDAO avaliacaoDAO){
        this.respostaDAO = respostaDAO;
        this.avaliacaoDAO = avaliacaoDAO;
    }



    // MÉTODOS
    // Método 1: Registrar as respostas de um aluno para uma avaliação (RF5)
    @PostMapping
    public ResponseEntity<Void> responder(@RequestBody SubmissaoAvaliacao submissao){
        // 1: Chama o método registrarRespostasAluno() do DAO para salvar as respostas no banco de dados
        respostaDAO.registrarRespostasAluno(submissao);

        // 2: Retorna uma resposta HTTP 201 (Created) sem corpo
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Método 2: Listar todas as respostas livres pendentes de correção (RF6)
    @GetMapping("/pendentes")
    public List<Resposta> listarPendentes(){
        // 1: Chama o método listarRespostasLivresPendentes() do DAO para obter a lista de respostas pendentes
        return avaliacaoDAO.listarRespostasLivresPendentes();
    }

    // Método 3: Corrigir uma resposta livre de um aluno, atribuindo uma nota (RF6)
    @PutMapping("/corrigir")
    public ResponseEntity<Void> corrigirQuestaoLivre(@RequestBody Map<String, Object> payload){
        // 1: Extrair os parâmetros do corpo da requisição
        Integer codAvaliacao = Integer.valueOf(payload.get("codigoAvaliacao").toString());
        Integer codQuestao   = Integer.valueOf(payload.get("codigoQuestao").toString());
        String matricula     = payload.get("matricula").toString();
        BigDecimal nota      = new BigDecimal(payload.get("nota").toString());

        // 2: Chama o método atribuirNotaQuestaoLivre() do DAO para atualizar a nota da resposta no banco de dados
        respostaDAO.atribuirNotaQuestaoLivre(codAvaliacao, codQuestao, matricula, nota);

        // 3: Retorna uma resposta HTTP 200 (OK) sem corpo
        return ResponseEntity.ok().build();
    }
}