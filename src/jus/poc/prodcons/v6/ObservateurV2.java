package jus.poc.prodcons.v6;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;


public class ObservateurV2 {
	
	public ObservateurV2() {
		m_controleur = new Controleur();
		m_controlClass = m_controleur.toString();
		m_operationnel = true;
	}
	
	public boolean coherent() {
		return m_coherent;
	}
	
	public void consommationMessage(_Consommateur c, Message m, int tpsTrait) {
		if(m_operationnel) {
			m_controleur.consommationMessage(c,m,tpsTrait);
		}
	}
	
	public void depotMessage(_Producteur p, Message m) {
		if(m_operationnel) {
			m_controleur.depotMessage(p,m);
		}
	}
	
	public void init(int nbproducteurs, int nbconsommateurs, int nbBuffers) {
		m_controleur.init(nbproducteurs, nbconsommateurs, nbBuffers);
		m_coherent = true;
	}
	
	public void initProd(_Producteur p, int nbMsg) {
		if(m_operationnel) {
			m_controleur.initProd(p, nbMsg);
		}
	}
	
	public void newConsommateur(_Consommateur c) {
		if(m_operationnel) {
			m_controleur.newConsommateur(c);
		}
	}

	public void newProducteur(_Producteur p) {
		if(m_operationnel) {
			m_controleur.newProducteur(p);
		}
	}
	
	public void productionMessage(_Producteur p, Message m, int tpsTrait) {
		if(m_operationnel) {	
			m_controleur.productionMessage(p,m,tpsTrait);
		}
	}
	
	public void retraitMessage(_Consommateur c, Message m) {
		if(m_operationnel) {
			m_controleur.retraitMessage(c,m);
		}
	}
	
	public void setOp(boolean b) {
		m_operationnel = b;
	}
	
	public void displayConsommateur() {
		if(m_operationnel) {
			m_controleur.displayConsommateur();
		}
	}
	
	public boolean isCoherent(boolean display) {
		if(m_operationnel) {
			return m_controleur.isCoherent(display);
		}
		return false;
	}
	
	public boolean isFamine(boolean display) {
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
