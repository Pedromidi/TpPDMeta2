package pt.meta_II.tppd.clienteRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ObserverInterface extends Remote {
    void notification(String change) throws RemoteException;
}
