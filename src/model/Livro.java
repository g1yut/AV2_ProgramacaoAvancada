package model;

/**
 * Classe que representa um Livro do acervo da biblioteca.
 * Relaciona-se com Autor (muitos para um) e Categoria (muitos para um).
 */
public class Livro {

    private int id;
    private String titulo;
    private String isbn;
    private int anoPublicacao;
    private int quantidade;
    private Autor autor;
    private Categoria categoria;

    public Livro() {}

    public Livro(int id, String titulo, String isbn, int anoPublicacao,
                 int quantidade, Autor autor, Categoria categoria) {
        this.id = id;
        this.titulo = titulo;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.quantidade = quantidade;
        this.autor = autor;
        this.categoria = categoria;
    }

    public Livro(String titulo, String isbn, int anoPublicacao,
                 int quantidade, Autor autor, Categoria categoria) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.quantidade = quantidade;
        this.autor = autor;
        this.categoria = categoria;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return titulo; // usado em JComboBox
    }
}
