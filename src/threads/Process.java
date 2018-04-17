package threads;

import java.util.concurrent.ThreadLocalRandom;

public class Process extends Thread{
	
	private String id;
	///////private Tail tail;
	private int order = 0;
	private double timestamp;
	
	public Process() {
		///////tail = new Tail();
	}
	
	@Override
	public void run() {
		//Repetir 100 veces
		for(int i = 1; i<=100; i++) {
			//Crear un identificador único de mensaje mediante el numero de mensaje y el identificador de proceso
			String idMessage = "P" + id + " " + i; //<P(id) i>
			////////Message message = new Message(idMessage, content, state, order, proposedOrder);
			
			//Multidifundir el mensaje ‘Pxx nnn’ donde xx es el identificador de proceso y nnn el número de mensaje
			
			
			//Dormir un tiempo aleatorio entre 1.0 y 1.5 seg
			double rand = ThreadLocalRandom.current().nextDouble(1.0, 1.5);
			try {
				Thread.sleep((long)rand*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*public void receiveMessage(Message message, String idMessage) {
		
	}
	
	public void receiveProposed(Message message) {
		
	}
	
	public void receiveAgreed(Message message) {
		
	}*/
}
