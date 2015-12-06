package jus.poc.prodcons.v3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class TestProdCons extends Simulateur {

	public static int nbProdAlive;
	
	public int nbProd;
	public int nbCons;
	public int nbBuffer;
	public int tempsMoyenProduction;
	public int deviationTempsMoyenProduction;
	public int tempsMoyenConsommation;
	public int deviationTempsMoyenConsommation;
	public int nombreMoyenDeProduction;
	public int deviationNombreMoyenDeProduction;
	public int nombreMoyenNbExemplaire;
	public int deviationNombreMoyenNbExemplaire;
	public int impression; // Pour inhiber les impressions réalisées par le programme, il faut mettre le flag impression à 0 dans le fichier xml


	
	/** Constructor TestProdCons
	 * @param observateur
	 */
	public TestProdCons(Observateur observateur){
		super(observateur);
	}

	
	/** Parser XML permettant d'initialiser le programme
	* Retreave the parameters of the application.
	* @param file the final name of the file containing the options.
	*/
	protected void init(String file) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, InvalidPropertiesFormatException, IOException {
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream(file));
		String key;
		int value;
		Class<?> Option = getClass();
		for(Map.Entry<Object,Object> entry : properties.entrySet()) {
			key = (String)entry.getKey();
			value = Integer.parseInt((String)entry.getValue());
			//if(impression == 1){
			//	System.out.println("Parser : " + key + " : " + value);
			//}
			Option.getDeclaredField(key).set(this,value);
		}
	}
		
	
	/** Initialisation du programme
	 */
	@Override
	protected void run() throws Exception{
		this.init("src/jus/poc/prodcons/options/options1.xml");
		nbProdAlive = nbProd;
		ProdCons buffer = new ProdCons(nbBuffer, observateur, impression);
		int i=0;
		Aleatoire aleaCons = new Aleatoire(tempsMoyenConsommation,deviationTempsMoyenConsommation);
		Aleatoire aleaTempsProd = new Aleatoire(tempsMoyenProduction, deviationTempsMoyenProduction);
		Aleatoire aleaNbreAProduire = new Aleatoire(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);

		try {
			observateur.init(nbProd, nbCons, nbBuffer); //a l'initialisation du systeme pour indiquer la configuration d'execution
		} catch (ControlException e) {
			e.printStackTrace();
		}
		
		
		while(this.nbProd>0) {
			Producteur p = new Producteur(observateur, tempsMoyenProduction, deviationTempsMoyenProduction, aleaNbreAProduire.next(), buffer, aleaTempsProd, impression);
			if(impression == 1){
				System.out.println("Init : producteur : " + p.identification() + " -> NbrAProduire : " + p.GetNbMsg());
			}
			observateur.newProducteur(p); //lorsqu'un nouveau producteur P est cree
			p.start();
			this.nbProd--;
		}
		
		while(this.nbCons>0) {
			Consommateur c = new Consommateur(observateur, tempsMoyenConsommation, deviationTempsMoyenConsommation, buffer, aleaCons, impression);
			if(impression == 1){
				System.out.println("Init : consommateur : " + c.identification());
			}
			observateur.newConsommateur(c); //lorsqu'un nouveau consommateur C est cree
			c.start();
			this.nbCons--;
		}

	}


	/** Main permettant d'executer le programme
	 */
	public static void main(String[] args){
		new TestProdCons(new Observateur()).start();
	}

}
