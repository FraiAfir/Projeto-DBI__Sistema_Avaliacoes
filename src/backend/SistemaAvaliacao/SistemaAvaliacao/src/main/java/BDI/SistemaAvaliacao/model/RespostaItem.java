package BDI.SistemaAvaliacao.model;

import java.math.BigDecimal;

public class RespostaItem {
    // ATRIBUTOS
    private Integer     codigoQuestao;
    private String      resposta;
    private BigDecimal  notaObtida;



    // CONSTRUTORES
    public RespostaItem() {}



    // GETTERS & SETTERS
    public Integer      getCodigoQuestao() {return codigoQuestao;}
    public String       getResposta     () {return resposta;     }
    public BigDecimal   getNotaObtida   () {return notaObtida;   }
    
    public void setCodigoQuestao(Integer codigoQuestao) {this.codigoQuestao = codigoQuestao;}
    public void setResposta     (String resposta      ) {this.resposta      = resposta;     }
    public void setNotaObtida   (BigDecimal notaObtida) {this.notaObtida    = notaObtida;   }
}