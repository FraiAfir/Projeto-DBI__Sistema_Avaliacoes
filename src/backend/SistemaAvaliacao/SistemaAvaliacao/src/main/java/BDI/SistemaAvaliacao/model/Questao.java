package BDI.SistemaAvaliacao.model;

import java.util.List;

public class Questao{
    // ATRIBUTOS
    private Integer             codigo;
    private String              pergunta;
    private String              tipo;               // "OBJETIVA" ou "LIVRE" (define qual subclasse inserir)
    private List<Alternativa>   alternativas;       // Preenchida apenas quando tipo = "OBJETIVA"


    
    // GETTERS & SETTERS
    public Integer           getCodigo      () {return codigo;      }
    public String            getPergunta    () {return pergunta;    }
    public String            getTipo        () {return tipo;        }
    public List<Alternativa> getAlternativas() {return alternativas;}

    public void setCodigo      (Integer codigo                ) {this.codigo       = codigo;      }
    public void setPergunta    (String pergunta               ) {this.pergunta     = pergunta;    }
    public void setTipo        (String tipo                   ) {this.tipo         = tipo;        }
    public void setAlternativas(List<Alternativa> alternativas) {this.alternativas = alternativas;}
}