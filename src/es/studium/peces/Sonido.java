package es.studium.peces;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Reproduce la música de fondo (en bucle) y los efectos de sonido.
 * Cada efecto se lanza en un hilo propio para que cargar el audio no
 * retrase nunca el bucle del juego ni la interfaz.
 */
public class Sonido
{
	public static final String MUSICA = "/sonidos/musica.wav";
	public static final String COMER = "/sonidos/comer.wav";
	public static final String COMIDO = "/sonidos/comido.wav";
	public static final String VICTORIA = "/sonidos/victoria.wav";

	private Clip musica;

	public void iniciarMusica()
	{
		try
		{
			musica = AudioSystem.getClip();
			musica.open(cargar(MUSICA));
			musica.loop(Clip.LOOP_CONTINUOUSLY); // Música constante
		}
		catch (LineUnavailableException | UnsupportedAudioFileException | IOException | IllegalArgumentException e)
		{
			System.err.println("No se pudo reproducir la música: " + e.getMessage());
		}
	}

	public void reproducirEfecto(String ruta)
	{
		Thread hilo = new Thread(() -> {
			try (AudioInputStream audio = cargar(ruta))
			{
				Clip clip = AudioSystem.getClip();
				clip.addLineListener(evento -> {
					if (evento.getType() == LineEvent.Type.STOP)
					{
						clip.close(); // Libera el recurso al terminar
					}
				});
				clip.open(audio);
				clip.start();
			}
			catch (LineUnavailableException | UnsupportedAudioFileException | IOException | IllegalArgumentException e)
			{
				System.err.println("No se pudo reproducir " + ruta + ": " + e.getMessage());
			}
		}, "Sonido-Efecto");
		hilo.setDaemon(true);
		hilo.start();
	}

	private AudioInputStream cargar(String ruta) throws UnsupportedAudioFileException, IOException
	{
		InputStream entrada = Sonido.class.getResourceAsStream(ruta);
		if (entrada == null)
		{
			throw new IOException("No se encuentra el fichero " + ruta);
		}
		return AudioSystem.getAudioInputStream(new BufferedInputStream(entrada));
	}
}
