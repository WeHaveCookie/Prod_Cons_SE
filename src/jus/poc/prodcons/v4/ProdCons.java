package jus.poc.prodcons.v4;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v4.TestProdCons;
import jus.poc.prodcons.v4.Producteur;

public class ProdCons implements Tampon {

	private Message[] buffer; //tableau de messages = buffer (memoire tampon)
	private int in; //indique l'indice de la premiere case vide du tableau (dans laquelle on peut ecrire un message)
	private int out; //indique l'indice de la premiere case pleinne du tableau (dans laquelle on peut recuperer un message)
	private int nbCasePleine;
	private int impression;
	
	public Observateur TheObservateur;
	
	private Semaphore FileCons;
	private Semaphore FileProd;
	private Semaphore mutexC;
	private Semaphore mutexP;
	private Semaphore ExProd;

	
	/** Constructor ProdCons
	 * @param taille : taille de la memoire tampon
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 */
	public ProdCons(int taille, Observateur obs, int impression) {
		this.buffer = new Message[taille];
		this.impression = impression;
		this.in = 0;
		this.out = 0;
		this.nbCasePleine = 0;
		this.TheObservateur = obs;
		
		FileCons = new Semaphore(0);
		FileProd = new Semaphore(taille);
		mutexC = new Semaphore(1);
		mutexP = new Semaphore(1);
		ExProd = new Semaphore(0);
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
		FileProd.p(); //File d'attente des producteurs : tant que le buffer est plein
		mutexP.p(); // Ne laisse passer qu'un seul producteur à la fois
		
		buffer[in] = arg1;
		in = (in + 1) % taille();
		nbCasePleine++;
		TheObservateur.depotMessage(arg0, arg1);
		
		if(impression == 1){
			System.out.println("Producteur_Depot : "+arg0.identification() + " depose " + arg1 + " - Exemplaires : " + ((MessageX) arg1).getNbMsgDepos());
		}
		if(!((Producteur) arg0).verif()){
			TestProdCons.nbProdAlive--;
			if(impression == 1){
				System.out.println("Producteur_Alive : " + TestProdCons.nbProdAlive);
				System.out.println("NbMsgBuffer : "+ this.enAttente());
			}
		}
		
		FileCons.v(); //Permet de notifier les consommateurs qu'un message à été déposé
		mutexP.v(); // Debloque l'accès à buffer[in]
		ExProd.p(); // Le producteur se bloque tant que tous les examplaires n'ont pas été lues
	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public Message get(_Consommateur arg0) throws Exception,InterruptedException {
		FileCons.p(); // File d'attente des consommateurs : tant que le buffer est vide
		mutexC.p(); // Ne laisse passer qu'un seul consommateur à la fois
		
		MessageX m = (MessageX) buffer[out];
		m.ConsommerMsg(); // Consommation d'un exemplaire du message //ATTENTION POSE UN PROBLEME DE POINTEUR NULL DE TEMPS A AUTRE
		if (impression == 1){
			System.out.println("Consommateur_Retrait : " + arg0.identification() + " retire " + m + " - NbExemplaireConso " + m.getNbConso());
		}
		
		
		if(m.IsConso()){
			// Si tous les exemplaires ont été consommés, on retire le message du buffer
			out = (out + 1) % taille();
			nbCasePleine--;
			TheObservateur.retraitMessage(arg0, m);
			if (impression == 1){
				System.out.println("   ----------------------------------------------------------  ");
				System.out.println("   || Detruction du message : "+ m + " || ");
				System.out.println("   ----------------------------------------------------------  ");
			}
			FileProd.v(); //Permet de notifier les producteurs qu'une case est libérée
			ExProd.v(); //Permet de débloquer le producteur ayant déposé le message
		}
		
		mutexC.v();
		FileCons.v();
		return m;
	}
	
	
	
	
}
