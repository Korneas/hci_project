import java.util.ArrayList;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import sound.Reproductor;

public class Potrero {
	private int energia;
	public PApplet app;
	private ArrayList<Cabra> cabras;
	private int cabrasFallecidas = 0;
	private int consumoEnergiaCabra = 15;
	private boolean inicializado = false;
	private int time2;
	private boolean seg, seg2, seg3;
	private int num_pastos;

	private LinkedList<Pasto> pastos;
	private int var = 10000;
	private int ranTime;

	private Reproductor r;

	public Potrero(PApplet app) {
		this.app = app;
		cabras = new ArrayList<Cabra>();
		pastos = new LinkedList<Pasto>();
		r = new Reproductor(app);
	}

	/**
	 * el potrero se pobla con un 5 cabras. Se invoca UNA SOLA VEZ
	 */
	public void init() {
		if (!inicializado) {
			energia = var;
			for (int i = 0; i < 5; i++) {
				cabras.add(new Cabra(app));
				cabras.get(i).start();
			}

			num_pastos = (int) PApplet.map(energia, 0, 10000, 0, 800);
			for (int i = 0; i < num_pastos; i++) {
				int x = (int) app.random(0, app.width);
				int y = (int) app.random(0, app.height);
				int radio = (int) app.random(10, 30);
				pastos.add(new Pasto(x, y, radio));
			}
			inicializado = true;
		}
	}

	public void administrarCabra(int code) {
		if (code == PConstants.UP) {
			addCabra();
		} else if (code == PConstants.DOWN) {
			removeCabra();
		}
	}

	/**
	 * Adiciona una cabra
	 */
	public void addCabra() {
		cabras.add(new Cabra(app));
		cabras.get(cabras.size() - 1).start();
	}

	/**
	 * Remueve la cabra mas vieja
	 */
	public void removeCabra() {
		cabras.get(0).interrupt();
		cabras.remove(cabras.get(0));
		cabrasFallecidas++;
		r.reproducir_sample(1);
	}

	/**
	 * Este metodo se debe usar cuando una cabra muere de hambre
	 * 
	 * @param cabra
	 *            la cabra que debe removerse del arreglo
	 */
	public void removeCabra(Cabra cabra) {
		// Remover la cabra que recibe como parametro del arreglo
		cabra.interrupt();
		cabras.remove(cabra);
		cabrasFallecidas++;
		r.reproducir_sample(1);
	}

	/**
	 * Este metodo recibe la cantidad de segundos que ha transcurrido desde que
	 * inició el juego (no desde que inicio la aplicacion). Cada 10 segundos la
	 * energía del potrero disminuye en x unidades por cada cabra que haya en el
	 * potrero. Las unidades estan definidas en la variable consumoEnergíaCabra
	 * 
	 * @param time
	 */
	public void actualizarEnergia(int time) {
//		if (time % 10 == 0) {
//			energia -= cabras.size() * consumoEnergiaCabra;
//			if (energia < 0) {
//				energia = 0;
//			}
//		}
	}

	public void updateGoat(int time) {

		if (time2 != time) {
			seg2 = true;
			seg = true;
			seg3 = true;
			time2 = time;
		}

		for (int i = 0; i < cabras.size(); i++) {
			cabras.get(i).setTime(time);
			cabras.get(i).existir();
			cabras.get(i).display();

			if (!cabras.get(i).isConVida()) {
				removeCabra(cabras.get(i));
			}

			if (seg) {
				if (energia >= consumoEnergiaCabra) {
					cabras.get(i).setEnergia(consumoEnergiaCabra);
					energia -= consumoEnergiaCabra;
				}

				if (cabras.get(i) == cabras.get(cabras.size() - 1)) {
					seg = false;
				}
			}
		}

		if (cabras.size() > 0) {
			if (time % 5 == 0 && seg3) {
				ranTime = (int) app.random(time, time + 5);
				seg3 = false;
			}

			if (ranTime == time) {
				r.reproducir_sample(0);
				ranTime = 0;
				System.out.println("Grito " + ranTime + ":" + time);
			}
		}
	}

	public void updateGrass() {
		for (Pasto pasto : pastos) {
			pasto.pintar();
			pasto.viento();
		}
	}

	/**
	 * Este método debe tomar la lectura de la fotocelda y acumularla en una
	 * variable. La acumulación se hace una vez por segundo. El potrero no tiene
	 * límite de acumulación de energía.
	 */
	public void addEnergia(int energy) {
		if (seg2) {
			energy = (int) PApplet.map(energy, 0, 1024, 0, 1000);
			energia += energy;
			System.out.println("Se adiciono: " + energia);
			seg2 = false;
		}
	}

	//// ***** GETTERS & SETTERS ******
	public int getTotalEnergia() {
		return energia;
	}

	public int getNumeroCabras() {
		return cabras.size();
	}

	public int getNumeroCabrasFallecidas() {
		return cabrasFallecidas;
	}

	// PASTO

	private class Pasto {

		private int t, x, y, radio, r, g, b;
		private double onda, indice, amplitud, suma;
		private boolean muerto;

		public Pasto(int x, int y, int radio) {
			this.x = x;
			this.y = y;
			this.radio = radio;
			r = 35;
			g = 200;
			b = 30;
			t = 100;
			amplitud = app.random(3, 10);
			suma = app.random((float) 0.01, (float) 0.1);
			indice = 0.1;
			muerto = false;
		}

		public void desvanecer(int t) {
			this.t = t;
		}

		public void agotar(int gasto) {
			if (muerto == false) {
				this.radio -= gasto;
				if (radio <= 5) {
					muerto = true;
				}
			}
		}

		public boolean isMuerto() {
			return muerto;
		}

		public void viento() {
			indice += suma;
			onda = (float) PApplet.sin((float) indice) * amplitud;
		}

		public void pintar() {
			app.fill(r, g, b, t);
			app.noStroke();
			app.ellipse((float) (x + onda), y, radio / 2 + energia / 1000, radio / 2 + energia / 1000);
		}

	}

}
