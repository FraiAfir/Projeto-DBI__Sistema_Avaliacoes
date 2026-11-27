import { useEffect, useState } from "react";
import api from "../api";

export default function Avaliacoes({ usuario }){
    // ESTADOS
    const [titulo,         setTitulo        ] = useState("");
    const [descricao,      setDescricao     ] = useState("");
    const [data,           setData          ] = useState("2026-11-27");
    const [horarioInicio,  setHorarioInicio ] = useState("19:15");
    const [horarioFim,     setHorarioFim    ] = useState("21:00");
    const [questoesDisp,   setQuestoesDisp  ] = useState([]);
    const [alunosDisp,     setAlunosDisp    ] = useState([]);
    const [selecionadas,   setSelecionadas  ] = useState({});
    const [alunosMarcados, setAlunosMarcados] = useState([]);
    const [mensagem,       setMensagem      ] = useState("");
    useEffect(() => {
        api.get("/questoes").then((r) => setQuestoesDisp(r.data));
        api.get("/alunos").then((r)   => setAlunosDisp  (r.data));
    }, []);



    // FUNÇÕES
    // Função 1: Marcar ou desmarcar uma questão na avaliação
    function toggleQuestao(codigo){
        setSelecionadas((prev) => {
            const copia = { ...prev };
            if  (copia[codigo] !== undefined) delete copia[codigo];
            else copia[codigo] = 2.0;
            return copia;
        });
    }

    // Função 2: Alterar o valor de uma questão na avaliação
    function alterarValorQuestao(codigo, valor){
        setSelecionadas((prev) => ({ ...prev, [codigo]: parseFloat(valor) || 0 }));
    }

    // Função 3: Marcar ou desmarcar um aluno na avaliação
    function toggleAluno(matricula){
        setAlunosMarcados((prev) => prev.includes(matricula) ? prev.filter((m) => m !== matricula) : [...prev, matricula]);
    }
    
    // Função 4: Selecionar todos os alunos
    function selecionarTodosAlunos(){
        if (alunosMarcados.length === alunosDisp.length) setAlunosMarcados([]);
        else                                             setAlunosMarcados(alunosDisp.map((a) => a.matricula));
    }

    // Função 5: Criar a avaliação no backend
    async function handleCriarAvaliacao(e){
        // 1: Evitar o comportamento padrão do formulário e limpar a mensagem de sucesso
        e.preventDefault();
        setMensagem("");

        // 2: Preparar os dados para envio
        const questoesPayload = Object.entries(selecionadas).map(([cod, val]) => ({
            codigoQuestao: Number(cod),
            valor:         val,
        }));

        // 3: Validar se há ao menos 1 questão e 1 aluno selecionados
        if(questoesPayload.length === 0 || alunosMarcados.length === 0){
            alert("Selecione ao menos 1 questão e 1 aluno.");
            return;
        }

        // 4: Enviar os dados para o backend e limpar os campos do formulário
        const totalAlunos = alunosMarcados.length;
        const payload     = {
            matriculaProfessor: usuario.matricula,
                titulo,
                descricao,
                data,
                horarioInicio,
                horarioFim,
            questoes:           questoesPayload,
            matriculasAlunos:   alunosMarcados,
        };

        // 5: Enviar a requisição para criar a avaliação e limpar os campos do formulário
        const res = await api.post("/avaliacoes", payload);
        setTitulo("");
        setDescricao("");
        setSelecionadas({});
        setAlunosMarcados([]);
        setMensagem(`Avaliação #${res.data} criada e atribuída a ${totalAlunos} aluno(s)!`);
    }

    // Cálculo do total de pontos da avaliação
    const somaPontos = Object.values(selecionadas).reduce((acc, v) => acc + v, 0);



    // RENDERIZAÇÃO
    return(
        <div className="card">
        <h2>Montar Nova Avaliação (RF2, RF3 e RF4)</h2>
        <form onSubmit={handleCriarAvaliacao}>
            <label>Título da Avaliação</label>
            <input
                type="text"
                value={titulo}
                onChange={(e) => setTitulo(e.target.value)}
                placeholder="Ex: Prova Substitutiva de Banco de Dados"
                required
            />

            <label>Descrição</label>
            <input
                type="text"
                value={descricao}
                onChange={(e) => setDescricao(e.target.value)}
                placeholder="Conteúdo cobrado na avaliação"
            />

            <div className="row">
            <div style={{ flex: 1 }}>
                <label>Data</label>
                <input type="date" value={data} onChange={(e) => setData(e.target.value)} required />
            </div>
            <div style={{ flex: 1 }}>
                <label>Início</label>
                <input type="time" value={horarioInicio} onChange={(e) => setHorarioInicio(e.target.value)} required />
            </div>
            <div style={{ flex: 1 }}>
                <label>Fim</label>
                <input type="time" value={horarioFim} onChange={(e) => setHorarioFim(e.target.value)} required />
            </div>
            </div>

            <h3>1. Escolher Questões e Valores (Total: {somaPontos.toFixed(2)} pts)</h3>
            <div className="scroll-box">
            {questoesDisp.map((q) => {
                const marcada = selecionadas[q.codigo] !== undefined;
                return (
                <div key={q.codigo} className="item-check">
                    <input
                        type="checkbox"
                        checked={marcada}
                        onChange={() => toggleQuestao(q.codigo)}
                    />
                    <span style={{ flex: 1 }}>
                    <strong>#{q.codigo}</strong> [{q.tipo}] — {q.pergunta}
                    </span>
                    {marcada && (
                    <input
                        type="number"
                        step="0.5"
                        min="0.5"
                        style={{ width: 80 }}
                        value={selecionadas[q.codigo]}
                        onChange={(e) => alterarValorQuestao(q.codigo, e.target.value)}
                    />
                    )}
                </div>
                );
            })}
            </div>

            <div className="row" style={{ justifyContent: "space-between", marginTop: 16 }}>
            <h3>2. Indicar Alunos ({alunosMarcados.length} selecionados)</h3>
            <button type="button" onClick={selecionarTodosAlunos}>
                Marcar / Desmarcar Todos
            </button>
            </div>
            <div className="scroll-box">
            {alunosDisp.map((a) => (
                <div key={a.matricula} className="item-check">
                <input
                    type="checkbox"
                    checked={alunosMarcados.includes(a.matricula)}
                    onChange={() => toggleAluno(a.matricula)}
                />
                <span>{a.matricula} — {a.nome}</span>
                </div>
            ))}
            </div>

            <button type="submit" className="btn-primary" style={{ marginTop: 16 }}>
                Publicar Avaliação
            </button>
            {mensagem && <p className="sucesso">{mensagem}</p>}
        </form>
        </div>
    );
}