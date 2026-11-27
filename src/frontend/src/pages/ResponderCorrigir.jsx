import { useEffect, useState } from "react";
import api from "../api";

export default function ResponderCorrigir({ usuario }){
    // ESTADOS
    const [minhasProvas,      setMinhasProvas     ] = useState([]);
    const [provaAtiva,        setProvaAtiva       ] = useState(null);
    const [questoesProva,     setQuestoesProva    ] = useState([]);
    const [respostas,         setRespostas        ] = useState({});
    const [pendentesCorrecao, setPendentesCorrecao] = useState([]);
    const [notasDigitadas,    setNotasDigitadas   ] = useState({});
    const [msg,               setMsg              ] = useState("");



    // FUNÇÕES
    // Função 1: Carregar as avaliações do aluno ou as respostas pendentes de correção do professor
    async function carregarDados(){
        // 1: Se o usuário for aluno, buscar as avaliações que ele precisa responder
        if(usuario.perfil === "ALUNO"){
            const res = await api.get(`/avaliacoes/aluno/${usuario.matricula}`);
            setMinhasProvas(res.data);
        }

        // 2: Se o usuário for professor, buscar as respostas discursivas pendentes de correção
        else{
            const res = await api.get("/respostas/pendentes");
            setPendentesCorrecao(res.data);
        }
    }
    useEffect(() => {carregarDados();}, [usuario]);

    // Função 2: Abrir uma prova para o aluno responder
    async function abrirProva(avaliacao){
        // 1: Limpar mensagens, armazenar a avaliação ativa e limpar as respostas do estado
        setMsg("");
        setProvaAtiva(avaliacao);
        setRespostas({});

        // 2: Buscar as questões da avaliação selecionada
        const res = await api.get(`/avaliacoes/${avaliacao.codigo}/questoes`);

        // 3: Armazenar as questões da avaliação no estado
        setQuestoesProva(res.data);
    }

    // Função 3: Enviar as respostas do aluno para o backend
    async function enviarProva(e){
        // 1: Evitar o comportamento padrão do formulário
        e.preventDefault();

        // 2: Preparar a lista de respostas para envio
        const listaRespostas = questoesProva.map((q) => ({
            codigoQuestao: q.codigo_questao,
            resposta: respostas[q.codigo_questao] || "",
        }));

        // 3: Enviar as respostas para o backend
        await api.post("/respostas", {
            codigoAvaliacao: provaAtiva.codigo,
            matricula: usuario.matricula,
            respostas: listaRespostas,
        });

        // 4: Limpar a avaliação ativa e exibir uma mensagem de sucesso
        setProvaAtiva(null);
        setMsg("Respostas enviadas! Questões objetivas corrigidas automaticamente.");

        // 5: Recarregar as avaliações do aluno
        carregarDados();
    }

    // Função 4: Salvar a nota digitada pelo professor para uma questão discursiva
    async function salvarNotaLivre(item){
        // 1: Validar se a nota digitada é um número válido e está dentro do intervalo permitido
        const chave = `${item.codigo_avaliacao}-${item.codigo_questao}-${item.matricula}`;
        const nota  = parseFloat(notasDigitadas[chave]);

        // 2: Se a nota for inválida, exibir um alerta e retornar
        if(isNaN(nota) || nota < 0 || nota > Number(item.valor_maximo)){
            alert(`Informe uma nota entre 0 e ${item.valor_maximo}`);
            return;
        }

        // 3: Enviar a nota para o backend e exibir uma mensagem de sucesso
        await api.put("/respostas/corrigir", {
            codigoAvaliacao: item.codigo_avaliacao,
            codigoQuestao: item.codigo_questao,
            matricula: item.matricula,
            nota,
        });
        setMsg(`Nota ${nota.toFixed(2)} lançada para ${item.nome_aluno}!`);

        // 5: Recarregar as respostas pendentes de correção
        carregarDados();
    }

    // Se o usuário for professor, exibir a lista de respostas discursivas pendentes de correção
    if(usuario.perfil === "PROFESSOR"){
        return(
        <div className="card">
            <h2>Correção de Questões Discursivas Pendentes (RF6)</h2>
            {msg && <p className="sucesso">{msg}</p>}
            {pendentesCorrecao.length === 0 ? (
            <p className="muted">Nenhuma resposta discursiva aguardando correção.</p>
            ) : (
            pendentesCorrecao.map((item) => {
                const chave = `${item.codigo_avaliacao}-${item.codigo_questao}-${item.matricula}`;
                return (
                <div key={chave} className="box-alternativas" style={{ marginBottom: 14 }}>
                    <p><strong>Prova:</strong> {item.titulo_avaliacao} | <strong>Aluno:</strong> {item.nome_aluno} ({item.matricula})</p>
                    <p><strong>Pergunta:</strong> {item.pergunta}</p>
                    <p><strong>Resposta do Aluno:</strong> <em>"{item.resposta}"</em></p>
                    <div className="row">
                    <input
                        type="number"
                        step="0.25"
                        min="0"
                        max={item.valor_maximo}
                        style={{ width: 120 }}
                        placeholder={`Máx: ${item.valor_maximo}`}
                        value={notasDigitadas[chave] ?? ""}
                        onChange={(e) =>
                        setNotasDigitadas((prev) => ({ ...prev, [chave]: e.target.value }))
                        }
                    />
                    <button type="button" className="btn-primary" onClick={() => salvarNotaLivre(item)}>
                        Atribuir Nota (máx. {item.valor_maximo})
                    </button>
                    </div>
                </div>
                );
            })
            )}
        </div>
        );
    }

    // Se o usuário for aluno, exibir a lista de avaliações disponíveis para resposta
    return(
        <div>
        {msg && <div className="card"><p className="sucesso">{msg}</p></div>}

        {!provaAtiva ? (
            <div className="card">
            <h2>Minhas Avaliações (RF5)</h2>
            <table>
                <thead>
                <tr>
                    <th>Avaliação</th>
                    <th>Data</th>
                    <th>Horário</th>
                    <th>Status</th>
                    <th>Nota Final</th>
                    <th>Ação</th>
                </tr>
                </thead>
                <tbody>
                {minhasProvas.map((p) => (
                    <tr key={p.codigo}>
                    <td>{p.titulo}</td>
                    <td>{p.data}</td>
                    <td>{p.horario_inicio} - {p.horario_fim}</td>
                    <td>{p.ja_respondeu ? "Respondida" : "Pendente"}</td>
                    <td>{p.nota_final !== null ? Number(p.nota_final).toFixed(2) : "-"}</td>
                    <td>
                        {!p.ja_respondeu && (
                        <button className="btn-primary" onClick={() => abrirProva(p)}>
                            Responder
                        </button>
                        )}
                    </td>
                    </tr>
                ))}
                </tbody>
            </table>
            </div>
        ) : (
            <div className="card">
            <h2>Respondendo: {provaAtiva.titulo}</h2>
            <form onSubmit={enviarProva}>
                {questoesProva.map((q, idx) => (
                <div key={q.codigo_questao} className="box-alternativas" style={{ marginBottom: 14 }}>
                    <p>
                    <strong>Questão {idx + 1} ({q.valor} pts):</strong> {q.pergunta}
                    </p>
                    {q.tipo === "OBJETIVA" ? (
                    q.alternativas.map((alt) => (
                        <label key={alt.codigo} className="item-check" style={{ fontWeight: "normal" }}>
                        <input
                            type="radio"
                            name={`q-${q.codigo_questao}`}
                            value={alt.letra}
                            checked={respostas[q.codigo_questao] === alt.letra}
                            onChange={(e) =>
                            setRespostas((prev) => ({ ...prev, [q.codigo_questao]: e.target.value }))
                            }
                            required
                        />
                        <span><strong>{alt.letra})</strong> {alt.texto}</span>
                        </label>
                    ))
                    ) : (
                    <textarea
                        rows={3}
                        placeholder="Digite sua resposta..."
                        value={respostas[q.codigo_questao] || ""}
                        onChange={(e) =>
                        setRespostas((prev) => ({ ...prev, [q.codigo_questao]: e.target.value }))
                        }
                        required
                    />
                    )}
                </div>
                ))}
                <div className="row">
                <button type="submit" className="btn-primary">Finalizar e Enviar Prova</button>
                <button type="button" onClick={() => setProvaAtiva(null)}>Cancelar</button>
                </div>
            </form>
            </div>
        )}
        </div>
    );
}