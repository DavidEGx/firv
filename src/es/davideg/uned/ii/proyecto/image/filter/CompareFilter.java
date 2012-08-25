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

/**
 * <p>Clase para la comparación de dos imagenes pixel a pixel
 * </p>
 * @author David Escribano García
 */
public class CompareFilter
{
    private byte[] data1;
    /**
     * Crea un objecto CompareFilter
     * @param img Imagen que va a ser comparada con otras imagenes
     */
    public CompareFilter(final BufferedImage img)
    {
        this.data1 = GraphicsUtilities.getPixels(img, 0, 0, img.getWidth(), img.getHeight(), (byte[])null);
    }
    
    /**
     * <p>Compara la imagen usada en la creación del objecto con la recibida como
     * parametro.<br />
     * Se compara pixel a pixel y se suman las diferencias en valor absoluto.
     * </p>
     * @param img Imagen a comparar
     * @return Diferencia entre las imagenes
     */
    public long compare(final BufferedImage img)
    {
        long compare = 0;
        final byte[] data2 = GraphicsUtilities.getPixels(img, 0, 0, img.getWidth(), img.getHeight(), (byte[])null);
        
        final int len = data1.length > data2.length ? data1.length : data2.length;
        byte pixel1, pixel2;
        
        for(int i = 0; i < len; i++)
        {
            try { pixel1 = data1[i]; }
            catch (ArrayIndexOutOfBoundsException ex) { pixel1 = 0; }
            
            try { pixel2 = data2[i]; }
            catch (ArrayIndexOutOfBoundsException ex) { pixel2 = 0; }

            compare += Math.abs(pixel1 - pixel2);
        }
        return compare;
    }
}
