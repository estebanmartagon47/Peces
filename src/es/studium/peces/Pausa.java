package es.studium.peces;

/**
 * Monitor que permite pausar todos los hilos del juego a la vez.
 * Los hilos llaman a esperarSiEstaPausado() en cada vuelta: si el juego
 * está en pausa se quedan bloqueados en wait() hasta que alguien llame a
 * notifyAll() al reanudar.
 */
public class Pausa
{
	private boolean pausado = false;

	public synchronized void esperarSiEstaPausado() throws InterruptedException
	{
		while (pausado)
		{
			wait();
		}
	}

	public synchronized void alternar()
	{
		pausado = !pausado;
		if (!pausado)
		{
			notifyAll(); // Despierta a todos los hilos que esperaban
		}
	}

	public synchronized void reanudar()
	{
		pausado = false;
		notifyAll();
	}

	public synchronized boolean isPausado()
	{
		return pausado;
	}
}
