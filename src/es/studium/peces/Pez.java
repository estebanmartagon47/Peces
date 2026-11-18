package es.studium.peces;

/**
 * Datos comunes a todos los peces (el del jugador y los enemigos).
 * Varios hilos leen y modifican estos datos a la vez (el hilo de cada pez,
 * el bucle del juego y el hilo de Swing que lo dibuja), por eso todos los
 * métodos que tocan los atributos son synchronized.
 */
public class Pez
{
	protected double x; // Esquina superior izquierda
	protected double y;
	protected int tamano;
	protected int direccion; // 1 = mira a la derecha, -1 = mira a la izquierda

	public Pez(double x, double y, int tamano, int direccion)
	{
		this.x = x;
		this.y = y;
		this.tamano = tamano;
		this.direccion = direccion;
	}

	/** El dibujo del pez crece con su tamaño. */
	public static int anchoPara(int tamano)
	{
		return 24 + tamano * 3;
	}

	public static int altoPara(int tamano)
	{
		return (int) (anchoPara(tamano) * 0.6);
	}

	public synchronized double getX()
	{
		return x;
	}

	public synchronized double getY()
	{
		return y;
	}

	public synchronized int getTamano()
	{
		return tamano;
	}

	public synchronized int getDireccion()
	{
		return direccion;
	}

	public synchronized int getAncho()
	{
		return anchoPara(tamano);
	}

	public synchronized int getAlto()
	{
		return altoPara(tamano);
	}
}
