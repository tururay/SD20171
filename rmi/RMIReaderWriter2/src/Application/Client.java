package Application;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable{

    private IReaderWriter stub;
    private Job job;
    private int value;
    private String path;

    public static enum Job{
        READ,
        WRITE
    }
    public Client(Job job, String path) {
        this(job, path, 0);
    }
    public Client(Job job, String path, int value) {
        try {
            Registry registry = LocateRegistry.getRegistry();
            stub = (IReaderWriter) registry.lookup("readerWriter");
            this.path = path;
            this.job = job;
            this.value = value;
        } catch (Exception e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void write(String path, int value) throws InterruptedException  {
        try {
            stub.write(path, value);
        } catch (RemoteException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void read(String path) throws InterruptedException  {
        String info = null;
        try {
            info = stub.read(path);
        } catch (RemoteException | InterruptedException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(info);
    }

    @Override
    public void run() {
      try{
        switch(job){
            case READ:
                read(path);
                break;
            case WRITE:
                write(path, value);
                break;
        }
      }catch(Exception e) {
        e.printStackTrace();
      }

    }
}