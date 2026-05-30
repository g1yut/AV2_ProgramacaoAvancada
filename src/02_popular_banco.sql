-- ============================================================
-- SISTEMA DE GERENCIAMENTO DE BIBLIOTECA
-- Arquivo: 02_popular_banco.sql
-- Descrição: Inserção de dados iniciais para teste
-- Disciplina: Programação Avançada (184987)
-- ============================================================

USE biblioteca;

-- Garante que os auto_increments começam do 1 limpo
ALTER TABLE devolucao  AUTO_INCREMENT = 1;
ALTER TABLE emprestimo AUTO_INCREMENT = 1;
ALTER TABLE usuario    AUTO_INCREMENT = 1;
ALTER TABLE funcionario AUTO_INCREMENT = 1;
ALTER TABLE livro      AUTO_INCREMENT = 1;
ALTER TABLE autor      AUTO_INCREMENT = 1;
ALTER TABLE categoria  AUTO_INCREMENT = 1;

-- ------------------------------------------------------------
-- CATEGORIAS  (IDs: 1 a 7)
-- ------------------------------------------------------------
INSERT INTO categoria (id, nome, descricao) VALUES
(1, 'Ficção Científica',  'Obras que exploram ciência e tecnologia fictícias'),
(2, 'Romance',            'Obras focadas em relacionamentos afetivos'),
(3, 'Programação',        'Livros técnicos sobre desenvolvimento de software'),
(4, 'História',           'Obras sobre eventos e períodos históricos'),
(5, 'Filosofia',          'Obras de pensamento filosófico e ético'),
(6, 'Aventura',           'Obras com narrativas de ação e exploração'),
(7, 'Biografia',          'Relatos sobre a vida de pessoas reais');

-- ------------------------------------------------------------
-- AUTORES  (IDs: 1 a 10)
-- ------------------------------------------------------------
INSERT INTO autor (id, nome, nacionalidade) VALUES
(1,  'Isaac Asimov',      'Americano'),
(2,  'Machado de Assis',  'Brasileiro'),
(3,  'Sérgio Furgeri',    'Brasileiro'),
(4,  'Yuval Noah Harari', 'Israelense'),
(5,  'Immanuel Kant',     'Alemão'),
(6,  'Julio Verne',       'Francês'),
(7,  'Walter Isaacson',   'Americano'),
(8,  'George Orwell',     'Britânico'),
(9,  'Clarice Lispector', 'Brasileira'),
(10, 'Robert C. Martin',  'Americano');

-- ------------------------------------------------------------
-- LIVROS  (IDs: 1 a 15)
-- id_autor e id_categoria referenciam as tabelas acima
-- ------------------------------------------------------------
INSERT INTO livro (id, titulo, isbn, ano_publicacao, quantidade, id_autor, id_categoria) VALUES
(1,  'Fundação',                        '978-0-553-29335-7', 1951, 3, 1, 1),
(2,  'Eu, Robô',                        '978-0-553-29438-5', 1950, 2, 1, 1),
(3,  'Dom Casmurro',                    '978-8-572-32839-2', 1899, 4, 2, 2),
(4,  'Memórias Póstumas de Brás Cubas', '978-8-535-90609-5', 1881, 3, 2, 2),
(5,  'Java 8 - Ensino Didático',        '978-8-536-51934-0', 2015, 5, 3, 3),
(6,  'Sapiens',                         '978-0-062-31609-7', 2011, 3, 4, 4),
(7,  'Homo Deus',                       '978-8-535-92880-6', 2015, 2, 4, 4),
(8,  'Crítica da Razão Pura',           '978-8-572-32055-6', 1781, 2, 5, 5),
(9,  'Volta ao Mundo em 80 Dias',       '978-8-525-04327-4', 1872, 3, 6, 6),
(10, 'Steve Jobs',                      '978-8-535-91924-1', 2011, 2, 7, 7),
(11, '1984',                            '978-0-451-52493-5', 1949, 4, 8, 1),
(12, 'A Paixão Segundo G.H.',           '978-8-535-90218-9', 1964, 2, 9, 2),
(13, 'Código Limpo',                    '978-8-576-08551-8', 2008, 3, 10, 3),
(14, '20.000 Léguas Submarinas',        '978-8-525-04221-5', 1870, 2, 6, 6),
(15, 'A Hora da Estrela',               '978-8-520-91139-1', 1977, 3, 9, 2);

-- ------------------------------------------------------------
-- FUNCIONÁRIOS  (IDs: 1 a 3)
-- ------------------------------------------------------------
INSERT INTO funcionario (id, nome, login, senha, cargo, ativo) VALUES
(1, 'Ana Paula Souza', 'ana.paula',  '123456', 'Bibliotecária Chefe', 1),
(2, 'Carlos Mendes',   'carlos.m',   '123456', 'Atendente',           1),
(3, 'Fernanda Lima',   'fernanda.l', '123456', 'Atendente',           1);

-- ------------------------------------------------------------
-- USUÁRIOS  (IDs: 1 a 8)
-- ------------------------------------------------------------
INSERT INTO usuario (id, nome, cpf, email, telefone, tipo, ativo) VALUES
(1, 'João Pedro Silva',    '111.111.111-11', 'joao.pedro@email.com',   '(47) 99111-1111', 'ALUNO',     1),
(2, 'Maria Clara Ramos',   '222.222.222-22', 'maria.clara@email.com',  '(47) 99222-2222', 'ALUNO',     1),
(3, 'Prof. Roberto Alves', '333.333.333-33', 'roberto.alves@email.com','(47) 99333-3333', 'PROFESSOR', 1),
(4, 'Profa. Lúcia Gomes',  '444.444.444-44', 'lucia.gomes@email.com',  '(47) 99444-4444', 'PROFESSOR', 1),
(5, 'Marcos Visitante',    '555.555.555-55', 'marcos.v@email.com',     '(47) 99555-5555', 'VISITANTE', 1),
(6, 'Beatriz Costa',       '666.666.666-66', 'beatriz.c@email.com',    '(47) 99666-6666', 'ALUNO',     1),
(7, 'Rafael Moreira',      '777.777.777-77', 'rafael.m@email.com',     '(47) 99777-7777', 'ALUNO',     1),
(8, 'Juliana Ferreira',    '888.888.888-88', 'juliana.f@email.com',    '(47) 99888-8888', 'ALUNO',     0);

-- ------------------------------------------------------------
-- EMPRÉSTIMOS  (IDs: 1 a 7)
-- id_usuario → tabela usuario | id_livro → tabela livro | id_funcionario → tabela funcionario
-- ------------------------------------------------------------
INSERT INTO emprestimo (id, data_emprestimo, data_prevista, status, id_usuario, id_livro, id_funcionario) VALUES
(1, '2026-05-01', '2026-05-15', 'DEVOLVIDO', 1, 1,  1),
(2, '2026-05-10', '2026-05-24', 'DEVOLVIDO', 2, 5,  2),
(3, '2026-05-15', '2026-05-29', 'ATRASADO',  3, 6,  1),
(4, '2026-05-20', '2026-06-03', 'ATIVO',     4, 13, 3),
(5, '2026-05-22', '2026-06-05', 'ATIVO',     1, 11, 2),
(6, '2026-05-23', '2026-06-06', 'ATIVO',     6, 3,  1),
(7, '2026-04-20', '2026-05-04', 'ATRASADO',  7, 9,  3);

-- ------------------------------------------------------------
-- DEVOLUÇÕES  (apenas empréstimos 1 e 2 que estão DEVOLVIDO)
-- ------------------------------------------------------------
INSERT INTO devolucao (id, data_devolucao, multa, observacao, id_emprestimo) VALUES
(1, '2026-05-14', 0.00, 'Devolvido no prazo, livro em bom estado.',   1),
(2, '2026-05-23', 0.00, 'Devolvido com 1 dia de atraso — sem multa.', 2);
