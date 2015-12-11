package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;

public class MessageX implements Message {

	private int idProd;
	private int idMsg;
	private int nbExDepos; //nombre d'examplaires du message deposé
	private int nbExConso; //nombre d'exemplaires du message consommé à l'instant t

	public MessageX(int idProd, int numMsg, int nbMsgDepos) {
		this.idProd = idProd;
		this.idMsg = numMsg;
		this. nbExDepos = nbMsgDepos;
		this.nbExConso = 0;
	}

	public String toString()
	{
		return "Message[IDprod: "+idProd + ", IDmsg: "+ idMsg +"]";
	}

	/** Getteur du nombre d'exemplaires consommés
	 */
	protected int getNbConso() {
		return nbExConso;
	}

	/** Getteur du nombre d'exemplaire du message initialement déposé
	 */
	protected int getNbMsgDepos() {
		return nbExDepos;
	}

	/** Methode permettant de consommer un exemplaire du message
	 */
	public void ConsommerMsg(){
		nbExConso++;
	}

	/** Methode permettant de savoir s'il reste des messages à consommer
	 */
	public boolean IsConso(){
		return ((nbExDepos - nbExConso) == 0);
	}
	
	
	
	

}
