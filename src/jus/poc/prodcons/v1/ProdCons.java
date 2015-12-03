package jus.poc.prodcons.v1;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	private Message[] buffer; //tableau de messages = buffer (memoire tampon)
	private int in; //indique l'indice de la premiere case vide du tableau (dans laquelle on peut ecrire un message)
	private int out; //indique l'indice de la premiere case pleinne du tableau (dans laquelle on peut recuperer un message)
	private int nbCasePleine;
	private int impression;

	
	/** Constructor ProdCons
	 * @param taille : taille de la memoire tampon
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 */
	public ProdCons(int taille, int impression) {
		this.buffer = new Message[taille];
		this.impression = impression;
		this.in = 0;
		this.out = 0;
		this.nbCasePleine = 0;
	}

	/** Nombre de messages en attente dans la memoire tampon (nombre de case pleinne dans le buffer)
	 */
	@Override
	public int enAttente() {
		return nbCasePleine;
	}
	
	/** Taille de la memoire tampon
	 */
	@Override
	public int taille() {
		return buffer.length;
	}

	/** Test si la memoire tampon est pleinne
	 */
	public boolean isPlein() {
		return nbCasePleine == taille();
	}
	
	/** Test si la memoire tampon est vide
	 */
	public boolean isVide()
	{
		return nbCasePleine == 0;
	}

	/** Set le nombre de case pleine dans le buffer
	 */
	public void setnbMsg(int nb){
		nbCasePleine = nb;
	}
	
	
	
	
	/** Deposer un message sur la memoire tampon
	 * 
	 */
	@Override
	public synchronized void put(_Producteur arg0, Message arg1) throws Exception,InterruptedException {
		//Tant que le tampon est plein, on attend puisque l'on ne peut rien deposer
		while(nbCasePleine==taille()) {
			wait();
		}
		buffer[in] = arg1;
		in = (in + 1) % taille();
		nbCasePleine++;
		notifyAll();  //Notification a tous les threads
	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public synchronized Message get(_Consommateur arg0) throws Exception,InterruptedException {
		//Tant que le tampon est vide, on attend puisque l'on ne peut rien recuperer
		while(nbCasePleine == 0) {
			wait();
		}
		//Si le tampon n'est pas vide, on recupere le premier message du tableau, et on met a jours l'indice out
		Message m = buffer[out];
		out = (out + 1) % taille();
		nbCasePleine--;
		notifyAll(); //Notification à tous les threads
		return m;
	}

}
