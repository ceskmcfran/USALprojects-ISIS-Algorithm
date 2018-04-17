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
			System.out.println("\nMessage ID: "+ msg.getId()+ " State: " + msg.getState()+ " Order: " + msg.getOrder());
		}
	}
	
	//M�todo para cola vac�a
	public Boolean tailIsEmpty(){
		return tail.isEmpty();
	}
	
	//M�todo para obtener un mensaje espec�fico mediante id de la cola
	public Message getSpecificMessage(String id){
		Message msg = new Message();
		for(int i=0;i<tail.size();i++){
			if (tail.get(i).getId().equals(id)){
				msg=tail.get(i);
			}
		}
		return msg;
	}
	
	//Reordenaci�n de la cola
	public void reorderTail(){
		Collections.sort(tail,new CompareOrder());
	}
}
