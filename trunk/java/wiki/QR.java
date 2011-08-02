package wiki;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public final class QR {

  static final int PIXEL_SIZE = Integer.parseInt(System.getProperty("qr.pixelSize", "5"));

  final BitMatrix bitMatrix;
  Graphics2D g;
  BufferedImage bi;
  int width, height, canvasWidth, canvasHeight, pixelSize;
  int inset = 4;

  @SuppressWarnings("unchecked") // Hashtable API to QRCodeWriter.
  public QR(final String msg) throws Exception {
    final ErrorCorrectionLevel encLevel = ErrorCorrectionLevel.H; // L for lowest, H for highest.
    final Hashtable hints = new Hashtable();
    hints.put(EncodeHintType.ERROR_CORRECTION, encLevel);
    // QRCodeWriter picks max(1, bitMatrix.getWidth(), ...), which
    // means we'll just use its computed values.
    bitMatrix = new QRCodeWriter().encode(msg, BarcodeFormat.QR_CODE, 1, 1, hints);
    width = bitMatrix.getWidth() - inset * 2;
    height = bitMatrix.getHeight() - inset * 2;
    canvasWidth = width * PIXEL_SIZE;
    canvasHeight = height * PIXEL_SIZE;
    bi = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
    g = (Graphics2D) bi.getGraphics();
  }

  void draw() {
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, canvasWidth, canvasHeight);
    g.setColor(Color.BLACK);
    // Draw main image.
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        final boolean on = bitMatrix.get(inset + x, inset + y);
        if (on) {
          g.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
        }
      }
    }
  }

  void write(final OutputStream os) throws IOException {
    ImageIO.write(bi, "PNG", os);
  }
}
