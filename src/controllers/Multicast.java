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

public class Multicast extends Thread {

	private Message message; //Mensaje a enviar
	private int idProcess; //ID del proceso que envia
	private String idServer; //se guardará la id del servidor al que se va a enviar
	private Semaphore controlMulticast;
	private String[] ipServer; //Array de ips
	private int idParent; //ID del server padre del proceso
	private final String http = "http://";
	private final String api = ":8080/ISIS-Algorithm/"; //TODO OJO CAMBIAR CON LO DE web.xml

	public Multicast(Message message, int id, Semaphore controlMulticast, String[] ipServer, int idParent) {
		this.message = message;
		this.idProcess = id;
		this.controlMulticast=controlMulticast;
		this.ipServer = ipServer;
		this.idParent = idParent;
	}

	@Override
	public void run() {
		for(int idProcess=1; idProcess<=6; idProcess++) {
			Client client=ClientBuilder.newClient();
			URI uri=UriBuilder.fromUri(http + ipServer[idParent] + api).build();
			WebTarget target = client.target(uri);
			target.path("rest").path("server/multicast")
			.queryParam("idProcess", idProcess)
			.queryParam("idMessage", message.getId())
			.queryParam("bodyMessage", message.getContent())
			.queryParam("orderMessage", message.getOrder())
			.queryParam("propOrderMessage", message.getProposedOrder())
			.queryParam("stateMessage", message.getState())
			.request(MediaType.TEXT_PLAIN).get(String.class);
			
			randomDelay();
		}
		controlMulticast.release(); //Release del semaforo para dar por terminada la multidifusion
	}

	/**
	 * Hace un delay de un tiempo aleatorio entre 0.2 y 0.5
	 * Usado para hacer delay entre los envios multicast de cada proceso
	 */
	private void randomDelay() {
		double rand = ThreadLocalRandom.current().nextDouble(0.2, 0.5);
		try {
			Thread.sleep((long)rand*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}