package enums;

/**
 * Enumeração que representa os tipos de usuário da biblioteca.
 */
public enum TipoUsuario {

    ALUNO("Aluno"),
    PROFESSOR("Professor"),
    VISITANTE("Visitante");

    private final String descricao;

    TipoUsuario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
