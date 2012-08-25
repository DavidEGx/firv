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
import java.security.InvalidParameterException;

/**
 * <p>
 * Esta clase implementa una transformación wavelet de tipo Haar.<br />
 * La implementación se limita a transformaciones sobre imagenes en escala
 * de grises, el uso de otro tipo de imagenes provocará un error
 * <code>InvalidParameterException</code>.
 * </p>
 * 
 * <p>
 * La transformación wavelet de Haar se aplica repetidamente hasta que se
 * llega al tamaño especificado en la creación del objeto.<br />
 * El número de iteraciones será el logaritmo en base de dos del tamaño original
 * de la imagen dividido por el tamaño destino, ya que en cada iteración se
 * reducen las dimensiones de la imagen por dos.
 * </p>
 * 
 * <p>Esta clase permite que la imagen origen y destino sean la misma.</p>
 * 
 * @author David Escribano García
 */
public class HaarFilter extends AbstractFilter
{
    private Integer width, height;

    /**
     * Crea un objecto HaarFilter usando los parametros especificados.
     * 
     * @param width Ancho de la imagen resultante
     * @param height Alto de la imagen resultante
     */
    public HaarFilter(final int width, final int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * <p>Aplica la transformacion wavelet de Haar sobre la imagen objetivo
     * tantas veces como sea necesario y devuelve el resultado.</p>
     * <p>Si la imagen de entrada no es en escala de grises se generará una
     * excepción <code>InvalidParameterException</code>.</p>
     * 
     * @param src La imagen a procesar
     * @param dest La imagen en la que se almacenaran los resultados del proceso, puede ser nula
     * @return Imagen con el filtro aplicado
     * @throws InvalidParameterException Si la imagen de entrada no esta en escala de grises
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest)
    {       
        if (src.getType() != BufferedImage.TYPE_BYTE_GRAY)
            throw new InvalidParameterException("Image must be of TYPE_BYTE_GRAY type");
        
        if (dest == null)
            dest = createCompatibleDestImage(src, null);

        // Obtengo la cantidad de interaciones para alcanzar el tamaño indicado
        final int originalWidth = src.getWidth();
        final int originalHeight = src.getHeight();
        final int iterations = calculateIterations(originalWidth, originalHeight, width, height);
        
        // Obtengo los pixels
        byte[] tmpData = GraphicsUtilities.getPixels(src, 0, 0, originalWidth, originalHeight, (byte[])null);
        
        // Proceso los datos
        int currentWidth = originalWidth;
        int currentHeight = originalHeight;
        for(int i = 0; i < iterations; i++)
        {
            int posicion = 0;
            for(int fila = 1; fila < currentHeight; fila += 2)
            {
                for(int columna = 1; columna < currentWidth; columna += 2)
                {
                    /*
                     * x1y1 x2y1
                     * x1y2 x2y2
                     */
                    int x1y1, x1y2, x2y1, x2y2;
                    x1y1 = tmpData[(fila - 1) * currentWidth + columna - 1] & 0xff;
                    x2y1 = tmpData[(fila - 1) * currentWidth + columna] & 0xff;
                    x1y2 = tmpData[fila * currentWidth + columna - 1] & 0xff;
                    x2y2 = tmpData[fila * currentWidth + columna] & 0xff;

                    int media = (x1y1 + x2y1 + x1y2 + x2y2) >> 2;
                    tmpData[posicion] = (byte)(media);
                    posicion++;
                }
            }
            currentWidth = currentWidth >> 1;
            currentHeight = currentHeight >> 1;
        }
        GraphicsUtilities.setPixels(dest, 0, 0, width, height, tmpData);
        return dest;
    }
    
    /**
     * Crea una imagen vacia compatible con la imagen resultante de aplicar
     * el filtro.
     * 
     * @param src Imagen de entrada
     * @param destCM El modelo de color de la imagen de destino, puede ser nulo
     * @return Imagen de destino compatible
     */
    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
    {
        if (destCM == null)
        {
            destCM = src.getColorModel();
        }

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(this.width, this.height), destCM.isAlphaPremultiplied(), null);
    }

    /**
     * <p>Calcula el número de veces que es necesario aplicar la transformación
     * wavelet de Haar.</p>
     * 
     * <p>En cada aplicación de la transformación wavelet el tamaño de la imagen
     * se reduce a la mitad.<br />
     * Basicamente: <code>iteraciones = Math.log(originalWidth / outWidth);</code>
     * </p>
     * @param originalWidth Ancho de la imagen de entrada
     * @param originalHeight Alto de la imagen de entrada
     * @param outWidth Ancho de la imagen destino
     * @param outHeight Alto de la imagen destino
     * @return Numero de iteraciones a aplicar
     */
    private Integer calculateIterations(final int originalWidth
                                      , final int originalHeight
                                      , final int outWidth
                                      , final int outHeight)
    {
        int i_w = (int) Math.floor(Math.log(originalWidth / outWidth) / Math.log(2));
        int i_h = (int) Math.floor(Math.log(originalHeight / outHeight) / Math.log(2));

        int diffCut_w = (int) (originalWidth - outWidth * Math.pow(2, i_w));
        int diffPad_w = (int) (outWidth * Math.pow(2, i_w + 1) - originalWidth);

        int diffCut_h = (int) (originalHeight - outHeight * Math.pow(2, i_h));
        int diffPad_h = (int) (outHeight * Math.pow(2, i_h + 1) - originalHeight);

        if (diffCut_w > diffPad_w)
            i_w = i_w + 1;
        if (diffCut_h > diffPad_h)
            i_h = i_h + 1;

        return (i_w + i_h) / 2;
    }
}
