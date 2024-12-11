package pt.meta_II.tppd.servers.RMI;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
public class RMIApplication {

    public static void main(String[] args) {

        try{
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            String url = "rmi://localhost/rmiservice";

            RMIService service = new RMIService();

            Naming.bind(url, service);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
