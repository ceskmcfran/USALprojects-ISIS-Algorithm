package controllers;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
	
	//TODO aceptar en el cliente las request con texto plano
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("create")
	public void createInstance(@DefaultValue("null") @QueryParam(value = "params") String params) {
		if(params == null) {
			//TODO retornar al cliente que no se han metido los parametros necesarios (aunque tambien controlarlo en el cliente) así protegemos en 2 ambitos contra inyecciones
		}
		//params es lo que recibe del cliente directamente.
		//Los parametros seran: ?????????
		//TODO separar dependiendo de los parametros y crear los hilos (procesos)
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje multicast
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("multicast")
	public void dispatchMulticastMessage() {
		//TODO REST: recoger mensaje
		//TODO elegir el servidor dependiendo del proceso al que se envie y llamar a receiveMulticastMessage
		//
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de propuesta
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("propose")
	public void dispatchProposed() {
		//TODO REST: recoger mensaje
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar propuesta
	}
	
	/**
	 * Selecciona el proceso al que le llegará el mensaje de acuerdo
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("agree")
	public void dispatchAgreed() {
		//TODO REST: recoger mensaje
		//TODO elegir el servidor dependiendo del proceso al que se envie y enviar acuerdo
	}
}
