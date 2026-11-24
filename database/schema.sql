CREATE TABLE questao (
    codigo      SERIAL PRIMARY KEY,
    pergunta    TEXT NOT NULL CHECK (char_length(trim(pergunta)) > 0)
);


CREATE TABLE objetiva (
    codigo_questao  INTEGER PRIMARY KEY,
    CONSTRAINT fk_objetiva_questao
        FOREIGN KEY (codigo_questao)
        REFERENCES questao (codigo)
        ON DELETE CASCADE
);


CREATE TABLE livre (
    codigo_questao  INTEGER PRIMARY KEY,
    CONSTRAINT fk_livre_questao
        FOREIGN KEY (codigo_questao)
        REFERENCES questao (codigo)
        ON DELETE CASCADE
);


CREATE TABLE alternativa (
    codigo_questao  INTEGER NOT NULL,
    codigo          INTEGER NOT NULL CHECK (codigo > 0),
    texto           TEXT NOT NULL CHECK (char_length(trim(texto)) > 0),
    status          BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (codigo_questao, codigo),
    CONSTRAINT fk_alternativa_objetiva
        FOREIGN KEY (codigo_questao)
        REFERENCES objetiva (codigo_questao)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX uq_alternativa_correta_por_questao
    ON alternativa (codigo_questao)
    WHERE status = TRUE;


CREATE TABLE professor (
    matricula       VARCHAR(20) PRIMARY KEY CHECK (char_length(trim(matricula)) > 0),
    cpf             CHAR(11) NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$'),
    nome            VARCHAR(150) NOT NULL CHECK (char_length(trim(nome)) > 0),
    data_nascimento DATE NOT NULL CHECK (data_nascimento < CURRENT_DATE)
);


CREATE TABLE avaliacao (
    codigo              SERIAL PRIMARY KEY,
    matricula_professor VARCHAR(20) NOT NULL,
    titulo              VARCHAR(150) NOT NULL CHECK (char_length(trim(titulo)) > 0),
    descricao           TEXT,
    data                DATE NOT NULL,
    horario_inicio      TIME NOT NULL,
    horario_fim         TIME NOT NULL,
    CONSTRAINT chk_avaliacao_horarios
        CHECK (horario_fim > horario_inicio),
    CONSTRAINT fk_avaliacao_professor
        FOREIGN KEY (matricula_professor)
        REFERENCES professor (matricula)
        ON DELETE RESTRICT
);


CREATE TABLE aluno (
    matricula       VARCHAR(20) PRIMARY KEY CHECK (char_length(trim(matricula)) > 0),
    cpf             CHAR(11) NOT NULL UNIQUE CHECK (cpf ~ '^[0-9]{11}$'),
    nome            VARCHAR(150) NOT NULL CHECK (char_length(trim(nome)) > 0),
    data_nascimento DATE NOT NULL CHECK (data_nascimento < CURRENT_DATE)
);


CREATE TABLE compoe (
    codigo_avaliacao    INTEGER NOT NULL,
    codigo_questao      INTEGER NOT NULL,
    valor               NUMERIC(5, 2) NOT NULL CHECK (valor > 0),
    PRIMARY KEY (codigo_avaliacao, codigo_questao),
    CONSTRAINT fk_compoe_avaliacao
        FOREIGN KEY (codigo_avaliacao)
        REFERENCES avaliacao (codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_compoe_questao
        FOREIGN KEY (codigo_questao)
        REFERENCES questao (codigo)
        ON DELETE RESTRICT
);


CREATE TABLE faz (
    codigo_avaliacao    INTEGER NOT NULL,
    matricula           VARCHAR(20) NOT NULL,
    nota_final          NUMERIC(5, 2) CHECK (nota_final >= 0),
    PRIMARY KEY (codigo_avaliacao, matricula),
    CONSTRAINT fk_faz_avaliacao
        FOREIGN KEY (codigo_avaliacao)
        REFERENCES avaliacao (codigo)
        ON DELETE CASCADE,
    CONSTRAINT fk_faz_aluno
        FOREIGN KEY (matricula)
        REFERENCES aluno (matricula)
        ON DELETE CASCADE
);


CREATE TABLE responde (
    codigo_avaliacao    INTEGER NOT NULL,
    codigo_questao      INTEGER NOT NULL,
    matricula           VARCHAR(20) NOT NULL,
    resposta            TEXT NOT NULL CHECK (char_length(trim(resposta)) > 0),
    nota_obtida         NUMERIC(5, 2) CHECK (nota_obtida >= 0),
    PRIMARY KEY (codigo_avaliacao, codigo_questao, matricula),
    CONSTRAINT fk_responde_compoe
        FOREIGN KEY (codigo_avaliacao, codigo_questao)
        REFERENCES compoe (codigo_avaliacao, codigo_questao)
        ON DELETE CASCADE,
    CONSTRAINT fk_responde_faz
        FOREIGN KEY (codigo_avaliacao, matricula)
        REFERENCES faz (codigo_avaliacao, matricula)
        ON DELETE CASCADE
);