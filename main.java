/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clase4_filtros;

import Funciones.Funciones_Image;
import java.awt.Color;
import java.awt.image.BandCombineOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author miguel
 */
public class main {
    public static void main(String[] args) {
        BufferedImage bi = null;
        BufferedImage bo = null;
        try{
            bi = ImageIO.read(new File("lenaSalt.png"));
        }catch(Exception e){}
        
        bo = sharpImage(bi);       
        Funciones_Image.saveBufferedImage(bo, "sharp.png");
        
//        bo = colorFilter(bi);       
 //       Funciones_Image.saveBufferedImage(bo, "colorFilter.png");
        
        bo = blurImage(bi);
        Funciones_Image.saveBufferedImage(bo, "blur.png");
        
        bo = edgeDetection(bi);
        Funciones_Image.saveBufferedImage(bo, "edge.png");
        
        bo = Gaussian(bi);
        Funciones_Image.saveBufferedImage(bo, "gau.png");
        
        bo = getSoftFilteredImage(bi);
        Funciones_Image.saveBufferedImage(bo, "bilinear.png");
        
        bo = blurImage2(bi);
        Funciones_Image.saveBufferedImage(bo, "blur2.png");
        
        bo = smoothFilter(bi);
        Funciones_Image.saveBufferedImage(bo, "smooth.png");
        
    }
    public static BufferedImage sharpImage(BufferedImage image) {
        float[] sharpenMatrix = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f };
        BufferedImageOp sharpenFilter = new ConvolveOp(new Kernel(3, 3, sharpenMatrix),
            ConvolveOp.EDGE_NO_OP, null);
        return sharpenFilter.filter(image, null);
      }
    public static BufferedImage colorFilter(BufferedImage image) {
        float[][] colorMatrix = { { 1f, 0f, 0f }, { 0.5f, 1.0f, 0.5f }, { 0.2f, 0.4f, 0.6f } };
        BandCombineOp changeColors = new BandCombineOp(colorMatrix, null);
        Raster sourceRaster = image.getRaster();
        WritableRaster displayRaster = sourceRaster.createCompatibleWritableRaster();
        changeColors.filter(sourceRaster, displayRaster);
        return new BufferedImage(image.getColorModel(), displayRaster, true, null);

      }
    public static BufferedImage blurImage(BufferedImage bi){
        BufferedImage bo = new BufferedImage(bi.getWidth(),bi.getHeight(), bi.getType());
        Kernel kernel = new Kernel(3, 3, new float[] { 1f / 3f, 1f / 3f, 1f / 3f,
        1f / 3f, 1f / 3f, 1f / 3f, 1f / 3f, 1f / 3f, 1f / 3f });
        BufferedImageOp op = new ConvolveOp(kernel);
        bo = op.filter(bi, null);
        return bo;
    }
    public static BufferedImage edgeDetection(BufferedImage bi){
        float[] sharpenMatrix = { 0.0f, -0.75f, 0.0f,
          -0.75f, 3.0f, -0.75f, 0.0f, -0.75f, 0.0f };
        
        BufferedImageOp sharpenFilter = new ConvolveOp(new Kernel(3, 3, sharpenMatrix),
            ConvolveOp.EDGE_NO_OP, null);
        return sharpenFilter.filter(bi, null);
        
    }
    public static BufferedImage Gaussian(BufferedImage bi)
    {
        BufferedImage bo = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
        float[] matrix = {
            1/16f, 1/8f, 1/16f, 
            1/8f, 1/4f, 1/8f, 
            1/16f, 1/8f, 1/16f, 
        };
        

        BufferedImageOp op = new ConvolveOp( new Kernel(3, 3, matrix) );
        bo = op.filter(bi, bo);
        
        return bo;
    }
    public static BufferedImage getSoftFilteredImage(BufferedImage bufferedImage) {
    // soften

    float softenFactor = 0.01f;
    float[] softenArray = { 0, softenFactor, 0, softenFactor, 1 - (softenFactor * 3), softenFactor, 0, softenFactor, 0 };
    Kernel kernel = new Kernel(3, 3, softenArray);
    ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    bufferedImage = cOp.filter(bufferedImage, null);

    return bufferedImage;
}
    public static BufferedImage blurImage2( BufferedImage image ) {
        ConvolveOp filter = new ConvolveOp( new Kernel( 3, 3, new float[] {
                0.111f, 0.111f, 0.111f,
                0.111f, 0.111f, 0.111f,
                0.111f, 0.111f, 0.111f
        }), ConvolveOp.EDGE_NO_OP, null );
        return filter.filter( image, null );
    }
    public static BufferedImage smoothFilter( BufferedImage img ){
        Color c;
        int r = 0, g = 0, b = 0;
        boolean bndOrilla = false;        
        
        for( int x = 0; x < img.getWidth(); x++)
        {
            for( int y = 0; y < img.getHeight(); y++) {    
                for(int i = x-1; i<=x+1;i++)
                    for(int j = y-1; j<=y+1; j++){//Aplicación del filtro
                        try{
                            c = new Color(img.getRGB(i, j));
                            r+=c.getRed();
                            g+=c.getGreen();
                            b+=c.getBlue();
                        }catch(Exception e){//en caso de que las coordenadas ubiquen esten en un pixel que esta en la orilla de la imagen
                            i++;            //solo se tomaran 4 pixeles para aplicar el filtro
                            j++;
                            bndOrilla = true;
                        }
                    }
                if( bndOrilla == true )
                {
                    try{img.setRGB(x, y,new Color((int)(r/4),(int)(g/4),(int)(b/4)).getRGB());//aplica el fltro a las orillas de la imagen
                    }catch(Exception e){}
                    
                }
                else
                {
                    try{img.setRGB(x, y,new Color((int)(r/9),(int)(g/9),(int)(b/9)).getRGB());//aplicación del filtro de 3x3
                    }catch(Exception e){}
                  
                }
                
                r   =   0;
                g   =   0;
                b   =   0;
                bndOrilla = false;               
                }
             
        }
        return img;
    }
}
