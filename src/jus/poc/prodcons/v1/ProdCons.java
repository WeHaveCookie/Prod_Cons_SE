package jus.poc.prodcons.v1;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	private Message[] msg; //tableau de messages = buffer (memoire tampon)
	private int in; //indique l'indice de la premiere case vide du tableau (dans laquelle on peut ecrire un message)
	private int out; //indique l'indice de la premiere case pleinne du tableau (dans laquelle on peut recuperer un message)
	private int nbMsg; //nombre de messages dans le tableau
	private int impression;

	
	/** Constructor ProdCons
	 * @param taille : taille de la memoire tampon
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 */
	public ProdCons(int taille, int impression) {
		this.msg = new Message[taille];
		this.impression = impression;
		this.in = 0;
		this.out = 0;
		this.nbMsg = 0;
	}

	/** Nombre de messages en attente dans la memoire tampon
	 */
	@Override
	public int enAttente() {
		return nbMsg;
	}
	
	/** Taille de la memoire tampon
	 */
	@Override
	public int taille() {
		return msg.length;
	}

	/** Test si la memoire tampon est pleinne
	 */
	private boolean isPlein() {
		return nbMsg == taille();
	}
	
	/** Test si la memoire tampon est vide
	 */
	private boolean isVide()
	{
		return nbMsg == 0;
	}

	
	/** Deposer un message sur la memoire tampon
	 * 
	 */
	@Override
	public synchronized void put(_Producteur arg0, Message arg1) throws Exception,InterruptedException {
		//Tant que le tampon est plein, on attend puisque l'on ne peut rien deposer
		while(isPlein()) {
			wait();
		}
		//Si le tampon n'est pas plein, on depose le message dans la premiere case vide du tableau, et on met a jours l'indice in et le nbMsg
		
		//if(!(((Producteur)arg0).verif())){
		//	TestProdCons.producteurAlive--;
		//	if(impression == 1){
		//		System.out.println("producteurAlive : "+TestProdCons.producteurAlive);
		//	}
		//}
		
		msg[in] = arg1;
		in = (in + 1) % taille();
		nbMsg++;
		//Notification a tous les threads
		notifyAll();
	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public synchronized Message get(_Consommateur arg0) throws Exception,InterruptedException {
		//Tant que le tampon est vide, on attend puisque l'on ne peut rien recuperer
		while(isVide()) {
			wait();
		}
		//Si le tampon n'est pas vide, on recupere le premier message du tableau, et on met a jours l'indice out et le nbMsg
		Message m = msg[out];
		out = (out + 1) % taille();
		nbMsg--;
		//Notification à tous les threads
		notifyAll();
		return m;
	}

}
