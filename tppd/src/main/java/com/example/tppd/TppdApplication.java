package com.example.tppd;

import com.example.tppd.server.RMI.RMIService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

@SpringBootApplication
public class TppdApplication {

    public static void main(String[] args) {
        SpringApplication.run(TppdApplication.class, args);

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
