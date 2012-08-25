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

import es.davideg.uned.ii.proyecto.image.filter.HaarFilter;
import es.davideg.uned.ii.proyecto.image.filter.ResizeFilter;
import es.davideg.uned.ii.proyecto.image.filter.GrayscaleFilter;
import es.davideg.uned.ii.proyecto.image.filter.NumericFilter;
import es.davideg.uned.ii.proyecto.image.filter.ThresholdFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.ImageIO;

/**
 * Representa una imagen buscada.
 * @author David Escribano García
 */
public class SearchedImage
{
    private File imageFile;
    private BufferedImage originalImage;
    private BufferedImage waveletImage;
    private BigInteger waveletValue;
    
    /**
     * Crea un nuevo objecto SearchedImage.
     * @param f Fichero con la imagen que se esta buscando
     * @param w Ancho de la imagen
     * @param h Alto de la imagen
     * @throws IOException Si no se puede leer el fichero
     */
    public SearchedImage(final File f, final int w, final int h) throws IOException
    {
        this.imageFile = f;

        // Convierto la imagen a escala de grises
        GrayscaleFilter grayscaleFilter = new GrayscaleFilter(GrayscaleFilter.METHOD_GIMP_LUMINOSITY);
        originalImage = grayscaleFilter.filter(ImageIO.read(f), null);

        // Redimensiono la imagen
        ResizeFilter resize = new ResizeFilter();
        resize.setDimensions(w, h, false);
        originalImage = resize.filter(originalImage, null);
    }

    /**
     * Devuelve el fichero que contiene la imagen buscada.
     * @return Fichero con la imagen
     */
    public File getFile()
    {
        return imageFile;
    }

    /**
     * Devuelve la imagen que se esta buscando.
     * @return Imagen buscada
     */
    public BufferedImage getImage()
    {
        return originalImage;
    }

    /**
     * Devuelve la imagen wavelet resultante de aplicar los filtros a la imagen
     * buscada.
     * @return Imagen wavelet
     */
    public BufferedImage getWaveletImage()
    {
        return waveletImage;
    }

    /**
     * Devuelve el valor numerico que representa la imagen.
     * @return Valor numerico de la wavelet
     */
    public BigInteger getWaveletValue()
    {
        return this.waveletValue;
    }

    /**
     * Genera la imagen wavelet a partir de la imagen de entrada.
     * @param w Ancho de la wavelet
     * @param h Alto de la wavelet
     */
    public void filter(final int w, final int h)
    {
        // Aplico filtro haar
        HaarFilter haar = new HaarFilter(w, h);
        BufferedImage image = haar.filter(originalImage, null);

        // Aplico umbral
        ThresholdFilter threshold = new ThresholdFilter();
        waveletImage = threshold.filter(image, null);

        // Me quedo con el resultado numerico
        NumericFilter numericFilter = new NumericFilter();
        waveletValue = numericFilter.filter(waveletImage);
    }
}
