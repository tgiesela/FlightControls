package controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class AttitudeControl extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage bufferedInstrument;
	private BufferedImage bufferedHeading;
	private BufferedImage bufferedHorizon;
	private BufferedImage bufferedWings;
	private Image transparantInstrument;
	private Image transparantHeading;
	private Image transparantWings;
	
	private float pitch = 0;
	private float roll = 0;
	public AttitudeControl() {
		setLayout(null);
        
        loadImages();
        setSurfaceSize();
    }
	private Image createTransparantImage(BufferedImage sourceImage, Color color) {
		
		RGBImageFilter filter = new RGBImageFilter() {
	         int transparentColor = color.getRGB() | 0xFF000000;

	         public final int filterRGB(int x, int y, int argb) {
	            if ((argb | 0xFF000000) == transparentColor) {
	               return 0x00FFFFFF & argb;
	            } else {
	               return argb;
	            }
	         }
	      };
	    ImageProducer filteredImgProd = new FilteredImageSource(sourceImage.getSource(), filter);
	    return Toolkit.getDefaultToolkit().createImage(filteredImgProd);
	}
	private void loadImages() {

    	try {
			bufferedInstrument = ImageIO.read(new File("src/resources/bezel.bmp"));
			bufferedHeading = ImageIO.read(new File("src/resources/heading.bmp"));
			bufferedHorizon = ImageIO.read(new File("src/resources/horizon.bmp"));
			bufferedWings = ImageIO.read(new File("src/resources/wings.bmp"));
		    transparantInstrument = createTransparantImage(bufferedInstrument, Color.YELLOW);
		    transparantHeading = createTransparantImage(bufferedHeading, Color.BLACK);
		    transparantWings = createTransparantImage(bufferedWings, Color.YELLOW);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private void setSurfaceSize() {
        Dimension d = new Dimension();
        //d.width = bufferedInstrument.getWidth(null);
        //d.height = bufferedInstrument.getHeight(null);
        setPreferredSize(new Dimension(400, 200));
    }
    private BufferedImage rotateImage2(BufferedImage src, float degrees) {
    	int transparency = src.getColorModel().getTransparency();
    	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    	GraphicsConfiguration gc = gd.getDefaultConfiguration();
    	BufferedImage dest = gc.createCompatibleImage(src.getWidth(), src.getHeight(), transparency);
    	Graphics2D g2d = dest.createGraphics();
    	AffineTransform origAT = g2d.getTransform(); // save original

    	// rotate the coord system of the dest. image around its center
    	AffineTransform rot = new AffineTransform();
    	rot.rotate(Math.toRadians(degrees), src.getWidth()/2, src.getHeight()/2);
    	g2d.transform(rot);
    	g2d.drawImage(src, 0, 0,  null);
    	g2d.setTransform(origAT);
    	g2d.dispose();
    	return dest;
    }
    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        drawHorizon(g2d);
        drawHeading(g2d);
        drawWings(g2d);
        g2d.drawImage(transparantInstrument, 0, 0, null);
    }

    private void drawHorizon(Graphics2D g2d) {
    	int horizonLeftStart = (bufferedHorizon.getWidth(null) - transparantInstrument.getWidth(null))/2;
    	int horizonBottomStart = (bufferedHorizon.getHeight(null) - transparantInstrument.getHeight(null))/2;
    	int horizonRightEnd = horizonLeftStart + transparantInstrument.getWidth(null);
    	
    	horizonBottomStart = horizonBottomStart - 4*(int)pitch;
    	int horizonTopEnd = horizonBottomStart + transparantInstrument.getHeight(null);
    	
    	g2d.drawImage(rotateImage2(bufferedHorizon, roll), 0, 0, transparantInstrument.getWidth(null), transparantInstrument.getHeight(null)
    						 , horizonLeftStart, horizonBottomStart, horizonRightEnd, horizonTopEnd, null);
    }
    private void drawHeading(Graphics2D g2d) {
    	/* Beetje sjoemelen (-7) om de streepjes precies in het midden te krijgen */
    	int horizonLeftStart = -7 + (transparantHeading.getWidth(null) - transparantInstrument.getWidth(null))/2;
    	int horizonBottomStart = (transparantHeading.getHeight(null) - transparantInstrument.getHeight(null))/2;
    	int horizonRightEnd = horizonLeftStart + transparantInstrument.getWidth(null);
    	int horizonTopEnd = horizonBottomStart + transparantInstrument.getHeight(null);
    	
    	g2d.drawImage(transparantHeading, 0, 0, transparantInstrument.getWidth(null), transparantInstrument.getHeight(null)
    						 , horizonLeftStart, horizonBottomStart, horizonRightEnd, horizonTopEnd, null);
   	}

    private void drawWings(Graphics2D g2d) {
    	int horizonLeftStart = (transparantWings.getWidth(null) - transparantInstrument.getWidth(null))/2;
    	int horizonBottomStart = (transparantWings.getHeight(null) - transparantInstrument.getHeight(null))/2;
    	int horizonRightEnd = horizonLeftStart + transparantInstrument.getWidth(null);
    	int horizonTopEnd = horizonBottomStart + transparantInstrument.getHeight(null);
    	
    	g2d.drawImage(transparantWings, 0, 0, transparantInstrument.getWidth(null), transparantInstrument.getHeight(null)
    						 , horizonLeftStart, horizonBottomStart, horizonRightEnd, horizonTopEnd, null);
   	}
	@Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
	public void setPitch(float value) {
		if (Math.abs(pitch - value) > 1 ) {
			pitch = value;
			super.repaint();
		}
	}
	public void setRoll(float value) {
		if (Math.abs(roll - value) > 1) {
			roll = value * -1;
			super.repaint();
		}
	}
}
