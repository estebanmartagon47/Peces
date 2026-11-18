package es.studium.peces;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/** El acuario: dibuja la comida, los peces y el marcador. */
public class PanelJuego extends JPanel
{
	private static final long serialVersionUID = 1L;

	private transient Modelo modelo;

	public PanelJuego()
	{
		setPreferredSize(new Dimension(Modelo.ANCHO, Modelo.ALTO));
		setFocusable(true);
	}

	public void setModelo(Modelo modelo)
	{
		this.modelo = modelo;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Dibujo.fondoMarino(g2, getWidth(), getHeight());
		if (modelo == null || modelo.getJugador() == null)
		{
			return;
		}
		Jugador jugador = modelo.getJugador();
		int tamanoJugador = jugador.getTamano();

		for (Comida c : modelo.getComidas())
		{
			Dibujo.comida(g2, c.getX(), c.getY());
		}
		for (PezEnemigo pez : modelo.getPeces())
		{
			int tamano = pez.getTamano();
			Color color = (tamano < tamanoJugador) ? Dibujo.COMESTIBLE : Dibujo.PELIGROSO;
			Dibujo.pez(g2, pez.getX(), pez.getY(), pez.getAncho(), pez.getAlto(), pez.getDireccion(), color,
					tamano);
		}
		Dibujo.pez(g2, jugador.getX(), jugador.getY(), jugador.getAncho(), jugador.getAlto(),
				jugador.getDireccion(), Dibujo.JUGADOR, tamanoJugador);

		dibujarMarcador(g2, tamanoJugador);
		if (modelo.isPausado())
		{
			dibujarPausa(g2);
		}
	}

	private void dibujarMarcador(Graphics2D g, int tamano)
	{
		g.setColor(new Color(0, 0, 0, 120));
		g.fillRoundRect(10, 10, 300, 40, 15, 15);
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		g.drawString("Tamaño: " + tamano + " / " + Modelo.TAMANO_META, 22, 36);
		// Barra de progreso hacia la meta
		int anchoBarra = 120;
		int relleno = Math.min(anchoBarra, anchoBarra * tamano / Modelo.TAMANO_META);
		g.drawRect(175, 22, anchoBarra, 16);
		g.setColor(Dibujo.JUGADOR);
		g.fillRect(176, 23, relleno - 1, 15);
	}

	private void dibujarPausa(Graphics2D g)
	{
		g.setColor(new Color(0, 0, 0, 140));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
		FontMetrics fm = g.getFontMetrics();
		String texto = "PAUSA";
		g.drawString(texto, (getWidth() - fm.stringWidth(texto)) / 2, getHeight() / 2);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
		fm = g.getFontMetrics();
		String ayuda = "Pulsa P para continuar";
		g.drawString(ayuda, (getWidth() - fm.stringWidth(ayuda)) / 2, getHeight() / 2 + 40);
	}
}
