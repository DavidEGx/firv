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
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import javax.imageio.ImageIO;

/**
 * Representa una imagen gestionada por la base de datos de la aplicación.
 * @author David Escribano García
 */
public class DatabaseImage
{
    private File file;
    private int frameNumber;
    private BigInteger waveletValue;
    
    /**
     * Crea una nueva imagen.
     * @param file Fichero que representa la imagen
     * @param frame Numero de frame que la imagen ocupa dentro de un video
     * @param waveletValue Valor numerico de la imagen
     */
    public DatabaseImage(final File file
                       , final int frame
                       , final BigInteger waveletValue)
    {
        this.file = file;
        this.frameNumber = frame;
        this.waveletValue = waveletValue;
    }
    
    /**
     * Devuelve el fichero al que hace referencia la imagen.
     * @return Fichero con la imagen
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Devuelve el número de frame que representa la imagen dentro de un vídeo.
     * @return Número de frame
     */
    public int getFrameNumber()
    {
        return this.frameNumber;
    }
    
    /**
     * Devuelve la imagen.
     * @return Imagen
     * @throws IOException 
     */
    public BufferedImage getImage() throws IOException
    {
        return ImageIO.read(file);
    }
    
    /**
     * Devuelve el valor de la wavelet correspondiente.
     * @return Valor de la wavelet
     */
    public BigInteger getWaveletValue()
    {
        return waveletValue;
    }
    
}
