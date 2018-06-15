package controllers;

import java.net.URI;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import utils.Message;
import utils.Tail;
import utils.Writer;

public class Process extends Thread{
//TODO revisar todos los bucles for para si empiezan en 1 o 0
	private int id; //Process' id
	private int idParent; //Id parent server
	private String ipServer[] = new String[3];
	private Tail tail; //Tail of the process
	private int order = 0;
	private String messageContent = "Contenido del mensaje"; //Content of the message
	private Semaphore controlOrder;
	private Semaphore controlTail;
	private Semaphore controlMulticast;
	private int isISIS;
	private final String http = "http://";
	private final String api = ":8080/ISIS-Algorithm/"; //TODO OJO CAMBIAR CON LO DE web.xml
	private final int maxProcesses = 6;

	public Process(int id, int idParent, String ip1, String ip2, String ip3, int isISIS) {
		this.id = id;
		this.idParent = idParent;
		tail = new Tail();
		this.ipServer[0] = ip1;
		this.ipServer[1] = ip2;
		this.ipServer[2] = ip3;
		initSem();
		this.isISIS = isISIS;
	}

	@Override
	public void run() {
		if(isISIS == 0){
		    Writer writer = new Writer();
			writer.write("/home/i0919093/Escritorio/salida/fichero"+id+".txt", "Inicio de fichero sin isis\n");
		}
		else{
			Writer writer = new Writer();
			writer.write("/home/i0919093/Escritorio/salida/fichero"+id+".txt", "Inicio de fichero con isis\n");
		}

		callAPI(ipServer[0], "server/synch");
System.out.printf("Soy %d, empiezo a emitir\n",this.id);
		for(int i = 0; i<20; i++) {
			String idMessage = "P" + id + " " + i; //<P(id) i>
			acquireSemOrder();
			Message message = new Message(idMessage, messageContent, "PROVISIONAL", order, 0, id);//el 0 es el proporder sera 0 a inicio hasta que lleguen proposiciones del mismo
			releaseSemOrder();

			acquireSemTail();
			tail.addToTail(message);
			releaseSemTail();

			Multicast multicast = new Multicast(message, id, controlMulticast, ipServer); //¿¿¿¿Consideramos que solo uno puede hacer multicast a la vez????/
			multicast.start();
			acquireSemMulti();

			randomSleep();
		}
	}

	/**
	 * Llama a la API sin parametros
	 * @param ip
	 * @param apiPath
	 */
	public void callAPI(String ip, String apiPath) {
		Client client=ClientBuilder.newClient();
		URI uri=UriBuilder.fromUri(http + ip + api).build();
		WebTarget target = client.target(uri);
		target.path("rest").path(apiPath).request(MediaType.TEXT_PLAIN).get(String.class);
	}

	/**
	 * Duerme el proceso durante 1.0 y 1.5 segundos
	 */
	private void randomSleep() {
		double rand = ThreadLocalRandom.current().nextDouble(1.0, 1.5);
		try {
			Thread.sleep((long)rand*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mensaje de multicast
	 */
	public void receiveMulticastMessage(Message message) {
		if(isISIS == 0){
			Writer writer = new Writer();
			writer.write("/home/i0919093/Escritorio/salida/fichero"+id+".txt", "(Msg: " + message.getId() + " )\n");
			//Aqui es sin isis se escriben segun nos llegan
		}
		else{
			acquireSemOrder();
			order = getLC1(order); //Actualizar variable que es LC1 sumando 1
			releaseSemOrder();

			Message msg = new Message(message.getId(),
					message.getContent(),
					message.getState(),
					order,
					message.getPropositions(),
					message.getIdSender());

			System.out.printf("Mcast,soy %d msg id: %s, es de %d\n", this.id, msg.getId(), msg.getIdSender());

			//Si yo no soy el sender: meto el mensaje recibido a la cola meter en cola, tendra propositions a 0 (Al inicio nadie habra mandado ninguna propuesta) y solo se usa en el proceso emisor
			if(msg.getIdSender() != id){
				acquireSemTail();
				tail.addToTail(msg);
				releaseSemTail();
			}

			int sendTo = 0;
			if (msg.getIdSender() == 0 || msg.getIdSender() == 1){
				sendTo = 0;
			}else if(msg.getIdSender() == 2 || msg.getIdSender() == 3){
				sendTo = 1;
			}else if(msg.getIdSender() == 4 || msg.getIdSender() == 5){
				sendTo = 2;
			}else {
				System.err.println("Error: Send propose to process in receiveMulticastMessage");
			}

			Client client=ClientBuilder.newClient();
			URI uri=UriBuilder.fromUri(http + ipServer[sendTo] + api).build();
			WebTarget target = client.target(uri);
			target.path("rest").path("server/propose")
			.queryParam("idProcess", id)
			.queryParam("idSender", msg.getIdSender())
			.queryParam("idMessage", msg.getId())
			.queryParam("bodyMessage", msg.getContent())
			.queryParam("orderMessage", msg.getOrder())
			.queryParam("propOrderMessage", msg.getPropositions())
			.queryParam("stateMessage", msg.getState())
			.request(MediaType.TEXT_PLAIN).get(String.class);
		}
	}

	/**
	 * Mensaje de recepcion
	 */
	public void receiveProposed(Message message) {
		acquireSemOrder();
		this.order=getLC2(this.order, message.getOrder());
		releaseSemOrder();

		//Saco el mensaje que sea de la cola para comparar
		Message msg = tail.getSpecificMessage(message.getId());

		
		if (message.getOrder() >  msg.getOrder()){
			msg.setOrder(message.getOrder());
		}
		msg.setPropositions(msg.getPropositions()+1);
		
		System.out.printf("PROP,soy %d msg id: %s, es de %d con prop %d\n", this.id, msg.getId(), msg.getIdSender(), msg.getPropositions());

		if(msg.getPropositions() == maxProcesses){// 6 porque es el numero de procesos que tenemos
			for(int idProcess=0; idProcess<6; idProcess++) {
				int sendTo = 0;
				if (idProcess == 0 || idProcess == 1){
					sendTo = 0;
				}else if(idProcess == 2 || idProcess == 3){
					sendTo = 1;
				}else if(idProcess == 4 || idProcess == 5){
					sendTo = 2;
				}else {
					System.err.println("Error: Send multicast to process");
				}

//DEBUG (si falla): Revisar esto de los idSender/idProcess y si hay que poner semaforo
				Client client=ClientBuilder.newClient();
				URI uri=UriBuilder.fromUri(http + ipServer[sendTo] + api).build();
				WebTarget target = client.target(uri);
				target.path("rest").path("server/agree")
				.queryParam("idProcess", idProcess)
				.queryParam("idSender", msg.getIdSender())
				.queryParam("idMessage", msg.getId())
				.queryParam("bodyMessage", msg.getContent())
				.queryParam("orderMessage", msg.getOrder())
				.queryParam("propOrderMessage", msg.getPropositions())
				.queryParam("stateMessage", msg.getState())
				.request(MediaType.TEXT_PLAIN).get(String.class);
			}
		}
	}

	/**
	 * Mensaje de acuerdo
	 */
	public synchronized void receiveAgreed(Message message) {
		acquireSemOrder();
		this.order=getLC2(this.order, message.getOrder());
		releaseSemOrder();
		acquireSemTail();
		Message msg = tail.getSpecificMessage(message.getId());
		msg.setOrder(message.getOrder());
		msg.setState("DEFINITIVE");
		releaseSemTail();
		
		System.out.printf("AGmsg, idmsg: %s, proc: %d",msg.getId(), this.id);
//DEBUG (si falla): Esto a lo mejor tiene que ser una seccion critica entera, revisar las secciones criticas
		acquireSemTail();
		tail.reorderTail();
		releaseSemTail();

		acquireSemTail();
		if(!tail.isEmpty()){
			msg = tail.getFromTail();
		}
		else{
			msg.setId(null);
		}
		releaseSemTail();

		if(msg.getId() != null) {
			while(msg.getState() == "DEFINITIVE"){
				Writer writer = new Writer();
				writer.write("/home/i0919093/Escritorio/salida/fichero"+id+".txt", "(Msg: " + msg.getId() + " )\n");

				acquireSemTail();
				if(!tail.isEmpty()){
					tail.removeFromTail();
					if(!tail.isEmpty()){
						msg = tail.getFromTail();
					}
				}
				releaseSemTail();
			}
		}
	}

	/*
	 * Metodos para la gestion de tiempos lógicos de Lamport
	 */
	public int getLC1 (int orden){
		return orden++;

	}

	public int getLC2 (int timestamp, int ordenpropio) {
		int lc2;
		if (timestamp >= ordenpropio){
			lc2 = timestamp+1;

		}else{
			lc2= ordenpropio+1;
		}
		return lc2;
	}

	/*
	 *  Metodos para la gestion de los semaforos
	 */
	private void initSem() {
		controlOrder = new Semaphore(1);
		controlTail = new Semaphore(1);
		controlMulticast = new Semaphore(0);
	}

	private void acquireSemOrder (){
		try {
			controlOrder.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void releaseSemOrder (){
		controlOrder.release();
	}

	private void acquireSemTail (){
		try {
			controlTail.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void releaseSemTail (){
		controlTail.release();
	}

	private void acquireSemMulti (){
		try {
			controlMulticast.acquire(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void releaseSemMulti(){
		controlMulticast.release();
	}


}
