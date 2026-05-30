package dao;

import enums.TipoUsuario;
import interfaces.IGerenciavel;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável pelas operações de banco de dados da entidade Usuario.
 * Disciplina: Programação Avançada (184987)
 */
public class UsuarioDAO implements IGerenciavel<Usuario> {

    private Connection con;

    public UsuarioDAO() {
        this.con = ConexaoDB.getConexao();
    }

    @Override
    public boolean salvar(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, cpf, email, telefone, tipo, ativo) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getTelefone());
            ps.setString(5, usuario.getTipo().name());
            ps.setBoolean(6, usuario.isAtivo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar usuário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, email = ?, "
                   + "telefone = ?, tipo = ?, ativo = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getCpf());
            ps.setString(3, usuario.getEmail());
            ps.setString(4, usuario.getTelefone());
            ps.setString(5, usuario.getTipo().name());
            ps.setBoolean(6, usuario.isAtivo());
            ps.setInt(7, usuario.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar usuário: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarUsuario(rs);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(montarUsuario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Retorna somente usuários ativos (habilitados para empréstimo).
     */
    public List<Usuario> listarAtivos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE ativo = 1 ORDER BY nome";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(montarUsuario(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar usuários ativos: " + e.getMessage());
        }
        return lista;
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("email"),
            rs.getString("telefone"),
            TipoUsuario.valueOf(rs.getString("tipo")),
            rs.getBoolean("ativo")
        );
    }
}
