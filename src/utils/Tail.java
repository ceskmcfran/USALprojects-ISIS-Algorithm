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
	
	//M�todo para a�adir mensaje
	public void addToTail(Message msg){
		tail.add(msg);
	}
	
	//M�todo para obtener mensaje
	public Message getFromTail(){
		Message msg = tail.get(0);
		return msg;
		
	}
	
	//M�todo para extraer un mensaje de la cola, quedando por tanto eliminado
	public Message extractFromTail(){
		Message msg = tail.get(0);//Obtenemos el mensaje
		tail.remove(0);//Eliminamos de la cola
		return msg;
	}
	
	//M�todo para mostrar por pantalla los mensajes que hay en la cola
	public void listMessages(){
		for(int i =0; i< tail.size();i++){
			Message msg = tail.get(i);
			System.out.println("\nMessage ID:"+ msg.getId()+ "State:" + msg.getState()+ "Order:" + msg.getOrder());
			
		}
	}
	
	//M�todo para cola vac�a
	public Boolean tailIsEmpty(){
		return tail.isEmpty();
	}
	
	//M�todo para obtener un mensaje espec�fico mediante id de la cola
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
