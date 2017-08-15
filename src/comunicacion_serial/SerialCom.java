package comunicacion_serial;

import java.util.Observable;
import java.util.Scanner;

import com.fazecast.jSerialComm.SerialPort;

public class SerialCom extends Observable implements Runnable {

	public static SerialCom ref = null;

	private int i = 1;
	private SerialPort[] ports = SerialPort.getCommPorts();
	private static Scanner s;
	private SerialPort port;
	private long promedio;
	private int indice = 0;
	private int[] muestras;
	private long sumatoria;
	private int var;

	private SerialCom() {
		System.out.println("Select a port:");
		for (SerialPort port : ports) {
			System.out.println(i++ + ": " + port.getSystemPortName());
		}
		port = ports[3];

		if (port.openPort()) {
			// System.out.println("start");
		}

		port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		s = new Scanner(port.getInputStream());

		promedio = 0;
		indice = 0;
		muestras = new int[10];
		sumatoria = 0;
	}

	public static SerialCom getRef() {
		if (ref == null) {
			ref = new SerialCom();
			return ref;
		}
		return ref;
	}

	public long filtro(int muestra) {

		if (indice > muestras.length - 1) {
			sumatoria = muestras[0] + muestras[1] + muestras[2] + muestras[3] + muestras[4] + muestras[5] + muestras[6]
					+ muestras[7] + muestras[8] + muestras[9];
			promedio = sumatoria / muestras.length;
			indice = 0;
			return promedio;
		}

		muestras[indice] = muestra;
		indice++;
		return 0;
	}

	@Override
	public void run() {
		while (true) {
			while (s.hasNextLine()) {
				try {
					var = (int) Float.parseFloat(s.nextLine());
					if (var != 0) {
						setChanged();
						notifyObservers(var);
						clearChanged();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
