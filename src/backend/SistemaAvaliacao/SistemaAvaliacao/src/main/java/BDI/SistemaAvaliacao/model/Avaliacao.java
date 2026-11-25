package BDI.SistemaAvaliacao.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Avaliacao{
    // ATRIBUTOS
    private Integer             codigo;
    private String              matriculaProfessor;
    private String              titulo;
    private String              descricao;
    private LocalDate           data;
    private LocalTime           horarioInicio;
    private LocalTime           horarioFim;
    private List<ItemCompoe>    questoes;           // Vai para a tabela 'compoe'
    private List<String>        matriculasAlunos;   // Vai para a tabela 'faz'


    
    // GETTERS & SETTERS
    public Integer          getCodigo            () {return codigo;            }
    public String           getMatriculaProfessor() {return matriculaProfessor;}
    public String           getTitulo            () {return titulo;            }
    public String           getDescricao         () {return descricao;         }
    public LocalDate        getData              () {return data;              }
    public LocalTime        getHorarioInicio     () {return horarioInicio;     }
    public LocalTime        getHorarioFim        () {return horarioFim;        }
    public List<ItemCompoe> getQuestoes          () {return questoes;          }
    public List<String>     getMatriculasAlunos  () {return matriculasAlunos;  }

    public void setCodigo            (Integer codigo               ) {this.codigo = codigo;                        }
    public void setMatriculaProfessor(String matriculaProfessor    ) {this.matriculaProfessor = matriculaProfessor;}
    public void setTitulo            (String titulo                ) {this.titulo = titulo;                        }
    public void setDescricao         (String descricao             ) {this.descricao = descricao;                  }
    public void setData              (LocalDate data               ) {this.data = data;                            }
    public void setHorarioInicio     (LocalTime horarioInicio      ) {this.horarioInicio = horarioInicio;          }
    public void setHorarioFim        (LocalTime horarioFim         ) {this.horarioFim = horarioFim;                }
    public void setQuestoes          (List<ItemCompoe> questoes    ) {this.questoes = questoes;                    }
    public void setMatriculasAlunos  (List<String> matriculasAlunos) {this.matriculasAlunos = matriculasAlunos;    }
}