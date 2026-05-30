-- ============================================================
-- SISTEMA DE GERENCIAMENTO DE BIBLIOTECA
-- Arquivo: 01_criar_banco.sql
-- Descrição: Criação do banco de dados e tabelas
-- Disciplina: Programação Avançada (184987)
-- ============================================================

CREATE DATABASE IF NOT EXISTS biblioteca
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE biblioteca;

-- ------------------------------------------------------------
-- TABELA: categoria
-- Armazena os gêneros/categorias dos livros
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(100) NOT NULL,
    descricao   VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uq_categoria_nome (nome)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: autor
-- Armazena os autores dos livros
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS autor (
    id          INT          NOT NULL AUTO_INCREMENT,
    nome        VARCHAR(150) NOT NULL,
    nacionalidade VARCHAR(100),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: livro
-- Armazena os livros do acervo da biblioteca
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS livro (
    id              INT          NOT NULL AUTO_INCREMENT,
    titulo          VARCHAR(200) NOT NULL,
    isbn            VARCHAR(20)  NOT NULL,
    ano_publicacao  SMALLINT,    -- SMALLINT suporta anos anteriores a 1901 (ex: 1881, 1899)
    quantidade      INT          NOT NULL DEFAULT 1,
    id_autor        INT          NOT NULL,
    id_categoria    INT          NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_livro_isbn (isbn),
    CONSTRAINT fk_livro_autor
        FOREIGN KEY (id_autor)    REFERENCES autor(id)    ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_livro_categoria
        FOREIGN KEY (id_categoria) REFERENCES categoria(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: funcionario
-- Armazena os funcionários que operam o sistema
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS funcionario (
    id       INT          NOT NULL AUTO_INCREMENT,
    nome     VARCHAR(150) NOT NULL,
    login    VARCHAR(50)  NOT NULL,
    senha    VARCHAR(255) NOT NULL,
    cargo    VARCHAR(100),
    ativo    TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_funcionario_login (login)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: usuario
-- Armazena os usuários que podem fazer empréstimos
-- tipo: ALUNO | PROFESSOR | VISITANTE  (enumeração no Java)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id        INT          NOT NULL AUTO_INCREMENT,
    nome      VARCHAR(150) NOT NULL,
    cpf       VARCHAR(14)  NOT NULL,
    email     VARCHAR(150),
    telefone  VARCHAR(20),
    tipo      ENUM('ALUNO','PROFESSOR','VISITANTE') NOT NULL DEFAULT 'ALUNO',
    ativo     TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_cpf (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: emprestimo
-- Registra cada empréstimo feito por um usuário
-- status: ATIVO | DEVOLVIDO | ATRASADO  (enumeração no Java)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS emprestimo (
    id               INT     NOT NULL AUTO_INCREMENT,
    data_emprestimo  DATE    NOT NULL,
    data_prevista    DATE    NOT NULL,
    status           ENUM('ATIVO','DEVOLVIDO','ATRASADO') NOT NULL DEFAULT 'ATIVO',
    id_usuario       INT     NOT NULL,
    id_livro         INT     NOT NULL,
    id_funcionario   INT     NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_emprestimo_usuario
        FOREIGN KEY (id_usuario)     REFERENCES usuario(id)     ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_livro
        FOREIGN KEY (id_livro)       REFERENCES livro(id)       ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_emprestimo_func
        FOREIGN KEY (id_funcionario) REFERENCES funcionario(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ------------------------------------------------------------
-- TABELA: devolucao
-- Registra a devolução de um empréstimo e a multa (se houver)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS devolucao (
    id              INT            NOT NULL AUTO_INCREMENT,
    data_devolucao  DATE           NOT NULL,
    multa           DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    observacao      VARCHAR(255),
    id_emprestimo   INT            NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_devolucao_emprestimo (id_emprestimo),
    CONSTRAINT fk_devolucao_emprestimo
        FOREIGN KEY (id_emprestimo) REFERENCES emprestimo(id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
