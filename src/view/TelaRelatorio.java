package view;

import dao.EmprestimoDAO;
import dao.LivroDAO;
import dao.UsuarioDAO;
import model.Emprestimo;
import model.Livro;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Tela de Relatórios do Sistema de Biblioteca.
 * Exibe relatórios de: todos os empréstimos, atrasados, acervo e usuários.
*/
public class TelaRelatorio extends JFrame {

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblInfo;
    private JTabbedPane abas;

    private EmprestimoDAO emprestimoDAO;
    private LivroDAO livroDAO;
    private UsuarioDAO usuarioDAO;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TelaRelatorio() {
        emprestimoDAO = new EmprestimoDAO();
        livroDAO      = new LivroDAO();
        usuarioDAO    = new UsuarioDAO();
        configurarJanela();
        iniciarComponentes();
    }

    private void configurarJanela() {
        setTitle("Relatórios");
        setSize(860, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void iniciarComponentes() {
        // Topo
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(23, 165, 137));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel lblTitulo = new JLabel("📊  Relatórios do Sistema");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelTopo.add(lblTitulo, BorderLayout.WEST);

        // Abas
        abas = new JTabbedPane();
        abas.addTab("Todos os Empréstimos",  criarAbaEmprestimos());
        abas.addTab("Atrasados",             criarAbaAtrasados());
        abas.addTab("Acervo de Livros",      criarAbaLivros());
        abas.addTab("Usuários Cadastrados",  criarAbaUsuarios());

        // Rodapé com contador
        lblInfo = new JLabel("  Selecione uma aba para visualizar o relatório.");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        add(painelTopo, BorderLayout.NORTH);
        add(abas, BorderLayout.CENTER);
        add(lblInfo, BorderLayout.SOUTH);
    }

    // ---- Aba: Todos os Empréstimos ----
    private JPanel criarAbaEmprestimos() {
        String[] colunas = {"ID", "Usuário", "Livro", "Empréstimo", "Prevista", "Status", "Funcionário"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        emprestimoDAO.atualizarAtrasados();
        List<Emprestimo> lista = emprestimoDAO.listarTodos();
        for (Emprestimo e : lista) {
            modelo.addRow(new Object[]{
                e.getId(),
                e.getUsuario().getNome(),
                e.getLivro().getTitulo(),
                e.getDataEmprestimo().format(FMT),
                e.getDataPrevista().format(FMT),
                e.getStatus().getDescricao(),
                e.getFuncionario().getNome()
            });
        }

        JTable tbl = criarTabela(modelo);
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        painel.add(criarRodapeInfo("Total de empréstimos: " + lista.size()), BorderLayout.SOUTH);
        return painel;
    }

    // ---- Aba: Atrasados ----
    private JPanel criarAbaAtrasados() {
        String[] colunas = {"ID", "Usuário", "Livro", "Prevista", "Dias Atraso"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Emprestimo> lista = emprestimoDAO.listarAtivos();
        int atrasados = 0;
        for (Emprestimo e : lista) {
            if (e.isAtrasado()) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(
                    e.getDataPrevista(), java.time.LocalDate.now());
                modelo.addRow(new Object[]{
                    e.getId(),
                    e.getUsuario().getNome(),
                    e.getLivro().getTitulo(),
                    e.getDataPrevista().format(FMT),
                    dias + " dia(s)"
                });
                atrasados++;
            }
        }

        JTable tbl = criarTabela(modelo);
        // Pinta tudo em vermelho claro
        tbl.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(new Color(255, 220, 220));
                return comp;
            }
        });

        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        painel.add(criarRodapeInfo("Empréstimos atrasados: " + atrasados), BorderLayout.SOUTH);
        return painel;
    }

    // ---- Aba: Acervo de Livros ----
    private JPanel criarAbaLivros() {
        String[] colunas = {"ID", "Título", "ISBN", "Ano", "Qtd. Disponível", "Autor", "Categoria"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Livro> lista = livroDAO.listarTodos();
        for (Livro l : lista) {
            modelo.addRow(new Object[]{
                l.getId(), l.getTitulo(), l.getIsbn(),
                l.getAnoPublicacao(), l.getQuantidade(),
                l.getAutor().getNome(), l.getCategoria().getNome()
            });
        }

        JTable tbl = criarTabela(modelo);
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        painel.add(criarRodapeInfo("Total de títulos no acervo: " + lista.size()), BorderLayout.SOUTH);
        return painel;
    }

    // ---- Aba: Usuários ----
    private JPanel criarAbaUsuarios() {
        String[] colunas = {"ID", "Nome", "CPF", "E-mail", "Telefone", "Tipo", "Ativo"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        List<Usuario> lista = usuarioDAO.listarTodos();
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getId(), u.getNome(), u.getCpf(),
                u.getEmail(), u.getTelefone(),
                u.getTipo().getDescricao(), u.isAtivo() ? "Sim" : "Não"
            });
        }

        JTable tbl = criarTabela(modelo);
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JScrollPane(tbl), BorderLayout.CENTER);
        painel.add(criarRodapeInfo("Total de usuários cadastrados: " + lista.size()), BorderLayout.SOUTH);
        return painel;
    }

    private JTable criarTabela(DefaultTableModel modelo) {
        JTable tbl = new JTable(modelo);
        tbl.setRowHeight(22);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.getTableHeader().setBackground(new Color(23, 165, 137));
        tbl.getTableHeader().setForeground(Color.WHITE);
        tbl.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tbl.setFont(new Font("Arial", Font.PLAIN, 12));
        if (modelo.getColumnCount() > 0)
            tbl.getColumnModel().getColumn(0).setMaxWidth(40);
        return tbl;
    }

    private JPanel criarRodapeInfo(String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBackground(new Color(240, 240, 240));
        JLabel lbl = new JLabel("  " + texto);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        lbl.setForeground(new Color(23, 165, 137));
        p.add(lbl);
        return p;
    }
}
