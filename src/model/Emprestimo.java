package model;

import enums.StatusEmprestimo;
import java.time.LocalDate;

/**
 * Classe que representa um Empréstimo de livro.
 * Relaciona-se com Usuario, Livro e Funcionario.
 * Utiliza a enumeração StatusEmprestimo para controlar o estado.
 */
public class Emprestimo {

    private int id;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevista;
    private StatusEmprestimo status;
    private Usuario usuario;
    private Livro livro;
    private Funcionario funcionario;

    public Emprestimo() {}

    public Emprestimo(int id, LocalDate dataEmprestimo, LocalDate dataPrevista,
                      StatusEmprestimo status, Usuario usuario,
                      Livro livro, Funcionario funcionario) {
        this.id = id;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevista = dataPrevista;
        this.status = status;
        this.usuario = usuario;
        this.livro = livro;
        this.funcionario = funcionario;
    }

    // Construtor para novo empréstimo (sem ID, status ATIVO, data hoje)
    public Emprestimo(Usuario usuario, Livro livro, Funcionario funcionario, int diasPrazo) {
        this.dataEmprestimo = LocalDate.now();
        this.dataPrevista = LocalDate.now().plusDays(diasPrazo);
        this.status = StatusEmprestimo.ATIVO;
        this.usuario = usuario;
        this.livro = livro;
        this.funcionario = funcionario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(LocalDate dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public LocalDate getDataPrevista() { return dataPrevista; }
    public void setDataPrevista(LocalDate dataPrevista) { this.dataPrevista = dataPrevista; }

    public StatusEmprestimo getStatus() { return status; }
    public void setStatus(StatusEmprestimo status) { this.status = status; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    /**
     * Verifica se o empréstimo está atrasado com base na data atual.
     * @return true se a data prevista já passou e o status ainda é ATIVO
     */
    public boolean isAtrasado() {
        return status == StatusEmprestimo.ATIVO
                && LocalDate.now().isAfter(dataPrevista);
    }

    @Override
    public String toString() {
        return "Empréstimo #" + id + " - " + livro.getTitulo()
                + " | " + usuario.getNome() + " | " + status.getDescricao();
    }
}
