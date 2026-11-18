package es.studium.peces;

/**
 * Pez controlado por el ordenador. Cada pez enemigo se mueve en su propio
 * hilo: nada en horizontal hasta salir del acuario o hasta que se lo comen.
 * Su velocidad depende directamente de su tamaño.
 */
public class PezEnemigo extends Pez implements Runnable
{
	private static final long PAUSA_MS = 16; // Unos 60 movimientos por segundo

	private final double velocidad; // Píxeles por movimiento
	private final int anchoArea;
	private final Pausa pausa;
	private volatile boolean vivo = true;

	public PezEnemigo(double x, double y, int tamano, int direccion, int anchoArea, Pausa pausa)
	{
		super(x, y, tamano, direccion);
		this.anchoArea = anchoArea;
		this.pausa = pausa;
		this.velocidad = velocidadPara(tamano);
	}

	/** Función directa del tamaño: a más tamaño, más velocidad. */
	public static double velocidadPara(int tamano)
	{
		return 0.8 + tamano * 0.09;
	}

	@Override
	public void run()
	{
		while (vivo)
		{
			try
			{
				pausa.esperarSiEstaPausado();
				mover();
				Thread.sleep(PAUSA_MS);
			}
			catch (InterruptedException e)
			{
				vivo = false; // Nos han pedido que terminemos
			}
		}
	}

	private synchronized void mover()
	{
		x += direccion * velocidad;
		boolean fueraPorDerecha = direccion == 1 && x > anchoArea;
		boolean fueraPorIzquierda = direccion == -1 && x < -getAncho();
		if (fueraPorDerecha || fueraPorIzquierda)
		{
			vivo = false; // Ha salido del acuario: el hilo termina
		}
	}

	public void morir()
	{
		vivo = false;
	}

	public boolean isVivo()
	{
		return vivo;
	}
}
