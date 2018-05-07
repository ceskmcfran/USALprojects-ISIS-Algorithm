package controllers;

import javax.inject.Singleton;
import javax.ws.rs.*;

@Singleton
@Path("server")
public class Server {
	private static int ready = 0;//numero de procesos preparados
	private Process process[]; //Será un vector de 2 elementos, ya que cada servidor tendrá 2 procesos
	private int ip; //ID del servidor (de momento no es una IPv4
	
	/**
	 * Sincronizar comienzo de todos los procesos al iniciar
	 */
	@GET
	@Path("synch")
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
	
	@GET
	@Path("create")
	public void createInstance(@DefaultValue("null") @QueryParam(value = "params") String params) {
		//params es lo que recibe del cliente directamente.
		//Los parametros seran: ?????????
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje multicast
	 */
	@GET
	@Path("multicast")
	public void dispatchMulticastMessage() {
		//TODO recoger mensaje del multicast
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar multicast
		//
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de propuesta
	 */
	@GET
	@Path("propose")
	public void dispatchProposed() {
		//TODO recibir mensaje p2p
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar propuesta
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de acuerdo
	 */
	@GET
	@Path("agree")
	public void dispatchAgreed() {
		//TODO recibir mensaje p2p
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar acuerdo
	}
}
