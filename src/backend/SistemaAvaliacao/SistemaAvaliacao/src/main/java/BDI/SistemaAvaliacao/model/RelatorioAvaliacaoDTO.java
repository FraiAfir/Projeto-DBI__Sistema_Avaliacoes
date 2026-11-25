package BDI.SistemaAvaliacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioAvaliacaoDTO{
    // ATRIBUTOS
    private Integer     codigoAvaliacao;
    private String      tituloAvaliacao;
    private LocalDate   dataAvaliacao;
    private String      nomeProfessor;
    private BigDecimal  valorTotalProva;
    private Integer     alunosDesignados;
    private Integer     alunosResponderam;
    private BigDecimal  mediaTurma;
    private BigDecimal  menorNota;
    private BigDecimal  maiorNota;



    // GETTERS & SETTERS
    public Integer      getCodigoAvaliacao  () {return codigoAvaliacao;  }
    public String       getTituloAvaliacao  () {return tituloAvaliacao;  }
    public LocalDate    getDataAvaliacao    () {return dataAvaliacao;    }
    public String       getNomeProfessor    () {return nomeProfessor;    }
    public BigDecimal   getValorTotalProva  () {return valorTotalProva;  }
    public Integer      getAlunosDesignados () {return alunosDesignados; }
    public Integer      getAlunosResponderam() {return alunosResponderam;}
    public BigDecimal   getMediaTurma       () {return mediaTurma;       }
    public BigDecimal   getMenorNota        () {return menorNota;        }
    public BigDecimal   getMaiorNota        () {return maiorNota;        }

    public void setCodigoAvaliacao  (Integer codigoAvaliacao   ) {this.codigoAvaliacao   = codigoAvaliacao;  }
    public void setTituloAvaliacao  (String tituloAvaliacao    ) {this.tituloAvaliacao   = tituloAvaliacao;  }
    public void setDataAvaliacao    (LocalDate dataAvaliacao   ) {this.dataAvaliacao     = dataAvaliacao;    }
    public void setNomeProfessor    (String nomeProfessor      ) {this.nomeProfessor     = nomeProfessor;    }
    public void setValorTotalProva  (BigDecimal valorTotalProva) {this.valorTotalProva   = valorTotalProva;  }
    public void setAlunosDesignados (Integer alunosDesignados  ) {this.alunosDesignados  = alunosDesignados; }
    public void setAlunosResponderam(Integer alunosResponderam ) {this.alunosResponderam = alunosResponderam;}
    public void setMediaTurma       (BigDecimal mediaTurma     ) {this.mediaTurma        = mediaTurma;       }
    public void setMenorNota        (BigDecimal menorNota      ) {this.menorNota         = menorNota;        }
    public void setMaiorNota        (BigDecimal maiorNota      ) {this.maiorNota         = maiorNota;        }
}