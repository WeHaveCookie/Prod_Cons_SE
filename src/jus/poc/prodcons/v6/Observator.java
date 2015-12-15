package jus.poc.prodcons.v6;

import jus.poc.prodcons.ControlException;
import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;


public final class Observator {
	
	public Observator() {
		m_controleur = new Controleur();
		m_controlClass = m_controleur.toString();
		m_operationnel = true;
	}
	
	public final boolean coherent() {
		return m_coherent;
	}
	
	public void consommationMessage(_Consommateur c, Message m, int tpsTrait) {
		if(m_operationnel) {
			m_controleur.consommationMessage(c,m,tpsTrait);
		}
	}
	
	public final void depotMessage(_Producteur p, Message m) {
		if(m_operationnel) {
			m_controleur.depotMessage(p,m);
		}
	}
	
	public final void init(int nbproducteurs, int nbconsommateurs, int nbBuffers) throws ControlException {
		try {
			m_controleur.init(nbproducteurs, nbconsommateurs, nbBuffers);
			m_coherent = true;
		} catch (ControlException e) {
			throw e;
		}
		
	}
	
	public final void initProd(_Producteur p, int nbMsg) {
		if(m_operationnel) {
			m_controleur.initProd(p, nbMsg);
		}
	}
	
	public final void newConsommateur(_Consommateur c) {
		if(m_operationnel) {
			m_controleur.newConsommateur(c);
		}
	}

	public final void newProducteur(_Producteur p) {
		if(m_operationnel) {
			m_controleur.newProducteur(p);
		}
	}
	
	public final void productionMessage(_Producteur p, Message m, int tpsTrait) {
		if(m_operationnel) {	
			m_controleur.productionMessage(p,m,tpsTrait);
		}
	}
	
	public final void retraitMessage(_Consommateur c, Message m) {
		if(m_operationnel) {
			m_controleur.retraitMessage(c,m);
		}
	}
	
	public final void setOp(boolean b) {
		m_operationnel = b;
	}
	
	public final void displayConsommateur() {
		if(m_operationnel) {
			m_controleur.displayConsommateur();
		}
	}
	
	public final boolean isCoherent(boolean display) {
		if(m_operationnel) {
			return m_controleur.isCoherent(display);
		}
		return false;
	}
	
	public final boolean isFamine(boolean display) {
		if(m_operationnel){
			return m_controleur.isFamine(display);
		}
		return false;
	}
	
	private boolean m_coherent;
	private static String m_controlClass;
	private Controleur m_controleur;
	private boolean m_operationnel;

	
}
