package wiki;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.regex.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Polygon {

  static {
    System.setProperty("java.awt.headless", "true");
  }

  static class CoordLine {
    static final String NUMBER = "-?\\d+(?:\\.\\d+)?";
    static final Pattern COORD = Pattern.compile("\\s*("+NUMBER+")\\s*,\\s*("+NUMBER+")\\s*");
    final Matcher m = COORD.matcher("");

    double lastX = Double.NaN, lastY = Double.NaN,
      x = Double.NaN, y = Double.NaN,
      minX = 0, minY = 0, maxX = 0, maxY = 0;
    boolean first = false;

    void reset(final String coordStr) {
      m.reset(coordStr);
      first = true;
    }

    boolean parse() {
      if (m.find()) {
	lastX = x;
	lastY = y;
	x = Double.parseDouble(m.group(1));
	y = Double.parseDouble(m.group(2));
	if (x < minX)
	  minX = x;
	if (y < minY)
	  minY = y;
	if (x > maxX)
	  maxX = x;
	if (y > maxY)
	  maxY = y;
	first = false;
	return true;
      } else {
	if (first) {
          x = Double.NaN;
          y = Double.NaN;
	  throw new RuntimeException("Unknown format!");
	}
        first = false;
        return false;
      }
    }

    public String toString() {
      return String.format("lastX: %f, lastY: %f, x: %f, y: %f", lastX, lastY, x, y);
    }
  }

  static final int SIZE = Integer.parseInt(System.getProperty("polygon.size", "100"));

  Graphics2D g;
  BufferedImage bi;
  int inset = 4;

  public Polygon(final String linearRing) {
    this(new StringReader(linearRing));
  }

  public Polygon(final Reader r) {
    try {
      final LineNumberReader lnr = new LineNumberReader(r);
      bi = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
      g = (Graphics2D) bi.getGraphics();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, SIZE, SIZE);
      g.setColor(Color.BLACK);
      String line;
      final CoordLine p = new CoordLine();
      while ((line = lnr.readLine()) != null) {
	p.reset(line);
	while (p.parse()) {
	  g.drawLine((int)p.lastX, (int)p.lastY, (int)p.x, (int)p.y);
	}
      }
    } catch (IOException e) {
      throw new IllegalArgumentException("Invalid polygon description: "+ e);
    }
  }

  void draw() {
  }

  public void write(final OutputStream os) {
    try {
      ImageIO.write(bi, "PNG", os);
    } catch(IOException e) {
      throw new IllegalArgumentException("Invalid polygon description: "+ e);
    }
  }
}
