package pt.meta_II.tppd.clienteHTTP;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                else{
                    System.out.println("\nNúmero Inválido. Tente novamente");
                    scanner.nextLine();
                }
            }catch (InputMismatchException e){
                System.out.println("\nNúmero Inválido. Tente novamente");
                scanner.nextLine();
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
                scanner.nextLine();
            }catch (JsonProcessingException e){
                System.out.println("Não foi possivel criar uma nova despesa....");
                scanner.nextLine();
            }
        }
    }

    private static void register(Scanner scanner) throws InputMismatchException{
        System.out.println("=== Registrar ===");
        System.out.print("Nome: ");
        String nome = scanner.next();
        System.out.print("Email: ");
        String email = scanner.next();
        System.out.print("Telefone: ");
        int phone = scanner.nextInt();
        System.out.print("Senha: ");
        String password = scanner.next();

        String request =  "http://localhost:8080/register?email="+email+"&nome="+nome+"&telefone="+phone+"&password="+ password;

        sendRequest(request, "POST", null, false);

        //dá login depois do registo
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

    private static void insertExpense(Scanner scanner) throws JsonProcessingException {
        System.out.println("\n=== Inserir Despesa ===");
        System.out.print("Nome do Grupo: ");
        scanner.nextLine();//flush
        String group = scanner.nextLine();
        System.out.print("Quem Pagou: ");
        String quem = scanner.nextLine();
        System.out.print("Valor: ");
        float valor = scanner.nextFloat();
        System.out.print("Data (dd/mm/aa): ");
        String data = scanner.next();

        System.out.print("Descricao: ");
        scanner.nextLine();//flush
        String descricao = scanner.nextLine();
        System.out.print("Elementos a partilhar (<email> <email> ...): ");
        //scanner.nextLine();//flush
        String comQuem = scanner.nextLine();

        String[] partilhas = comQuem.split(" ");

        Despesa despesa = new Despesa(quem,data,valor,descricao,partilhas);
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(despesa);

        sendRequest("http://localhost:8080/" + group + "/adicionar", "POST", jsonString, true);
    }

    private static void listExpenses(Scanner scanner) {
        System.out.println("\n=== Listar Despesas ===");
        System.out.print("Nome do Grupo: ");
        scanner.nextLine();
        String group = scanner.nextLine();
        sendRequest("http://localhost:8080/" + group + "/despesas", "GET", null, true);
    }

    private static void deleteExpense(Scanner scanner) throws InputMismatchException{
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
