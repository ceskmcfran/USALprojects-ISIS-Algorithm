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
	private String messageContent; //Content of the message
	private Semaphore controlOrder;
	private Semaphore controlTail;
	private Semaphore controlMulticast;
	private int isISIS;
	private final String http = "http://";
	private final String api = ":8080/ISIS-Algorithm/"; //TODO OJO CAMBIAR CON LO DE web.xml

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
		//Crear la clase Writer asociada
		if(isISIS == 0){
			Writer writer = new Writer();
			//hacer lo que sea
			//Crear nuevo fichero, para cada ejecucion llamando al metodo de la clase Writer
		}
		else{
			Writer writer = new Writer();
			//hacer lo que sea
			//Crear nuevo fichero, para cada ejecucion llamando al metodo de la clase Writer
		}

		callAPI(ipServer[0], "server/synch");

		for(int i = 0; i<100; i++) {
			//Crear un identificador único de mensaje mediante el numero de mensaje y el identificador de proceso
			String idMessage = "P" + id + " " + i; //<P(id) i>
			messageContent = idMessage;
			acquireSemOrder();
			Message message = new Message(idMessage, messageContent, "PROVISIONAL", order, 0, id);//el 0 es el proporder sera 0 a inicio hasta que lleguen proposiciones del mismo
			releaseSemOrder();

			acquireSemTail();
			tail.addToTail(message);
			releaseSemTail();

			//Multidifundir el mensaje ‘Pxx nnn’ donde xx es el identificador de proceso y nnn el número de mensaje
			Multicast multicast = new Multicast(message, id, controlMulticast, ipServer); //¿¿¿¿Consideramos que solo uno puede hacer multicast a la vez????/
			multicast.start();
			//Esperar a la multidifusion **Semaforo
			acquireSemMulti();
			//Dormir un tiempo aleatorio entre 1.0 y 1.5 seg
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
			// Una vez se entregue se llama al metodo para escribr en fichero simulando la entrega
		}
		else{
			acquireSemOrder();
			order = getLC1(order);//Actualizar variable que es LC1 sumando 1
			releaseSemOrder();

			Message msg = new Message(message.getId(),
					message.getContent(),
					message.getState(),
					order,
					message.getProposedOrder(),
					message.getIdSender());

			//Vemos con id si el mensaje recibido es mio, para saber si metemos o no en la cola el mismo
			int x = Integer.parseInt(msg.getId());
			//Si soy yo meto el mensaje recibido a la cola
			if(x != this.id){
				acquireSemTail();
				tail.addToTail(msg);//meter en cola, tendra proposedOrder a 0 (Al inicio nadie habra mandado ninguna propuesta) y solo se usa en el proceso emisor
				releaseSemTail();
			}
			//Mandamos
			Client client=ClientBuilder.newClient();
			URI uri=UriBuilder.fromUri(http + ipServer[idParent] + api).build();
			WebTarget target = client.target(uri);
			target.path("rest").path("server/propose")
			.queryParam("idProcess", id)
			.queryParam("idSender", msg.getIdSender())
			.queryParam("idMessage", msg.getId())
			.queryParam("bodyMessage", msg.getContent())
			.queryParam("orderMessage", msg.getOrder())
			.queryParam("propOrderMessage", msg.getProposedOrder())
			.queryParam("stateMessage", msg.getState())
			.request(MediaType.TEXT_PLAIN).get(String.class);
		}
	}

	/**
	 * Mensaje de recepcion
	 */
	public void receiveProposed(Message message) {
		int proposedorder;
		//Comparo orden recibido con el orden de mi mensaje, si este es mahyor actualizo sino no hago nada, quedara el que estaba
		acquireSemTail();
		Message msg = tail.getSpecificMessage(message.getId());//Saco el mensaje que sea de la cola para comparar
		releaseSemTail();
		if (message.getOrder() > msg.getOrder()){
			msg.setOrder(message.getOrder());
		}
		order = getLC2(msg.getOrder(), order); //Calculo mi nuevo lamptime LC2 en funcion del timestamp del que recibo y del mio actual
		proposedorder = msg.getProposedOrder()+1;//Actualizo proposed order pues hay propuesta recibida
		msg.setProposedOrder(proposedorder);
		if(msg.getProposedOrder()==6){// 6 porque es el numero de procesos que tenemos
			msg.setState("DEFINITIVE");//ya habra recibido todas las propuestas
			//Mandamos el acuerdo
			Client client=ClientBuilder.newClient();
			URI uri=UriBuilder.fromUri(http + ipServer[idParent] + api).build();
			WebTarget target = client.target(uri);
			target.path("rest").path("server/agree")
			.queryParam("idProcess", id)
			.queryParam("idSender", msg.getIdSender())
			.queryParam("idMessage", msg.getId())
			.queryParam("bodyMessage", msg.getContent())
			.queryParam("orderMessage", msg.getOrder())
			.queryParam("propOrderMessage", msg.getProposedOrder())
			.queryParam("stateMessage", msg.getState())
			.request(MediaType.TEXT_PLAIN).get(String.class);
		}
	}

	/**
	 * Mensaje de acuerdo
	 */
	public void receiveAgreed(Message message) { //TODO revisar si es syncronized
		acquireSemTail();
		Message msg = tail.getSpecificMessage(message.getId());//Saco el mensaje que sea de la cola para comparar
		releaseSemTail();
		msg.setOrder(message.getOrder());
		order = getLC2(msg.getOrder(), order);
		msg.setState("DEFINITIVE");
		tail.reorderTail();
		//Sacamos sin eliminar el mensaje que haya primero en la cola
		//Comprobamos si hay mensajes en la cola y extraemos el primero
		if(!tail.tailIsEmpty()){
			msg = tail.getFromTail();
		}

		while(msg.getState()== "DEFINITIVE"){
			//ENTREGA mensaje simulando con escritura en fichero
			if(!tail.tailIsEmpty()){
				tail.extractFromTail();
			}
			if(!tail.tailIsEmpty()){
				msg = tail.getFromTail();
			}
			//************************
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
