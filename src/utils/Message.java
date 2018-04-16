package utils;

public class Message {
	//Atributos del mensaje
	private String id;
	private String content;
	private String state;
	private int order;
	private int proposedOrder;
	//Strings para los posibles estados de un mensaje
	public static final String PROVISIONAL="PROVISIONAL";
	public static final String DEFINITIVE="DEFINITIVE";
	
	
	//Constructor
	public Message(){
		
	}
	//Constructor con parámetros
	public Message(String id, String content, String state, int order, int proposedOrder){
		this.id=id;
		this.content=content;
		this.state=state;
		this.order=order;
		this.proposedOrder=proposedOrder;
		
	}
	/*Setters y Getter de la clase Message*/
	//Setters
	
	public void setId(String id){
		this.id=id;
	}
	
	public void setContent(String content){
		this.content=content;
	}
	
	public void setState(String state){
		this.state=state;
		
	}
	
	public void setOrder (int Order){
		this.order=order;
	}
	
	public void setProposedOrder (int proposedOrder){
		this.proposedOrder=proposedOrder;
	}
	
	//Getters
	
	public String getId(){
		return this.id;
	}
	
	public String getContent(){
		return this.content;
	}
	
	public String getState(){
		return this.state;
	}
	
	public int getOrder(){
		return this.order;
	}
	
	public int getProposedOrder(){
		return this.proposedOrder;
	}
	

}
