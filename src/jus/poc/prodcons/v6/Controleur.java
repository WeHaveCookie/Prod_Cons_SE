package jus.poc.prodcons.v6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import jus.poc.prodcons.Message;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;

public class Controleur {
	
	class ExceptionSimulation extends Exception {
		private static final long serialVersionUID = 1L;

		public ExceptionSimulation() {
			super();
		}
		
		public void display(){
			
		}
	}
	
	class ExceptionDepot extends ExceptionSimulation {
		private static final long serialVersionUID = 1L;
		
		public ExceptionDepot(_Producteur p, Message m){
			super();
			m_prod = p;
			m_msg = m;
		}
		
		public void display() {
			System.out.println("*******************");
			System.out.println("| Erreur de dépot |");
			System.out.println("*******************");
			System.out.println("Le producteur " + m_prod.identification() + " n'a pas pu deposer le message " + m_msg);
		}
		
		private _Producteur m_prod;
		private Message m_msg;
	}
	
	 class ExceptionRetrait extends ExceptionSimulation {
		private static final long serialVersionUID = 1L;
		
		public ExceptionRetrait(_Consommateur c, Message m) {
			super();
			m_cons = c;
			m_msg = m;
		}
		
		public void display() {
			System.out.println("*********************");
			System.out.println("| Erreur de retrait |");
			System.out.println("*********************");
			System.out.println("Le consommateur " + m_cons.identification() + " a tente de retirer le message " + m_msg + " alors que celui n'existait pas dans le tampon");
		}
		
		private _Consommateur m_cons;
		private Message m_msg;
		 
	 }
	 
	 class ExceptionSizeBuffer extends ExceptionSimulation {
		private static final long serialVersionUID = 1L;
		
		public ExceptionSizeBuffer(_Producteur p, Message m) {
			 super();
			 m_prod = p;
			 m_msg = m;
		 }
		 
		 public void display() {
			System.out.println("******************************");
			System.out.println("| Erreur de taille de buffer |");
			System.out.println("******************************");	
			System.out.println("Le producteur " + m_prod.identification() + " a tente de deposer le message " + m_msg + " alors que le buffer ete plein");
		 }
		 
		 private _Producteur m_prod;
		 private Message m_msg;
	 }
	
	public Controleur() {
		m_famine = false;
		m_coherent = true;
		m_production = true;
		
		m_backtrack = new LinkedBlockingQueue<Message>();
		m_listFamineProds = new ArrayList<Integer>();
		for(int i = 0; i < TestProdCons.nbProd; i++) {
			m_listFamineProds.add(0);
		}
		m_prods = new HashMap<_Producteur, ArrayList<Message>>();
		m_cons = new HashMap<_Consommateur, ArrayList<Message>>();
		m_listException = new ArrayList<ExceptionSimulation>();
	}
	
	public void consommationMessage(_Consommateur c, Message m, int tpsTrait) {
			m_cons.get(c).add(m);
	}

	public void depotMessage(_Producteur p, Message m) {
		try {
			m_backtrack.put(m);
			//System.out.println("Dans Prod : on ajoute Msg : " + m + " dans la queue de taille : " + m_backtrack.size() + " sur " + m_nbBuffers );
			if(m_backtrack.size() > m_nbBuffers) {
				throw new ExceptionSizeBuffer(p,m);
			}
			if(!m_prods.get(p).remove(m)){
				throw new ExceptionDepot(p,m);
			}
			m_listFamineProds.set(p.identification()-1, m_listFamineProds.get(p.identification()-1)-1);
		} catch (ExceptionDepot e) {
			m_listException.add(e);
			m_production = false;
		} catch (ExceptionSizeBuffer e){
			m_listException.add(e);
		} catch (IllegalStateException e){
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void init(int nbProd, int nbCons, int nbBuffers) {
		m_nbProd = nbProd;
		m_nbCons = nbCons;
		m_nbBuffers = nbBuffers;
	}

	public void initProd(_Producteur p, int nbMsg) {
		m_listFamineProds.set(p.identification()-1, nbMsg);
	}
	
	public void newConsommateur(_Consommateur c) {
		m_cons.put(c, new ArrayList<Message>());
	}

	public void newProducteur(_Producteur p) {
		m_prods.put(p, new ArrayList<Message>());
	}

	public void productionMessage(_Producteur p, Message m, int tpsTrait) {		
		m_prods.get(p).add(m);
	}

	public void retraitMessage(_Consommateur c, Message m) {
		try {
			if(m_backtrack.poll() == null) {
				throw new ExceptionRetrait(c, m);
			}
		} catch(ExceptionRetrait e) {
			m_listException.add(e);
			m_coherent = false;
		}
	}

	
	public void displayConsommateur() {
		for(Entry<_Consommateur,ArrayList<Message>> entry : m_cons.entrySet()) {
			System.out.println("Le consommateur " + entry.getKey().identification() + " a consomme les " + entry.getValue().size() + "  messages suivant :");
			System.out.println("************************************************");
			for(int i = 0; i < entry.getValue().size(); i++) {
				System.out.println(entry.getValue().get(i));
				System.out.println("------------------------------------------------");
			}
		}
	}
	
	public boolean isFamine(boolean display) {
		for(Entry<_Consommateur, ArrayList<Message>> entry : m_cons.entrySet()) {
			if(entry.getValue().isEmpty()) {
				m_famine = true;
				if(display) {
					System.out.println("Le consommateur : " + entry.getKey().identification() + " n'a rien pu consommer");
				}
			}
		}
		for(Entry<_Producteur, ArrayList<Message>> entry : m_prods.entrySet()) {
			if(!entry.getValue().isEmpty()) {
				m_famine = true;
				if(display) {
					System.out.println("Le producteur : " + entry.getKey().identification() + " n'a pas pu déposer " + entry.getValue().size() + " messages");
				}
			}
		}
		return m_famine;
	}
	
	public boolean isCoherent(boolean display) {
		
		for(int i = 0; i < m_listFamineProds.size(); i++){
			if(m_listFamineProds.get(i) != 0) {
				if(display) {
					System.out.println("Le producteur " + i + " n'a pas pu deposer tout les messages qu'il devait deposer. Il en reste " + m_listFamineProds.get(i));
				}
				m_production = false;
			}
		}
		
		if(m_cons.size() != m_nbCons) {
			if(display) {
				System.out.println("La simulation attendait " + m_nbCons + " consommateur et seulement " + m_cons.size() + " on ete creer");
			}
		}
		if(m_prods.size() != m_nbProd) {
			if(display) {
				System.out.println("La simulation attendait" + m_nbProd + " producteur et seulement " + m_prods.size() + " on ete creer");
			}
		}
		for(Entry<_Producteur, ArrayList<Message>> entry : m_prods.entrySet()) {
			if(!entry.getValue().isEmpty()) {
				m_production = false;
				if(display) {
					System.out.println("Le producteur " + entry.getKey().identification() + " n'a pas depose " + entry.getValue().size() + " messages dans le buffer");
				}
			}
		}
		if(!m_listException.isEmpty()) {
			if(display) {
				for(int i = 0; i < m_listException.size(); i++) {
					m_listException.get(i).display();
				}
			}
		}
		if(!m_backtrack.isEmpty()) {
			if(display) {
				System.out.println("Il reste " + m_backtrack.size() + " dans le buffer");
			}
		}
		
		return m_coherent && m_backtrack.isEmpty() && m_production && m_listException.isEmpty() && (m_cons.size() == m_nbCons) && (m_prods.size() == m_nbProd);
	}
	
	private boolean m_famine;
	private boolean m_coherent;
	private boolean m_production;
	private int m_nbProd;
	private int m_nbCons;
	private int m_nbBuffers;
	
	private LinkedBlockingQueue<Message> m_backtrack;
	private ArrayList<Integer> m_listFamineProds;
	private HashMap<_Producteur,ArrayList<Message>> m_prods;
	private HashMap<_Consommateur,ArrayList<Message>> m_cons;
	private ArrayList<ExceptionSimulation> m_listException;
}
