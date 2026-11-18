package es.studium.peces;

import javax.swing.SwingUtilities;

public class Principal
{
	public static void main(String[] args)
	{
		// La interfaz se crea en el hilo de eventos de Swing (EDT)
		SwingUtilities.invokeLater(() -> {
			Modelo modelo = new Modelo();
			Vista vista = new Vista();
			new Controlador(modelo, vista, new Sonido());
		});
	}
}
