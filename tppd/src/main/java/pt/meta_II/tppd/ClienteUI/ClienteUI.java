package pt.meta_II.tppd.ClienteUI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class ClienteUI {
    public static final int TIMEOUT = 10;
    public static Socket socket;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    public static String enviaComando(String comando) throws IOException, ClassNotFoundException {
        out.writeObject(comando);
        out.flush();//envio imediato

        String response = (String) in.readObject();
        return "\nServer: " + response;
    }

    public static String desconectarDoServidor() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            return "Desconectado do servidor com sucesso.";
        } catch (IOException e) {
            return "Erro ao desconectar do servidor: " + e.getMessage();
        }
    }

    public static String primeiraConexao(String[] args) throws IOException, ClassNotFoundException {
        InetAddress serverAddr;
        int serverPort;

        serverAddr = InetAddress.getByName(args[0]);
        serverPort = Integer.parseInt(args[1]);

        socket = new Socket(serverAddr, serverPort);
        socket.setSoTimeout(TIMEOUT * 1000);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject("Hello - cliente");
        out.flush();

        in = new ObjectInputStream(socket.getInputStream());
        String response = (String) in.readObject();

        return "Server:\n" + response;
    }


    public static void main(String[] args) {

        String command;
        String res;
        //inclui letras ou numeros,@, letras,., 2 ou 4 letras
        Pattern emailPattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z]+\\.[A-Za-z]{2,4}");
        //um ou douss digitos/../dois digitos
        Pattern dataPattern = Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{2}$");
        //digito de 0-9, ocorre 9 vezes
        Pattern telemovelPattern = Pattern.compile("^[0-9]{9}");

        if (args.length != 2) {
            System.out.println("Sintaxe: java Client serverAddress serverPort");
            return;
        }

        try {
            System.out.println(primeiraConexao(args));

            Scanner input = new Scanner(System.in);
            int opcao = 0;

            System.out.println("Escolha uma opcao:");
            System.out.println("1. Autenticacao");
            System.out.println("2. Registo ");
            System.out.print("3. Sair\n>");
            do {
                try {
                    opcao = input.nextInt();

                    if (opcao < 1 || opcao > 3) { // se a excecao for lancada este if n é executado
                        System.out.print("Opcao invalida. Por favor escolha entre 1 e 3\n> ");
                    }
                    if (opcao == 3) {
                        desconectarDoServidor();
                        exit(0);
                    }


                } catch (InputMismatchException e) {
                    System.out.print("Opcao invalida. Por favor escolha entre 1 e 3\n> ");
                    input.nextLine();
                }
            } while (opcao < 1 || opcao > 3);

            if (opcao == 1) {

                System.out.print("Email: ");
                String email = input.next();
                System.out.print("Password: ");
                String pass = input.next();

                //Envia comando login ao servidor - Codigo 1
                command = "1 " + email + " " + pass;
                res = enviaComando(command);
                System.out.println(res);

                if (res.contains("desconectar"))
                    exit(1);

                input.nextLine(); //flush
            } else {
                System.out.print("Email: ");
                String email;
                do {
                    email = input.next();
                    if (!emailPattern.matcher(email).matches()) {
                        System.out.print("Email Inválido. Email deve ser do formato: <a-z1-9>@<abc>.<abc>\n>");
                    }
                } while (!emailPattern.matcher(email).matches());

                System.out.print("Nome: ");
                String nome = input.next();
                System.out.print("Numero de Telefone: ");
                String telefone;
                do {
                    telefone = input.next();

                    if (!telemovelPattern.matcher(telefone).matches()) {
                        System.out.print("Telefone Inválido. Deve ser do formato: xxxxxxxxx\n>");
                    }
                } while (!telemovelPattern.matcher(telefone).matches());

                System.out.print("Password: ");
                String password = input.next();

                //enviar ao server como register - Codigo 2
                command = "2 " + email + " " + nome + " " + telefone + " " + password;
                res = enviaComando(command);
                System.out.println(res);
            }

            boolean continuar = true;  // Variável de controle para encerrar o loop

            do {
                Thread.sleep(1500);
                System.out.println("\n\nEscolha uma opção:");

                System.out.println("1. Editar dados de registo");
                System.out.println("2. Criar novo grupo");
                System.out.println("3. Selecionar grupo atual");
                System.out.println("4. Criar convinte para adesao a um grupo");
                System.out.println("5. Ver convites recebidos/pendentes");
                System.out.println("6. Gerir (aceitar/recusar) convites");
                System.out.println("7. Listar grupos");
                System.out.println("8. Editar nome do grupo");
                System.out.println("9. Eliminar grupo");
                System.out.println("10. Sair do grupo");
                System.out.println("11. Inserir despesa");
                System.out.println("12. Ver valor total de gastos");
                System.out.println("13. Ver historico de despesas");
                System.out.println("14. Exportar lista de despesas");
                System.out.println("15. Editar campo de uma despesa");
                System.out.println("16. Eliminar despesa");
                System.out.println("17. Inserir pagamento efetuado (de/para)");
                System.out.println("18. Listar pagamentos efetuados");
                System.out.println("19. Eliminar pagamento efetuado");
                System.out.println("20. Ver saldos do grupo");
                System.out.print("21. Logout\n> ");

                int option = 0;
                do {
                    try {
                        option = input.nextInt();

                        if (option < 1 || option > 21) {
                            System.out.print("Opcao invalida. Por favor escolha novamente\n> ");
                        }
                    } catch (InputMismatchException e) {
                        System.out.print("Opcao invalida. Por favor escolha novamente\n> ");
                        input.nextLine();
                    }
                } while (option < 1 || option > 21);


                switch (option) {
                    case 1 -> {
                        System.out.println("\nEscolha o campo a editar:");
                        System.out.println("1. Editar nome");
                        System.out.println("2. Editar numero de telefone");
                        System.out.println("3. Editar email");
                        System.out.println("4. Editar password");
                        System.out.print("5. Cancelar\n> ");

                        int escolha = 0;
                        do {
                            try {
                                escolha = input.nextInt();

                                if (escolha < 1 || escolha > 5) {
                                    System.out.print("Escolha invalida. Por favor escolha novamente\n> ");
                                }
                            } catch (InputMismatchException e) {
                                System.out.print("Escolha invalida. Por favor escolha novamente\n> ");
                                input.nextLine();
                            }
                        } while (escolha < 1 || escolha > 5);

                        if (escolha == 5) break;

                        System.out.print("\nNovo dado: ");
                        String novoCampo;

                        if (escolha == 2) {
                            do {
                                novoCampo = input.next();
                                if (!telemovelPattern.matcher(novoCampo).matches()) {
                                    System.out.print("Telefone Inválido. Deve ser do formato: xxxxxxxxx\n>");
                                }
                            } while (!telemovelPattern.matcher(novoCampo).matches());
                        } else if (escolha == 3) {
                            do {
                                novoCampo = input.next();
                                if (!emailPattern.matcher(novoCampo).matches()) {
                                    System.out.print("Email Inválido. Email deve ser do formato: <a-z1-9>@<abc>.<abc>\n>");
                                }
                            } while (!emailPattern.matcher(novoCampo).matches());
                        } else
                            novoCampo = input.next();

                        System.out.print("\nConfirme a sua password:  ");
                        String password = input.next();

                        //enviar ao server como mundanca de campo - Codigo 3
                        command = "3 " + escolha + " " + novoCampo + " " + password;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 2 -> {
                        input.nextLine();
                        System.out.print("\nNome do novo grupo: ");
                        String novo = input.nextLine();

                        //enviar ao server como mundanca de nome de grupo - Codigo 4
                        command = "4 " + novo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 3 -> {
                        input.nextLine();
                        System.out.print("\nNome do grupo: ");
                        String novo = input.nextLine();

                        //enviar ao server como selecao de grupo - Codigo 5
                        command = "5 " + novo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 4 -> {
                        System.out.print("\nEmail do destinatário: ");
                        String email = input.next();
                        System.out.print("\nGrupo a convidar: ");
                        input.nextLine();
                        String grupo = input.nextLine();
                        //enviar ao server como criacao de novo convite - Codigo 6
                        command = "6 " + email + " " + grupo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 5 -> {
                        //enviar ao server como mostrar convites - Codigo 7
                        command = "7 ";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 6 -> {
                        System.out.print("\nId do convite: "); //convites teem id? ou é so o nome do grupo. é possivel receber dois convites do mesmo grupo?
                        int id = -1;
                        do {
                            try {
                                id = input.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.print("Id invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (id < 0);

                        System.out.println("Escolha o que fazer com o convite: ");

                        System.out.println("\n1. Aceitar");
                        System.out.println("2. Recusar");
                        System.out.print("3. Cancelar\n> ");

                        int escolha = 0;
                        do {
                            try {
                                escolha = input.nextInt();

                                if (escolha < 1 || escolha > 3) { // se a excecao for lancada este if n é executado
                                    System.out.print("Escolha invalida. Por favor escolha novamente\n> ");
                                }
                            } catch (InputMismatchException e) {
                                System.out.print("Escolha invalida. Por favor escolha novamente\n> ");
                                input.nextLine();
                            }
                        } while (escolha < 1 || escolha > 3);

                        if (escolha == 3) break;

                        //enviar ao server como criacao de novo convite - Codigo 8
                        command = "8 " + id + " " + escolha;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 7 -> {
                        //enviar ao server como listar grupos - Codigo 9
                        command = "9";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 8 -> {
                        input.nextLine();
                        System.out.print("\nNovo nome do grupo: ");
                        String novo = input.nextLine();

                        //enviar ao server como edicao do nome do grupo - Codigo 10
                        command = "10 " + novo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 9 -> {
                        input.nextLine();
                        System.out.print("\nNome do grupo a eliminar: ");
                        String novo = input.nextLine();

                        //enviar ao server como eliminar grupo - Codigo 9
                        command = "11 " + novo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 10 -> {
                        input.nextLine();
                        System.out.print("\nNome do grupo: ");
                        String novo = input.nextLine();

                        //enviar ao server como sair de grupo - Codigo 9
                        command = "12 " + novo;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 11 -> {

                        System.out.print("Valor: ");
                        float valor = -1;
                        do {
                            try {
                                valor = input.nextFloat();
                            } catch (InputMismatchException e) {
                                System.out.print("Valor invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (valor < 0);

                        System.out.print("Data (dd/mm/aa): ");
                        String data;
                        do {
                            data = input.next();
                            if (!dataPattern.matcher(data).matches()) {
                                System.out.print("Data Inválida. Deve ser do formato: xx/xx/xx\n>");
                            }
                        } while (!dataPattern.matcher(data).matches());


                        System.out.print("Quem pagou: ");
                        String quem = input.next();
                        input.nextLine();//flush
                        System.out.print("Elementos a partilhar (<email> <email> ...): ");
                        String comQuem = input.nextLine();
                        System.out.print("Descricao: ");
                        String descricao = input.nextLine();

                        //enviar ao server como nova despesa - Codigo 15
                        command = "13 " + valor + " " + data + " " + quem + " ;" + comQuem + ";" + descricao;

                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 12 -> {
                        //enviar ao server como ver valor das despesas- Codigo 14
                        command = "14";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 13 -> {
                        //enviar ao server como ver historico das despesas- Codigo 15
                        command = "15";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 14 -> {
                        //enviar ao server como exportar historico das despesas- Codigo 16
                        System.out.print("Nome ficheiro: ");
                        String nome = input.next();
                        command = "16 " + nome;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 15 -> {
                        System.out.print("Id da despesa: ");
                        int id = -1;
                        do {
                            try {
                                id = input.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.print("Id invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (id < 0);

                        System.out.println("\nParametro a editar: ");

                        System.out.println("1. Data (dd/mm/aa) ");
                        System.out.println("2. Descricao ");
                        System.out.println("3. Valor ");
                        System.out.println("4. Quem pagou ");
                        System.out.println("5. Adicionar elemento partilhado (<email> <email> ...)");
                        System.out.println("6. Eliminar elemento partilhado (<email> <email> ...)");
                        System.out.print("7. Cancelar\n> ");

                        int campo = 0;
                        do {
                            try {
                                campo = input.nextInt();

                                if (campo < 1 || campo > 7) { // se a excecao for lancada este if n é executado
                                    System.out.print("Opcao invalida. Por favor escolha novamente\n> ");
                                }
                            } catch (InputMismatchException e) {
                                System.out.print("Opcao invalida. Por favor escolha de 1 a 7\n> ");
                                input.nextLine();
                            }
                        } while (campo < 1 || campo > 7);

                        if (campo == 7) break;

                        input.nextLine();
                        System.out.print("Novo valor: ");
                        String valor;

                        if (campo == 1) {
                            do {
                                valor = input.next();
                                if (!dataPattern.matcher(valor).matches()) {
                                    System.out.print("Data Inválida. Deve ser do formato: xx/xx/xx\n>");
                                }
                            } while (!dataPattern.matcher(valor).matches());
                        } else
                            valor = input.nextLine();

                        //enviar ao server como edicao de  despesa - Codigo 17
                        command = "17 " + id + " " + campo + " ;" + valor;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 16 -> {
                        System.out.print("Id da despesa: ");
                        int id = -1;
                        do {
                            try {
                                id = input.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.print("Id invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (id < 0);

                        //enviar ao server como eliminar despesa - Codigo 18
                        command = "18 " + id;
                        res = enviaComando(command);
                        System.out.println(res);

                    }
                    case 17 -> {
                        System.out.print("Quem pagou: ");
                        String quemP = input.next();
                        System.out.print("Quem recebeu: ");
                        String quemR = input.next();
                        System.out.print("Data (dd/mm/aa): ");
                        String data;

                        do {
                            data = input.next();

                            if (!dataPattern.matcher(data).matches()) {
                                System.out.print("Data Inválida. Deve ser do formato: xx/xx/xx\n>");
                            }
                        } while (!dataPattern.matcher(data).matches());


                        System.out.print("Valor: ");
                        float valor = -1;
                        do {
                            try {
                                valor = input.nextFloat();
                            } catch (InputMismatchException e) {
                                System.out.print("Valor invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (valor < 0);

                        //enviar ao server como novo pagamento - Codigo 19
                        command = "19 " + quemP + " " + quemR + " " + data + " " + valor;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 18 -> {
                        //enviar ao server como novo pagamento - Codigo 19
                        command = "20";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 19 -> {
                        System.out.print("Id do pagamento: ");
                        int id = -1;
                        do {
                            try {
                                id = input.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.print("Id invalido. Tente novamente\n> ");
                                input.nextLine();
                            }
                        } while (id < 0);

                        command = "21 " + id;
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 20 -> {
                        command = "22";
                        res = enviaComando(command);
                        System.out.println(res);
                    }
                    case 21 -> {

                        continuar = false;
                        System.out.println("\nAté a proxima!");

                    }
                }

            } while (continuar);

            //logout
            System.out.println(desconectarDoServidor());

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            System.out.println("Destino desconhecido:\n\t" + e);
        } catch (NumberFormatException e) {
            System.out.println("O porto do servidor deve ser um inteiro positivo.");
        } catch (SocketTimeoutException e) {
            System.out.println("Nao foi recebida qualquer resposta:\n\t" + e);
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro ao nivel do socket TCP:\n\t" + e);
        } catch (IOException e) {
            System.out.println("Ocorreu um erro no acesso ao socket:\n\t" + e);
        } catch (ClassNotFoundException e) {
            System.out.println("O objecto recebido não é do tipo esperado:\n\t" + e);
        }

    }
}
