package dao;

import interfaces.IGerenciavel;
import model.Autor;
import model.Categoria;
import model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe DAO responsável pelas operações de banco de dados da entidade Livro.
 * Realiza JOIN com as tabelas autor e categoria para montar o objeto completo.
 */
public class LivroDAO implements IGerenciavel<Livro> {

    private Connection con;

    public LivroDAO() {
        this.con = ConexaoDB.getConexao();
    }

    @Override
    public boolean salvar(Livro livro) {
        String sql = "INSERT INTO livro (titulo, isbn, ano_publicacao, quantidade, id_autor, id_categoria) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getIsbn());
            ps.setInt(3, livro.getAnoPublicacao());
            ps.setInt(4, livro.getQuantidade());
            ps.setInt(5, livro.getAutor().getId());
            ps.setInt(6, livro.getCategoria().getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar livro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean atualizar(Livro livro) {
        String sql = "UPDATE livro SET titulo = ?, isbn = ?, ano_publicacao = ?, "
                   + "quantidade = ?, id_autor = ?, id_categoria = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, livro.getTitulo());
            ps.setString(2, livro.getIsbn());
            ps.setInt(3, livro.getAnoPublicacao());
            ps.setInt(4, livro.getQuantidade());
            ps.setInt(5, livro.getAutor().getId());
            ps.setInt(6, livro.getCategoria().getId());
            ps.setInt(7, livro.getId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar livro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletar(int id) {
        String sql = "DELETE FROM livro WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao deletar livro: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Livro buscarPorId(int id) {
        String sql = "SELECT l.*, a.nome AS nomeAutor, a.nacionalidade, "
                   + "c.nome AS nomeCategoria, c.descricao AS descCategoria "
                   + "FROM livro l "
                   + "JOIN autor a ON l.id_autor = a.id "
                   + "JOIN categoria c ON l.id_categoria = c.id "
                   + "WHERE l.id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return montarLivro(rs);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Livro> listarTodos() {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT l.*, a.nome AS nomeAutor, a.nacionalidade, "
                   + "c.nome AS nomeCategoria, c.descricao AS descCategoria "
                   + "FROM livro l "
                   + "JOIN autor a ON l.id_autor = a.id "
                   + "JOIN categoria c ON l.id_categoria = c.id "
                   + "ORDER BY l.titulo";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(montarLivro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar livros: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Busca livros cujo título contenha o texto informado (busca parcial).
     */
    public List<Livro> buscarPorTitulo(String titulo) {
        List<Livro> lista = new ArrayList<>();
        String sql = "SELECT l.*, a.nome AS nomeAutor, a.nacionalidade, "
                   + "c.nome AS nomeCategoria, c.descricao AS descCategoria "
                   + "FROM livro l "
                   + "JOIN autor a ON l.id_autor = a.id "
                   + "JOIN categoria c ON l.id_categoria = c.id "
                   + "WHERE l.titulo LIKE ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montarLivro(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar livro por título: " + e.getMessage());
        }
        return lista;
    }

    // Monta objeto Livro a partir do ResultSet
    private Livro montarLivro(ResultSet rs) throws SQLException {
        Autor autor = new Autor(
            rs.getInt("id_autor"),
            rs.getString("nomeAutor"),
            rs.getString("nacionalidade")
        );
        Categoria categoria = new Categoria(
            rs.getInt("id_categoria"),
            rs.getString("nomeCategoria"),
            rs.getString("descCategoria")
        );
        return new Livro(
            rs.getInt("id"),
            rs.getString("titulo"),
            rs.getString("isbn"),
            rs.getInt("ano_publicacao"),
            rs.getInt("quantidade"),
            autor,
            categoria
        );
    }
}
