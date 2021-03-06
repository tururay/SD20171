package Protocol;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import Application.Controller;
import Application.DataManager.Strategy;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Server implements IReaderWriter {
	private static Controller controller;

	public Server() {
		controller = new Controller();
	}

  @Override
	public String read(String path) throws InterruptedException {
		return controller.read(path);
	}

	@Override
	public void write(String path, int toInsert) throws InterruptedException {
		controller.write(path, toInsert);
	}

	private static void setController(Controller ctrl) {
		controller = ctrl;
	}

	public static void main(String args[]) {
		System.setProperty("java.security.policy","test.policy");
		System.setProperty("java.rmi.server.hostname", "localhost");

		if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
		}

		try {
			Server readerWriterServer = new Server();
			IReaderWriter stub = (IReaderWriter) UnicastRemoteObject.exportObject(readerWriterServer, 0);
			Registry registry = LocateRegistry.createRegistry(1099);

			registry.bind("readerWriter", stub);

			if(args.length > 0 && args[0].equals("-R")) {
        setController(new Controller(Strategy.FAVORING_READERS));
			} else if(args.length > 0 && args[0].equals("-W")) {
        setController(new Controller(Strategy.FAVORING_WRITERS));
			} else {
        setController(new Controller(Strategy.SELFISH));
			}

			System.out.println("Reader/Writer Server is ready!");
		} catch (AlreadyBoundException | RemoteException e) {
			System.err.println("Server Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
