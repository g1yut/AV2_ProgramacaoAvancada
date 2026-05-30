package model;

/**
 * Classe que representa um Autor do acervo da biblioteca.
 */
public class Autor {

    private int id;
    private String nome;
    private String nacionalidade;

    // Construtor vazio (necessário para o DAO)
    public Autor() {}

    // Construtor completo
    public Autor(int id, String nome, String nacionalidade) {
        this.id = id;
        this.nome = nome;
        this.nacionalidade = nacionalidade;
    }

    // Construtor sem ID (para inserção)
    public Autor(String nome, String nacionalidade) {
        this.nome = nome;
        this.nacionalidade = nacionalidade;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getNacionalidade() { return nacionalidade; }
    public void setNacionalidade(String nacionalidade) { this.nacionalidade = nacionalidade; }

    @Override
    public String toString() {
        return nome; // usado em JComboBox
    }
}
