package es.studium.peces;

import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Ventana del juego. Usa un CardLayout para cambiar entre la pantalla de
 * bienvenida y el acuario.
 */
public class Vista extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final String BIENVENIDA = "bienvenida";
	private static final String JUEGO = "juego";

	private final CardLayout tarjetas = new CardLayout();
	private final JPanel contenedor = new JPanel(tarjetas);
	private final PanelBienvenida panelBienvenida = new PanelBienvenida();
	private final PanelJuego panelJuego = new PanelJuego();

	public Vista()
	{
		super("Come o serás comido");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		contenedor.add(panelBienvenida, BIENVENIDA);
		contenedor.add(panelJuego, JUEGO);
		setContentPane(contenedor);
		pack();
		setLocationRelativeTo(null);
	}

	public void mostrarBienvenida()
	{
		tarjetas.show(contenedor, BIENVENIDA);
	}

	public void mostrarJuego()
	{
		tarjetas.show(contenedor, JUEGO);
		// Sin el foco el panel no recibiría las pulsaciones de teclado
		SwingUtilities.invokeLater(panelJuego::requestFocusInWindow);
	}

	public void repintarJuego()
	{
		panelJuego.repaint(); // repaint() se puede llamar desde cualquier hilo
	}

	/** Devuelve true si el usuario quiere jugar otra vez. */
	public boolean preguntarRevancha(boolean victoria, int tamano)
	{
		String titulo = victoria ? "¡Has ganado!" : "¡Te han comido!";
		String mensaje = victoria
				? "¡Enhorabuena! Has llegado al tamaño " + tamano + " y eres el rey del acuario."
				: "Un pez más grande te ha comido cuando medías " + tamano + ".";
		Object[] opciones = { "Jugar otra vez", "Volver al inicio" };
		int respuesta = JOptionPane.showOptionDialog(this, mensaje, titulo, JOptionPane.DEFAULT_OPTION,
				victoria ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE, null, opciones,
				opciones[0]);
		return respuesta == 0;
	}

	public JButton getBotonJugar()
	{
		return panelBienvenida.getBotonJugar();
	}

	public PanelJuego getPanelJuego()
	{
		return panelJuego;
	}
}
