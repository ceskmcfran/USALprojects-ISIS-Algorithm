package controllers;

import java.util.concurrent.ThreadLocalRandom;
import utils.Message;
import utils.Tail;

public class Process extends Thread{
	
	private String id; //Process' id
	private Tail tail; //Tail of the process
	private int order = 0; //Order set in the algorithm WHY?
	private double timestamp; //Timestamp to lampard algorithm
	private String messageContent = "Soy " + id + " mi mensaje es "; //Content of the message
	
	public Process(String id) {
		this.id = id;
		///////tail = new Tail();
	}
	
	@Override
	public void run() {
		//Repetir 100 veces
		for(int i = 1; i<=100; i++) {
			//Crear un identificador único de mensaje mediante el numero de mensaje y el identificador de proceso
			String idMessage = "P" + id + " " + i; //<P(id) i>
			messageContent = messageContent + idMessage;
			Message message = new Message(idMessage, messageContent, "PROVISIONAL", order, proposedOrder);
			
			//Multidifundir el mensaje ‘Pxx nnn’ donde xx es el identificador de proceso y nnn el número de mensaje
			Multicast multicast = new Multicast(message, id); //¿¿¿¿Consideramos que solo uno puede hacer multicast a la vez????/
			multicast.start();
			
			//Dormir un tiempo aleatorio entre 1.0 y 1.5 seg
			randomSleep();
		}
	}
	
	/**
	 * Duerme el proceso durante 1.0 y 1.5 segundos
	 */
	public void randomSleep() {
		double rand = ThreadLocalRandom.current().nextDouble(1.0, 1.5);
		try {
			Thread.sleep((long)rand*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void receiveMulticastMessage(Message message, String idMessage) {
		
	}
	
	/**
	 * 
	 */
	public void receiveProposed(Message message) {
		
	}
	
	/**
	 * 
	 */
	public void receiveAgreed(Message message) {
		
	}
}
