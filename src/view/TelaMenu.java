package view;

import model.Funcionario;

import javax.swing.*;
import java.awt.*;

/**
 * Tela do Menu Principal do Sistema de Biblioteca.
 * Exibe os módulos disponíveis e o funcionário logado.
*/
public class TelaMenu extends JFrame {

    private Funcionario funcionarioLogado;

    public TelaMenu(Funcionario funcionario) {
        this.funcionarioLogado = funcionario;
        configurarJanela();
        iniciarComponentes();
    }

    private void configurarJanela() {
        setTitle("Biblioteca - Menu Principal");
        setSize(520, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
    }

    private void iniciarComponentes() {
        // Topo azul com título e usuário logado
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(33, 97, 140));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("📚 Sistema de Gerenciamento de Biblioteca");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel lblUsuario = new JLabel("Olá, " + funcionarioLogado.getNome() + "  |  " + funcionarioLogado.getCargo());
        lblUsuario.setForeground(new Color(200, 230, 255));
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 12));

        painelTopo.add(lblTitulo, BorderLayout.CENTER);
        painelTopo.add(lblUsuario, BorderLayout.SOUTH);

        // Painel central com os botões de módulos
        JPanel painelBotoes = new JPanel(new GridLayout(3, 2, 15, 15));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));
        painelBotoes.setBackground(new Color(245, 245, 245));

        JButton btnLivros      = criarBotao("📖  Livros",        new Color(41, 128, 185));
        JButton btnUsuarios    = criarBotao("👤  Usuários",       new Color(39, 174, 96));
        JButton btnEmprestimos = criarBotao("🔄  Empréstimos",    new Color(142, 68, 173));
        JButton btnDevolucoes  = criarBotao("↩  Devoluções",      new Color(211, 84, 0));
        JButton btnRelatorio   = criarBotao("📊  Relatórios",      new Color(23, 165, 137));
        JButton btnSair        = criarBotao("🚪  Sair",           new Color(150, 150, 150));

        painelBotoes.add(btnLivros);
        painelBotoes.add(btnUsuarios);
        painelBotoes.add(btnEmprestimos);
        painelBotoes.add(btnDevolucoes);
        painelBotoes.add(btnRelatorio);
        painelBotoes.add(btnSair);

        // Rodapé
        JPanel painelRodape = new JPanel();
        painelRodape.setBackground(new Color(230, 230, 230));
        JLabel lblRodape = new JLabel("Disciplina: Programação Avançada (184987) — UNIASSELVI");
        lblRodape.setFont(new Font("Arial", Font.PLAIN, 11));
        lblRodape.setForeground(Color.GRAY);
        painelRodape.add(lblRodape);

        add(painelTopo, BorderLayout.NORTH);
        add(painelBotoes, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);

        // Eventos dos botões
        btnLivros.addActionListener(e -> new TelaLivros().setVisible(true));
        btnUsuarios.addActionListener(e -> new TelaUsuarios().setVisible(true));
        btnEmprestimos.addActionListener(e -> new TelaEmprestimos(funcionarioLogado).setVisible(true));
        btnDevolucoes.addActionListener(e -> new TelaDevolucoes().setVisible(true));
        btnRelatorio.addActionListener(e -> new TelaRelatorio().setVisible(true));
        btnSair.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this,
                "Deseja sair do sistema?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                new TelaLogin().setVisible(true);
            }
        });
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton btn = new JButton(texto);
        btn.setBackground(cor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(180, 70));
        return btn;
    }
}
