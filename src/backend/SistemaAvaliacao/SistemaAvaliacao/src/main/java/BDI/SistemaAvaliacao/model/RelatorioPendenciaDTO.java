package BDI.SistemaAvaliacao.model;

import java.time.LocalDate;

public class RelatorioPendenciaDTO{
    // ATRIBUTOS
    private Integer     codigoAvaliacao;
    private String      tituloAvaliacao;
    private LocalDate   dataAvaliacao;
    private String      matriculaAluno;
    private String      nomeAluno;
    private String      situacao;


    
    // GETTERS & SETTERS
    public Integer   getCodigoAvaliacao() {return codigoAvaliacao;}
    public String    getTituloAvaliacao() {return tituloAvaliacao;}
    public LocalDate getDataAvaliacao  () {return dataAvaliacao;  }
    public String    getMatriculaAluno () {return matriculaAluno; }
    public String    getNomeAluno      () {return nomeAluno;      }
    public String    getSituacao       () {return situacao;       }

    public void setCodigoAvaliacao  (Integer codigoAvaliacao) {this.codigoAvaliacao = codigoAvaliacao;}
    public void setTituloAvaliacao  (String tituloAvaliacao ) {this.tituloAvaliacao = tituloAvaliacao;}
    public void setDataAvaliacao    (LocalDate dataAvaliacao) {this.dataAvaliacao   = dataAvaliacao;  }
    public void setMatriculaAluno   (String matriculaAluno  ) {this.matriculaAluno  = matriculaAluno; }
    public void setNomeAluno        (String nomeAluno       ) {this.nomeAluno       = nomeAluno;      }
    public void setSituacao         (String situacao        ) {this.situacao        = situacao;       }
}