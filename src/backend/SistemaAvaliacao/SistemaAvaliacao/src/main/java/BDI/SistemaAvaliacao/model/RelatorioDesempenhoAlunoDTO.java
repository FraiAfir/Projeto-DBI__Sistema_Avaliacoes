package BDI.SistemaAvaliacao.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RelatorioDesempenhoAlunoDTO{
    // ATRIBUTOS
    private String     matriculaAluno;
    private String     nomeAluno;
    private Integer    codigoAvaliacao;
    private String     tituloAvaliacao;
    private LocalDate  dataAvaliacao;
    private BigDecimal notaAluno;
    private BigDecimal mediaTurma;
    private BigDecimal diferencaParaMedia;



    // GETTERS & SETTERS
    public String       getMatriculaAluno    () {return matriculaAluno;    }
    public String       getNomeAluno         () {return nomeAluno;         }
    public Integer      getCodigoAvaliacao   () {return codigoAvaliacao;   }
    public String       getTituloAvaliacao   () {return tituloAvaliacao;   }
    public LocalDate    getDataAvaliacao     () {return dataAvaliacao;     }
    public BigDecimal   getNotaAluno         () {return notaAluno;         }
    public BigDecimal   getMediaTurma        () {return mediaTurma;        }
    public BigDecimal   getDiferencaParaMedia() {return diferencaParaMedia;}

    public void setMatriculaAluno       (String matriculaAluno        ) {this.matriculaAluno     = matriculaAluno;    }
    public void setNomeAluno            (String nomeAluno             ) {this.nomeAluno          = nomeAluno;         }
    public void setCodigoAvaliacao      (Integer codigoAvaliacao      ) {this.codigoAvaliacao    = codigoAvaliacao;   }
    public void setTituloAvaliacao      (String tituloAvaliacao       ) {this.tituloAvaliacao    = tituloAvaliacao;   }
    public void setDataAvaliacao        (LocalDate dataAvaliacao      ) {this.dataAvaliacao      = dataAvaliacao;     }
    public void setNotaAluno            (BigDecimal notaAluno         ) {this.notaAluno          = notaAluno;         }
    public void setMediaTurma           (BigDecimal mediaTurma        ) {this.mediaTurma         = mediaTurma;        }
    public void setDiferencaParaMedia   (BigDecimal diferencaParaMedia) {this.diferencaParaMedia = diferencaParaMedia;}
}