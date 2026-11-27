import { useState }       from "react";
import Login              from "./pages/Login";
import Questoes           from "./pages/Questoes";
import Avaliacoes         from "./pages/Avaliacoes";
import ResponderCorrigir  from "./pages/ResponderCorrigir";
import Relatorios         from "./pages/Relatorios";

export default function App(){
  // ESTADOS
  const [usuario, setUsuario] = useState(null);
  const [aba,     setAba    ] = useState("relatorios");



  // FUNÇÕES
  // Função 1: Tratar o login do usuário
  function handleLogin(dadosUsuario){
    // 1: Armazenar os dados do usuário no estado
    setUsuario(dadosUsuario);

    // 2: Definir a aba inicial com base no perfil do usuário
    setAba(dadosUsuario.perfil === "PROFESSOR" ? "questoes" : "responder");
  }



  // RENDERIZAÇÃO
  // 1: Se o usuário não estiver logado, exibir a tela de login
  if (!usuario) {return <Login onLogin={handleLogin} />;}

  // 2: Se o usuário estiver logado, exibir a interface principal com base na aba selecionada
  return(
    <div>
      <header className="topbar">
        <div>
          <strong>Sistema de Avaliações — BDI</strong>
          <span style={{ marginLeft: 12, fontSize: 13 }}>
            {usuario.nome} ({usuario.perfil})
          </span>
        </div>

        <nav className="row">
          {usuario.perfil === "PROFESSOR" && (
            <>
              <button onClick={() => setAba("questoes")}>Questões</button>
              <button onClick={() => setAba("avaliacoes")}>Avaliações</button>
              <button onClick={() => setAba("responder")}>Corrigir Livres</button>
            </>
          )}
          {usuario.perfil === "ALUNO" && (
            <button onClick={() => setAba("responder")}>Minhas Provas</button>
          )}
          <button onClick={() => setAba("relatorios")}>Relatórios</button>
          <button onClick={() => setUsuario(null)}>Sair</button>
        </nav>
      </header>

      <main className="container">
        {aba === "questoes" && <Questoes />}
        {aba === "avaliacoes" && <Avaliacoes usuario={usuario} />}
        {aba === "responder" && <ResponderCorrigir usuario={usuario} />}
        {aba === "relatorios" && <Relatorios />}
      </main>
    </div>
  );
}