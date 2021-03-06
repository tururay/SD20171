package Simulator;

import Protocol.Client;
import Protocol.Client.Job;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClientSimulator {

	public static void main(String[] args) {
//		Random rand = new Random();
		int numberOfReaders = 0, numberOfWriters = 1;
		String[] files = new String[] { "arq1", "arq2", "arq3" };

		simulate(files, numberOfReaders, numberOfWriters, false, 5);
	}

	private static void simulate(String[] files, int numberOfReaders, int numberOfWriters, boolean doBenchmark,
			int numberOfExecutions) {
		Client client;
		Random rand = new Random();
		String file;
		long start=0, end;
    boolean verbose = true;
    int porcentagem = 0;

		int major = (numberOfReaders > numberOfWriters) ? numberOfReaders : numberOfWriters;

		if (doBenchmark) {
      verbose = true;
      System.out.println("Desligando os prints do Cliente...");
      System.out.println("Iniciando o benchmark...");
			start = System.currentTimeMillis();
		} else {
			numberOfExecutions = 1;
		}

		for (int c = 0; c < numberOfExecutions; c++) {
      int newPorcentagem = (int) ((float) c/numberOfExecutions * 100);

      if(newPorcentagem > porcentagem) {
        porcentagem = newPorcentagem;
        System.out.println(String.format("%02d", porcentagem)+"%");
      }

      List<Thread> threads = new ArrayList<>();

			for (int i = 0; i < major; i++) {
				System.out.println("Iniciando Thread " + i);
				// file = files[rand.nextInt(3)];
				// client = new Client(Job.READ, file, verbose);
				// threads.add(new Thread(client));

				file = files[rand.nextInt(3)];
				client = new Client(Job.WRITE, file, rand.nextInt(100));
				threads.add(new Thread(client));
				System.out.println("Thread " + i + " Iniciada");
			}

			for (Thread thread : threads) {
				thread.start();
			}
			System.out.println("Threads iniciadas");


			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
		}

		if (doBenchmark) {
			end = System.currentTimeMillis();
      System.out.println("Finalizando o benchmark...");
			long elapsed = end - start;

      long seconds = milliToSeconds(elapsed);
      long minutes = milliToMinutes(elapsed);
      long hours   = milliToHours(elapsed);
      String formatted = String.format("%03d:%02d:%02d", hours, minutes, seconds);

      long seconds_per_execution = (long) seconds / numberOfExecutions;

      System.out.println("Inicial\t\tFinal\t\tTotal\t\tTotal\t\tPor Execução");
      System.out.println(start+"\t"+end+"\t"+elapsed+"ms\t"+formatted+"\t"+seconds_per_execution);
		}
	}

  private static long milliToSeconds(long ms) {
    return ( ms / 1000 ) % 60;
  }

  private static long milliToHours(long ms) {
    return ms / 3600000; // 3600000 = 60 * 60 * 1000
  }

  private static long milliToMinutes(long ms) {
    return ( ms / 60000 ) % 60; // 60000   = 60 * 1000
  }
}
