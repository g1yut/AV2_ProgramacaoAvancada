package view;

import dao.FuncionarioDAO;
import model.Funcionario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela de Login do Sistema de Biblioteca.
 * Autentica o funcionário antes de abrir o menu principal.
 */
public class TelaLogin extends JFrame {

    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnSair;
    private FuncionarioDAO funcionarioDAO;

    public TelaLogin() {
        funcionarioDAO = new FuncionarioDAO();
        configurarJanela();
        iniciarComponentes();
    }

    private void configurarJanela() {
        setTitle("Biblioteca - Login");
        setSize(380, 260);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout());
    }

    private void iniciarComponentes() {
        // Painel do título
        JPanel painelTopo = new JPanel();
        painelTopo.setBackground(new Color(33, 97, 140));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel lblTitulo = new JLabel("📚 Sistema de Biblioteca");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelTopo.add(lblTitulo);

        // Painel central com campos
        JPanel painelCentro = new JPanel(new GridBagLayout());
        painelCentro.setBackground(new Color(245, 245, 245));
        painelCentro.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 5, 6, 5);

        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        painelCentro.add(lblLogin, gbc);

        txtLogin = new JTextField();
        txtLogin.setFont(new Font("Arial", Font.PLAIN, 13));
        txtLogin.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        painelCentro.add(txtLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setFont(new Font("Arial", Font.PLAIN, 13));
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        painelCentro.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        txtSenha.setFont(new Font("Arial", Font.PLAIN, 13));
        txtSenha.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7;
        painelCentro.add(txtSenha, gbc);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelBotoes.setBackground(new Color(245, 245, 245));

        btnEntrar = new JButton("Entrar");
        btnEntrar.setBackground(new Color(33, 97, 140));
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEntrar.setPreferredSize(new Dimension(100, 35));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSair = new JButton("Sair");
        btnSair.setFont(new Font("Arial", Font.PLAIN, 13));
        btnSair.setPreferredSize(new Dimension(100, 35));
        btnSair.setFocusPainted(false);
        btnSair.setCursor(new Cursor(Cursor.HAND_CURSOR));

        painelBotoes.add(btnEntrar);
        painelBotoes.add(btnSair);

        add(painelTopo, BorderLayout.NORTH);
        add(painelCentro, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        // Eventos
        btnEntrar.addActionListener(e -> autenticar());
        btnSair.addActionListener(e -> System.exit(0));

        // Enter no campo senha também aciona o login
        txtSenha.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) autenticar();
            }
        });
    }

    private void autenticar() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Preencha o login e a senha.", "Atenção",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Funcionario funcionario = funcionarioDAO.autenticar(login, senha);

        if (funcionario != null) {
            dispose(); // fecha a tela de login
            new TelaMenu(funcionario).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Login ou senha inválidos.", "Erro de autenticação",
                JOptionPane.ERROR_MESSAGE);
            txtSenha.setText("");
            txtLogin.requestFocus();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
