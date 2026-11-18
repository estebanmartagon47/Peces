package es.studium.peces;

/** Bolita de comida. No se mueve y siempre vale 1. */
public class Comida
{
	public static final int VALOR = 1;
	public static final int DIAMETRO = 12;

	private final int x;
	private final int y;

	public Comida(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}
}
