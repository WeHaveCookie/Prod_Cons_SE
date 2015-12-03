package jus.poc.prodcons.v2;

import java.util.Date;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;



// Threads consommateurs
public class Consommateur extends Acteur implements _Consommateur {

	private ProdCons tampon; //tampon sur lequel on retire les messages
	private int nbMessageRetire; //nombre de messages retires par le consommateur
	private Aleatoire alea; //variable aleatoire permettant de simuler un delais de traitement
	private int impression; // Permet d'inhiber les System.out.println produit par le programme

	
	/** Constructor Consommateur
	 * @param observateur
	 * @param moyenneTempsDeTraitement : moyenne du temps de traitement
	 * @param deviationTempsDeTraitement : dispersion autour de la moyenne du temps de traitement
	 * @param tampon : tampon sur lequel on retire les messages
	 * @param alea : variable aleatoire permettant de simuler un delais de traitement
	 * @param impression : permet d'inhiber les System.out.println produit par le programme s'il vaut 1
	 * @throws ControlException
	 */
	public Consommateur(Observateur observateur, int moyenneTempsDeTraitement, int deviationTempsDeTraitement, ProdCons tampon, Aleatoire alea, int impression) throws ControlException {
		super(Acteur.typeConsommateur, observateur, moyenneTempsDeTraitement, deviationTempsDeTraitement);
		this.tampon = tampon;
		//this.nbMessage = nbMessage;
		this.nbMessageRetire = 0;
		this.alea = alea;
		this.impression = impression;
	}
	

	
	/** Methode de recuperation et de traitement des messages par le consommateur
	 * 
	 */
	public void run()
	{
			try {
				//Le consommateur recupere le message depuis le tampon et l'affiche
				Message msg = tampon.get(this);
				if (impression == 1){
					System.out.println("Consommateur_Retrait : "+ super.identification() + " recupere "+msg);
				}
				//On incremente alors le nombre de message retire et on simule un delais de traitement
				nbMessageRetire++;
				tampon.setnbMsg(tampon.enAttente() - 1);
				if (impression == 1){
					System.out.println("Consommateur_Traitement : "+ super.identification() + " effectue le traitement sur "+msg);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}


	}
	
	
	
	
	

	

	/** Methode permetant de connaitre le nombre de message retire par le consommateur
	 * 
	 */
	public int nombreDeMessages() {
		return nbMessageRetire;
	}



}
