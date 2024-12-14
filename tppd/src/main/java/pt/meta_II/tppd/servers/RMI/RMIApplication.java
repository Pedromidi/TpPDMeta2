package pt.meta_II.tppd.servers.RMI;

import pt.meta_II.tppd.DbManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.System.exit;

public class RMIApplication {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(6005);

        DbManager manager = new DbManager("../DataBase", "TP_DB.db");
        DbManager db = DbManager.getInstance();
        db.connect();

        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            String url = "rmi://localhost/rmiservice";

            RMIService service = new RMIService(db);

            Naming.bind(url, service);

            System.out.println("Criei servico");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // Criar e iniciar a thread que vai processar/atender cada cliente
                Runnable clientThread = new AtendeCliente(clientSocket, db, service);
                Thread t = new Thread(clientThread);
                t.start();
            }
        } catch (MalformedURLException | AlreadyBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
