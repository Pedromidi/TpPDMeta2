package pt.meta_II.tppd.clienteHTTP;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Scanner;

public class RestClient {
    private String jwtToken;

    // Método para registar um novo utilizador
    public void registerUser(String name, String phone, String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/api/users/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String payload = String.format(
                    "{\"name\":\"%s\", \"phone\":\"%s\", \"email\":\"%s\", \"password\":\"%s\"}",
                    name, phone, email, password
            );

            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload.getBytes());
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("User registered successfully.");
            } else {
                System.out.printf("Failed to register user. HTTP response code: %d%n", responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para autenticar um utilizador
    public void authenticateUser(String email, String password) {
        try {
            URL url = new URL("http://localhost:8080/api/users/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((email + ":" + password).getBytes()));
            connection.setRequestProperty("Content-Type", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                jwtToken = scanner.nextLine(); // Supondo que o token JWT vem como resposta.
                System.out.println("Authentication successful. JWT: " + jwtToken);
                scanner.close();
            } else {
                System.out.printf("Authentication failed. HTTP response code: %d%n", responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RestClient client = new RestClient();
        client.registerUser("Random Brandon", "123456789", "random.brandon@example.com", "password123");
        client.authenticateUser("random.brandon@example.com", "password123");
    }
}
