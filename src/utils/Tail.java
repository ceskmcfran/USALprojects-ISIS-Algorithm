package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import comparators.CompareOrder;

public class Tail {
	//Arraylist para guardar mensajes
	private ArrayList<Message> tail;
	
	//Constructor
	public Tail(){
		//Creamos el ArrayList
		tail = new ArrayList<Message>();	
	}
	
	//Setter
	public void setTailList(ArrayList<Message> tailList) {
		  this.tail = tailList;
	}
	
	//Getter
    public ArrayList<Message> getTailList() {
		    return tail;
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
			System.out.println("\nMessage ID: "+ msg.getId()+ " State: " + msg.getState()+ " Order: " + msg.getOrder());
		}
	}
	
	//Método para cola vacía
	public Boolean tailIsEmpty(){
		return tail.isEmpty();
	}
	
	//Método para obtener un mensaje específico mediante id de la cola
	public Message getSpecificMessage(String id){
		Message msg = new Message();
		for(int i=0;i<tail.size();i++){
			if (tail.get(i).getId().equals(id)){
				msg=tail.get(i);
			}
		}
		return msg;
	}
	
	//Reordenación de la cola
	public void reorderTail(){
		Collections.sort(tail,new CompareOrder());
	}
}
