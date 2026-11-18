package es.studium.peces;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Pantalla inicial que explica la mecánica del juego. */
public class PanelBienvenida extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final JButton botonJugar = new JButton("¡A jugar!");

	public PanelBienvenida()
	{
		setPreferredSize(new Dimension(Modelo.ANCHO, Modelo.ALTO));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

		JLabel titulo = new JLabel("Come o serás comido");
		titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 46));
		titulo.setForeground(Color.WHITE);

		JLabel reglas = new JLabel("<html><div style='width:560px; font-size:15px; color:white'>"
				+ "<p>Eres el pez <b><font color='#ff8c00'>naranja</font></b> y empiezas con tamaño "
				+ Modelo.TAMANO_INICIAL + ". Tu objetivo es crecer hasta llegar a <b>"
				+ Modelo.TAMANO_META + "</b>.</p><br>"
				+ "<ul>"
				+ "<li>Cada bolita de <b><font color='#ffd73c'>comida</font></b> te hace crecer 1.</li>"
				+ "<li>Los peces <b><font color='#3cc864'>verdes</font></b> son más pequeños que tú: "
				+ "si te los comes, creces lo mismo que medían.</li>"
				+ "<li>Los peces <b><font color='#ff6060'>rojos</font></b> son de tu tamaño o mayores: "
				+ "si los tocas, te comen y se acaba la partida.</li>"
				+ "<li>Cuanto más grande es un pez, más rápido nada.</li>"
				+ "</ul><br>"
				+ "<p><b>Controles:</b> flechas del teclado (o W A S D) o mueve el ratón. "
				+ "Pulsa <b>P</b> para pausar.</p></div></html>");

		botonJugar.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
		botonJugar.setFocusPainted(false);

		for (Component c : new Component[] { titulo, reglas, botonJugar })
		{
			((javax.swing.JComponent) c).setAlignmentX(Component.CENTER_ALIGNMENT);
		}
		add(titulo);
		add(Box.createVerticalStrut(25));
		add(reglas);
		add(Box.createVerticalStrut(30));
		add(botonJugar);
		add(Box.createVerticalGlue());
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Dibujo.fondoMarino(g2, getWidth(), getHeight());
		// Peces decorativos de ejemplo
		Dibujo.pez(g2, 90, 560, Pez.anchoPara(5), Pez.altoPara(5), 1, Dibujo.JUGADOR, 5);
		Dibujo.pez(g2, 260, 590, Pez.anchoPara(3), Pez.altoPara(3), -1, Dibujo.COMESTIBLE, 3);
		Dibujo.pez(g2, 760, 540, Pez.anchoPara(20), Pez.altoPara(20), -1, Dibujo.PELIGROSO, 20);
		Dibujo.comida(g2, 400, 575);
		Dibujo.comida(g2, 620, 600);
	}

	public JButton getBotonJugar()
	{
		return botonJugar;
	}
}
