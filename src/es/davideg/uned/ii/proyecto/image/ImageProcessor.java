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
import es.davideg.uned.ii.proyecto.image.filter.NumericFilter;
import es.davideg.uned.ii.proyecto.image.filter.ThresholdFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Clase para procesar una imagen.
 * @author David Escribano García
 */
public class ImageProcessor implements Runnable
{
    private File file;
    private int width, height;
    private int frame;
    private Vector<DatabaseImage> images;
    private static final Logger logger = Logger.getLogger(ImageProcessor.class.getName());

    /**
     * Crea un objecto ImageProcessor.
     * @param file Fichero que contiene la imagen
     * @param frame Número de frame que corresponde a la imagen dentro del vídeo
     * @param width Ancho de la wavelet
     * @param height Alto de la wavelet
     * @param images Vector donde se añadira la imagen
     */
    public ImageProcessor(final File file
                        , final int frame
                        , final int width
                        , final int height
                        , Vector<DatabaseImage> images)
    {
        this.file = file;
        this.width = width;
        this.frame = frame;
        this.height = height;
        this.images = images;
    }

    /**
     * <p>Procesa una imagen y la añade al vector <code>images</code></p>
     * <p>La imagen procesada ya debe estar redimensionada al tamaño adecuado
     * y tiene que ser una imagen en blanco y negro. En este punto se realiza
     * el siguiente proceso:
     * <ul>
     *      <li>Generar una imagen wavelet de Haar a partir de la imagen original</li>
     *      <li>Umbralizar la imagen wavelet</li>
     *      <li>Generar un valor numerico a partir de la imagen umbralizada</li>
     *      <li>Usando el valor numerico se crea un ojbecto <code>DatabaseImage
     *      </code> y se añade a <code>images</code>
     * </ul></p>
     */
    @Override
    public void run()
    {
        try
        {
            // Aplico filtro haar
            HaarFilter haar = new HaarFilter(width, height);
            BufferedImage image = haar.filter(ImageIO.read(file), null);

            // Aplico umbral
            ThresholdFilter threshold = new ThresholdFilter();
            threshold.filter(image, image);

            // Me quedo con el resultado numerico
            NumericFilter numericFilter = new NumericFilter();
            BigInteger waveletValue = numericFilter.filter(image);
            
            // Añado la imagen
            DatabaseImage dbimage = new DatabaseImage(file, frame, waveletValue);
            images.add(dbimage);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "No se puede procesar la imagen {0}: {1}", new String[] {file.getName(), ex.getMessage()});
        }
        
    }
}
