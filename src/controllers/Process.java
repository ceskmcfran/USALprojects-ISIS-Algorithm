package controllers;

import java.util.concurrent.ThreadLocalRandom;
import utils.Message;
import utils.Tail;

public class Process extends Thread{
	
	private String id; //Process' id
	private Tail tail; //Tail of the process
	private int lampTime = 0; //Order set in the algorithm WHY? 
	private double timestamp; //Timestamp to lampard algorithm
	private String messageContent = "Soy " + id + " mi mensaje es "; //Content of the message
	
	public Process() {
		///////tail = new Tail();
	}
	
	@Override
	public void run() {
		//Repetir 100 veces
		for(int i = 1; i<=100; i++) {
			//Crear un identificador único de mensaje mediante el numero de mensaje y el identificador de proceso
			String idMessage = "P" + id + " " + i; //<P(id) i>
			messageContent = messageContent + idMessage;
			Message message = new Message(idMessage, messageContent, "PROVISIONAL", order, proposedOrder);
			
			//Multidifundir el mensaje ‘Pxx nnn’ donde xx es el identificador de proceso y nnn el número de mensaje
			Multicast multicast = new Multicast(message, id); //¿¿¿¿Consideramos que solo uno puede hacer multicast a la vez????/
			multicast.start();
			
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
		lampTime = getLC1(lampTime);//Actualizar variable que es LC1 sumando 1
		tail.addToTail(msg);//meter en cola con proposedOrder a 0 (Al inicio nadie habra mandado ninguna propuesta) y solo se usa en el proceso emisor
		//Envio de propuesta de orden (ordenseralampTime, id de MSG) a pj
	}
	
	/*
	 Mensaje de recepci�n
	 */
	public void receiveProposed(Message message) {
		int proposedorder;
	//	mensaje.orden = max(mensaje.orden, ordenj//Se sacara del mensaje y se comparara
	// Se est� recibiendo un MSG (k, orderj) con k id y orderj orden del que te propone	
		//Comparo orden recibido con el orden de mi mensaje, si este es mahyor actualizo sino no hago nada, quedara el que estaba
	Message msg = tail.getSpecificMessage(message.getId());//Saco el mensaje que sea de la cola para comparar
	if (message.getOrder() > msg.getOrder()){
		msg.setOrder(message.getOrder());
	}
		lampTime = getLC2(msg.getOrder(), lampTime); //Calculo mi nuevo lamptime LC2 en funcion del timestamp del que recibo y del mio actual
		proposedorder = msg.getProposedOrder()+1;//Actualizo proposed order con una propuesta recibida
		msg.setProposedOrder(proposedorder);
		if(msg.getProposedOrder()==6){// 6 porque es el numero de procesos ue tenemos
			msg.setState("DEFINITIVE");
			//multidifundir ACUERDO(k, mensaje.orden)
		}				
	}
	
	/**
	 * 
	 */
	public void receiveAgreed(Message message) {//De nuevo se recibe id del mensaje y orden del proceso
		Message msg = tail.getSpecificMessage(message.getId());//Saco el mensaje que sea de la cola para comparar
		msg.setOrder(message.getOrder());
		lampTime = getLC2(msg.getOrder(), lampTime);
		msg.setState("DEFINITIVE");
		tail.reorderTail();
		//Sacamos sin eliminar el mensaje que haya primero en la cola
		//Comprobamos si hay mensajes en la cola y extraemos el primero
		if(!tail.tailIsEmpty()){
			msg = tail.getFromTail();
		}
		
		while(msg.getState()== "DEFINITIVE"){
			//ENTREGA mensaje
			tail.extractFromTail();
			msg = tail.getFromTail();	
		}
		
		
	}
	
	public int getLC1 (int orden){
		int lc1 = orden+1;
		return lc1;
		
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
}
