package BDI.SistemaAvaliacao.controller;

import BDI.SistemaAvaliacao.dao.RelatorioDAO;
import BDI.SistemaAvaliacao.model.RelatorioAvaliacaoDTO;
import BDI.SistemaAvaliacao.model.RelatorioDesempenhoAlunoDTO;
import BDI.SistemaAvaliacao.model.RelatorioPendenciaDTO;
import BDI.SistemaAvaliacao.model.RelatorioRankingQuestaoDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "http://localhost:5173")
public class RelatorioController{
    // ATRIBUTOS
    private final RelatorioDAO relatorioDAO;



    // CONSTRUTOR
    public RelatorioController(RelatorioDAO relatorioDAO) {this.relatorioDAO = relatorioDAO;}



    // MÉTODOS
    // Método 1: Retornar a média de notas por avaliação, com filtros opcionais de data e título
    // Ex: GET /api/relatorios/medias?dataInicio=2026-01-01&dataFim=2026-12-31&titulo=Prova
    @GetMapping("/medias")
    public List<RelatorioAvaliacaoDTO> mediaPorAvaliacao(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String titulo){
        return relatorioDAO.buscarMediaPorAvaliacao(dataInicio, dataFim, titulo);
    }

    // Método 2: Retornar a evolução do desempenho de um aluno ao longo do tempo
    // Ex: GET /api/relatorios/evolucao-aluno/20260001
    @GetMapping("/evolucao-aluno/{matricula}")
    public List<RelatorioDesempenhoAlunoDTO> evolucaoAluno(@PathVariable String matricula){
        return relatorioDAO.buscarEvolucaoAluno(matricula);
    }
    
    // Método 3: Retornar o ranking das questões mais erradas em uma ou mais avaliações
    // Ex: GET /api/relatorios/ranking-questoes?avaliacoes=1,2,3
    @GetMapping("/ranking-questoes")
    public List<RelatorioRankingQuestaoDTO> rankingQuestoes(
            @RequestParam(required = false) List<Integer> avaliacoes){
        return relatorioDAO.buscarRankingQuestoesMaisErradas(avaliacoes);
    }

    // Método 4: Retornar os alunos que ainda não responderam cada avaliação, com filtro opcional por código de avaliação
    // Ex: GET /api/relatorios/pendentes?codigoAvaliacao=1
    @GetMapping("/pendentes")
    public List<RelatorioPendenciaDTO> pendentes(
            @RequestParam(required = false) Integer codigoAvaliacao){
        return relatorioDAO.buscarAlunosPendentes(codigoAvaliacao);
    }
}