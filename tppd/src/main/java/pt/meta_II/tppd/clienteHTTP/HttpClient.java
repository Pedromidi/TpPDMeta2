package pt.meta_II.tppd.clienteHTTP;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.InputMismatchException;
import java.util.Scanner;

public class HttpClient {

    private static String jwtToken;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        do {
            System.out.println("\nSelecione uma opção:");
            System.out.println("1. Autenticar");
            System.out.println("2. Registrar");

            try{
                int opcao = scanner.nextInt();

                if(opcao == 1){
                    System.out.println("=== Autenticar ===");
                    System.out.print("Email: ");
                    String email = scanner.next();
                    System.out.print("Senha: ");
                    String password = scanner.next();

                    authenticate(email,password);
                    if (jwtToken!=null)
                        break;
                }
                if (opcao == 2){
                    register(scanner);
                    if (jwtToken!=null)
                        break;
                }

                else System.out.println("\nNúmero Inválido. Tente novamente");
            }catch (InputMismatchException e){
                System.out.println("\nNúmero Inválido. Tente novamente");
            }
        }while (true);

        while (true) {
            System.out.println("\nSelecione uma opção:");
            System.out.println("1. Listar grupos");
            System.out.println("2. Inserir despesa");
            System.out.println("3. Listar despesas");
            System.out.println("4. Eliminar despesa");
            System.out.println("0. Sair");

            try{
                int opcao = scanner.nextInt();

                switch (opcao) {
                    case 1:
                        listGroups();
                        break;
                    case 2:
                        insertExpense(scanner);
                        break;
                    case 3:
                        listExpenses(scanner);
                        break;
                    case 4:
                        deleteExpense(scanner);
                        break;
                    case 0:
                        System.out.println("A sair...");
                        return;
                    default:
                        System.out.println("Opção inválida.");
                }
            }catch (InputMismatchException e){
                System.out.println("Número Inválido. Tente novamente");
            }
        }
    }

    private static void register(Scanner scanner) {
        System.out.println("=== Registrar ===");
        System.out.print("Nome: ");
        String nome = scanner.next();
        System.out.print("Email: ");
        String email = scanner.next();
        System.out.print("Telefone: ");
        String phone = scanner.next();
        System.out.print("Senha: ");
        String password = scanner.next();

        String request =  "http://localhost:8080/register?email="+email+"&nome="+nome+"&telefone="+phone+"&password="+ password;

        sendRequest(request, "POST", null, false);

        //authenticate(email,password);
        String credentials = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8080/login");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Basic " + credentials);
            connection.setDoOutput(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static void authenticate(String email, String password) {
        String credentials = Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
        HttpURLConnection connection = null;
        try {
            URL url = new URL("http://localhost:8080/login");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
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
            }
                else {
                    System.out.println("Erro na requisição. Código de resposta: " + responseCode);
                    if (connection.getErrorStream() != null) {
                        Scanner errorScanner = new Scanner(connection.getErrorStream());
                        System.out.println("Mensagem de erro:");
                        while (errorScanner.hasNextLine()) {
                            System.out.println(errorScanner.nextLine());
                        }
                        errorScanner.close();
                    }
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
        System.out.println("\n=== Listar Grupos ===");
        sendRequest("http://localhost:8080/grupos", "GET", null, true);
    }

    private static void insertExpense(Scanner scanner) {
        System.out.println("\n=== Inserir Despesa ===");
        System.out.print("Nome do Grupo: ");
        String group = scanner.nextLine();
        System.out.print("Quem Pagou: ");
        String quem = scanner.nextLine();
        System.out.print("Valor: ");
        float value = scanner.nextFloat();
        System.out.print("Data (dd/mm/aa): ");
        String data = scanner.next();

        System.out.print("Descrição: ");
        String description = scanner.nextLine();

        scanner.nextFloat(); // Consumir nova linha

        //TODO, o metodo nao está a receber uma string.... mas sim uma Despesa, e tbm não é no corpo...
        String body = String.format("{\"description\":\"%s\",\"value\":%f}", description, value);
        sendRequest("http://localhost:8080/" + group + "/adicionar", "POST", body, true);
    }

    private static void listExpenses(Scanner scanner) {
        System.out.println("\n=== Listar Despesas ===");
        System.out.print("Nome do Grupo: ");
        scanner.nextLine();
        String group = scanner.nextLine();
        sendRequest("http://localhost:8080/" + group + "/despesas", "GET", null, true);
    }

    private static void deleteExpense(Scanner scanner) {
        System.out.println("=== Eliminar Despesa ===");
        System.out.print("Nome do Grupo: ");
        scanner.nextLine();
        String group = scanner.nextLine();
        System.out.print("ID da Despesa: ");
        int expenseId = scanner.nextInt();
        sendRequest("http://localhost:8080/" + group + "/eliminar?id=" + expenseId, "POST", null, true);
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

            if (responseCode == 200 || responseCode == 201) {
                System.out.println("Código de resposta: " + responseCode);
                Scanner responseScanner = new Scanner(connection.getInputStream());
                while (responseScanner.hasNextLine()) {
                    System.out.println(responseScanner.nextLine());
                }
                responseScanner.close();
            } else {
                System.out.println("Erro na requisição. Código de resposta: " + responseCode);
                if (connection.getErrorStream() != null) {
                    Scanner errorScanner = new Scanner(connection.getErrorStream());
                    System.out.println("Mensagem de erro:");
                    while (errorScanner.hasNextLine()) {
                        System.out.println(errorScanner.nextLine());
                    }
                    errorScanner.close();
                }
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
