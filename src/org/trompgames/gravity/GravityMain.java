package org.trompgames.gravity;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GravityMain {

	
	public static void main(String[] args) {
		new GameHandler();
	}
	
	public static class Planet{
		
		private Color color;
		
		private double mass;
		private double radius;
		
		private Vector2 location;
		
		private Vector2 velocity = new Vector2(0, 0);
		private Vector2 acceleration = new Vector2(0, 0);
		
		private Planet(Vector2 loacation, double mass, double radius, Color color){
			this.location = loacation;
			this.mass = mass;
			this.radius = radius;
			this.color = color;
		}
		
		private Planet(Vector2 location, double mass, double radius, Vector2 velocity, Color color){
			this.location = location;
			this.mass = mass;
			this.radius = radius;
			this.velocity = velocity;
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public Vector2 getLocation() {
			return location;
		}

		public void setLocation(Vector2 location) {
			this.location = location;
		}

		public double getMass() {
			return mass;
		}

		public void setMass(double mass) {
			this.mass = mass;
		}

		public double getRadius() {
			return radius;
		}

		public void setRadius(double radius) {
			this.radius = radius;
		}

		public Vector2 getVelocity() {
			return velocity;
		}

		public void setVelocity(Vector2 velocity) {
			this.velocity = velocity;
		}

		public Vector2 getAcceleration() {
			return acceleration;
		}

		public void setAcceleration(Vector2 acceleration) {
			this.acceleration = acceleration;
		}
		
		
		
	}
	
	
	public static class GameHandler{
		
		private GameFrame frame;
		private GameThread thread;
		
		//public static double GCONST = 0.0000000000667;
		public static double GCONST = 100;

		
		private ArrayList<Planet> planets = new ArrayList<Planet>();
		
		public GameHandler(){
			frame = new GameFrame(this);
			((Thread)(thread = new GameThread(this))).start();
			//planets.add(new Planet(new Vector2(500, 500), .59720, 10, new Vector2(0, 0), Color.red));//Mass *10^25
			//planets.add(new Planet(new Vector2(500, 600), .00734, 10, new Vector2(750, 0), Color.blue));
			
			//planets.add(new Planet(new Vector2(500, 400), 1000000, 25, new Vector2(0, 0), Color.red));
			
			
			planets.add(new Planet(new Vector2(500, 300), 1500, 1500/10, new Vector2(0, 0), Color.red));

			//planets.add(new Planet(new Vector2(500, 600), 100, 10, new Vector2(500, -5), Color.green));//Mass *10^25

			//planets.add(new Planet(new Vector2(500, 650), 25, 5, new Vector2(600, 5), Color.pink));//Mass *10^25

			
			for(int i = 0; i < 500; i++){
				int x = (int) (Math.random() * 20000) - 10000;
				int y = (int) (Math.random() * 20000) - 10000;
				double size = (1.0 * Math.random()*250);
				planets.add(new Planet(new Vector2(x, y), size, (int) (1.0 * size/10), new Vector2(Math.random() * 2000 - 1000, Math.random() * 2000 - 1000), Color.blue));

			}
			
		}
		
		public void update(){
			
			//Physics
			for(int i = planets.size()-1; i >= 0; i--){
				Planet planet = planets.get(i);
				for(int j = planets.size()-1; j >= 0; j--){ 
					Planet other = planets.get(j);
					if(planet.equals(other)) continue;
				
					double dist = Vector2.dist(planet.getLocation(), other.getLocation());
					if(dist < planet.getRadius() + other.getRadius()){
						Planet larger;
						Planet smaller;
						if(planet.getMass() > other.getMass()){
							larger = planet;
							smaller = other;
						}
						else{
							smaller = planet;
							larger = other;
						}
						
						larger.setMass(larger.getMass()+smaller.getMass());
						larger.setRadius(larger.getRadius() + smaller.getRadius());
						
						larger.getVelocity().setX((larger.getVelocity().getX()*larger.getMass() + smaller.getVelocity().getX()*smaller.getMass())/(larger.getMass() + smaller.getMass()));
						larger.getVelocity().setY((larger.getVelocity().getY()*larger.getMass() + smaller.getVelocity().getY()*smaller.getMass())/(larger.getMass() + smaller.getMass()));
						planets.remove(smaller);
						continue;
					}
					double force = (1.0 * (GCONST*planet.getMass()*other.getMass())) / (1.0 * Math.pow(dist, 2));
					//if(planet.getColor().equals(Color.blue)) System.out.println("C: " + planet.getColor());
					//if(planet.getColor().equals(Color.blue)) System.out.println("F: " + force);
					
					//Backwards to get normal counter clockwise					
					//double angle = Math.atan2(planet.getLocation().getY() - other.getLocation().getY(), planet.getLocation().getX() - other.getLocation().getX());
					if(other.getLocation().getX() - planet.getLocation().getX() == 0) continue;
					double angle = Math.atan(Math.abs(other.getLocation().getY() - planet.getLocation().getY()) / Math.abs(other.getLocation().getX() - planet.getLocation().getX()));
					//if(angle < 0) angle += Math.PI*2;
					
					double xSign = 1;
					double ySign = 1;
					
					if(other.getLocation().getX() - planet.getLocation().getX() == Math.abs(other.getLocation().getX() - planet.getLocation().getX())) xSign = -1;
					if(other.getLocation().getY() - planet.getLocation().getY() == Math.abs(other.getLocation().getY() - planet.getLocation().getY())) ySign = -1;

					
					double fx = force * Math.cos(angle) * xSign;
					double fy = force * Math.sin(angle) * ySign;
					

					
					//if(planet.getColor().equals(Color.blue)) System.out.println("FX: " + fx);
					//if(planet.getColor().equals(Color.blue)) System.out.println("FY: " + fy);
					
					planet.setAcceleration(planet.getAcceleration().add(new Vector2(-(fx)/planet.getMass(), -(fy)/planet.getMass())));
					
					
					angle = Math.toDegrees(angle);
					if(planet.getColor().equals(Color.blue)){
						
						//System.out.println("Angle: " + angle);
					}
				}
				planet.setVelocity(planet.getVelocity().add(planet.getAcceleration()));
				planet.setAcceleration(new Vector2(0,0));
			}
			
			
			
			//System.out.println("Dist: " + planet.getLocation().dist(other.getLocation()));

			
			
			for(Planet planet : planets){
				Vector2 f = planet.getVelocity().mult(thread.getDeltaTime());			
				
				Vector2 v = planet.getLocation().add(f);
				
				/*
				if(v.getX() < 0){
					v.setX(0);
					planet.getVelocity().setX(0);
				}
				if(v.getX() >= this.getGameFrame().getWidth()-100){
					v.setX(this.getGameFrame().getWidth()-100);
					planet.getVelocity().setX(0);

				}
				if(v.getY() < 0){
					v.setY(0);
					planet.getVelocity().setY(0);

				}
				if(v.getY() >= this.getGameFrame().getHeight()-100){
					v.setY(this.getGameFrame().getHeight()-100);
					planet.getVelocity().setY(0);

				}
				*/
				planet.setLocation(v);
				//System.out.println(v);
				//System.out.println(this.getGameFrame().getHeight());
				
			}
			
			//System.out.println(thread.getDeltaTime());
			
			//Paint canvas
			getGameFrame().getGamePanel().repaint();
		}
		
		public GameFrame getGameFrame(){
			return frame;
		}
		
		public GameThread getGameThread(){
			return thread;
		}
		
		public ArrayList<Planet> getPlanets(){
			return planets;
		}
		
	}
	
	public static class GameFrame extends JFrame{
		
		private GameHandler handler;
		private GamePanel panel;
		
		
		public GameFrame(GameHandler handler){
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setSize(1000, 1000);
			this.add((panel = new GamePanel(handler)));
			this.setVisible(true);	
			this.handler = handler;
		}
		
		public GamePanel getGamePanel(){
			return panel;
		}
		
	}
	
	public static class GamePanel extends JPanel{
		
		private GameHandler handler;
		
		
		public GamePanel(GameHandler handler){
			this.handler = handler;
		}
		
		
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			
			g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
			
			int xOffset = 1000;
			int yOffset = 1000;
			
			double scale = 0.1;
			
			for(int i = 0; i < handler.getPlanets().size(); i++){
				Planet planet = handler.getPlanets().get(i);
				if(planet.getColor().equals(Color.red)){
					//xOffset = (int) -planet.getLocation().getX() + this.getWidth()/2;
					//yOffset = (int) -planet.getLocation().getY() + this.getHeight()/2;
					//System.out.println("True");
				}
			}
			
			for(int i = 0; i < handler.getPlanets().size(); i++){
				Planet planet = handler.getPlanets().get(i);
				Vector2 loc = planet.getLocation();
				Vector2 v  = planet.getVelocity();
				g2d.setColor(planet.getColor());
				
				g2d.fillOval((int) ((loc.getX() - planet.getRadius() + xOffset)*scale), (int) ((loc.getY() - planet.getRadius() + yOffset)*scale), (int) (planet.getRadius()*2*scale), (int) (planet.getRadius()*2*scale));
				
				g2d.drawLine((int) ((loc.getX() + xOffset)*scale), (int) ((loc.getY() + yOffset)*scale), (int) ((loc.getX()+v.getX() + xOffset)*scale), (int) ((loc.getY()+v.getY() + yOffset)*scale));
			}
			
		}
		
	}
	
	
	
	
	public static class GameThread extends Thread{
		
		private GameHandler handler;
		
		private double deltaTime = 0;

		
		public GameThread(GameHandler handler){
			this.handler = handler;
		}
		
		@Override
		public void run(){
			long lastTime = System.currentTimeMillis();
			int count = 0;
			int millCount = 0;
			while(true){
				if((System.currentTimeMillis() - 17 < lastTime)) continue;
				deltaTime = (1.0 * System.currentTimeMillis()-lastTime)/1000;

				handler.update();
				
				count++;
				millCount+=System.currentTimeMillis()-lastTime;
				if(millCount >= 1000){
					//System.out.println("60 Frames in: " + millCount + "ms");
					//System.out.println("Count: " + count);
					count = 0;
					millCount = 0;
				}
				lastTime = System.currentTimeMillis();
			}
		}
		
		public double getDeltaTime(){
			return deltaTime;
		}
		
	}
	
	
	
	
	public static class Vector2{
		
		private double x;
		private double y;
		
		public Vector2(double x, double y){
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}
		
		public Vector2 add(Vector2 v2){
			return new Vector2(x + v2.x, y + v2.y);
		}
		
		public Vector2 mult(double d){
			return new Vector2(x * d, y * d);
		}
		
		public static double dist(Vector2 v1, Vector2 v2){
			return Math.sqrt(Math.pow((v2.x - v1.x), 2) + Math.pow((v2.y - v1.y), 2));
		}
		
		@Override
		public String toString(){
			return "X: " + x + " Y: " + y;
		}
		
	}
	
}
