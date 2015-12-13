package jus.poc.prodcons.v6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

import jus.poc.prodcons.Acteur;
import jus.poc.prodcons._Acteur;
import jus.poc.prodcons._Consommateur;
import jus.poc.prodcons._Producteur;


public class Observation {
	
	public enum Type {
		Consommateur,
		Producteur
	}

	public class Actor{
		
		public Actor(Type type, int id) {
			m_type = type;
			m_id = id;
			m_nbrServed = 0;
		}
		
		public Type getType() {
			return m_type;
		}
		
		public int getId() {
			return m_id;
		}
		
		public void serve() {
			m_nbrServed++;
		}
		
		public int getServ() {
			return m_nbrServed;
		}
		
		private Type m_type;
		private int m_id;
		private int m_nbrServed;
	}
	
	public Observation() {
		m_queue = new PriorityQueue<Observation.Actor>();
	}
	
	
	public void addToQueue(Producteur prod) {
		if(prod != null){
			try{
				m_queue.add(new Actor(Type.Producteur,prod.identification()));
			} catch (IllegalStateException e) {
				e.getMessage();
			}
		}
	}
	
	public void addToQueue(Consommateur cons){
		if(cons != null){
			try{
				m_queue.add(new Actor(Type.Consommateur,cons.identification()));
			} catch (IllegalStateException e) {
				e.getMessage();
			}
		}
	}
	
	public void serve() {
		try {
			Actor act = m_queue.remove();
			if (m_actors.putIfAbsent(act, 1) != null ) {
				m_actors.put(act,m_actors.get(act)+1);
			}
		} catch (NoSuchElementException e){
			e.printStackTrace();
		}
	}
	
	public void display() {
		System.out.println("------------- OBSERVATION ------------");
		for(Entry<Actor,Integer> entry : m_actors.entrySet()) {
			if(entry.getKey().getType() == Type.Consommateur) {
				System.out.println("Consommateur : " + entry.getKey() + "A été servie : " + entry.getValue() + " fois");
			} else {
				System.out.println("Producteur : " + entry.getKey() + "A pu ecrire : " + entry.getValue() + " fois");
			}
		}
		System.out.println("----------------------------------------");
	}
	
	private Queue<Actor> m_queue;
	private HashMap<Actor,Integer> m_actors;
}
