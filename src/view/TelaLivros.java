package view;

import dao.AutorDAO;
import dao.CategoriaDAO;
import dao.LivroDAO;
import model.Autor;
import model.Categoria;
import model.Livro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Tela de gerenciamento de Livros (CRUD completo).
 * Permite cadastrar, editar, excluir e buscar livros do acervo.
*/
public class TelaLivros extends JFrame {

    private JTextField txtTitulo, txtIsbn, txtAno, txtQuantidade, txtBusca;
    private JComboBox<Autor> cmbAutor;
    private JComboBox<Categoria> cmbCategoria;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JButton btnSalvar, btnEditar, btnExcluir, btnLimpar, btnBuscar;

    private LivroDAO livroDAO;
    private AutorDAO autorDAO;
    private CategoriaDAO categoriaDAO;
    private int idSelecionado = -1;

    public TelaLivros() {
        livroDAO = new LivroDAO();
        autorDAO = new AutorDAO();
        categoriaDAO = new CategoriaDAO();
        configurarJanela();
        iniciarComponentes();
        carregarTabela();
    }

    private void configurarJanela() {
        setTitle("Gerenciar Livros");
        setSize(800, 580);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void iniciarComponentes() {
        // ---- Painel de Formulário ----
        JPanel painelForm = new JPanel(new GridBagLayout());
        painelForm.setBorder(BorderFactory.createTitledBorder("Dados do Livro"));
        painelForm.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtTitulo    = new JTextField();
        txtIsbn      = new JTextField();
        txtAno       = new JTextField();
        txtQuantidade = new JTextField();
        cmbAutor     = new JComboBox<>();
        cmbCategoria = new JComboBox<>();

        // Carrega autores e categorias nos ComboBox
        for (Autor a : autorDAO.listarTodos()) cmbAutor.addItem(a);
        for (Categoria c : categoriaDAO.listarTodos()) cmbCategoria.addItem(c);

        adicionarCampo(painelForm, gbc, "Título:",      txtTitulo,    0, 0, 3);
        adicionarCampo(painelForm, gbc, "ISBN:",        txtIsbn,      0, 2, 1);
        adicionarCampo(painelForm, gbc, "Ano:",         txtAno,       2, 2, 1);
        adicionarCampo(painelForm, gbc, "Quantidade:",  txtQuantidade, 0, 4, 1);
        adicionarCampo(painelForm, gbc, "Autor:",       cmbAutor,     0, 6, 1);
        adicionarCampo(painelForm, gbc, "Categoria:",   cmbCategoria, 2, 6, 1);

        // ---- Painel de Botões ----
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

        // ---- Painel de Busca ----
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        painelBusca.setBackground(new Color(240, 240, 240));
        txtBusca = new JTextField(25);
        btnBuscar = criarBotao("Buscar", new Color(33, 97, 140));
        JButton btnTodos = criarBotao("Listar Todos", new Color(100, 100, 100));
        painelBusca.add(new JLabel("Buscar por título:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnTodos);

        // ---- Tabela ----
        String[] colunas = {"ID", "Título", "ISBN", "Ano", "Qtd", "Autor", "Categoria"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        tabela.setRowHeight(22);
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setPreferredSize(new Dimension(760, 200));

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelBusca, BorderLayout.NORTH);
        painelSul.add(scroll, BorderLayout.CENTER);

        JPanel painelNorte = new JPanel(new BorderLayout());
        painelNorte.add(painelForm, BorderLayout.CENTER);
        painelNorte.add(painelBotoes, BorderLayout.SOUTH);

        add(painelNorte, BorderLayout.NORTH);
        add(painelSul, BorderLayout.CENTER);

        // ---- Eventos ----
        btnSalvar.addActionListener(e -> salvar());
        btnEditar.addActionListener(e -> editar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limpar());
        btnBuscar.addActionListener(e -> buscar());
        btnTodos.addActionListener(e -> carregarTabela());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                preencherFormularioDaTabela();
            }
        });
    }

    private void salvar() {
        if (!validarCampos()) return;
        Livro livro = montarLivroDoscampos();
        if (livroDAO.salvar(livro)) {
            JOptionPane.showMessageDialog(this, "Livro salvo com sucesso!");
            limpar(); carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao salvar livro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editar() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela para editar.");
            return;
        }
        if (!validarCampos()) return;
        Livro livro = montarLivroDoscampos();
        livro.setId(idSelecionado);
        if (livroDAO.atualizar(livro)) {
            JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
            limpar(); carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar livro.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para excluir.");
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
            "Confirma a exclusão do livro selecionado?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            if (livroDAO.deletar(idSelecionado)) {
                JOptionPane.showMessageDialog(this, "Livro excluído com sucesso!");
                limpar(); carregarTabela();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Não é possível excluir. O livro possui empréstimos vinculados.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscar() {
        String termo = txtBusca.getText().trim();
        if (termo.isEmpty()) { carregarTabela(); return; }
        List<Livro> lista = livroDAO.buscarPorTitulo(termo);
        preencherTabela(lista);
    }

    private void carregarTabela() {
        preencherTabela(livroDAO.listarTodos());
    }

    private void preencherTabela(List<Livro> lista) {
        modeloTabela.setRowCount(0);
        for (Livro l : lista) {
            modeloTabela.addRow(new Object[]{
                l.getId(), l.getTitulo(), l.getIsbn(),
                l.getAnoPublicacao(), l.getQuantidade(),
                l.getAutor().getNome(), l.getCategoria().getNome()
            });
        }
    }

    private void preencherFormularioDaTabela() {
        int row = tabela.getSelectedRow();
        idSelecionado = (int) modeloTabela.getValueAt(row, 0);
        Livro livro = livroDAO.buscarPorId(idSelecionado);
        if (livro != null) {
            txtTitulo.setText(livro.getTitulo());
            txtIsbn.setText(livro.getIsbn());
            txtAno.setText(String.valueOf(livro.getAnoPublicacao()));
            txtQuantidade.setText(String.valueOf(livro.getQuantidade()));
            selecionarComboBox(cmbAutor, livro.getAutor().getId());
            selecionarComboBox(cmbCategoria, livro.getCategoria().getId());
        }
    }

    private void selecionarComboBox(JComboBox<?> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            if (item instanceof Autor && ((Autor) item).getId() == id) { combo.setSelectedIndex(i); return; }
            if (item instanceof Categoria && ((Categoria) item).getId() == id) { combo.setSelectedIndex(i); return; }
        }
    }

    private Livro montarLivroDoscampos() {
        return new Livro(
            txtTitulo.getText().trim(),
            txtIsbn.getText().trim(),
            Integer.parseInt(txtAno.getText().trim()),
            Integer.parseInt(txtQuantidade.getText().trim()),
            (Autor) cmbAutor.getSelectedItem(),
            (Categoria) cmbCategoria.getSelectedItem()
        );
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty() || txtIsbn.getText().trim().isEmpty()
                || txtAno.getText().trim().isEmpty() || txtQuantidade.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(txtAno.getText().trim());
            Integer.parseInt(txtQuantidade.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ano e Quantidade devem ser números.", "Atenção", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpar() {
        txtTitulo.setText(""); txtIsbn.setText("");
        txtAno.setText(""); txtQuantidade.setText("");
        cmbAutor.setSelectedIndex(0); cmbCategoria.setSelectedIndex(0);
        txtBusca.setText(""); idSelecionado = -1;
        tabela.clearSelection();
    }

    private void adicionarCampo(JPanel p, GridBagConstraints g, String label, JComponent campo, int x, int y, int largura) {
        g.gridx = x; g.gridy = y; g.gridwidth = 1;
        p.add(new JLabel(label), g);
        g.gridx = x + 1; g.gridwidth = largura;
        p.add(campo, g);
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
