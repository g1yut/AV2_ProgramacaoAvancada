package view;

import dao.EmprestimoDAO;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Emprestimo;
import model.Funcionario;
import model.Livro;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Tela de registro de Empréstimos.
 * Permite registrar um novo empréstimo selecionando usuário e livro.
 * Exibe todos os empréstimos ativos na tabela.
 */
public class TelaEmprestimos extends JFrame {

    private JComboBox<Usuario> cmbUsuario;
    private JComboBox<Livro> cmbLivro;
    private JTextField txtDias;
    private JLabel lblDataEmprestimo, lblDataPrevista;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnRegistrar, btnLimpar;

    private EmprestimoDAO emprestimoDAO;
    private UsuarioDAO usuarioDAO;
    private LivroDAO livroDAO;
    private Funcionario funcionarioLogado;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaEmprestimos(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;
        emprestimoDAO = new EmprestimoDAO();
        usuarioDAO    = new UsuarioDAO();
        livroDAO      = new LivroDAO();
        configurarJanela();
        iniciarComponentes();
        emprestimoDAO.atualizarAtrasados(); // atualiza status ao abrir
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Registrar Empréstimo");
        setSize(820, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void iniciarComponentes() {
        // ---- Formulário ----
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Novo Empréstimo"));
        painelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbUsuario = new JComboBox<>();
        cmbLivro   = new JComboBox<>();
        txtDias    = new JTextField("14", 5);
        lblDataEmprestimo = new JLabel("Hoje: " + java.time.LocalDate.now().format(FMT));
        lblDataPrevista   = new JLabel("Prevista: " + java.time.LocalDate.now().plusDays(14).format(FMT));
        lblDataPrevista.setForeground(new Color(39, 174, 96));

        for (Usuario u : usuarioDAO.listarAtivos()) cmbUsuario.addItem(u);
        for (Livro l : livroDAO.listarTodos()) cmbLivro.addItem(l);

        adicionarCampo(painelForm, gbc, "Usuário:",          cmbUsuario,        0, 0, 3);
        adicionarCampo(painelForm, gbc, "Livro:",            cmbLivro,          0, 2, 3);
        adicionarCampo(painelForm, gbc, "Prazo (dias):",     txtDias,           0, 4, 1);
        adicionarCampo(painelForm, gbc, "Data empréstimo:",  lblDataEmprestimo, 2, 4, 1);
        adicionarCampo(painelForm, gbc, "Devolução prevista:", lblDataPrevista, 4, 4, 1);

        // Atualiza data prevista ao mudar o prazo
        txtDias.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                try {
                    int dias = Integer.parseInt(txtDias.getText().trim());
                    lblDataPrevista.setText("Prevista: " +
                        java.time.LocalDate.now().plusDays(dias).format(FMT));
                } catch (NumberFormatException ignored) {}
            }
        });

        // ---- Botões ----
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoes.setBackground(Color.WHITE);
        btnRegistrar = criarBotao("Registrar Empréstimo", new Color(142, 68, 173));
        btnLimpar    = criarBotao("Limpar", new Color(127, 140, 141));
        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnLimpar);

        // ---- Tabela de Empréstimos Ativos ----
        String[] colunas = {"ID", "Usuário", "Livro", "Empréstimo", "Prevista", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setRowHeight(22);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        JScrollPane scroll = new JScrollPane(tabela);

        JLabel lblTabela = new JLabel("  Empréstimos registrados:");
        lblTabela.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel painelNorte = new JPanel(new BorderLayout());
        painelNorte.add(painelForm, BorderLayout.CENTER);
        painelNorte.add(painelBotoes, BorderLayout.SOUTH);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(lblTabela, BorderLayout.NORTH);
        painelSul.add(scroll, BorderLayout.CENTER);

        add(painelNorte, BorderLayout.NORTH);
        add(painelSul, BorderLayout.CENTER);

        // ---- Eventos ----
        btnRegistrar.addActionListener(e -> registrarEmprestimo());
        btnLimpar.addActionListener(e -> {
            cmbUsuario.setSelectedIndex(0);
            cmbLivro.setSelectedIndex(0);
            txtDias.setText("14");
            lblDataPrevista.setText("Prevista: " + java.time.LocalDate.now().plusDays(14).format(FMT));
        });
    }

    private void registrarEmprestimo() {
        Usuario usuario = (Usuario) cmbUsuario.getSelectedItem();
        Livro livro     = (Livro) cmbLivro.getSelectedItem();

        if (usuario == null || livro == null) {
            JOptionPane.showMessageDialog(this, "Selecione o usuário e o livro.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dias;
        try {
            dias = Integer.parseInt(txtDias.getText().trim());
            if (dias <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Informe um prazo válido em dias.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (livro.getQuantidade() <= 0) {
            JOptionPane.showMessageDialog(this, "Este livro não possui exemplares disponíveis.", "Indisponível", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Emprestimo emprestimo = new Emprestimo(usuario, livro, funcionarioLogado, dias);
        if (emprestimoDAO.salvar(emprestimo)) {
            // Diminui a quantidade disponível do livro
            livro.setQuantidade(livro.getQuantidade() - 1);
            new LivroDAO().atualizar(livro);
            JOptionPane.showMessageDialog(this, "Empréstimo registrado com sucesso!");
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao registrar empréstimo.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (Emprestimo e : emprestimoDAO.listarTodos()) {
            modeloTabela.addRow(new Object[]{
                e.getId(),
                e.getUsuario().getNome(),
                e.getLivro().getTitulo(),
                e.getDataEmprestimo().format(FMT),
                e.getDataPrevista().format(FMT),
                e.getStatus().getDescricao()
            });
        }
    }

    private void adicionarCampo(JPanel p, GridBagConstraints g, String label, JComponent campo, int x, int y, int largura) {
        g.gridx = x; g.gridy = y; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = x + 1; g.gridwidth = largura;
        p.add(campo, g);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
