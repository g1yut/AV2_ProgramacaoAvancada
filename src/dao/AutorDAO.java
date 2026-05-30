package dao;

import interfaces.IGerenciavel;
import model.Autor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável pelas operações de banco de dados da entidade Autor.
 * Implementa a interface IGerenciavel com operações CRUD completas.
 */
public class AutorDAO implements IGerenciavel<Autor> {

    private Connection con;

    public AutorDAO() {
        this.con = ConexaoDB.getConexao();
    }

    @Override
    public boolean salvar(Autor autor) {
        String sql = "INSERT INTO autor (nome, nacionalidade) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, autor.getNome());
            ps.setString(2, autor.getNacionalidade());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Autor autor) {
        String sql = "UPDATE autor SET nome = ?, nacionalidade = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, autor.getNome());
            ps.setString(2, autor.getNacionalidade());
            ps.setInt(3, autor.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM autor WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar autor: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Autor buscarPorId(int id) {
        String sql = "SELECT * FROM autor WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Autor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("nacionalidade")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar autor: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Autor> listarTodos() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autor ORDER BY nome";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Autor(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("nacionalidade")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar autores: " + e.getMessage());
        }
        return lista;
    }
}
