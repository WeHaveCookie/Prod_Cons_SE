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
	private int nbExemplaire;
	private int impression;
	
	public Observateur TheObservateur;
	
	private Semaphore FileCons;
	private Semaphore FileProd;
	private Semaphore ExemplaireCons;
	private Semaphore mutexC;
	private Semaphore mutexP;
	private Semaphore ExemplaireProd;

	
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
		this.nbExemplaire = 0;
		this.TheObservateur = obs;
		
		FileCons = new Semaphore(0);
		FileProd = new Semaphore(taille);
		ExemplaireCons = new Semaphore(0);
		mutexC = new Semaphore(1);
		mutexP = new Semaphore(1);
		ExemplaireProd = new Semaphore(0);
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
		
		FileProd.p(); //tant que le buffer est plein on attend
		
		synchronized(this){ 
			buffer[in] = arg1;
			in = (in + 1) % taille();
			TheObservateur.depotMessage(arg0, arg1);
			nbCasePleine++;
			if(impression == 1){
				System.out.println("Producteur_Depot : "+arg0.identification() + " depose " + arg1 + " - Exemplaires : " + ((MessageX) arg1).getNbExDepos());
			}
			if(!((Producteur) arg0).verif()){
				TestProdCons.nbProdAlive--;
				if(impression == 1){
					System.out.println("Producteur_Alive : " + TestProdCons.nbProdAlive);
					System.out.println("NbMsgBuffer : "+ this.enAttente());
				}
			}
		}	
		ExemplaireCons.v(((MessageX) arg1).getNbExDepos()); // notifie les consommateurs que x nouveaux exemplaires sont à retirer
		ExemplaireProd.p(); // Le prod se bloque tant que tous les exemplaires n'ont pas été lu

	}

	
	/** Retirer un message du tampon
	 */
	@Override
	public Message get(_Consommateur arg0) throws Exception,InterruptedException {
		
		ExemplaireCons.p(); // Tant qu'il n'y a aucun exemplaire à retirer, les consommateurs attendent
		MessageX m;
		
		synchronized(this){ 
			m = (MessageX) buffer[out];
			m.ConsommerEx(); // Consommation d'un exemplaire du message
			if (impression == 1){
				System.out.println("Consommateur_Retrait : " + arg0.identification() + " retire " + m + " - NbExemplaireConso " + m.getNbConso());
			}
		}
			
		if(m.IsConso()){ // Si tous les exemplaires ont été consommés, on retire le message du buffer
			
			synchronized(this){ 
				out = (out + 1) % taille();
				TheObservateur.retraitMessage(arg0, m);
				nbCasePleine--;
				if (impression == 1){
					System.out.println("   ----------------------------------------------------------  ");
					System.out.println("   || Detruction du message : "+ m + " || ");
					System.out.println("   ----------------------------------------------------------  ");
				}
			}
			FileProd.v(1); //Notifie les producteur qu'une nouvelle place est dispo dans le buffer
			ExemplaireProd.v(1); //Debloque le producteur bloque car tous les exemplaires ont été lu
		}
		return m;
	}
	
	
	
	
}
