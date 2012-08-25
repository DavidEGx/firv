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

import es.davideg.uned.ii.proyecto.db.DbManager;
import es.davideg.uned.ii.proyecto.image.filter.CompareFilter;
import es.davideg.uned.ii.proyecto.video.Video;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;



/**
 * <p>Tarea encarga de realizar una busqueda de una imagen.</p>
 * @author David Escribano García
 */
public class ImageTask extends SwingWorker<Boolean, Object>
{
    private DbManager dbManager;  // Base de datos donde se buscará la imagen
    private File imageFile;        // Imagen que se buscara
    private SearchedImage image;
    private Vector<ImageComparation> comparationResults;
    private boolean errorFound = false;

    private final ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
    private static final Logger logger = Logger.getLogger(ImageTask.class.getName());

    /**
     * Crea un nuevo objecto ImageTask
     * @param dbManager Base de datos donde se buscarán las imágenes
     * @param imageFile Imagen a buscar
     */
    public ImageTask(final DbManager dbManager, final File imageFile)
    {
        this.dbManager = dbManager;
        this.imageFile = imageFile;
    }

    /**
     * <p>Busca una imagen en la base de datos</p>
     * @return Verdadero si todo ha ido bien
     */
    @Override
    protected Boolean doInBackground()
    {
        setProgress(0);
        setMessage(bundle.getString("processing.image"));
        try
        {
            // Contadores para controlar los tiempos de ejecucion
            long timeProcess = 0;
            long timeSearchDb = 0;
            long timeSearchFiles = 0;
            Date now;

            // Obtengo los tamaños de las imagenes y de la wavelet
            final int IMAGE_WIDTH = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.IMAGE_SIZE_X));
            final int IMAGE_HEIGHT = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.IMAGE_SIZE_Y));
            final int WAVELET_WIDTH = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.HAAR_SIZE_X));
            final int WAVELET_HEIGHT = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.HAAR_SIZE_Y));

            // Creo la imagen
            image = new SearchedImage(imageFile, IMAGE_WIDTH, IMAGE_HEIGHT);
            now = new Date();

            // Proceso la imagen seleccionada
            image.filter(WAVELET_WIDTH, WAVELET_HEIGHT);
            timeProcess += new Date().getTime() - now.getTime();
            now = new Date();
            setProgress(1);
            setMessage(bundle.getString("processing.image.db"));
            if (this.isCancelled()) return true;

            // Busco la imagen en base de datos
            final List<Video> videos = dbManager.searchImage(image);
            timeSearchDb += new Date().getTime() - now.getTime();
            now = new Date();
            setProgress(10);
            if (this.isCancelled()) return true;

            // Refino la busqueda buscando en los ficheros originales
            comparationResults = compare(image, videos);

            timeSearchFiles += new Date().getTime() - now.getTime();
            
            int numberOfFiles = 0;
            for (Video v : videos)
            {
                numberOfFiles += v.getImages().size();
            }

            logger.log(Level.INFO,
                    "Tiempo empleado en procesar imagen: {0}" + "\n" +
                    "Tiempo empleado en buscar en la base de datos: {1}" + "\n" +
                    "Tiempo empleado en buscar en ficheros: {2} ({3} ficheros)",
                    new Object[]{timeProcess, timeSearchDb, timeSearchFiles, numberOfFiles}
            );
        }
        catch(Exception ex)
        {
            logger.log(Level.SEVERE, "Error al buscar la imagen: {0}", ex.getMessage());
            errorFound = true;
        }
        setProgress(100);
        return !errorFound;
    }
    
    
    /**
     * <p>Realiza una comparación pixel a pixel de la imagen recibida como primer
     * parametro con todas las imágenes recibidas en la lista de vídeos.</p>
     * @param image Imagen a comparar
     * @param videos Videos con imagenes a comparar
     * @return Restulado de la comparación
     * @throws IOException 
     */
    private Vector<ImageComparation> compare(final SearchedImage image, final List<Video> videos) throws IOException
    {
        Vector<ImageComparation> source = new Vector<ImageComparation>();
        if (videos == null || videos.isEmpty())
            return source;

        long contador = 0;
        final int video_step = (int)Math.floor(90 / videos.size());
        final CompareFilter cf = new CompareFilter(image.getImage());

        for (Video video : videos)
        {
            if (this.isCancelled()) return source;

            final String message = MessageFormat.format(bundle.getString("processing.image.video")
                                                     , video.getName());
            setMessage(message);
            List<DatabaseImage> images = video.getImages();
            for(DatabaseImage currentImage : images)
            {
                logger.log(Level.INFO, "Procesando imagen {0}", contador++);
                Long difference = cf.compare(currentImage.getImage());
                source.add(new ImageComparation(video, currentImage, difference));
            }
            setProgress(getProgress() + video_step);
        }
        Collections.sort(source, new ImageComparation());

        return source;
    }

    /**
     * Devuelve la imagen buscada.
     * @return Imagen buscada
     */
    public SearchedImage getSearchedImage()
    {
        return image;
    }

    /**
     * Devuelve los resultados de la comparación.
     * @return Resultados de la comparación
     */
    public Vector<ImageComparation> getCompartionResults()
    {
        return comparationResults;
    }
    
    /**
     * Envia el mensaje especificado a través de <code>firePropertyChange</code>
     * para informar sobre el progreso de la tarea.
     * @param message Información que se quiere enviar
     */
    private void setMessage(final String message)
    {
        firePropertyChange("message", null, message);
    }
}
