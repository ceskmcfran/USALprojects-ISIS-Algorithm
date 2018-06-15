package controllers;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import utils.Message;

@Singleton
@Path("server")
public class Server {
	private static int ready = 0; //numero de procesos preparados
	private Process process[] = new Process[2]; //Será un vector de 2 elementos, ya que cada servidor tendrá 2 procesos
	private int whoami; //ID del servidor
	private int isISIS;
	private final int maxProcesses = 6;

	@GET
	@Path("hello")
	public void helloWorld(){
		System.out.println("HELLOWORLD\n");
	}
	
	/**
	 * Sincronizar comienzo de todos los procesos al iniciar
	 */
	@GET
	@Path("synch")
	public void synch() {
		System.out.println("He llegao al synch");
		//Esto será llamado por cada process antes del bucle
		synchronized(getClass()) {
			ready++;
		}
		if(ready != maxProcesses) {
			synchronized(getClass()) {
				try {
					getClass().wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			synchronized(getClass()) {
				getClass().notifyAll();
			}
		}

	}

	/**
	 * Crea la instancia de cada hilo en cada servidor.
	 * @param params
	 * params = whoami;ip1;ip2;ip3;isISIS
	 */
	@GET
	//@Produces(MediaType.TEXT_PLAIN)
	@Path("create")
	public void createInstance(
			@DefaultValue("null")
			@QueryParam(value = "params") String params){

		System.out.println("HE LLEGAO");
		String eachParam[] = splitParams(params, 5); //sacar fuera eachParamy poner static ???????????
		whoami = Integer.parseInt(eachParam[0]);
		isISIS = Integer.parseInt(eachParam[4]);

		if(whoami == 0){
			process[0] = new Process(0, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[1] = new Process(1, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[0].start();
			process[1].start();
		}
		else if (whoami == 1){
			process[0] = new Process(2, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[1] = new Process(3, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[0].start();
			process[1].start();
		}
		else if (whoami == 2){
			process[0] = new Process(4, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[1] = new Process(5, whoami, eachParam[1], eachParam[2], eachParam[3], isISIS);
			process[0].start();
			process[1].start();
		}

	}

	/**
	 * Selecciona el proceso al que le llegará el mensaje multicast
	 */
	@GET
	//@Produces(MediaType.TEXT_PLAIN)
	@Path("multicast")
	public void dispatchMulticastMessage(
			@QueryParam(value="idProcess") int idProcess,
			@QueryParam(value="idSender") int idSender,
			@QueryParam(value="idMessage") String idMessage,
			@QueryParam(value="bodyMessage") String bodyMessage,
			@QueryParam(value="orderMessage") int orderMessage,
			@QueryParam(value="propositions") int propositions,
			@QueryParam(value="stateMessage") String stateMessage) {

		Message message = new Message(idMessage, bodyMessage, stateMessage, orderMessage, propositions, idSender);

		System.out.println(idMessage);
		System.out.println(bodyMessage);
		
		if(whoami == 0){
			if(idProcess == 0){
				process[0].receiveMulticastMessage(message);
			}else if(idProcess == 1) {
				process[1].receiveMulticastMessage(message);
			}
		}else if (whoami == 1){
			if(idProcess == 2){
				process[0].receiveMulticastMessage(message);
			}else if(idProcess == 3) {
				process[1].receiveMulticastMessage(message);
			}
		}else if (whoami == 2){
			if(idProcess == 4){
				process[0].receiveMulticastMessage(message);
			}else if(idProcess == 5) {
				process[1].receiveMulticastMessage(message);
			}
		}
	}

	/**
	 * Selecciona el proceso al que le llegará el mensaje de propuesta
	 */
	@GET
	//@Produces(MediaType.TEXT_PLAIN)
	@Path("propose")
	public void dispatchProposed(
			@QueryParam(value="idProcess") int idProcess,
			@QueryParam(value="idSender") int idSender,
			@QueryParam(value="idMessage") String idMessage,
			@QueryParam(value="bodyMessage") String bodyMessage,
			@QueryParam(value="orderMessage") int orderMessage,
			@QueryParam(value="propositions") int propositions,
			@QueryParam(value="stateMessage") String stateMessage) {

		Message message = new Message(idMessage, bodyMessage, stateMessage, orderMessage, propositions, idSender);
		int idProcessToSend;
		if((message.getIdSender()==0)||(message.getIdSender()==2)||(message.getIdSender()==4))
			idProcessToSend = 0;
		else
			idProcessToSend = 1;
		
		process[idProcessToSend].receiveProposed(message);
	}

	/**
	 * Selecciona el proceso al que le llegará el mensaje de acuerdo
	 */
	@GET
	//@Produces(MediaType.TEXT_PLAIN)
	@Path("agree")
	public void dispatchAgreed(
			@QueryParam(value="idProcess") int idProcess,
			@QueryParam(value="idSender") int idSender,
			@QueryParam(value="idMessage") String idMessage,
			@QueryParam(value="bodyMessage") String bodyMessage,
			@QueryParam(value="orderMessage") int orderMessage,
			@QueryParam(value="propositions") int propositions,
			@QueryParam(value="stateMessage") String stateMessage) {

		Message message = new Message(idMessage, bodyMessage, stateMessage, orderMessage, propositions, idSender);

		if(whoami == 0){
			if(idProcess == 0){
				process[0].receiveAgreed(message);
			}else if(idProcess == 1) {
				process[1].receiveAgreed(message);
			}
		}else if (whoami == 1){
			if(idProcess == 2){
				process[0].receiveAgreed(message);
			}else if(idProcess == 3) {
				process[1].receiveAgreed(message);
			}
		}else if (whoami == 2){
			if(idProcess == 4){
				process[0].receiveAgreed(message);
			}else if(idProcess == 5) {
				process[1].receiveAgreed(message);
			}
		}
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
