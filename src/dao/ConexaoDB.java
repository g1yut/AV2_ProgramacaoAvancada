package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por gerenciar a conexão com o banco de dados MySQL.
 * Utiliza o padrão Singleton para garantir uma única instância de conexão.
 */
public class ConexaoDB {

    // ⚠️ Altere a senha abaixo para a senha do seu MySQL root
    private static final String URL    = "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "root";
    private static final String SENHA   = "root123";

    private static Connection conexao = null;

    // Construtor privado — impede instanciar diretamente
    private ConexaoDB() {}

    /**
     * Retorna a conexão ativa com o banco. Cria uma nova se não existir.
     * @return objeto Connection com o banco MySQL
     */
    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                System.out.println("✔ Conexão com o banco estabelecida.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC não encontrado. Adicione o .jar ao Build Path.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erro ao conectar ao banco de dados.");
            e.printStackTrace();
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco de dados.
     */
    public static void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("✔ Conexão encerrada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erro ao fechar a conexão.");
            e.printStackTrace();
        }
    }
}
