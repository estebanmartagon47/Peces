package es.studium.peces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Lógica del juego. No sabe nada de Swing: avisa de lo que pasa a través
 * de la interfaz Oyente, que implementa el Controlador.
 *
 * Hilos que se ponen en marcha en cada partida:
 *  - "Bucle-Juego": mueve al jugador, comprueba choques y pide repintar.
 *  - "Generador-Peces": crea peces nuevos cada cierto tiempo aleatorio.
 *  - "Generador-Comida": coloca comida en posiciones aleatorias.
 *  - "Pez-N": un hilo por cada pez enemigo (ver PezEnemigo).
 */
public class Modelo
{
	public static final int ANCHO = 1000;
	public static final int ALTO = 650;
	public static final int TAMANO_INICIAL = 5;
	public static final int TAMANO_META = 50;

	private static final int MAX_PECES = 12;
	private static final int MAX_COMIDA = 8;
	private static final long FOTOGRAMA_MS = 16;

	/** Lo que el Modelo comunica al Controlador. */
	public interface Oyente
	{
		void actualizar();

		void haComido();

		void finDePartida(boolean victoria, int tamanoFinal);
	}

	// Listas compartidas entre hilos: se accede a ellas siempre con synchronized
	private final List<PezEnemigo> peces = new ArrayList<>();
	private final List<Comida> comidas = new ArrayList<>();

	private final Random azar = new Random();
	private final Pausa pausa = new Pausa();
	private Oyente oyente;
	private Jugador jugador;
	private volatile boolean enJuego = false;
	private Thread hiloJuego;
	private Thread hiloGeneradorPeces;
	private Thread hiloGeneradorComida;
	private int contadorPeces = 0;

	// Entrada del usuario (la escribe el hilo de Swing y la lee el bucle del juego)
	private volatile int dirX = 0;
	private volatile int dirY = 0;
	private volatile boolean usandoRaton = false;
	private volatile int ratonX;
	private volatile int ratonY;

	public void setOyente(Oyente oyente)
	{
		this.oyente = oyente;
	}

	// ------------------------------------------------------------------
	// Inicio y fin de partida
	// ------------------------------------------------------------------

	public synchronized void iniciarPartida()
	{
		detenerPartida();
		synchronized (peces)
		{
			peces.clear();
		}
		synchronized (comidas)
		{
			comidas.clear();
		}
		jugador = new Jugador((ANCHO - Pez.anchoPara(TAMANO_INICIAL)) / 2.0,
				(ALTO - Pez.altoPara(TAMANO_INICIAL)) / 2.0, TAMANO_INICIAL);
		dirX = 0;
		dirY = 0;
		usandoRaton = false;
		pausa.reanudar();
		enJuego = true;

		hiloJuego = new Thread(this::bucleJuego, "Bucle-Juego");
		hiloGeneradorPeces = new Thread(this::generarPeces, "Generador-Peces");
		hiloGeneradorComida = new Thread(this::generarComida, "Generador-Comida");
		for (Thread hilo : new Thread[] { hiloJuego, hiloGeneradorPeces, hiloGeneradorComida })
		{
			hilo.setDaemon(true);
			hilo.start();
		}
	}

	public synchronized void detenerPartida()
	{
		enJuego = false;
		pausa.reanudar(); // Por si algún hilo estaba esperando en la pausa
		for (Thread hilo : new Thread[] { hiloJuego, hiloGeneradorPeces, hiloGeneradorComida })
		{
			if (hilo != null && hilo != Thread.currentThread())
			{
				hilo.interrupt();
			}
		}
		synchronized (peces)
		{
			for (PezEnemigo pez : peces)
			{
				pez.morir(); // Cada hilo Pez-N termina en su siguiente vuelta
			}
		}
		// Las listas no se vacían aquí para que el último fotograma siga
		// mostrando cómo acabó la partida
	}

	private void terminar(boolean victoria)
	{
		int tamanoFinal = jugador.getTamano();
		detenerPartida();
		if (oyente != null)
		{
			oyente.finDePartida(victoria, tamanoFinal);
		}
	}

	// ------------------------------------------------------------------
	// Hilos
	// ------------------------------------------------------------------

	/** Hilo principal del juego: unas 60 vueltas por segundo. */
	private void bucleJuego()
	{
		while (enJuego)
		{
			try
			{
				pausa.esperarSiEstaPausado();
				moverJugador();
				comprobarComida();
				comprobarPeces();
				if (enJuego && jugador.getTamano() >= TAMANO_META)
				{
					terminar(true);
				}
				if (oyente != null)
				{
					oyente.actualizar();
				}
				Thread.sleep(FOTOGRAMA_MS);
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}

	/** Crea un pez cada 0,5 - 1,4 segundos (al azar). */
	private void generarPeces()
	{
		while (enJuego)
		{
			try
			{
				Thread.sleep(500 + azar.nextInt(900));
				pausa.esperarSiEstaPausado();
				crearPez();
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}

	/** Coloca una bolita de comida cada 1 - 2 segundos (al azar). */
	private void generarComida()
	{
		while (enJuego)
		{
			try
			{
				Thread.sleep(1000 + azar.nextInt(1000));
				pausa.esperarSiEstaPausado();
				synchronized (comidas)
				{
					if (enJuego && comidas.size() < MAX_COMIDA)
					{
						int x = 20 + azar.nextInt(ANCHO - 40 - Comida.DIAMETRO);
						int y = 60 + azar.nextInt(ALTO - 80 - Comida.DIAMETRO);
						comidas.add(new Comida(x, y));
					}
				}
			}
			catch (InterruptedException e)
			{
				return;
			}
		}
	}

	private void crearPez()
	{
		synchronized (peces)
		{
			if (!enJuego || peces.size() >= MAX_PECES)
			{
				return;
			}
			int tamano = tamanoAleatorio(jugador.getTamano());
			int direccion = azar.nextBoolean() ? 1 : -1; // Sale por la izquierda o por la derecha
			int ancho = Pez.anchoPara(tamano);
			int alto = Pez.altoPara(tamano);
			double x = (direccion == 1) ? -ancho : ANCHO;
			double y = azar.nextInt(Math.max(1, ALTO - alto)); // Altura aleatoria
			PezEnemigo pez = new PezEnemigo(x, y, tamano, direccion, ANCHO, pausa);
			peces.add(pez);
			Thread hilo = new Thread(pez, "Pez-" + (++contadorPeces));
			hilo.setDaemon(true);
			hilo.start();
		}
	}

	/**
	 * El 60 % de los peces son más pequeños que el jugador (se pueden comer)
	 * y el 40 % son de su tamaño o mayores (peligrosos).
	 */
	private int tamanoAleatorio(int tamanoJugador)
	{
		if (azar.nextInt(100) < 60)
		{
			return 1 + azar.nextInt(tamanoJugador - 1);
		}
		return Math.min(tamanoJugador + azar.nextInt(16), 70);
	}

	// ------------------------------------------------------------------
	// Lógica de cada fotograma (la ejecuta el hilo Bucle-Juego)
	// ------------------------------------------------------------------

	private void moverJugador()
	{
		double dx;
		double dy;
		if (usandoRaton)
		{
			// Avanza hacia el puntero sin pasarse
			double cx = jugador.getX() + jugador.getAncho() / 2.0;
			double cy = jugador.getY() + jugador.getAlto() / 2.0;
			double distX = ratonX - cx;
			double distY = ratonY - cy;
			double distancia = Math.hypot(distX, distY);
			if (distancia < 1)
			{
				return;
			}
			double paso = Math.min(Jugador.VELOCIDAD, distancia);
			dx = distX / distancia * paso;
			dy = distY / distancia * paso;
		}
		else
		{
			if (dirX == 0 && dirY == 0)
			{
				return;
			}
			double factor = (dirX != 0 && dirY != 0) ? Math.sqrt(0.5) : 1; // Misma velocidad en diagonal
			dx = dirX * Jugador.VELOCIDAD * factor;
			dy = dirY * Jugador.VELOCIDAD * factor;
		}
		jugador.mover(dx, dy, ANCHO, ALTO);
	}

	private void comprobarComida()
	{
		synchronized (comidas)
		{
			Iterator<Comida> it = comidas.iterator();
			while (it.hasNext())
			{
				Comida c = it.next();
				if (tocaAlJugador(c.getX(), c.getY(), Comida.DIAMETRO, Comida.DIAMETRO))
				{
					it.remove();
					jugador.crecer(Comida.VALOR, ANCHO, ALTO);
					avisarComida();
				}
			}
		}
	}

	private void comprobarPeces()
	{
		boolean nosHanComido = false;
		synchronized (peces)
		{
			Iterator<PezEnemigo> it = peces.iterator();
			while (it.hasNext())
			{
				PezEnemigo pez = it.next();
				if (!pez.isVivo())
				{
					it.remove(); // Ya salió del acuario y su hilo terminó
					continue;
				}
				if (tocaAlJugador(pez.getX(), pez.getY(), pez.getAncho(), pez.getAlto()))
				{
					if (pez.getTamano() < jugador.getTamano())
					{
						pez.morir(); // Su hilo termina en la siguiente vuelta
						it.remove();
						jugador.crecer(pez.getTamano(), ANCHO, ALTO);
						avisarComida();
					}
					else
					{
						nosHanComido = true; // Igual o mayor que nosotros
						break;
					}
				}
			}
		}
		if (nosHanComido)
		{
			terminar(false); // Fuera del synchronized para no bloquear la lista
		}
	}

	private void avisarComida()
	{
		if (oyente != null)
		{
			oyente.haComido();
		}
	}

	/**
	 * Choque entre rectángulos, recortando un 20 % de cada borde para que
	 * solo cuente cuando los peces se tocan de verdad y no por las esquinas.
	 */
	private boolean tocaAlJugador(double ox, double oy, int oAncho, int oAlto)
	{
		double jx = jugador.getX();
		double jy = jugador.getY();
		double jAncho = jugador.getAncho();
		double jAlto = jugador.getAlto();
		double mj = 0.2;
		double mo = (oAncho == Comida.DIAMETRO) ? 0 : 0.2;
		return jx + jAncho * mj < ox + oAncho * (1 - mo)
				&& ox + oAncho * mo < jx + jAncho * (1 - mj)
				&& jy + jAlto * mj < oy + oAlto * (1 - mo)
				&& oy + oAlto * mo < jy + jAlto * (1 - mj);
	}

	// ------------------------------------------------------------------
	// Entrada del usuario (llamado desde el Controlador)
	// ------------------------------------------------------------------

	public void setDireccion(int dirX, int dirY)
	{
		this.dirX = dirX;
		this.dirY = dirY;
		usandoRaton = false;
	}

	public void setDestinoRaton(int x, int y)
	{
		ratonX = x;
		ratonY = y;
		usandoRaton = true;
	}

	public void alternarPausa()
	{
		if (enJuego)
		{
			pausa.alternar();
		}
	}

	// ------------------------------------------------------------------
	// Consultas para la Vista (devuelven copias para dibujar sin bloquear)
	// ------------------------------------------------------------------

	public List<PezEnemigo> getPeces()
	{
		synchronized (peces)
		{
			return new ArrayList<>(peces);
		}
	}

	public List<Comida> getComidas()
	{
		synchronized (comidas)
		{
			return new ArrayList<>(comidas);
		}
	}

	public Jugador getJugador()
	{
		return jugador;
	}

	public boolean isPausado()
	{
		return pausa.isPausado();
	}

	public boolean isEnJuego()
	{
		return enJuego;
	}
}
