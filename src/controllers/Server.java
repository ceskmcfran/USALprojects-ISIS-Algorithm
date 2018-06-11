package controllers;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("server")
public class Server {
	private static int ready = 0;//numero de procesos preparados
	private Process process[] = new Process[2]; //Será un vector de 2 elementos, ya que cada servidor tendrá 2 procesos
	private int whoami; //ID del servidor (de momento no es una IPv4

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

	/**
	 * Crea la instancia de cada hilo en cada servidor.
	 * @param params
	 * params = whoami;ip1;ip2;ip3;isISIS
	 */
	/* TODO Ojo el cliente ejecuta el script con ./.... ip1 ip2 ip3 ISIS
	 * Tras esto en el cliente deberemos crear la Query del cliente al servidor en la que mendiante un for(3)
	 * mandará a cada servidor una query del tipo: indiceFor;ip1;ip2;ip3;isISIS y la mandará a args[x] (cada una de las ip)
	 * Con ello el server1 siempre será ip1, server2 será ip2 y server3 será ip3*/
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("create")
	public void createInstance(
			@DefaultValue("null")
			@QueryParam(value = "params") String params,
			@QueryParam(value = "numParams") int numParams){

		if(params == null) {
			//TODO retornar al cliente que no se han metido los parametros necesarios (aunque tambien controlarlo en el cliente) así protegemos en 2 ambitos contra inyecciones
		}

		String eachParam[] = splitParams(params, numParams); //sacar fuera eachParamy poner static ???????????
		whoami = Integer.parseInt(eachParam[0]);

		//Diferencio que numero soy yo
		if(whoami == 0){
			process[0] = new Process(0);
			process[1] = new Process(1);
			process[0].start();
			process[1].start();
		}
		else if (whoami == 1){
			process[0] = new Process(2);
			process[1] = new Process(3);
			process[0].start();
			process[1].start();
		}
		else if (whoami == 2){
			process[0] = new Process(4);
			process[1] = new Process(5);
			process[0].start();
			process[1].start();
		}

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

	/**
	 * Hace el split a los parametros que le llegan del cliente
	 * @param params
	 * @return eachParam[]
	 */
	private String[] splitParams(String params, int numParams){
		String[] eachParam = null;

		for(int i=0; i<numParams; i++){
			eachParam = params.split(";");
		}

		return eachParam;
	}
}
