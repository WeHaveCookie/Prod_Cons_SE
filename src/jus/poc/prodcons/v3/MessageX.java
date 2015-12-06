package jus.poc.prodcons.v3;

import jus.poc.prodcons.Message;

public class MessageX implements Message {

	private int idProd;
	private int idMsg;

	public MessageX(int idProd, int numMsg) {
		this.idProd = idProd;
		this.idMsg = numMsg;
	}

	public String toString()
	{
		return "Message[IDprod: "+idProd + ", IDmsg: "+ idMsg +"]";
	}


}
