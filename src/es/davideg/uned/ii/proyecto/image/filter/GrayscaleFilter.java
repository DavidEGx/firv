/** Copyright 2010 David Escribano García
*
* Author: David Escribano García
* Director: Jesús Antonio Vega Sánchez (CIEMAT)
* Supervisor: Sebastián Dormido Canto (UNED)
*
* Licensed under the EUPL, Version 1.1 or - as soon they will be
* approved by the European Commission - subsequent versions of the
* EUPL (the "Licence"); you may not use this work except in
* compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the Licence is distributed on an "AS
* IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing permissions
* and limitations under the Licence.
*/

package es.davideg.uned.ii.proyecto.image.filter;

import es.davideg.uned.ii.proyecto.image.GraphicsUtilities;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * <p>Esta clase proporciona la transformación de una imagen en color a una
 * imagen en blanco y negro.<br />
 * La transformación se realiza aplicando un coeficiente a cada color,
 * dependiendo del coeficiente aplicado la imagen resultante será diferente.
 * </p>
 * 
 * <p>
 * Si la imagen original ya esta en blanco y negro no se aplicará ninguna
 * transformación y se devolverá directamente.
 * </p>
 * 
 * <p>
 * La imagen origen no puede ser la imagen destino ya que el tipo de imagen será
 * diferente.
 * </p>
 * @author David Escribano García
 */
public class GrayscaleFilter extends AbstractFilter
{
    /** Coeficientes que dan igual importancia a todos los colores:  <code>{0.33, 0.33, 0.33}</code> */
    public final static double[] METHOD_AVERAGE = {1.0/3.0, 1.0/3.0, 1.0/3.0};
    /** Coeficientes BT709:  <code>{0.2125, 0.7154, 0.0721}</code> */
    public final static double[] METHOD_BT709   = {0.2125 , 0.7154 , 0.0721 };
    /** Coeficientes usados en Gimp por defecto:  <code>{0.21, 0.71, 0.07}</code> */
    public final static double[] METHOD_GIMP_LUMINOSITY = {0.21, 0.71, 0.07};
    /** Coeficientes RMY:  <code>{0.5, 0.419, 0.081}</code> */
    public final static double[] METHOD_RMY     = {0.5    , 0.419  , 0.081  };
    /** Coeficientes YIQ:  <code>{0.299, 0.587, 0.114}</code> */
    public final static double[] METHOD_YIQ     = {0.299   , 0.587  , 0.114  };

    private double red_part;
    private double green_part;
    private double blue_part;

    /**
     * Crea un objecto GrayscaleFilter con los coeficientes especificados.<br />
     * Los coeficientes deben ser valores entre 0 y 1, valores más cercanos a
     * 1 indican que ese color será tenido más en cuenta a la hora de obtener
     * el gris resultante.
     * @param red Coeficiente para el color rojo.
     * @param green Coeficiente para el color verde.
     * @param blue Coeficiente para el color verde.
     */
    public GrayscaleFilter(final double red, final double green, final double blue)
    {
        this.red_part = red;
        this.green_part = green;
        this.blue_part = blue;
    }
    
    /**
     * Crea un objecto GrayscaleFilter con los coeficientes especificados.<br />
     * @param rgb Vector de dimension tres con los coeficientes rgb
     */
    public GrayscaleFilter(final double[] rgb)
    {
        this(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * <p>
     * Aplica un filtro para obtener una imagen en blanco y negro a partir de
     * la imagen de entrada.
     * </p>
     * @param src La imagen a procesar
     * @param dest La imagen en la que se almacenaran los resultados del proceso, puede ser nula
     * @return Imagen con el filtro aplicado
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest)
    {
        if (src.getType() == BufferedImage.TYPE_BYTE_GRAY)
        {
            dest = src;
            return dest;
        }

        if (dest == null)
            dest = createCompatibleDestImage(src, null);
        
        final int width = src.getWidth();
        final int height = src.getHeight();

        int[] inPixels = new int[width * height];
        GraphicsUtilities.getPixels(src, 0, 0, width, height, inPixels);
        byte[] outPixels = doFilter(inPixels);
        GraphicsUtilities.setPixels(dest, 0, 0, width, height, outPixels);
        return dest;
    }
    
    /**
     * Procesa el array de pixeles de entrada y lo transforma en un array
     * de pixels en blanco y negro.
     * @param inputPixels Pixeles originales de la imagen
     * @return Pixeles en blanco y negro
     */
    private byte[] doFilter(int[] inputPixels)
    {
        int red, green, blue;
        int i = 0;
        byte[] outPixels = new byte[inputPixels.length];

        for(int pixel : inputPixels)
        {
            // Obtengo valores originales
            red   = (pixel >> 16) & 0xFF;
            green = (pixel >> 8) & 0xFF;
            blue  = pixel & 0xFF;
            
            // Calculo valores nuevos
            outPixels[i++] = (byte)(
                 red   * red_part   +
                 green * green_part +
                 blue  * blue_part
            );
        }
        return outPixels;
    }

    /**
     * Crea una imagen vacia destino compatible con el tipo de datos a albergar.<br />
     * La imagen tendrá el mismo tamaño que la original pero será una imagen
     * de tipo <code>BufferdImage.TYP_BYTE_GRAY</code>.
     * 
     * @param src Imagen de entrada
     * @param destCM El modelo de color de la imagen de destino, puede ser nulo
     * @return Imagen de destino compatible
     */
    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
    {
        return new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
    }
}
