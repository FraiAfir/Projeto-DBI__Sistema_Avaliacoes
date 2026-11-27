import { useState } from "react";
import api from "../api";

export default function Login({ onLogin }){
    // ESTADOS
    const [matricula, setMatricula] = useState("");
    const [erro,      setErro     ] = useState("");



    // FUNÇÕES
    // FunÇÃO 1: Tratar o envio do formulário
    async function handleSubmit(e){
        // 1: Evitar o comportamento padrão do formulário e limpar a mensagem de erro
        e.preventDefault();
        setErro("");

        // 2: Enviar a matrícula para o backend e chamar a função onLogin com os dados do usuário
        try{
            const res = await api.post("/login", {matricula: matricula.trim()});
            onLogin(res.data);
        }

        // 3: Caso ocorra algum erro, exibir a mensagem de erro
        catch {setErro("Matrícula não encontrada nas tabelas de professor ou aluno.");}
    }



    // RENDERIZAÇÃO
    return(
        <div className="card" style={{ maxWidth: 460, margin: "40px auto" }}>
        <h2>Acesso ao Sistema de Avaliações</h2>
        <p className="muted">Informe sua matrícula.</p>

        <form onSubmit={handleSubmit}>
            <label>Matrícula</label>
            <input
                type="text"
                value={matricula}
                onChange={(e) => setMatricula(e.target.value)}
                placeholder="Ex: 20260020 ou 20260001"
                required
            />
            {erro && <p className="erro">{erro}</p>}
            <button type="submit" className="btn-primary">Entrar</button>
        </form>

        <div className="atalhos">
            <small>Acesso rápido (dados do carga.sql):</small>
            <div className="row">
            <button type="button" onClick={() => setMatricula("20260020")}>
                Prof. Vitória (20260020)
            </button>
            <button type="button" onClick={() => setMatricula("20260001")}>
                Aluna Ana (20260001)
            </button>
            <button type="button" onClick={() => setMatricula("20260015")}>
                Aluno Otávio (20260015)
            </button>
            </div>
        </div>
        </div>
    );
}