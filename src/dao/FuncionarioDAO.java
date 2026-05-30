package dao;

import interfaces.IGerenciavel;
import model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável pelas operações de banco de dados da entidade Funcionario.
 * Inclui método de autenticação usado na tela de login.
 */
public class FuncionarioDAO implements IGerenciavel<Funcionario> {

    private Connection con;

    public FuncionarioDAO() {
        this.con = ConexaoDB.getConexao();
    }

    @Override
    public boolean salvar(Funcionario f) {
        String sql = "INSERT INTO funcionario (nome, login, senha, cargo, ativo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getLogin());
            ps.setString(3, f.getSenha());
            ps.setString(4, f.getCargo());
            ps.setBoolean(5, f.isAtivo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar funcionário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Funcionario f) {
        String sql = "UPDATE funcionario SET nome = ?, login = ?, senha = ?, cargo = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getNome());
            ps.setString(2, f.getLogin());
            ps.setString(3, f.getSenha());
            ps.setString(4, f.getCargo());
            ps.setBoolean(5, f.isAtivo());
            ps.setInt(6, f.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM funcionario WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar funcionário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Funcionario buscarPorId(int id) {
        String sql = "SELECT * FROM funcionario WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarFuncionario(rs);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM funcionario ORDER BY nome";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(montarFuncionario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionários: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Autentica um funcionário pelo login e senha.
     * Usado pela tela de login do sistema.
     * @return Funcionario autenticado ou null se credenciais inválidas
     */
    public Funcionario autenticar(String login, String senha) {
        String sql = "SELECT * FROM funcionario WHERE login = ? AND senha = ? AND ativo = 1";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarFuncionario(rs);
        } catch (SQLException e) {
            System.err.println("Erro na autenticação: " + e.getMessage());
        }
        return null;
    }

    private Funcionario montarFuncionario(ResultSet rs) throws SQLException {
        return new Funcionario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("login"),
            rs.getString("senha"),
            rs.getString("cargo"),
            rs.getBoolean("ativo")
        );
    }
}
