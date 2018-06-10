package controllers;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import utils.Message;
import utils.Tail;
import utils.Writer;

public class Process extends Thread{
	
	private int id; //Process' id
	private Tail tail; //Tail of the process
	private int order = 0; //Order set in the algorithm WHY? 
	private String messageContent = "Soy " + id + " mi mensaje es "; //Content of the message
	private Semaphore controlOrder;
	private Semaphore controlTail;
	private Semaphore controlMulticast;
	
	public Process(int id) {
		this.id=id;
		tail = new Tail();
		//Inicializar semaforos
		
		//TODO sacar inicialización de semaforos a un metodo
		controlOrder = new Semaphore(1);
		controlTail = new Semaphore(1);
		controlMulticast = new Semaphore(0);
	}
	
	@Override
	public void run() {
		//Obtener parametros del proceso para operar de forma correcta
		//Crear la clase Writer asociada
		Writer writer = new Writer();
		//Crear nuevo fichero, para cada ejecucion llamando al metodo de la clase Writer
		
		//Asginar ips asociadas 
		
		//Mediante Rest avisar de que el proceso esta listo para el comienzo
		
		
		//Repetir 100 veces
		for(int i = 1; i<=100; i++) {
			//Crear un identificador único de mensaje mediante el numero de mensaje y el identificador de proceso
			String idMessage = "P" + id + " " + i; //<P(id) i>
			messageContent = messageContent + idMessage;
			acquireSemOrder();
			Message message = new Message(idMessage, messageContent, "PROVISIONAL", order, 0);//el 0 es el proporder sera 0 a inicio hasta que lleguen proposiciones del mismo
			releaseSemOrder();
			
			//Multidifundir el mensaje ‘Pxx nnn’ donde xx es el identificador de proceso y nnn el número de mensaje
			Multicast multicast = new Multicast(message, id, controlMulticast); //¿¿¿¿Consideramos que solo uno puede hacer multicast a la vez????/
			multicast.start();
			//Esperar a la multidifusion **Semaforo
			acquireSemMulti();
			//Dormir un tiempo aleatorio entre 1.0 y 1.5 seg
			randomSleep();
		}
	}
	
	/**
	 * Duerme el proceso durante 1.0 y 1.5 segundos
	 */
	public void randomSleep() {
		double rand = ThreadLocalRandom.current().nextDouble(1.0, 1.5);
		try {
			Thread.sleep((long)rand*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public void receiveMulticastMessage(Message message, String idMessage) {
		Message msg = new Message(message.getId(),message.getContent(),message.getState(),message.getOrder(),message.getProposedOrder());//serael mensaje recibido
		acquireSemOrder();
		order = getLC1(order);//Actualizar variable que es LC1 sumando 1
		releaseSemOrder();
		
		//Vemos con id si el mensaje recibido es mio, para saber si metemos o no en la cola el mismo
		int x = Integer.parseInt(msg.getId());
		//Si soy yo meto el mensaje recibido a la cola
		if(x != this.id){
		acquireSemTail();
		tail.addToTail(msg);//meter en cola, tendra proposedOrder a 0 (Al inicio nadie habra mandado ninguna propuesta) y solo se usa en el proceso emisor
		releaseSemTail();
		}
		//Envio de propuesta de orden (orden, idMessage) a pj
		//Aqui se realiza todo lo del uri y client, y se rellenan los parametros con el mensaje que ira al servidor como propuesta
		//Ademas hay que diferenciar a que ip de servidor envias
		// Una vez se entregue se llama al metodo para escribr en fichero simulando la entrega
	}
	
	/*
	 Mensaje de recepci�n
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
			//multidifundir ACUERDO(k, mensaje.orden)
			//De 0 a 6 se  realiza client y uri construyendo la ip que corresponde
		}				
	}
	
	/**
	 * 
	 */
	public void receiveAgreed(Message message) {
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
	
	/* M�todos para la gesti�n de los semaforos */
	private void acquireSemOrder (){
		try {
			controlOrder.acquire(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void releaseSemMulti(){
		controlMulticast.release();
	}
	
	
}
