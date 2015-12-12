package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class ProdCons implements Tampon {

	private MessageX[] buffer; //tableau de messages = buffer (memoire tampon)
	private int in; //indique l'indice de la premiere case vide du tableau (dans laquelle on peut ecrire un message)
	private int out; //indique l'indice de la premiere case pleinne du tableau (dans laquelle on peut recuperer un message)
	private int nbCasePleine;
	private int impression;
	public Observateur TheObservateur;
	
	private Semaphore FileCons;
	private Semaphore FileProd;
	private Semaphore mutexP;
	private Semaphore mutexC;
	
	public Semaphore consProd;

	
	/** Constructor ProdCons
	 * @param taille : taille de la memoire tampon
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 */
	public ProdCons(int taille, Observateur obs, int impression) {
		this.buffer = new MessageX[taille];
		this.impression = impression;
		this.in = 0;
		this.out = 0;
		this.nbCasePleine = 0;
		this.TheObservateur = obs;
		
		FileCons = new Semaphore(0);
		FileProd = new Semaphore(taille);
		mutexP = new Semaphore(1);
		mutexC = new Semaphore(1);
		
		consProd = new Semaphore(0);
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
	public void put(_Producteur arg0, Message arg1) throws Exception,InterruptedException {
		FileProd.p(); //File d'attente des producteurs
		mutexP.p(); // Permet l'acces unique d'un consommateur au buffer
		buffer[in] = (MessageX) arg1;
		TheObservateur.depotMessage(arg0, arg1); //lorsqu'un message M est déposé dans le tampon par le producteur P
		in = (in + 1) % taille();
		nbCasePleine++;
		mutexP.p(); // Permet l'acces unique d'un consommateur au buffer
		FileCons.v(); //Permet de notifier les consommateurs qu'un message à été déposé
		consProd.p(); // Blocage du producteur tant que tous les exemplaires de son message n'ont pas été lus.
	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public Message get(_Consommateur arg0) throws Exception,InterruptedException {
		FileCons.p(); // File d'attente des consommateurs : tant qu'il n'y a pas de message a consommer
		mutexC.p(); // Permet l'acces unique d'un consommateur au buffer
		MessageX m = buffer[out];
		m.ConsommerMsg(); // Consommation d'un exemplaire du message
		TheObservateur.retraitMessage(arg0, m); //lorsqu'un message M est retiré du tampon par le consommateur C
		if(m.IsConso()){
			// Si tous les examplaires ont été consommés, on retire le message du buffer
			out = (out + 1) % taille();
			nbCasePleine--;
			consProd.v(); //Permet de débloquer le producteur ayant déposé le message
			FileProd.v(); //Permet de notifier les producteurs qu'une case est libérée
			if (impression == 1){
				System.out.println("Tous les exemplaires de " + m.toString() + "ont été consommé.");
			}
		}
		mutexC.v(); // Debloque l'accès au buffer
		return m;
	}
	
	
	
	
}
