package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Classe que representa a Devolução de um empréstimo.
 * Calcula automaticamente a multa por atraso (R$ 1,00 por dia).
 * Relaciona-se com Emprestimo (um para um).
 */
public class Devolucao {

    private static final double MULTA_POR_DIA = 1.00;

    private int id;
    private LocalDate dataDevolucao;
    private double multa;
    private String observacao;
    private Emprestimo emprestimo;

    public Devolucao() {}

    public Devolucao(int id, LocalDate dataDevolucao, double multa,
                     String observacao, Emprestimo emprestimo) {
        this.id = id;
        this.dataDevolucao = dataDevolucao;
        this.multa = multa;
        this.observacao = observacao;
        this.emprestimo = emprestimo;
    }

    // Construtor para nova devolução — calcula multa automaticamente
    public Devolucao(Emprestimo emprestimo, String observacao) {
        this.dataDevolucao = LocalDate.now();
        this.emprestimo = emprestimo;
        this.observacao = observacao;
        this.multa = calcularMulta(emprestimo);
    }

    /**
     * Calcula a multa com base nos dias de atraso.
     * @param emprestimo empréstimo a ser verificado
     * @return valor da multa (R$ 1,00 por dia de atraso)
     */
    public static double calcularMulta(Emprestimo emprestimo) {
        LocalDate prevista = emprestimo.getDataPrevista();
        LocalDate devolucao = LocalDate.now();

        if (devolucao.isAfter(prevista)) {
            long diasAtraso = ChronoUnit.DAYS.between(prevista, devolucao);
            return diasAtraso * MULTA_POR_DIA;
        }
        return 0.00;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(LocalDate dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public double getMulta() { return multa; }
    public void setMulta(double multa) { this.multa = multa; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public Emprestimo getEmprestimo() { return emprestimo; }
    public void setEmprestimo(Emprestimo emprestimo) { this.emprestimo = emprestimo; }

    @Override
    public String toString() {
        return "Devolução #" + id + " - " + dataDevolucao
                + " | Multa: R$ " + String.format("%.2f", multa);
    }
}
