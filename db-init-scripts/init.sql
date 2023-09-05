DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'rinhadb') THEN
        CREATE DATABASE rinhadb;
    END IF;
END $$;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
DROP TABLE IF EXISTS pessoa;
CREATE TABLE IF NOT EXISTS pessoa (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    apelido VARCHAR(32) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    nascimento DATE NOT NULL,
    stack TEXT[]
    );
