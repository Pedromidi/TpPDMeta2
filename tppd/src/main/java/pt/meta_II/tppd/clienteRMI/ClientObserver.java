package pt.meta_II.tppd.clienteRMI;

import pt.meta_II.tppd.server.RMI.RMIServiceInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientObserver extends UnicastRemoteObject implements  ObserverInterface{
    static String url;

    protected ClientObserver(String arg) throws RemoteException {
        url = arg;
    }

    @Override
    public void notification(String change) throws RemoteException {
        System.out.println("Observação: " + change);
    }

    public void setup() {
        try{
            RMIServiceInterface service = (RMIServiceInterface) Naming.lookup(url);

            ClientObserver obs =  new ClientObserver(url);

            service.addObserver(obs);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}
