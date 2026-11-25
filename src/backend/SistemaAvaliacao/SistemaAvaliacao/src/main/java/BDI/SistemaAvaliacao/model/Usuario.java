package BDI.SistemaAvaliacao.model;

import java.time.LocalDate;

public class Usuario{
    private String      matricula;
    private String      cpf;
    private String      nome;
    private LocalDate   dataNascimento;
    private String      perfil;             // "PROFESSOR" ou "ALUNO"

    public String       getMatricula     () {return matricula;     }
    public String       getCpf           () {return cpf;           }
    public String       getNome          () {return nome;          }
    public LocalDate    getDataNascimento() {return dataNascimento;}
    public String       getPerfil        () {return perfil;        }

    public void setMatricula        (String matricula        ) {this.matricula      = matricula;      }
    public void setCpf              (String cpf              ) {this.cpf            = cpf;            }
    public void setNome             (String nome             ) {this.nome           = nome;           }
    public void setDataNascimento   (LocalDate dataNascimento) {this.dataNascimento = dataNascimento; }
    public void setPerfil           (String perfil           ) {this.perfil         = perfil;         }
}