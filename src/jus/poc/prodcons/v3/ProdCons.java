package jus.poc.prodcons.v3;

import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v3.TestProdCons;
import jus.poc.prodcons.v3.Producteur;

public class ProdCons implements Tampon {

	private Message[] buffer; //tableau de messages = buffer (memoire tampon)
	private int in; //indique l'indice de la premiere case vide du tableau (dans laquelle on peut ecrire un message)
	private int out; //indique l'indice de la premiere case pleinne du tableau (dans laquelle on peut recuperer un message)
	private int nbCasePleine;
	private int impression;
	
	public Observateur TheObservateur;
	
	private Semaphore FileCons;
	private Semaphore FileProd;

	
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
		synchronized(this){ 
			buffer[in] = arg1;
			in = (in + 1) % taille();
			nbCasePleine++;
			TheObservateur.depotMessage(arg0, arg1);
			if(impression == 1){
				System.out.println("Producteur_Depot : "+arg0.identification() + " depose " + arg1);
			}
			if(!((Producteur) arg0).verif()){
				TestProdCons.nbProdAlive--;
				if(impression == 1){
					System.out.println("Producteur_Alive : " + TestProdCons.nbProdAlive);
					System.out.println("NbMsgBuffer : "+ this.enAttente());
				}
			}
		} 
		FileCons.v(); //Permet de notifier les consommateurs qu'un message à été déposé
	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public Message get(_Consommateur arg0) throws Exception,InterruptedException {
		FileCons.p(); //File d'attente des consommateurs
		Message m;
		synchronized(this){ 
			m = buffer[out];
			out = (out + 1) % taille();
			nbCasePleine--;
			TheObservateur.retraitMessage(arg0, m);
			if (impression == 1){
				System.out.println("Consommateur_Retrait : "+ arg0.identification() + " recupere "+ m);
			}
		} 
		FileProd.v(); //Permet de notifier les producteurs qu'une case est libérée
		return m;
	}
	
	
	
	
}
