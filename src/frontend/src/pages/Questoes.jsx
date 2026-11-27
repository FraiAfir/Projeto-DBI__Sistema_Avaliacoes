import { useEffect, useState } from "react";
import api from "../api";

export default function Questoes(){
    // ESTADOS
    const [pergunta,     setPergunta    ] = useState("        ");
    const [tipo,         setTipo        ] = useState("OBJETIVA");
    const [alternativas, setAlternativas] = useState([
    { texto: "", status: true },
    { texto: "", status: false },
    { texto: "", status: false },
    { texto: "", status: false },
    ]);
    const [lista,    setLista   ] = useState([]);
    const [mensagem, setMensagem] = useState("");



    // FUNÇÕES
    // Função 1: Carregar a lista de questões do backend
    async function carregarQuestoes(){
        const res = await api.get("/questoes");
        setLista(res.data);
    }
    useEffect(() => {carregarQuestoes();}, []); 

    // Função 2: Atualizar o texto de uma alternativa específica
    function atualizarTextoAlt(index, texto){
        setAlternativas((prev) => prev.map((alt, i) => (i === index ? { ...alt, texto } : alt)));
    }

    // Função 3: Marcar uma alternativa como correta
    function marcarCorreta(index){
        setAlternativas((prev) => prev.map((alt, i) => ({ ...alt, status: i === index })));
    }

    // Função 4: Salvar a questão no backend
    async function handleSalvar(e){
        // 1: Evitar o comportamento padrão do formulário e limpar a mensagem de sucesso
        e.preventDefault();
        setMensagem("");

        // 2: Preparar os dados para envio
        const payload = { // Dados da questão
            pergunta,
            tipo,
            alternativas: tipo === "OBJETIVA" ? alternativas : [],
        };

        // 3: Enviar os dados para o backend e atualizar a lista de questões
        await api.post("/questoes", payload);
        setPergunta("");
        setAlternativas([
            { texto: "", status: true },
            { texto: "", status: false },
            { texto: "", status: false },
            { texto: "", status: false },
        ]);
        setMensagem("Questão cadastrada com sucesso!");

        // 4: Recarregar a lista de questões
        carregarQuestoes();
    }



    // RENDERIZAÇÃO
    return(
    <div>
        <div className="card">
        <h2>Cadastrar Nova Questão</h2>
        <form onSubmit={handleSalvar}>
            <label>Tipo de Questão</label>
            <select value={tipo} onChange={(e) => setTipo(e.target.value)}>
            <option value="OBJETIVA">Objetiva (Múltipla Escolha)</option>
            <option value="LIVRE">Texto Livre (Discursiva)</option>
            </select>

            <label>Enunciado da Pergunta</label>
            <textarea
                rows={3}
                value={pergunta}
                onChange={(e) => setPergunta(e.target.value)}
                required
            />

            {tipo === "OBJETIVA" && (
            <div className="box-alternativas">
                <label>Alternativas (marque a correta)</label>
                {alternativas.map((alt, idx) => (
                <div key={idx} className="row" style={{ marginBottom: 8 }}>
                    <input
                        type="radio"
                        name="correta"
                        checked={alt.status}
                        onChange={() => marcarCorreta(idx)}
                    />
                    <strong>{String.fromCharCode(65 + idx)})</strong>
                    <input
                        type="text"
                        value={alt.texto}
                        onChange={(e) => atualizarTextoAlt(idx, e.target.value)}
                        placeholder={`Texto da alternativa ${String.fromCharCode(65 + idx)}`}
                        required
                    />
                </div>
                ))}
            </div>
            )}

            <button type="submit" className="btn-primary">Salvar Questão</button>
            {mensagem && <p className="sucesso">{mensagem}</p>}
        </form>
        </div>

        <div className="card">
        <h3>Banco de Questões Cadastradas ({lista.length})</h3>
        <table>
            <thead>
            <tr>
                <th>Cód.</th>
                <th>Tipo</th>
                <th>Pergunta</th>
            </tr>
            </thead>
            <tbody>
            {lista.map((q) => (
                <tr key={q.codigo}>
                <td>{q.codigo}</td>
                <td><span className="badge">{q.tipo}</span></td>
                <td>{q.pergunta}</td>
                </tr>
            ))}
            </tbody>
        </table>
        </div>
    </div>
    );
}