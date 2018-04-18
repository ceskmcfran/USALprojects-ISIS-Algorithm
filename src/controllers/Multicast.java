package controllers;

import java.util.concurrent.ThreadLocalRandom;

import utils.Message;

public class Multicast extends Thread {

	private Message message;
	private String idProcess;
	
	public Multicast(Message message, String id) {
		this.message = message;
		this.idProcess = id;
	}
	
	@Override
	public void run() {
		//Envio multicast
		switch(idProcess) {
			case "1":
			case "2":
				//Pertenece a server 1
				break;
				
			case "3":
			case "4":
				//Pertenece a server 2
				break;
				
			case "5":
			case "6":
				//Pertenece a server 3
				break;
				
			default:
		}
		
		randomDelay();
	}
	
	/**
	 * Hace un delay de un tiempo aleatorio entre 0.2 y 0.5
	 * Usado para hacer delay entre los envios multicast de cada proceso
	 */
	public void randomDelay() {
		double rand = ThreadLocalRandom.current().nextDouble(0.2, 0.5);
		try {
			Thread.sleep((long)rand*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}