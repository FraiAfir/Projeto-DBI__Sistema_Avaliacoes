import { useEffect, useState } from "react";
import {BarChart, Bar, LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, Legend, ResponsiveContainer} from "recharts";
import api from "../api";

export default function Relatorios(){
    // ESTADOS
    const [medias,       setMedias      ] = useState([]);
    const [filtroTitulo, setFiltroTitulo] = useState("");
    const [alunos,       setAlunos      ] = useState([]);
    const [matriculaSel, setMatriculaSel] = useState("20260001");
    const [evolucao,     setEvolucao    ] = useState([]);
    const [ranking,      setRanking     ] = useState([]);
    const [pendentes,    setPendentes   ] = useState([]);

    

    // FUNÇÕES
    // Função 1: Carregar as médias das avaliações do backend
    async function carregarMedias(){
        // 1: Fazer a requisição para o backend, passando o filtro de título se houver
        const res = await api.get("/relatorios/medias", {params: filtroTitulo ? { titulo: filtroTitulo } : {},});

        // 2: Armazenar os dados recebidos no estado
        setMedias(res.data);
    }

    // Função 2: Carregar a evolução do aluno selecionado
    async function carregarEvolucao(mat){
        // 1: Fazer a requisição para o backend, passando a matrícula do aluno
        const res = await api.get(`/relatorios/evolucao-aluno/${mat}`);

        // 2: Armazenar os dados recebidos no estado
        setEvolucao(res.data);
    }
    useEffect(() => {
        carregarMedias();
        api.get("/alunos").then((r) => setAlunos(r.data));
        carregarEvolucao("20260001");
        api.get("/relatorios/ranking-questoes").then((r) => setRanking(r.data));
        api.get("/relatorios/pendentes").then((r) => setPendentes(r.data));
    }, []);

    // Função 3: Preparar os dados para o gráfico de pendentes por avaliação
    const resumoPendentesGrafico = Object.values(
        // 1: Reduzir a lista de pendentes em um objeto que agrupa por avaliação, contando a quantidade de pendentes
        pendentes.reduce((acc, item) => {
            // 1.1: Se a avaliação ainda não estiver no acumulador, inicializar com título e quantidade 0
            if(!acc[item.codigoAvaliacao]) {acc[item.codigoAvaliacao] = { titulo: item.tituloAvaliacao, qtdPendentes: 0 };}

            // 1.2: Incrementar a quantidade de pendentes para a avaliação atual
            acc[item.codigoAvaliacao].qtdPendentes += 1;

            // 1.3: Retornar o acumulador atualizado
            return acc;
        }, {})
    );

    // RENDERIZAÇÃO
    return(
        <div>
        {/* RELATÓRIO 1 */}
        <div className="card">
            <h2>Relatório 1: Desempenho Geral por Avaliação</h2>
            <div className="row" style={{ marginBottom: 12 }}>
            <input
                type="text"
                placeholder="Filtrar avaliação por título..."
                value={filtroTitulo}
                onChange={(e) => setFiltroTitulo(e.target.value)}
            />
            <button className="btn-primary" onClick={carregarMedias}>Filtrar</button>
            </div>

            <div style={{ width: "100%", height: 280 }}>
            <ResponsiveContainer>
                <BarChart data={medias}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="tituloAvaliacao" />
                <YAxis domain={[0, 10]} />
                <Tooltip />
                <Legend />
                <Bar dataKey="mediaTurma" name="Média da Turma" fill="#2563eb" />
                <Bar dataKey="maiorNota" name="Maior Nota" fill="#16a34a" />
                </BarChart>
            </ResponsiveContainer>
            </div>

            <table>
            <thead>
                <tr>
                <th>Avaliação</th>
                <th>Professor</th>
                <th>Data</th>
                <th>Responderam</th>
                <th>Média</th>
                <th>Menor</th>
                <th>Maior</th>
                </tr>
            </thead>
            <tbody>
                {medias.map((m) => (
                <tr key={m.codigoAvaliacao}>
                    <td>{m.tituloAvaliacao}</td>
                    <td>{m.nomeProfessor}</td>
                    <td>{m.dataAvaliacao}</td>
                    <td>{m.alunosResponderam} / {m.alunosDesignados}</td>
                    <td><strong>{Number(m.mediaTurma).toFixed(2)}</strong></td>
                    <td>{Number(m.menorNota).toFixed(2)}</td>
                    <td>{Number(m.maiorNota).toFixed(2)}</td>
                </tr>
                ))}
            </tbody>
            </table>
        </div>

        {/* RELATÓRIO 2 */}
        <div className="card">
            <h2>Relatório 2: Evolução do Aluno vs. Média da Turma</h2>
            <div className="row" style={{ marginBottom: 12 }}>
            <select
                value={matriculaSel}
                onChange={(e) => {
                setMatriculaSel(e.target.value);
                carregarEvolucao(e.target.value);
                }}
            >
                {alunos.map((a) => (
                <option key={a.matricula} value={a.matricula}>
                    {a.matricula} — {a.nome}
                </option>
                ))}
            </select>
            </div>

            <div style={{ width: "100%", height: 280 }}>
            <ResponsiveContainer>
                <LineChart data={evolucao}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="tituloAvaliacao" />
                <YAxis domain={[0, 10]} />
                <Tooltip />
                <Legend />
                <Line type="monotone" dataKey="notaAluno" name="Nota do Aluno" stroke="#2563eb" strokeWidth={3} />
                <Line type="monotone" dataKey="mediaTurma" name="Média da Turma" stroke="#f59e0b" strokeWidth={2} />
                </LineChart>
            </ResponsiveContainer>
            </div>

            <table>
            <thead>
                <tr>
                <th>Data</th>
                <th>Avaliação</th>
                <th>Nota do Aluno</th>
                <th>Média da Turma</th>
                <th>Diferença</th>
                </tr>
            </thead>
            <tbody>
                {evolucao.map((ev) => (
                <tr key={ev.codigoAvaliacao}>
                    <td>{ev.dataAvaliacao}</td>
                    <td>{ev.tituloAvaliacao}</td>
                    <td><strong>{Number(ev.notaAluno).toFixed(2)}</strong></td>
                    <td>{Number(ev.mediaTurma).toFixed(2)}</td>
                    <td>{Number(ev.diferencaParaMedia).toFixed(2)}</td>
                </tr>
                ))}
            </tbody>
            </table>
        </div>

        {/* RELATÓRIO 3 */}
        <div className="card">
            <h2>Relatório 3: Top 10 Questões com Maior Taxa de Erro</h2>
            <div style={{ width: "100%", height: 280 }}>
            <ResponsiveContainer>
                <BarChart data={ranking}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="codigoQuestao" tickFormatter={(v) => `Q#${v}`} />
                <YAxis unit="%" domain={[0, 100]} />
                <Tooltip />
                <Bar dataKey="percentualErro" name="% de Erro" fill="#dc2626" />
                </BarChart>
            </ResponsiveContainer>
            </div>

            <table>
            <thead>
                <tr>
                <th>Rank</th>
                <th>Questão</th>
                <th>Enunciado</th>
                <th>Respostas</th>
                <th>Erros/Parciais</th>
                <th>% Erro</th>
                </tr>
            </thead>
            <tbody>
                {ranking.map((r) => (
                <tr key={r.codigoQuestao}>
                    <td>#{r.posicaoRanking}</td>
                    <td>Q{r.codigoQuestao}</td>
                    <td>{r.resumoPergunta}...</td>
                    <td>{r.totalRespostasCorrigidas}</td>
                    <td>{r.totalErrosOuParciais}</td>
                    <td><strong>{Number(r.percentualErro).toFixed(1)}%</strong></td>
                </tr>
                ))}
            </tbody>
            </table>
        </div>

        {/* RELATÓRIO 4 */}
        <div className="card">
            <h2>Relatório 4: Alunos Pendentes por Avaliação (LEFT JOIN)</h2>
            {resumoPendentesGrafico.length > 0 && (
            <div style={{ width: "100%", height: 220 }}>
                <ResponsiveContainer>
                <BarChart data={resumoPendentesGrafico}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="titulo" />
                    <YAxis allowDecimals={false} />
                    <Tooltip />
                    <Bar dataKey="qtdPendentes" name="Alunos Pendentes" fill="#7c3aed" />
                </BarChart>
                </ResponsiveContainer>
            </div>
            )}

            <table>
            <thead>
                <tr>
                <th>Avaliação</th>
                <th>Data</th>
                <th>Matrícula</th>
                <th>Aluno</th>
                <th>Situação</th>
                </tr>
            </thead>
            <tbody>
                {pendentes.length === 0 ? (
                <tr><td colSpan="5">Nenhuma pendência encontrada.</td></tr>
                ) : (
                pendentes.map((p) => (
                    <tr key={`${p.codigoAvaliacao}-${p.matriculaAluno}`}>
                    <td>{p.tituloAvaliacao}</td>
                    <td>{p.dataAvaliacao}</td>
                    <td>{p.matriculaAluno}</td>
                    <td>{p.nomeAluno}</td>
                    <td><span className="badge">{p.situacao}</span></td>
                    </tr>
                ))
                )}
            </tbody>
            </table>
        </div>
        </div>
    );
}