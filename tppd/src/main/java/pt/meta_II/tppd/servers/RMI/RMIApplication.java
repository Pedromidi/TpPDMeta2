package pt.meta_II.tppd.servers.RMI;

import pt.meta_II.tppd.DbManager;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static java.lang.System.exit;

public class RMIApplication {

    public static void main(String[] args) {

        DbManager manager = new DbManager("../DataBase", "TP_DB.db");
        DbManager db = DbManager.getInstance();
        db.connect();

        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            String url = "rmi://localhost/rmiservice";

            RMIService service = new RMIService(db);

            Naming.bind(url, service);
        } catch (MalformedURLException | AlreadyBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }
}
