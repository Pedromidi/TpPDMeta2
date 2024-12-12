package pt.meta_II.tppd.clienteHTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

public class HttpClient {

    private static String jwtToken;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Selecione uma opção:");
            System.out.println("1. Registrar");
            System.out.println("2. Autenticar");
            System.out.println("3. Listar grupos");
            System.out.println("4. Inserir despesa");
            System.out.println("5. Listar despesas");
            System.out.println("6. Eliminar despesa");
            System.out.println("0. Sair");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            switch (opcao) {
                case 1:
                    register(scanner);
                    break;
                case 2:
                    authenticate(scanner);
                    break;
                case 3:
                    listGroups();
                    break;
                case 4:
                    insertExpense(scanner);
                    break;
                case 5:
                    listExpenses(scanner);
                    break;
                case 6:
                    deleteExpense(scanner);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private static void register(Scanner scanner) {
        System.out.println("=== Registrar ===");
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String phone = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        String body = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"phone\":\"%s\",\"password\":\"%s\"}",
                name, email, phone, password);

        sendRequest("http://localhost:8080/register", "POST", body, false);
    }

    private static void authenticate(Scanner scanner) {
        System.out.println("=== Autenticar ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Senha: ");
        String password = scanner.nextLine();

        String credentials = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8080/login");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setDoOutput(true);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Scanner responseScanner = new Scanner(connection.getInputStream());
                if (responseScanner.hasNext()) {
                    jwtToken = responseScanner.nextLine();
                    System.out.println("Autenticação bem-sucedida. Token JWT recebido.");
                }
                responseScanner.close();
            } else {
                System.out.println("Falha na autenticação. Código: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void listGroups() {
        System.out.println("=== Listar Grupos ===");
        sendRequest("http://localhost:8080/groups", "GET", null, true);
    }

    private static void insertExpense(Scanner scanner) {
        System.out.println("=== Inserir Despesa ===");
        System.out.print("ID do Grupo: ");
        String groupId = scanner.nextLine();
        System.out.print("Descrição: ");
        String description = scanner.nextLine();
        System.out.print("Valor: ");
        double value = scanner.nextDouble();
        scanner.nextLine(); // Consumir nova linha

        String body = String.format("{\"description\":\"%s\",\"value\":%f}", description, value);
        sendRequest("http://localhost:8080/groups/" + groupId + "/expenses", "POST", body, true);
    }

    private static void listExpenses(Scanner scanner) {
        System.out.println("=== Listar Despesas ===");
        System.out.print("ID do Grupo: ");
        String groupId = scanner.nextLine();
        sendRequest("http://localhost:8080/groups/" + groupId + "/expenses", "GET", null, true);
    }

    private static void deleteExpense(Scanner scanner) {
        System.out.println("=== Eliminar Despesa ===");
        System.out.print("ID do Grupo: ");
        String groupId = scanner.nextLine();
        System.out.print("ID da Despesa: ");
        String expenseId = scanner.nextLine();
        sendRequest("http://localhost:8080/groups/" + groupId + "/expenses/" + expenseId, "DELETE", null, true);
    }

    private static void sendRequest(String urlString, String method, String body, boolean requiresAuth) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            if (requiresAuth && jwtToken != null) {
                connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            }

            if (body != null) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.getBytes());
                    os.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Código de resposta: " + responseCode);
            if (responseCode == 200 || responseCode == 201) {
                Scanner responseScanner = new Scanner(connection.getInputStream());
                while (responseScanner.hasNextLine()) {
                    System.out.println(responseScanner.nextLine());
                }
                responseScanner.close();
            } else {
                System.out.println("Erro na requisição.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
