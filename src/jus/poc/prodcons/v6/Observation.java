package jus.poc.prodcons.v6;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

import jus.poc.prodcons.Acteur;


public class Observation {
	
	public class Actor {
		private Acteur m_acteur;
		private int m_nbServed;
		
		public Actor(Acteur act) {
			m_acteur = act;
			m_nbServed = 0;
		}
		
		public void served() {
			m_nbServed++;
		}
		
		public Acteur getActeur(){
			return m_acteur;
		}
		
		public int getNbServed() {
			return m_nbServed;
		}
	}
	
	public Observation() {
		m_queue = new PriorityQueue<Actor>();
		m_actors = new HashMap<Actor, Actor>();
	}
	
	
	public void addToQueue(Acteur act){
		if(act != null) {
			try {
				m_queue.add(new Actor(act));
			} catch (IllegalStateException e) {
				e.getMessage();
			}
		}
	}
	public void serve() {
		try {
			Actor act = m_queue.remove();
			if (m_actors.putIfAbsent(act, act) != null ) {
				act.served();
				m_actors.put(act,act);
			}
		} catch (NoSuchElementException e){
			e.printStackTrace();
		}
	}
	
	public void display() {
		System.out.println("------------- OBSERVATION ------------");
		for(Entry<Actor,Actor> entry : m_actors.entrySet()) {
			if(entry.getKey().getActeur() instanceof Consommateur) {
				System.out.println("Consommateur : " + entry.getKey().getActeur().identification() + "A été servie : " + entry.getValue().getNbServed() + " fois");
			} else {
				System.out.println("Producteur : " + entry.getKey().getActeur().identification() + "A pu ecrire : " + entry.getValue().getNbServed() + " fois");
			}
		}
		System.out.println("----------------------------------------");
	}
	
	private Queue<Actor> m_queue;
	private HashMap<Actor,Actor> m_actors;
}
