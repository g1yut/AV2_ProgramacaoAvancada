package view;

import dao.UsuarioDAO;
import enums.TipoUsuario;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de gerenciamento de Usuários (CRUD completo).
 * Permite cadastrar, editar, desativar e buscar usuários da biblioteca.
*/
public class TelaUsuarios extends JFrame {

    private JTextField txtNome, txtCpf, txtEmail, txtTelefone;
    private JComboBox<TipoUsuario> cmbTipo;
    private JCheckBox chkAtivo;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar;

    private UsuarioDAO usuarioDAO;
    private int idSelecionado = -1;

    public TelaUsuarios() {
        usuarioDAO = new UsuarioDAO();
        configurarJanela();
        iniciarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciar Usuários");
        setSize(750, 530);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void iniciarComponentes() {
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Usuário"));
        painelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNome      = new JTextField(20);
        txtCpf       = new JTextField(15);
        txtEmail     = new JTextField(20);
        txtTelefone  = new JTextField(15);
        cmbTipo      = new JComboBox<>(TipoUsuario.values());
        chkAtivo     = new JCheckBox("Ativo", true);

        adicionarCampo(painelForm, gbc, "Nome:",     txtNome,     0, 0, 3);
        adicionarCampo(painelForm, gbc, "CPF:",      txtCpf,      0, 2, 1);
        adicionarCampo(painelForm, gbc, "Telefone:", txtTelefone, 2, 2, 1);
        adicionarCampo(painelForm, gbc, "E-mail:",   txtEmail,    0, 4, 3);
        adicionarCampo(painelForm, gbc, "Tipo:",     cmbTipo,     0, 6, 1);
        gbc.gridx = 2; gbc.gridy = 6; gbc.gridwidth = 1;
        painelForm.add(chkAtivo, gbc);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelBotoes.setBackground(Color.WHITE);
        btnSalvar  = criarBotao("Salvar",   new Color(39, 174, 96));
        btnEditar  = criarBotao("Editar",   new Color(41, 128, 185));
        btnExcluir = criarBotao("Excluir",  new Color(192, 57, 43));
        btnLimpar  = criarBotao("Limpar",   new Color(127, 140, 141));
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnLimpar);

        String[] colunas = {"ID", "Nome", "CPF", "E-mail", "Telefone", "Tipo", "Ativo"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        tabela.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tabela);

        JPanel painelNorte = new JPanel(new BorderLayout());
        painelNorte.add(painelForm, BorderLayout.CENTER);
        painelNorte.add(painelBotoes, BorderLayout.SOUTH);

        add(painelNorte, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnSalvar.addActionListener(e -> salvar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                preencherFormularioDaTabela();
            }
        });
    }

    private void salvar() {
        if (!validarCampos()) return;
        Usuario u = montarUsuarioDoscampos();
        if (usuarioDAO.salvar(u)) {
            JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!");
            limpar(); carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar. CPF já cadastrado?", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSelecionado == -1) { JOptionPane.showMessageDialog(this, "Selecione um usuário."); return; }
        if (!validarCampos()) return;
        Usuario u = montarUsuarioDoscampos();
        u.setId(idSelecionado);
        if (usuarioDAO.atualizar(u)) {
            JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            limpar(); carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == -1) { JOptionPane.showMessageDialog(this, "Selecione um usuário."); return; }
        int op = JOptionPane.showConfirmDialog(this, "Confirma a exclusão?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (usuarioDAO.deletar(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
                limpar(); carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Não é possível excluir. Usuário possui empréstimos vinculados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (Usuario u : usuarioDAO.listarTodos()) {
            modeloTabela.addRow(new Object[]{
                u.getId(), u.getNome(), u.getCpf(), u.getEmail(),
                u.getTelefone(), u.getTipo().getDescricao(), u.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    private void preencherFormularioDaTabela() {
        int row = tabela.getSelectedRow();
        idSelecionado = (int) modeloTabela.getValueAt(row, 0);
        Usuario u = usuarioDAO.buscarPorId(idSelecionado);
        if (u != null) {
            txtNome.setText(u.getNome());
            txtCpf.setText(u.getCpf());
            txtEmail.setText(u.getEmail());
            txtTelefone.setText(u.getTelefone());
            cmbTipo.setSelectedItem(u.getTipo());
            chkAtivo.setSelected(u.isAtivo());
        }
    }

    private Usuario montarUsuarioDoscampos() {
        return new Usuario(
            txtNome.getText().trim(), txtCpf.getText().trim(),
            txtEmail.getText().trim(), txtTelefone.getText().trim(),
            (TipoUsuario) cmbTipo.getSelectedItem()
        );
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e CPF são obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpar() {
        txtNome.setText(""); txtCpf.setText("");
        txtEmail.setText(""); txtTelefone.setText("");
        cmbTipo.setSelectedIndex(0); chkAtivo.setSelected(true);
        idSelecionado = -1; tabela.clearSelection();
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
