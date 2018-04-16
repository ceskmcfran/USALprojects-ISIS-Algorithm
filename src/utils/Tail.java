package utils;

import java.util.ArrayList;

public class Tail {
	//Arraylist para guardar mensajes
	public ArrayList<Message> tail;
	
	//Constructor
	public Tail(){
		//Creamos el ArrayList
		tail = new ArrayList<Message>();
		
	}
	
	//Método para añadir mensaje
	public void addToTail(Message msg){
		tail.add(msg);
	}
	
	//Método para obtener mensaje
	public Message getFromTail(){
		Message msg = tail.get(0);
		return msg;
		
	}
	
	//Método para extraer un mensaje de la cola, quedando por tanto eliminado
	public Message extractFromTail(){
		Message msg = tail.get(0);//Obtenemos el mensaje
		tail.remove(0);//Eliminamos de la cola
		return msg;
	}
	
	//Método para mostrar por pantalla los mensajes que hay en la cola
	public void listMessages(){
		for(int i =0; i< tail.size();i++){
			Message msg = tail.get(i);
			System.out.println("\nMessage ID:"+ msg.getId()+ "State:" + msg.getState()+ "Order:" + msg.getOrder());
			
		}
	}
	
	//Método para cola vacía
	public Boolean tailIsEmpty(){
		return tail.isEmpty();
	}
	
	//Método para obtener un mensaje específico mediante id de la cola
	public Message getSpecificMessage(String id){
		Message msg = new Message();
		//mirar bien este if tengo dudas de si funcionaria
		for(int i=0;i<tail.size();i++){
			if (tail.get(i).getId().equals(id)){
				msg=tail.get(i);
			}
	
		}//fin for
		
		return msg;
		
		
	}



}
