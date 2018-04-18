package controllers;

public class Server {
	//Levanto las 3 instancias de los servidores dependiendo de los parametros que me pase el "main-cliente-script"
	
	private static int ready = 0;
	private Process process[]; //Será un vector de 2 elementos, ya que cada servidor tendrá 2 procesos
	
	public Server(){
		
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
}
