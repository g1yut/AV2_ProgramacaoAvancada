package view;

import dao.EmprestimoDAO;
import model.Devolucao;
import model.Emprestimo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tela de registro de Devoluções.
 * Lista os empréstimos ativos/atrasados e permite registrar a devolução,
 * calculando automaticamente a multa por atraso.
 */
public class TelaDevolucoes extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblMulta, lblStatus;
    private JTextField txtObservacao;
    private JButton btnDevolver, btnAtualizar;
    private EmprestimoDAO emprestimoDAO;
    private int idEmprestimoSelecionado = -1;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaDevolucoes() {
        emprestimoDAO = new EmprestimoDAO();
        configurarJanela();
        iniciarComponentes();
        emprestimoDAO.atualizarAtrasados();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Registrar Devolução");
        setSize(820, 530);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void iniciarComponentes() {
        // ---- Tabela de empréstimos pendentes ----
        String[] colunas = {"ID", "Usuário", "Livro", "Empréstimo", "Prevista", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(24);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);

        // Colore linhas ATRASADO em vermelho claro
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                String status = (String) modeloTabela.getValueAt(row, 5);
                if (!isSelected) {
                    c.setBackground("Atrasado".equals(status)
                        ? new Color(255, 220, 220)
                        : Color.WHITE);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createTitledBorder("Empréstimos Ativos e Atrasados — clique para selecionar"));

        // ---- Painel inferior de devolução ----
        JPanel painelDevolucao = new JPanel(new GridBagLayout());
        painelDevolucao.setBorder(BorderFactory.createTitledBorder("Registrar Devolução"));
        painelDevolucao.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        lblStatus = new JLabel("Nenhum empréstimo selecionado.");
        lblStatus.setFont(new Font("Arial", Font.ITALIC, 12));
        lblStatus.setForeground(Color.GRAY);

        lblMulta = new JLabel("Multa: R$ 0,00");
        lblMulta.setFont(new Font("Arial", Font.BOLD, 14));
        lblMulta.setForeground(new Color(192, 57, 43));

        txtObservacao = new JTextField("Livro devolvido em bom estado.");

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        painelDevolucao.add(lblStatus, gbc);

        gbc.gridy = 1; gbc.gridwidth = 1;
        painelDevolucao.add(new JLabel("Observação:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        painelDevolucao.add(txtObservacao, gbc);
        gbc.gridx = 3; gbc.gridwidth = 1;
        painelDevolucao.add(lblMulta, gbc);

        // ---- Botões ----
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        painelBotoes.setBackground(Color.WHITE);

        btnDevolver  = criarBotao("✔  Confirmar Devolução", new Color(211, 84, 0));
        btnAtualizar = criarBotao("↻  Atualizar Lista",     new Color(33, 97, 140));
        btnDevolver.setEnabled(false);

        painelBotoes.add(btnDevolver);
        painelBotoes.add(btnAtualizar);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelDevolucao, BorderLayout.CENTER);
        painelSul.add(painelBotoes, BorderLayout.SOUTH);

        add(scroll, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        // ---- Eventos ----
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                selecionarEmprestimo();
            }
        });

        btnDevolver.addActionListener(e -> registrarDevolucao());
        btnAtualizar.addActionListener(e -> {
            emprestimoDAO.atualizarAtrasados();
            carregarTabela();
        });
    }

    private void selecionarEmprestimo() {
        int row = tabela.getSelectedRow();
        idEmprestimoSelecionado = (int) modeloTabela.getValueAt(row, 0);

        Emprestimo emp = emprestimoDAO.buscarPorId(idEmprestimoSelecionado);
        if (emp != null) {
            double multa = Devolucao.calcularMulta(emp);
            lblMulta.setText("Multa: R$ " + String.format("%.2f", multa));
            lblMulta.setForeground(multa > 0 ? new Color(192, 57, 43) : new Color(39, 174, 96));

            String info = "Selecionado: " + emp.getLivro().getTitulo()
                + "  |  Usuário: " + emp.getUsuario().getNome()
                + "  |  Prevista: " + emp.getDataPrevista().format(FMT);
            lblStatus.setText(info);
            lblStatus.setForeground(Color.DARK_GRAY);
            lblStatus.setFont(new Font("Arial", Font.PLAIN, 12));

            btnDevolver.setEnabled(true);
        }
    }

    private void registrarDevolucao() {
        if (idEmprestimoSelecionado == -1) return;

        Emprestimo emp = emprestimoDAO.buscarPorId(idEmprestimoSelecionado);
        if (emp == null) return;

        double multa = Devolucao.calcularMulta(emp);
        String msg = multa > 0
            ? "Há multa de R$ " + String.format("%.2f", multa) + " por atraso.\nConfirma a devolução?"
            : "Devolução dentro do prazo. Confirma?";

        int op = JOptionPane.showConfirmDialog(this, msg, "Confirmar Devolução", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        Devolucao devolucao = new Devolucao(emp, txtObservacao.getText().trim());

        if (emprestimoDAO.registrarDevolucao(devolucao)) {
            // Devolve o exemplar ao acervo
            emp.getLivro().setQuantidade(emp.getLivro().getQuantidade() + 1);
            new dao.LivroDAO().atualizar(emp.getLivro());

            JOptionPane.showMessageDialog(this, "Devolução registrada com sucesso!");
            idEmprestimoSelecionado = -1;
            btnDevolver.setEnabled(false);
            lblStatus.setText("Nenhum empréstimo selecionado.");
            lblStatus.setForeground(Color.GRAY);
            lblMulta.setText("Multa: R$ 0,00");
            lblMulta.setForeground(new Color(192, 57, 43));
            txtObservacao.setText("Livro devolvido em bom estado.");
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar devolução.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        List<Emprestimo> lista = emprestimoDAO.listarAtivos();
        for (Emprestimo e : lista) {
            modeloTabela.addRow(new Object[]{
                e.getId(),
                e.getUsuario().getNome(),
                e.getLivro().getTitulo(),
                e.getDataEmprestimo().format(FMT),
                e.getDataPrevista().format(FMT),
                e.getStatus().getDescricao()
            });
        }
        if (lista.isEmpty()) {
            lblStatus.setText("Nenhum empréstimo ativo ou atrasado no momento.");
        }
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 35));
        return btn;
    }
}
