CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS Pauta(
    id UUID DEFAULT uuid_generate_v4(),
    titulo VARCHAR(255) NOT NULL,
    descricao VARCHAR(255),
    started_at TIMESTAMPTZ NOT NULL,
    end_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Sessao(
    id UUID DEFAULT uuid_generate_v4(),
    pauta_id UUID NOT NULL,
    cpf VARCHAR(255) NOT NULL,
    voto VARCHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    FOREIGN KEY (pauta_id) REFERENCES Pauta(id) ON DELETE CASCADE,
    UNIQUE(pauta_id, cpf)
);

