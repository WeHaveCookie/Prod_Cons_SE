package jus.poc.prodcons.v6;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import jus.poc.prodcons.Aleatoire;
import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Observateur;
import jus.poc.prodcons.Simulateur;

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
		//this.init("src/jus/poc/prodcons/options/options1.xml");
		//this.init("src/jus/poc/prodcons/options/optionsDevia1.xml");
		//this.init("src/jus/poc/prodcons/options/optionsDevia2.xml");
		//this.init("src/jus/poc/prodcons/options/optionsDevia3.xml");
		//this.init("src/jus/poc/prodcons/options/optionsDevia4.xml");
		//this.init("src/jus/poc/prodcons/options/optionsMultiBuff.xml");
		//this.init("src/jus/poc/prodcons/options/optionsNoBuffer.xml");
		//this.init("src/jus/poc/prodcons/options/optionsNoCons.xml");
		//this.init("src/jus/poc/prodcons/options/optionsNoProd.xml");
		//this.init("src/jus/poc/prodcons/options/optionsOverload.xml");
		this.init("src/jus/poc/prodcons/options/optionsTpsProd0.xml");
		Observator m_observator = new Observator();
		ProdCons buffer = new ProdCons(nbBuffer, observateur, m_observator, impression);
		Aleatoire aleaCons = new Aleatoire(tempsMoyenConsommation,deviationTempsMoyenConsommation);
		Aleatoire aleaTempsProd = new Aleatoire(tempsMoyenProduction, deviationTempsMoyenProduction);
		Aleatoire aleaNbreAProduire = new Aleatoire(nombreMoyenDeProduction, deviationNombreMoyenDeProduction);
		nbProdAlive = nbProd;
		Producteur[] p = new Producteur[nbProd];
		Consommateur[] c = new Consommateur[nbCons];
		
		try {
			m_observator.init(nbProd, nbCons, nbBuffer);
			//observateur.init(nbProd, nbCons, nbBuffer);
		} catch (ControlException e) {
			e.printStackTrace();
		}
		for(int i=0; i<nbProd; i++) { 
			p[i]= new Producteur(observateur, m_observator, tempsMoyenProduction, deviationTempsMoyenProduction, aleaNbreAProduire.next(), buffer, aleaTempsProd, impression);
			m_observator.newProducteur(p[i]);
			//observateur.newProducteur(p[i]);
			if(impression == 1){
				System.out.println("Init : producteur : " + p[i].identification() + " -> NbrAProduire : " + p[i].GetNbMsg());
			}
			p[i].start();
		}
				
		for(int j=0; j<nbCons; j++) { 
			c[j] = new Consommateur(observateur, m_observator, tempsMoyenConsommation, deviationTempsMoyenConsommation, buffer, aleaCons, impression);
			m_observator.newConsommateur(c[j]);
			//observateur.newConsommateur(c[j]);
			if(impression == 1){
				System.out.println("Init : consommateur : " + c[j].identification());
			}
			c[j].start();
		}
		for(int i=0; i<nbProd; i++) { 
			p[i].join();
		}
				
		for(int j=0; j<nbCons; j++) { 
			c[j].join();
		}
		System.out.println("Fin Simulation");
		if(m_observator.isCoherent(false)) {
			System.out.println("La simulation est coh�rente");
		} else {
			System.out.println("La simulation n'est pas coh�rente");
			m_observator.isCoherent(true);
		}
		if(m_observator.isFamine(false)) {
			System.out.println("La simulation comporte de la famine");
			m_observator.isFamine(true);
		} else {
			System.out.println("La simulation ne comporte pas de famine");
		}
		//m_observator.displayConsommateur();
}


	/** Main permettant d'executer le programme
	 */
	public static void main(String[] args){
		new TestProdCons(new Observateur()).start();
	}
}
