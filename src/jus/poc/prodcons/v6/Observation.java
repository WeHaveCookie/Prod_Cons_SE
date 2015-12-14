package jus.poc.prodcons.v6;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;


public class Observation{
	
	public Observation() {
		m_coherent = true;
		m_famine = false;
		m_production = true;
		m_backtrack = new PriorityQueue<Message>();
		m_listFamineCons = new ArrayList<Integer>(TestProdCons.nbCons);
		for(int i = 0; i < TestProdCons.nbCons; i++) {
			m_listFamineCons.add(0);
		}
		m_listFamineProd = new ArrayList<Integer>(TestProdCons.nbProd);
		for(int i = 0; i < TestProdCons.nbProd; i++) {
			m_listFamineProd.add(0);
		}
	}
	
	public void initProd(int id, int nbMsg) {
		m_listFamineProd.set(id-1, nbMsg);
	}
	
	
	public void depotMessage(_Producteur prod, Message msg) {
		m_prod = prod;
		m_msg = msg;
		m_backtrack.add(m_msg);
		try {
			m_listFamineProd.set(m_prod.identification()-1, m_listFamineProd.get(m_prod.identification()-1)-1);
		} catch (IndexOutOfBoundsException e) {
			m_coherent = false;
			m_prod = prod;
			e.printStackTrace();
		}
		
	}
	
	public void retraitMessage(_Consommateur cons, Message msg) {
		if(msg.equals(m_backtrack.peek())) {
			m_cons = cons;
			m_backtrack.poll();
			try {
				m_listFamineCons.set(m_cons.identification()-1, m_listFamineCons.get(m_cons.identification()-1)+1);
			} catch (IndexOutOfBoundsException e) {
				m_coherent = false;
				e.printStackTrace();
			}
		} else {
			m_coherent = false;
			m_cons = cons;
			m_msg = msg;
		}
	}
	
	public boolean isCoherent(boolean display) {
		for(int i = 0; i<m_listFamineProd.size();i++) {
			if(m_listFamineProd.get(i) != 0) {
				m_production = false;
			}
			if(display){
				System.out.println("Il reste " + m_listFamineProd.get(i) + " Message au producteur : " + i );	
			}
		}
		if(display) {
			if (!m_coherent) {
				System.out.println("Le msg " + m_msg + " posé par le producteur : " + m_prod.identification() + " n'est pas le même que celui retirer par le consomateur " + m_cons.identification());
			}
			if(m_backtrack.isEmpty()) {
				System.out.println("Il reste : " + m_backtrack.size() + " non consommé");
			}
		}
		return m_coherent && m_backtrack.isEmpty() && m_production;
	}
	
	public boolean famine(boolean display) {
		if (display) {
			for(int i = 0; i <m_listFamineCons.size(); i++) {
				if(m_listFamineCons.get(i) == 0) {
					System.out.println("Le consommateur : " + i + " n'a rien pu consommer ");
				}
			}
		}
		m_famine = (m_listFamineCons.contains(0)) || (m_listFamineProd.contains(0));
		return m_famine;
	}
	

	private boolean m_famine;
	private boolean m_coherent;
	private boolean m_production;
	private Message m_msg;
	private _Producteur m_prod;
	private _Consommateur m_cons;
	private ArrayList<Integer> m_listFamineCons;
	private ArrayList<Integer> m_listFamineProd;
	private Queue<Message> m_backtrack;
}
