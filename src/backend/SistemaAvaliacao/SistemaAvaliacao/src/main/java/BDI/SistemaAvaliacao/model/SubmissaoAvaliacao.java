package BDI.SistemaAvaliacao.model;

import java.util.List;

public class SubmissaoAvaliacao{
    // ATRIBUTOS
    private Integer             codigoAvaliacao;
    private String              matricula;
    private List<RespostaItem>  respostas;



    // CONSTRUTORES
    public SubmissaoAvaliacao() {}



    // GETTERS & SETTERS
    public Integer              getCodigoAvaliacao() {return codigoAvaliacao;}
    public String               getMatricula      () {return matricula;      }
    public List<RespostaItem>   getRespostas      () {return respostas;      }

    public void setCodigoAvaliacao  (Integer codigoAvaliacao     ) {this.codigoAvaliacao = codigoAvaliacao;}
    public void setMatricula        (String matricula            ) {this.matricula       = matricula;      }
    public void setRespostas        (List<RespostaItem> respostas) {this.respostas       = respostas;      }
}