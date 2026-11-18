package es.studium.peces;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;

/** Métodos de dibujo compartidos por los paneles de la Vista. */
public final class Dibujo
{
	public static final Color JUGADOR = new Color(255, 140, 0);
	public static final Color COMESTIBLE = new Color(60, 200, 100);
	public static final Color PELIGROSO = new Color(225, 55, 55);
	public static final Color COMIDA = new Color(255, 215, 60);

	private Dibujo()
	{
	}

	public static void fondoMarino(Graphics2D g, int ancho, int alto)
	{
		g.setPaint(new GradientPaint(0, 0, new Color(40, 150, 210), 0, alto, new Color(5, 30, 80)));
		g.fillRect(0, 0, ancho, alto);
		// Arena del fondo
		g.setColor(new Color(194, 170, 110));
		g.fillRect(0, alto - 18, ancho, 18);
	}

	/** Dibuja un pez con la cola atrás, el ojo delante y su tamaño escrito. */
	public static void pez(Graphics2D g, double x, double y, int ancho, int alto, int direccion,
			Color color, int tamano)
	{
		int ix = (int) x;
		int iy = (int) y;
		int cola = (int) (ancho * 0.28);
		int cuerpoAncho = ancho - cola;
		int cuerpoX = (direccion == 1) ? ix + cola : ix;
		int colaBase = (direccion == 1) ? cuerpoX + 4 : cuerpoX + cuerpoAncho - 4;
		int colaPunta = (direccion == 1) ? ix : ix + ancho;

		Polygon triangulo = new Polygon();
		triangulo.addPoint(colaBase, iy + alto / 2);
		triangulo.addPoint(colaPunta, iy);
		triangulo.addPoint(colaPunta, iy + alto);
		g.setColor(color.darker());
		g.fillPolygon(triangulo);

		Ellipse2D cuerpo = new Ellipse2D.Double(cuerpoX, iy, cuerpoAncho, alto);
		g.setColor(color);
		g.fill(cuerpo);
		g.setColor(color.darker().darker());
		g.setStroke(new BasicStroke(2));
		g.draw(cuerpo);

		// Ojo
		int ojo = Math.max(4, alto / 5);
		int ojoX = (direccion == 1) ? cuerpoX + cuerpoAncho - ojo * 2 - 2 : cuerpoX + ojo + 2;
		int ojoY = iy + alto / 3 - ojo / 2;
		g.setColor(Color.WHITE);
		g.fillOval(ojoX, ojoY, ojo, ojo);
		g.setColor(Color.BLACK);
		g.fillOval(ojoX + ojo / 4, ojoY + ojo / 4, ojo / 2, ojo / 2);

		// Tamaño en el centro del cuerpo
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(11, alto / 2)));
		FontMetrics fm = g.getFontMetrics();
		String texto = String.valueOf(tamano);
		int tx = cuerpoX + (cuerpoAncho - fm.stringWidth(texto)) / 2;
		int ty = iy + (alto + fm.getAscent() - fm.getDescent()) / 2;
		g.setColor(new Color(0, 0, 0, 170));
		g.drawString(texto, tx, ty);
	}

	public static void comida(Graphics2D g, int x, int y)
	{
		g.setColor(COMIDA);
		g.fillOval(x, y, Comida.DIAMETRO, Comida.DIAMETRO);
		g.setColor(COMIDA.darker());
		g.drawOval(x, y, Comida.DIAMETRO, Comida.DIAMETRO);
	}
}
