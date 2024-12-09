package com.example.tppd.server.RMI;

import com.example.tppd.clienteRMI.ObserverInterface;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIService extends UnicastRemoteObject implements RMIServiceInterface {
    List<ObserverInterface> observers;
    String change;

    public void notifyObservers(){
        for (ObserverInterface o : this.observers) {
            try {
                o.notification(this.change);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public RMIService() throws RemoteException {
        this.change = "none";
        this.observers = new ArrayList<>();
    }

    @Override
    public void addObserver(ObserverInterface observer) throws RemoteException {
        this.observers.add(observer);
    }

    @Override
    public String getUsers() throws RemoteException{
        return "aqui estao os users";
    }

    @Override
    public String getGroups() throws RemoteException{
        return "aqui estao os groups";
    }

    @Override
    public void registaUsers() throws RemoteException {
        this.change = "Foi registado um novo utilizador";
        notifyObservers();
    }

    @Override
    public void autenticaUsers() throws RemoteException {
        this.change = "Foi registado um utilizador";
        notifyObservers();
    }

    @Override
    public void insereEliminaDespesa() throws RemoteException {
        this.change = "Alteração de despesa";
        notifyObservers();
    }
}
