package jus.poc.prodcons.v5;

public class Semaphore {

	
	private int residu;

	public Semaphore(int initialisation) {
		residu = initialisation;
	}
	
	
	/** Methode ATTENDRE
	 */
	public synchronized void p() throws InterruptedException
	{
		while(residu<=0){
			wait();
		}
		residu--;
	}

	
	/** Methode REVEILLER
	 */
	public synchronized void v()
	{
		residu++;
		notify();
	}



}