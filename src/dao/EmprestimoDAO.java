package dao;

import enums.StatusEmprestimo;
import enums.TipoUsuario;
import interfaces.IGerenciavel;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável pelas operações de Empréstimo e Devolução no banco.
 * Realiza JOINs entre usuario, livro, autor, categoria e funcionario.
 * Contém método registrarDevolucao que atualiza o status do empréstimo
 * e insere o registro na tabela devolucao em uma única transação.
 */
public class EmprestimoDAO implements IGerenciavel<Emprestimo> {

    private Connection con;

    public EmprestimoDAO() {
        this.con = ConexaoDB.getConexao();
    }

    @Override
    public boolean salvar(Emprestimo e) {
        String sql = "INSERT INTO emprestimo (data_emprestimo, data_prevista, status, id_usuario, id_livro, id_funcionario) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(e.getDataEmprestimo()));
            ps.setDate(2, Date.valueOf(e.getDataPrevista()));
            ps.setString(3, e.getStatus().name());
            ps.setInt(4, e.getUsuario().getId());
            ps.setInt(5, e.getLivro().getId());
            ps.setInt(6, e.getFuncionario().getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao salvar empréstimo: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Emprestimo e) {
        String sql = "UPDATE emprestimo SET status = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getStatus().name());
            ps.setInt(2, e.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            System.err.println("Erro ao atualizar empréstimo: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar empréstimo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Emprestimo buscarPorId(int id) {
        String sql = buildQueryBase() + " WHERE e.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarEmprestimo(rs);
        } catch (SQLException ex) {
            System.err.println("Erro ao buscar empréstimo: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = buildQueryBase() + " ORDER BY e.data_emprestimo DESC";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(montarEmprestimo(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar empréstimos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista somente empréstimos com status ATIVO.
     */
    public List<Emprestimo> listarAtivos() {
        List<Emprestimo> lista = new ArrayList<>();
        String sql = buildQueryBase() + " WHERE e.status = 'ATIVO' ORDER BY e.data_prevista";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(montarEmprestimo(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar empréstimos ativos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Atualiza todos os empréstimos ATIVO cuja data prevista já passou para ATRASADO.
     */
    public void atualizarAtrasados() {
        String sql = "UPDATE emprestimo SET status = 'ATRASADO' "
                   + "WHERE status = 'ATIVO' AND data_prevista < CURDATE()";
        try (Statement st = con.createStatement()) {
            int linhas = st.executeUpdate(sql);
            System.out.println("Empréstimos marcados como atrasados: " + linhas);
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar atrasados: " + e.getMessage());
        }
    }

    /**
     * Registra uma devolução em transação:
     * 1. Atualiza status do empréstimo para DEVOLVIDO
     * 2. Insere registro na tabela devolucao
     * @return true se ambas operações foram concluídas
     */
    public boolean registrarDevolucao(Devolucao devolucao) {
        String sqlEmprestimo = "UPDATE emprestimo SET status = 'DEVOLVIDO' WHERE id = ?";
        String sqlDevolucao  = "INSERT INTO devolucao (data_devolucao, multa, observacao, id_emprestimo) "
                             + "VALUES (?, ?, ?, ?)";
        try {
            con.setAutoCommit(false); // inicia transação

            try (PreparedStatement ps1 = con.prepareStatement(sqlEmprestimo)) {
                ps1.setInt(1, devolucao.getEmprestimo().getId());
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(sqlDevolucao)) {
                ps2.setDate(1, Date.valueOf(devolucao.getDataDevolucao()));
                ps2.setDouble(2, devolucao.getMulta());
                ps2.setString(3, devolucao.getObservacao());
                ps2.setInt(4, devolucao.getEmprestimo().getId());
                ps2.executeUpdate();
            }

            con.commit(); // confirma transação
            return true;

        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            System.err.println("Erro ao registrar devolução: " + e.getMessage());
            return false;
        } finally {
            try { con.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // Monta a query base com todos os JOINs necessários
    private String buildQueryBase() {
        return "SELECT e.*, "
             + "u.nome AS nomeUsuario, u.cpf, u.email, u.telefone, u.tipo AS tipoUsuario, u.ativo AS ativoUsuario, "
             + "l.titulo, l.isbn, l.ano_publicacao, l.quantidade, "
             + "a.id AS idAutor, a.nome AS nomeAutor, a.nacionalidade, "
             + "c.id AS idCategoria, c.nome AS nomeCategoria, c.descricao AS descCategoria, "
             + "f.nome AS nomeFuncionario, f.login, f.senha, f.cargo, f.ativo AS ativoFunc "
             + "FROM emprestimo e "
             + "JOIN usuario u ON e.id_usuario = u.id "
             + "JOIN livro l ON e.id_livro = l.id "
             + "JOIN autor a ON l.id_autor = a.id "
             + "JOIN categoria c ON l.id_categoria = c.id "
             + "JOIN funcionario f ON e.id_funcionario = f.id";
    }

    private Emprestimo montarEmprestimo(ResultSet rs) throws SQLException {
        Autor autor = new Autor(rs.getInt("idAutor"), rs.getString("nomeAutor"), rs.getString("nacionalidade"));
        Categoria cat = new Categoria(rs.getInt("idCategoria"), rs.getString("nomeCategoria"), rs.getString("descCategoria"));
        Livro livro = new Livro(rs.getInt("id_livro"), rs.getString("titulo"), rs.getString("isbn"),
                                rs.getInt("ano_publicacao"), rs.getInt("quantidade"), autor, cat);
        Usuario usuario = new Usuario(rs.getInt("id_usuario"), rs.getString("nomeUsuario"),
                                      rs.getString("cpf"), rs.getString("email"), rs.getString("telefone"),
                                      TipoUsuario.valueOf(rs.getString("tipoUsuario")), rs.getBoolean("ativoUsuario"));
        Funcionario func = new Funcionario(rs.getInt("id_funcionario"), rs.getString("nomeFuncionario"),
                                           rs.getString("login"), rs.getString("senha"),
                                           rs.getString("cargo"), rs.getBoolean("ativoFunc"));
        return new Emprestimo(
            rs.getInt("id"),
            rs.getDate("data_emprestimo").toLocalDate(),
            rs.getDate("data_prevista").toLocalDate(),
            StatusEmprestimo.valueOf(rs.getString("status")),
            usuario, livro, func
        );
    }
}
