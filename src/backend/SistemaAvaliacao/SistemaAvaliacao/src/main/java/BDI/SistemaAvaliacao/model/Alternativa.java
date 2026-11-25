package BDI.SistemaAvaliacao.model;

public class Alternativa{
    // ATRIBUTOS
    private Integer codigo;     // 1, 2, 3, 4... (PK composta com codigo_questao)
    private String  texto;
    private boolean status;     // true = alternativa correta



    // GETTERS & SETTERS
    public Integer getCodigo() {return codigo;}
    public String  getTexto () {return texto; }
    public boolean getStatus() {return status;}

    public void setCodigo(Integer codigo) {this.codigo = codigo;}
    public void setTexto (String texto  ) {this.texto = texto;  }
    public void setStatus(boolean status) {this.status = status;}
}