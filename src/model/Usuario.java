package model;

import enums.TipoUsuario;

/**
 * Classe que representa um Usuário da biblioteca (quem faz empréstimos).
 * Utiliza a enumeração TipoUsuario para classificar o usuário.
 */
public class Usuario {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private TipoUsuario tipo;
    private boolean ativo;

    public Usuario() {}

    public Usuario(int id, String nome, String cpf, String email,
                   String telefone, TipoUsuario tipo, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo;
        this.ativo = ativo;
    }

    public Usuario(String nome, String cpf, String email,
                   String telefone, TipoUsuario tipo) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo;
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return nome + " (" + tipo.getDescricao() + ")";
    }
}
