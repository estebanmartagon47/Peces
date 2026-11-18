package es.studium.peces;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

/**
 * Une la Vista con el Modelo: recoge el teclado, el ratón y el botón, y
 * reacciona a los avisos del Modelo (repintar, sonidos y fin de partida).
 */
public class Controlador implements ActionListener, KeyListener, MouseMotionListener, Modelo.Oyente
{
	private final Modelo modelo;
	private final Vista vista;
	private final Sonido sonido;

	// Teclas pulsadas en este momento
	private boolean izquierda;
	private boolean derecha;
	private boolean arriba;
	private boolean abajo;

	public Controlador(Modelo modelo, Vista vista, Sonido sonido)
	{
		this.modelo = modelo;
		this.vista = vista;
		this.sonido = sonido;

		modelo.setOyente(this);
		vista.getPanelJuego().setModelo(modelo);
		vista.getBotonJugar().addActionListener(this);
		vista.getPanelJuego().addKeyListener(this);
		vista.getPanelJuego().addMouseMotionListener(this);

		sonido.iniciarMusica();
		vista.mostrarBienvenida();
		vista.setVisible(true);
	}

	private void empezarPartida()
	{
		izquierda = derecha = arriba = abajo = false;
		vista.mostrarJuego();
		modelo.iniciarPartida();
	}

	// ------------------------------------------------------------------
	// Eventos de la interfaz (se ejecutan en el hilo de Swing)
	// ------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent e)
	{
		empezarPartida();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_P)
		{
			modelo.alternarPausa();
			vista.repintarJuego();
			return;
		}
		cambiarTecla(e.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		cambiarTecla(e.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	private void cambiarTecla(int codigo, boolean pulsada)
	{
		switch (codigo)
		{
			case KeyEvent.VK_LEFT, KeyEvent.VK_A -> izquierda = pulsada;
			case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> derecha = pulsada;
			case KeyEvent.VK_UP, KeyEvent.VK_W -> arriba = pulsada;
			case KeyEvent.VK_DOWN, KeyEvent.VK_S -> abajo = pulsada;
			default -> {
				return;
			}
		}
		int dx = (derecha ? 1 : 0) - (izquierda ? 1 : 0);
		int dy = (abajo ? 1 : 0) - (arriba ? 1 : 0);
		modelo.setDireccion(dx, dy);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		modelo.setDestinoRaton(e.getX(), e.getY());
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		modelo.setDestinoRaton(e.getX(), e.getY());
	}

	// ------------------------------------------------------------------
	// Avisos del Modelo (llegan desde el hilo Bucle-Juego, NO desde Swing)
	// ------------------------------------------------------------------

	@Override
	public void actualizar()
	{
		vista.repintarJuego();
	}

	@Override
	public void haComido()
	{
		sonido.reproducirEfecto(Sonido.COMER);
	}

	@Override
	public void finDePartida(boolean victoria, int tamanoFinal)
	{
		sonido.reproducirEfecto(victoria ? Sonido.VICTORIA : Sonido.COMIDO);
		// Los diálogos de Swing solo se pueden abrir desde el hilo de Swing
		SwingUtilities.invokeLater(() -> {
			vista.repintarJuego();
			if (vista.preguntarRevancha(victoria, tamanoFinal))
			{
				empezarPartida();
			}
			else
			{
				vista.mostrarBienvenida();
			}
		});
	}
}
