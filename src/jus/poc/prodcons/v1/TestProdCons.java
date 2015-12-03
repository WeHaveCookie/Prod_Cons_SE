package jus.poc.prodcons.v1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;
import jus.poc.prodcons.Tampon;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class TestProdCons extends Simulateur {

	public static int producteurAlive;
	public static int consommateurAlive;
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
	private HashMap<Integer, _Consommateur> consommateurs = new HashMap<Integer, _Consommateur>();
	private HashMap<Integer, _Producteur> producteurs = new HashMap<Integer, _Producteur>();

	
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
		producteurAlive = nbProd;
		consommateurAlive = nbCons;
		ProdCons buffer = new ProdCons(nbBuffer, impression);
		int i=0;
		Aleatoire aleaCons = new TirageAlea(tempsMoyenConsommation,deviationTempsMoyenConsommation);
		Aleatoire aleaTempsProd = new TirageAlea(tempsMoyenProduction, deviationTempsMoyenProduction);
		Aleatoire aleaNbreAProduire = new TirageAlea(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);

		for(i=0;i<nbProd;i++) {
			Producteur p = new Producteur(observateur, tempsMoyenProduction, deviationTempsMoyenProduction, aleaNbreAProduire.next(), buffer, aleaTempsProd, impression);
			producteurs.put(p.identification(), p);
			p.start();
			if(impression == 1){
				System.out.println("Init : producteur : " + p.identification() + " -> NbrAProduire : " + p.GetNbMsg());
			}
		}
		
		for(i=0;i<nbCons;i++) {
			Consommateur c = new Consommateur(observateur, tempsMoyenConsommation, deviationTempsMoyenConsommation, buffer, aleaCons, impression);
			consommateurs.put(c.identification(), c);
			c.start();
			if(impression == 1){
				System.out.println("Init : consommateur : " + c.identification());
			}
		}

	}


	/** Main permettant d'executer le programme
	 */
	public static void main(String[] args){
		new TestProdCons(new Observateur()).start();
	}

}
