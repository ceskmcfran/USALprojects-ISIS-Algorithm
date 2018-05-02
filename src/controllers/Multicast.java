package controllers;

import java.util.concurrent.ThreadLocalRandom;
import utils.Message;

public class Multicast extends Thread {

	private Message message; //Mensaje a enviar
	private int idSenderProcess; //ID del proceso que envia
	private String idServer; //se guardará la id del servidor al que se va a enviar
	
	public Multicast(Message message, int id) {
		this.message = message;
		this.idSenderProcess = id;
	}
	
	@Override
	public void run() {
		for(int idProcess=1; idProcess<=6; idProcess++) {
			//Diferencio entre los procesos que voy a enviar para enviar a su servidor
			switch(idProcess) {
				case 1:
					idServer = "1";
				case 2:
					idServer = "1";
					break;
					
				case 3:
					idServer = "2";
				case 4:
					idServer = "2";
					break;
					
				case 5:
					idServer = "3";
				case 6:
					idServer = "3";
					break;
					
				default:
					System.err.println("Error: Choosing server to send multicast message.");
			}
			//TODO Envia al Servidor que sea
			//Uri + client rellenar parametros con las querys correspondientes para el mensaje
			
			randomDelay();
		}
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