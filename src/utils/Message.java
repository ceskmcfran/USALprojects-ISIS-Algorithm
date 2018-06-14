package utils;

public class Message {
	private String id;
	private String content;
	private String state;
	private int idSender;
	private int order;
	private int proposedOrder;
	public static final String PROVISIONAL="PROVISIONAL"; //State
	public static final String DEFINITIVE="DEFINITIVE"; //State


	public Message(){ }

	public Message(String id, String content, String state, int order, int proposedOrder, int idSender){
		this.id=id;
		this.content=content;
		this.state=state;
		this.order=order;
		this.proposedOrder=proposedOrder;
		this.idSender = idSender;
	}


	//Getters and setters
	public void setId(String id){
		this.id=id;
	}

	public void setContent(String content){
		this.content=content;
	}

	public void setState(String state){
		this.state=state;

	}

	public void setOrder (int order){
		this.order=order;
	}

	public void setProposedOrder (int proposedOrder){
		this.proposedOrder=proposedOrder;
	}

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

	public int getIdSender() {
		return idSender;
	}

	public void setIdSender(int idSender) {
		this.idSender = idSender;
	}
}
