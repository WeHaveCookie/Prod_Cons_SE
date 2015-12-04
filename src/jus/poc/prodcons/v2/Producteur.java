package jus.poc.prodcons.v2;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Producteur;
import jus.poc.prodcons.v1.TestProdCons;



//Threads producteurs
public class Producteur extends Acteur implements _Producteur {

	private ProdCons tampon; //tampon sur lequel on depose les messages
	private int nbMessage; //nombre total de message que le producteur doit produire et deposer
	private int nbMessageDepose; //nombre de message que le producteur a deja depose
	private Aleatoire alea; //variable aleatoire permettant de simuler un delais de traitement
	private int impression; // Permet d'inhiber les System.out.println produit par le programme

	
	/** Constructor Producteur
	 * @param observateur : 
	 * @param moyenneTempsDeTraitement : moyenne du temps de traitement
	 * @param deviationTempsDeTraitement : dispersion autour de la moyenne du temps de traitement
	 * @param nbMessage : nombre total de message que le producteur doit produire et deposer
	 * @param tampon : tampon sur lequel on depose les messages
	 * @param alea : variable aleatoire permettant de simuler un delais de traitement
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 * @throws ControlException
	 */
	public Producteur(Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement, int nbMessage, ProdCons tampon, Aleatoire alea, int impression) throws ControlException {
		super(Acteur.typeProducteur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		this.tampon = tampon;
		this.nbMessageDepose = 0;
		this.nbMessage = nbMessage;
		this.alea = alea;
		this.impression = impression;
	}
	
	
	/** Nombre de message restant a deposer sur le tampon
	 */
	@Override
	public int nombreDeMessages() {
		//nombre de message a produire et deposer, moins le nombre de messages deja deposes
		return nbMessage - nbMessageDepose;
	}

	
	/** Verification de la possibilite de deposer un message sur le tampon
	 * @return : true si le tampon n'est pas plein
	 */
	public boolean verif(){
		return (nbMessageDepose+1) < nbMessage;
	}

	
	public int GetNbMsg(){
		return nbMessage;
	}
	
	
	/** Méthode permettant de produire un message et de le deposer dans la memoire tampon
	 * 
	 */
	public void run()
	{
		//Tant qu'il reste des messages a produire : on les produits et on les deposes sur la memoire tampon
		while(nombreDeMessages() != 0)
		{
			try {
				Message msg = new MessageX(identification(),nbMessageDepose);
				if(impression == 1){
					System.out.println("Producteur_Creation : "+super.identification() + " produit " +msg);
				}
				tampon.put(this, msg);
				nbMessageDepose++; 
				if(impression == 1){
					System.out.println("Producteur_Depot : "+super.identification() + " depose " +msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(impression == 1){
			System.out.println("STOP : producteur : " + this.identification());
		}
		TestProdCons.nbProdAlive--;
		if(impression == 1){
			System.out.println("Producteur_Alive : " + TestProdCons.nbProdAlive);
			System.out.println("NbMsgBuffer : "+ tampon.enAttente());
		}

	}



}
