package BDI.SistemaAvaliacao.model;

import java.math.BigDecimal;

public class ItemCompoe{
    // ATRIBUTOS
    private Integer    codigoQuestao;
    private BigDecimal valor;           // NUMERIC(5, 2) > 0



    // GETTERS & SETTERS
    public Integer    getCodigoQuestao() {return codigoQuestao;}
    public BigDecimal getValor        () {return valor;        }

    public void setCodigoQuestao(Integer codigoQuestao) {this.codigoQuestao = codigoQuestao;}
    public void setValor        (BigDecimal valor     ) {this.valor = valor;                }
}