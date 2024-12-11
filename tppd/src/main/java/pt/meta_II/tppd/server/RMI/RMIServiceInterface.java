package pt.meta_II.tppd.server.RMI;

import pt.meta_II.tppd.clienteRMI.ObserverInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIServiceInterface extends Remote {
    void addObserver(ObserverInterface observer) throws RemoteException;
    String getUsers() throws RemoteException;
    String getGroups() throws RemoteException;
    void registaUsers() throws RemoteException;
    void autenticaUsers() throws RemoteException;
    void insereEliminaDespesa() throws RemoteException;
}
