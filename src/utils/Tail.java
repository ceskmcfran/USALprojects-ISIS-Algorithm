package utils;

import java.util.ArrayList;
import java.util.Collections;
import comparators.CompareOrder;
import java.util.Comparator;

public class Tail {
	private ArrayList<Message> tail;
	
	public Tail(){
		tail = new ArrayList<Message>();	
	}
	
	/**
	 * Setter tail
	 * @param tailList
	 */
	public void setTailList(ArrayList<Message> tailList) {
		this.tail = tailList;
	}
	
	/**
	 * Getter tail
	 * @return tail
	 */
    public ArrayList<Message> getTailList() {
    	return tail;
	}
	
	/**
	 * Add message
	 * @param message
	 */
	public void addToTail(Message message){
		tail.add(message);
	}
	
	/**
	 * Get message
	 * @return
	 */
	public Message getFromTail(){
		Message message = tail.get(0);
		return message;	
	}
	
	/**
	 * Extract from tail and remove the extracted message
	 * @return message
	 */
	public Message extractFromTail(){
		Message message = tail.get(0);
		tail.remove(0);
		return message;
	}
	
	/**
	 * DEBUG: Display messages of tail 
	 * @param idProcess
	 */
	public void listMessages(int idProcess){
		for(int i =0; i< tail.size();i++){
			Message message = tail.get(i);
			System.out.println("\n (Soy " + idProcess + "ID: "+ message.getId()+ " State " + message.getState()+ " Order: " + message.getOrder());
		}
	}
	
	/**
	 * Returns true if the tail is empty
	 * @return boolean
	 */
	public Boolean isEmpty(){
		return tail.isEmpty();
	}
	
	/**
	 * Get a specific message by its ID
	 * @param idMessage
	 * @return message
	 */
	public Message getSpecificMessage(String idMessage){
		Message message = new Message();
		for(int i=0;i<tail.size();i++){
			if (tail.get(i).getId().equals(idMessage)){
				message=tail.get(i);
			}
		}
		return message;
	}
	
	/**
	 * Reorder the tail
	 */
	public void reorderTail(){
		Collections.sort(tail,new CompareOrder());
	}
}
