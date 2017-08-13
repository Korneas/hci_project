import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PVector;

public class Cabra extends Thread {
	public int energia;
	private boolean conVida = true;
	private int time = 0;
	private int timeGoat, timeOrg;

	private PApplet p;
	private PVector location;
	private PVector velocity;
	private PVector acceleration;
	private PVector direccion;
	private float magni;

	private float topspeed;
	private int cambio;
	private int t, r, g, b;
	private LinkedList<ColaDeCabra> cola = new LinkedList<ColaDeCabra>();

	public Cabra(PApplet p) {
		energia = 150;
		conVida = true;
		this.p = p;
		location = new PVector(p.width / 2, p.height / 2);
		velocity = new PVector(0, 0);
		direccion = new PVector(p.random(p.width), p.random(p.height));
		topspeed = (int) p.random(1, 5);
		cambio = (int) p.random(10, 100);

		this.r = 255;
		this.g = 250;
		this.b = 240;
	}

	public void setEnergia(int energiaDelPotrero) {
		energia += energiaDelPotrero;
		if (energia > 150) {
			energia = 150;
		}
	}

	public void existir() {
		// Cada 2 segundos debe consumir 2 unidades de energia
		// Si el nivel de energia es <= 0 debe invocar el metodo morir()
		if (timeGoat != time) {
			timeGoat = time;
			timeOrg++;
			// System.out.println(timeOrg);
			if (timeOrg % 2 == 0 && timeOrg != 0) {
				energia -= 2;
			}

		}

		if (energia <= 0) {
			morir();
		}
	}

	public void morir() {
		conVida = false;
		System.out.println("Finish Him!");
	}

	private class ColaDeCabra {

		private float x, y, d;
		private int r, g, b;

		public ColaDeCabra(float x, float y, float d, int r, int g, int b) {
			this.x = x;
			this.y = y;
			this.d = d;

			this.r = r;
			this.g = g;
			this.b = b;
		}

		public void pintar() {
			p.noStroke();
			p.fill(r, g, b, t);
			p.ellipse(x, y, d, d);
		}

		public void encoger() {

			if (p.frameCount % 1 == 0) {
				this.d -= 1;
			}
			// this.t -= 5;
			t = 100;
		}

	}

	public void run() {
		while (true) {

			try {
				update();
				sleep(25);
			} catch (Exception e) {
			}
		}
	}

	void update() {

		// Compute a vector that points from location to mouse

		if (p.frameCount % cambio == 0) {
			direccion = new PVector(p.random(p.width), p.random(p.height));
		}

		acceleration = PVector.sub(direccion, location);
		// Set magnitude of acceleration
		magni = PApplet.map(energia, 0, 150, 0, (float) 0.5);
		acceleration.setMag(magni);
		// Velocity changes according to acceleration
		velocity.add(acceleration);
		// Limit the velocity by topspeed
		velocity.limit(topspeed);
		// Location changes by velocity
		location.add(velocity);
	}

	public float getX() {
		return location.x;
	}

	public float getY() {
		return location.y;
	}

	void display() {
		p.noStroke();
		p.fill(r, g, b, 100);
		p.ellipse(location.x, location.y, 30, 30);

		if (p.frameCount % 1 == 0) {
			cola.add(new ColaDeCabra(getX(), getY(), 30, r, g, b));
		}
		if (cola.size() > 30) {
			cola.removeFirst();
		}

		for (ColaDeCabra e : cola) {
			e.pintar();
			e.encoger();
		}
	}

	//// ****** Getters & Setters

	/**
	 * La cabra vive?
	 * 
	 * @return false si esta muerta
	 */
	public boolean isConVida() {
		return conVida;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
