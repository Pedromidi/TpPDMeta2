package pt.meta_II.tppd.clienteRMI;

import pt.meta_II.tppd.servers.RMI.RMIServiceInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class ClienteRMI  {
    public static void main(String[] args) throws RemoteException {
        boolean end = false;

        if(args.length != 1){
            System.out.println("Por favor passe apenas o ip do serviço remoto na linha de comando");
            return;
        }

        ClientObserver observer = new ClientObserver(args[0]);
        observer.setup();

        try {
            //System.out.println(args[0]);

            RMIServiceInterface service = (RMIServiceInterface) Naming.lookup(args[0]);

            Scanner sc = new Scanner(System.in);
            while(!end){
                System.out.println("""
                        Escolha uma opção:
                        1 - Listar grupos
                        2 - Listar utilizadores
                        Opção:\s""");

                if (sc.hasNextInt()) {
                    int op = sc.nextInt();
                    switch (op) {
                        case 1 -> System.out.println(service.getGroups());
                        case 2 -> System.out.println(service.getUsers());
                        default -> System.out.println("Por favor escolha uma opção válida");
                    }
                } else {
                    System.out.println("Por favor escolha uma opção válida");
                    sc.next();
                }
            }

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
