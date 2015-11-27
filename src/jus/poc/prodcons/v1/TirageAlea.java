package jus.poc.prodcons.v1;

import jus.poc.prodcons.Aleatoire;

public class TirageAlea extends Aleatoire {

	/** Constructor TirageAlea
	 * @param moyenne
	 * @param deviation
	 */
	public TirageAlea(int moyenne, int deviation) {
		super(moyenne, deviation);
	}

	/** Recuperer le prochain nombre aleatoire
	 * 
	 */
	public synchronized int next()
	{
		return super.next();
	}

}
