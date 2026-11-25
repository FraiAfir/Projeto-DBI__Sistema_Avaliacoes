package BDI.SistemaAvaliacao.model;

import java.math.BigDecimal;

public class RelatorioRankingQuestaoDTO{
    // ATRIBUTOS
    private Integer     posicaoRanking;
    private Integer     codigoQuestao;
    private String      resumoPergunta;
    private Integer     totalRespostasCorrigidas;
    private Integer     totalErrosOuParciais;
    private BigDecimal  percentualErro;
    private BigDecimal  mediaNotaObtida;



    // GETTERS & SETTERS
    public Integer    getPosicaoRanking          () {return posicaoRanking;          }
    public Integer    getCodigoQuestao           () {return codigoQuestao;           }
    public String     getResumoPergunta          () {return resumoPergunta;          }
    public Integer    getTotalRespostasCorrigidas() {return totalRespostasCorrigidas;}
    public Integer    getTotalErrosOuParciais    () {return totalErrosOuParciais;    }
    public BigDecimal getPercentualErro          () {return percentualErro;          }
    public BigDecimal getMediaNotaObtida         () {return mediaNotaObtida;         }

    public void setPosicaoRanking           (Integer posicaoRanking          ) {this.posicaoRanking           = posicaoRanking;          }
    public void setCodigoQuestao            (Integer codigoQuestao           ) {this.codigoQuestao            = codigoQuestao;           }
    public void setResumoPergunta           (String resumoPergunta           ) {this.resumoPergunta           = resumoPergunta;          }
    public void setTotalRespostasCorrigidas (Integer totalRespostasCorrigidas) {this.totalRespostasCorrigidas = totalRespostasCorrigidas;}
    public void setTotalErrosOuParciais     (Integer totalErrosOuParciais    ) {this.totalErrosOuParciais     = totalErrosOuParciais;    }
    public void setPercentualErro           (BigDecimal percentualErro       ) {this.percentualErro           = percentualErro;          }
    public void setMediaNotaObtida          (BigDecimal mediaNotaObtida      ) {this.mediaNotaObtida          = mediaNotaObtida;         }
}