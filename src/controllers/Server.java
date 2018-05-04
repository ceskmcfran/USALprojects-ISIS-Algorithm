package controllers;

public class Server {
	//Levanto las 3 instancias de los servidores dependiendo de los parametros que me pase el "main-cliente-script"
	
	private static int ready = 0;//numero de procesos preparados
	private Process process[]; //Será un vector de 2 elementos, ya que cada servidor tendrá 2 procesos
	private int ip; //ID del servidor (de momento no es una IPv4
	
	public Server(int ip){
		this.ip = ip;
	}
	
	/**
	 * Sincronizar comienzo de todos los procesos al iniciar
	 */
	public void synch() {
		//Esto será llamado por cada process antes del bucle
		synchronized(getClass()) {
			ready++;
		}
		if(ready != 6) {
			synchronized(getClass()) {
				try {
					getClass().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			//¿¿¿¿¿¿Necesita SC como el wait???????
			getClass().notifyAll();
		}
		
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje multicast
	 */
	public void dispatchMulticastMessage() {
		//TODO recoger mensaje del multicast
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar multicast
		//
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de propuesta
	 */
	public void dispatchProposed() {
		//TODO recibir mensaje p2p
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar propuesta
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de acuerdo
	 */
	public void dispatchAgreed() {
		//TODO recibir mensaje p2p
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar acuerdo
	}
}
