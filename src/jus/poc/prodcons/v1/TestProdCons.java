package jus.poc.prodcons.v1;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

public class TestProdCons extends Simulateur {

	public static int nbProdAlive;
	
	public int nbProd;
	public int nbCons;
	public int idCons = 1;
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
			if(impression == 1){
				System.out.println("Parser : " + key + " : " + value);
			}
			Option.getDeclaredField(key).set(this,value);
		}
	}
		
	
	/** Initialisation du programme
	 */
	@Override
	protected void run() throws Exception{
		this.init("src/jus/poc/prodcons/options/options1.xml");
		ProdCons buffer = new ProdCons(nbBuffer, impression);
		Aleatoire aleaCons = new Aleatoire(tempsMoyenConsommation,deviationTempsMoyenConsommation);
		Aleatoire aleaTempsProd = new Aleatoire(tempsMoyenProduction, deviationTempsMoyenProduction);
		Aleatoire aleaNbreAProduire = new Aleatoire(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
		nbProdAlive = nbProd;
		Producteur[] p = new Producteur[nbProd];
		Consommateur[] c = new Consommateur[nbCons];
		
		for(int i=0; i<nbProd; i++) { 
			p[i]= new Producteur(observateur, tempsMoyenProduction, deviationTempsMoyenProduction, aleaNbreAProduire.next(), buffer, aleaTempsProd, impression);
			if(impression == 1){
				System.out.println("Init : producteur : " + p[i].identification() + " -> NbrAProduire : " + p[i].GetNbMsg());
			}
			p[i].start();
		}
				
		for(int j=0; j<nbCons; j++) { 
			c[j] = new Consommateur(observateur, tempsMoyenConsommation, deviationTempsMoyenConsommation, buffer, aleaCons, impression);
			if(impression == 1){
				System.out.println("Init : consommateur : " + c[j].identification());
			}
			c[j].start();
		}
	
	}

	


	/** Main permettant d'executer le programme
	 */
	public static void main(String[] args){
		new TestProdCons(new Observateur()).start();
	}

}
