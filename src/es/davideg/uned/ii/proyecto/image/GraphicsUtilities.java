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

package es.davideg.uned.ii.proyecto.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Utilidades graficas para la manipulación de imágenes.
 * @author David Escribano García
 */
public class GraphicsUtilities
{
    /**
     * <p>Devuelve un array con los pixeles de una imagen.</p>
     * <p> Mediante los parametros se puede obtener un subconjunto de los pixeles
     * de la imagen o bien todos los pixeles.</p>
     * @param img Imagen de la que se quieren obtener los pixeles
     * @param x Desplazamiento horizontal
     * @param y Desplazamiento vertical
     * @param w Tamaño horizontal
     * @param h Tamaño vertical
     * @param pixels Contenedor para los pixeles de la imagen, puede ser nulo
     * @return Pixeles de la imagen
     * @throws IllegalArgumentException Si los pixeles que se quieren obtener
     * (<code>w * h</code>) no cabe en el array de pixeles recibido como parametro
     */
    public static int[] getPixels(final BufferedImage img
                               , final int x, final int y
                               , final int w, final int h
                               , int[] pixels)
    {
        if (w == 0 || h == 0)
        {
            return new int[0];
        }
        if (pixels == null)
        {
            pixels = new int[w * h];
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB || imageType == BufferedImage.TYPE_INT_RGB)
        {
            Raster raster = img.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }
        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    /**
     * <p>Devuelve un array con los pixeles de una imagen de tipo BYTE_GRAY. </p>
     * <p>Mediante los parametros se puede obtener un subconjunto de los pixeles
     * de la imagen o bien todos los pixeles.</p>
     * @param img Imagen de la que se quieren obtener los pixeles
     * @param x Desplazamiento horizontal
     * @param y Desplazamiento vertical
     * @param w Tamaño horizontal
     * @param h Tamaño vertical
     * @param pixels Contenedor para los pixeles de la imagen, puede ser nulo
     * @return Pixeles de la imagen
     * @throws IllegalArgumentException Si la imagen no es de tipo TYPE_BYTE_GRAY
     * @throws IllegalArgumentException Si los pixeles que se quieren obtener
     * (<code>w * h</code>) no cabe en el array de pixeles recibido como parametro
     */
    public static byte[] getPixels(final BufferedImage img
                                , final int x, final int y
                                , final int w, final int h
                                , byte[] pixels)
    {
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY)
            throw new IllegalArgumentException("Image must be of type TYPE_BYTE_GRAY");
        
        if (w == 0 || h == 0)
        {
            return new byte[0];
        }
        if (pixels == null)
        {
            pixels = new byte[w * h];
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        img.getTile(0,0).getDataElements(x, y, w, h, pixels);
        return pixels;
    }

    /**
     * <p>Establece los pixeles de una imagen a partir de un array. </p>
     * <p>Mediante los parametros se establece que conjunto de pixeles se van a
     * establecer.</p>
     * @param img Imagen a la que se le van a modificar los pixeles
     * @param x Desplazamiento horizontal
     * @param y Desplazamiento vertical
     * @param w Tamaño horizontal
     * @param h Tamaño vertical
     * @param pixels Pixeles a modificar
     * @throws IllegalArgumentException Si el tamaño del array de pixeles a
     * asignar es menor que el número se quiere asignar (<code>w * h</code>)
     */
    public static void setPixels(BufferedImage img
                              , final int x, final int y
                              , final int w, final int h
                              , final int[] pixels)
    {
        if (pixels == null || w == 0 || h == 0)
        {
            return;
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
            imageType == BufferedImage.TYPE_INT_RGB)
        {
            WritableRaster raster = img.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        }
        else
        {
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }

    /**
     * <p>Establece los pixeles de una imagen de tipo TYPE_BYTE_GRAY a partir de un
     * array de bytes. </p>
     * <p>Mediante los parametros se establece que conjunto de pixeles se van a
     * establecer.</p>
     * @param img Imagen a la que se le van a modificar los pixeles
     * @param x Desplazamiento horizontal
     * @param y Desplazamiento vertical
     * @param w Tamaño horizontal
     * @param h Tamaño vertical
     * @param pixels Pixeles a modificar
     * @throws IllegalArgumentException Si la imagen no es de tipo TYPE_BYTE_GRAY
     * @throws IllegalArgumentException Si el tamaño del array de pixeles a
     * asignar es menor que el número se quiere asignar (<code>w * h</code>)
     */
    public static void setPixels(BufferedImage img
                              , final int x, final int y
                              , final int w, final int h
                              , final byte[] pixels)
    {
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY)
            throw new IllegalArgumentException("Image must be of type TYPE_BYTE_GRAY");

        if (pixels == null || w == 0 || h == 0)
        {
            return;
        }
        else if (pixels.length < w * h)
        {
            throw new IllegalArgumentException("pixels array must have a length >= w*h");
        }

        img.getWritableTile(x, y).setDataElements(x, y, w, h, pixels);
        img.releaseWritableTile(x, y);
    }
}
