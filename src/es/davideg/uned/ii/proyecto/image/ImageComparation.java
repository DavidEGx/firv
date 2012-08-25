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

import es.davideg.uned.ii.proyecto.video.Video;
import java.util.Comparator;

/**
 * Representa el resultado de una comparación de imágenes.
 * @author David Escribano García
 */
public class ImageComparation implements Comparator<ImageComparation>
{
    private DatabaseImage image;
    private Video video;
    private Long difference;

    public ImageComparation() { }

    public ImageComparation(Video video, DatabaseImage image, Long difference)
    {
        this.video = video;
        this.image = image;
        this.difference = difference;
    }

    /**
     * Devuelve la imagen usada en la comparación.
     * @return Imagen
     */
    public DatabaseImage getImage()
    {
        return image;
    }

    /**
     * Devuelve el vídeo al que pertence la imagen comparada.
     * @return Vídeo
     */
    public Video getVideo()
    {
        return video;
    }

    /**
     * Devuelve la diferencia resultado de la comparación.
     * @return Diferencia
     */
    public Long getDifference()
    {
        return difference;
    }

    /**
     * Compara dos resultados de una comparación determinando cual es el
     * más parecido.
     * @param a Primer resultado de comparación
     * @param b Segundo resultado de comparación
     * @return Diferencia entre las comparaciones
     */
    @Override
    public int compare(ImageComparation a, ImageComparation b)
    {
        final long diff = (a.getDifference() - b.getDifference());

        if (diff > Integer.MAX_VALUE)
            return Integer.MAX_VALUE;
        else if (diff < Integer.MIN_VALUE)
            return Integer.MIN_VALUE;
        else
            return (int)diff;
    }
}