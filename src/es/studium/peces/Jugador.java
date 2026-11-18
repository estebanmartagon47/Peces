package es.studium.peces;

/**
 * El pez que maneja el usuario. No tiene hilo propio: lo mueve el bucle
 * del juego según las teclas pulsadas o la posición del ratón.
 */
public class Jugador extends Pez
{
	public static final double VELOCIDAD = 5; // Píxeles por fotograma

	public Jugador(double x, double y, int tamano)
	{
		super(x, y, tamano, 1);
	}

	/** Desplaza el pez sin dejar que se salga del acuario. */
	public synchronized void mover(double dx, double dy, int anchoArea, int altoArea)
	{
		x = limitar(x + dx, 0, anchoArea - getAncho());
		y = limitar(y + dy, 0, altoArea - getAlto());
		if (dx > 0)
		{
			direccion = 1;
		}
		else if (dx < 0)
		{
			direccion = -1;
		}
	}

	/** Aumenta el tamaño manteniendo el pez centrado en el mismo punto. */
	public synchronized void crecer(int cantidad, int anchoArea, int altoArea)
	{
		double centroX = x + getAncho() / 2.0;
		double centroY = y + getAlto() / 2.0;
		tamano += cantidad;
		x = limitar(centroX - getAncho() / 2.0, 0, anchoArea - getAncho());
		y = limitar(centroY - getAlto() / 2.0, 0, altoArea - getAlto());
	}

	private static double limitar(double valor, double minimo, double maximo)
	{
		return Math.max(minimo, Math.min(valor, maximo));
	}
}
