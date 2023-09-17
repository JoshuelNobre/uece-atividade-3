import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

class Contato {
    private int id;
    private String nome;
    private String endereco;
    private String telefone;
    private String email;

    // Construtor
    public Contato(int id, String nome, String endereco, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }
}

public class AgendaContatos {
    private static final String DATABASE_URL = "jdbc:sqlite:agenda.db";

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL);

            criarTabelaContatos(conn);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\nMenu de Opções:");
                System.out.println("1. Incluir Contato");
                System.out.println("2. Consultar Contato");
                System.out.println("3. Modificar Contato");
                System.out.println("4. Deletar Contato");
                System.out.println("5. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao = scanner.nextInt();
                scanner.nextLine(); // Limpar o buffer de entrada

                switch (opcao) {
                    case 1:
                        incluirContato(conn, scanner);
                        break;
                    case 2:
                        consultarContato(conn, scanner);
                        break;
                    case 3:
                        modificarContato(conn, scanner);
                        break;
                    case 4:
                        deletarContato(conn, scanner);
                        break;
                    case 5:
                        conn.close();
                        System.out.println("Encerrando o programa.");
                        System.exit(0);
                    default:
                        System.out.println("Opção inválida.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void criarTabelaContatos(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS contatos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nome TEXT," +
                "endereco TEXT," +
                "telefone TEXT," +
                "email TEXT" +
                ")";
        conn.createStatement().execute(sql);
    }

    private static void incluirContato(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();

        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();

        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        String sql = "INSERT INTO contatos (nome, endereco, telefone, email) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, nome);
        preparedStatement.setString(2, endereco);
        preparedStatement.setString(3, telefone);
        preparedStatement.setString(4, email);

        int rowsInserted = preparedStatement.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Contato inserido com sucesso.");
        } else {
            System.out.println("Erro ao inserir o contato.");
        }
    }

    private static void consultarContato(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Digite o nome do contato a ser consultado: ");
        String nomeConsulta = scanner.nextLine();

        String sql = "SELECT * FROM contatos WHERE nome = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, nomeConsulta);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Contato contato = new Contato(
                    resultSet.getInt("id"),
                    resultSet.getString("nome"),
                    resultSet.getString("endereco"),
                    resultSet.getString("telefone"),
                    resultSet.getString("email")
            );

            System.out.println("\nInformações do Contato:");
            System.out.println("ID: " + contato.getId());
            System.out.println("Nome: " + contato.getNome());
            System.out.println("Endereço: " + contato.getEndereco());
            System.out.println("Telefone: " + contato.getTelefone());
            System.out.println("Email: " + contato.getEmail());
        } else {
            System.out.println("Contato não encontrado.");
        }
    }

    private static void modificarContato(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Digite o nome do contato a ser modificado: ");
        String nomeConsulta = scanner.nextLine();

        System.out.print("Digite o novo nome (ou deixe em branco para manter o mesmo): ");
        String novoNome = scanner.nextLine();

        System.out.print("Digite o novo endereço (ou deixe em branco para manter o mesmo): ");
        String novoEndereco = scanner.nextLine();

        System.out.print("Digite o novo telefone (ou deixe em branco para manter o mesmo): ");
        String novoTelefone = scanner.nextLine();

        System.out.print("Digite o novo email (ou deixe em branco para manter o mesmo): ");
        String novoEmail = scanner.nextLine();

        StringBuilder sqlBuilder = new StringBuilder("UPDATE contatos SET ");
        boolean hasUpdates = false;

        if (!novoNome.isEmpty()) {
            sqlBuilder.append("nome = ?, ");
            hasUpdates = true;
        }
        if (!novoEndereco.isEmpty()) {
            sqlBuilder.append("endereco = ?, ");
            hasUpdates = true;
        }
        if (!novoTelefone.isEmpty()) {
            sqlBuilder.append("telefone = ?, ");
            hasUpdates = true;
        }
        if (!novoEmail.isEmpty()) {
            sqlBuilder.append("email = ?, ");
            hasUpdates = true;
        }

        if (hasUpdates) {
            sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
            sqlBuilder.append(" WHERE nome = ?");
            String sql = sqlBuilder.toString();
            PreparedStatement preparedStatement = conn.prepareStatement(sql);

            int parameterIndex = 1;

            if (!novoNome.isEmpty()) {
                preparedStatement.setString(parameterIndex++, novoNome);
            }
            if (!novoEndereco.isEmpty()) {
                preparedStatement.setString(parameterIndex++, novoEndereco);
            }
            if (!novoTelefone.isEmpty()) {
                preparedStatement.setString(parameterIndex++, novoTelefone);
            }
            if (!novoEmail.isEmpty()) {
                preparedStatement.setString(parameterIndex++, novoEmail);
            }

            preparedStatement.setString(parameterIndex, nomeConsulta);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Contato modificado com sucesso.");
            } else {
                System.out.println("Contato não existe.");
            }
        } else {
            System.out.println("Nenhum campo foi especificado para modificação.");
        }
    }

    private static void deletarContato(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Digite o nome do contato a ser deletado: ");
        String nomeConsulta = scanner.nextLine();

        String sql = "DELETE FROM contatos WHERE nome = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, nomeConsulta);

        int rowsDeleted = preparedStatement.executeUpdate();

        if (rowsDeleted > 0) {
            System.out.println("Contato deletado com sucesso.");
        } else {
            System.out.println("Nenhum contato foi deletado.");
        }
    }
}
