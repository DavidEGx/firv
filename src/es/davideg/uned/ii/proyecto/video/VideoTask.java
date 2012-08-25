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
package es.davideg.uned.ii.proyecto.video;

import es.davideg.uned.ii.proyecto.crypt.Hash;
import es.davideg.uned.ii.proyecto.db.DbManager;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * <p>Clase encargada de procesar los videos y añadirlos a la base de datos.</p>
 * @author David Escribano García
 */
public class VideoTask extends SwingWorker<Boolean, Object>
{
    private DbManager   dbManager;  // Base de datos donde se añaden/elimina videos
    private List<Video> newVideos;  // Lista de videos a añadir
    private List<Video> oldVideos;  // Lista de videos a eliminar
    
    // Mensajes de error
    private StringBuffer errorMessage = new StringBuffer();

    private final ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
    private static final Logger logger = Logger.getLogger(VideoTask.class.getName());

    private File IMAGE_PATH;
    private int IMAGE_WIDTH;
    private int IMAGE_HEIGHT;
    private int WAVELET_WIDTH;
    private int WAVELET_HEIGHT;
    
    // Contadores para medir tiempos
    private long timeHash = 0;
    private long timeFrames = 0;
    private long timeProccess = 0;
    private long timeDb = 0;
    
    private int step = 10;

    /**
     * Crea un nuevo objecto VideoTask.
     * @param dbManager Base de datos en la que se añadirán o elimarán videos
     * @param newVideos Lista de videos a añadir
     * @param oldVideos Lista de videos a eliminar
     */
    public VideoTask(final DbManager dbManager
                   , final List<Video> newVideos
                   , final List<Video> oldVideos
    )
    {
        this.dbManager = dbManager;
        this.newVideos = newVideos;
        this.oldVideos = oldVideos;

        final double dstep = Math.floor(100 / (newVideos.size() * 4 + oldVideos.size()));
        step = (int)dstep;
    }

    /**
     * <p>Añade la lista de videos <code>newVideos</code> a la base de datos y
     * elimina de la base de datos los videos de la lista <code>oldVideos</code>
     * </p>
     * @return Verdadero si no hay errores
     */
    @Override
    public Boolean doInBackground()
    {
        final String strVideoAdd = bundle.getString("processing.add");
        final String strVideoRemove = bundle.getString("processing.remove");
        boolean errorFound = false;

        if (!getConfiguration())
            return false;
        
        // Añado los nuevos videos
        for(Video video : newVideos)
        {
            if (this.isCancelled()) return true;
            setMessage(MessageFormat.format(strVideoAdd, video.getName()));
            if (!addVideo(video))
                errorFound = true;
        }
        logger.log(Level.INFO, "Tiempo empleado en calcular hashes: {0}" + "\n" +
                              "Tiempo empleado en obtener frames: {1}" + "\n" +
                              "Tiempo empleado en procesar frames: {2}" + "\n" +
                              "Tiempo empleado en añadir a base de datos: {3}"
                , new Object[]{timeHash, timeFrames, timeProccess, timeDb});

        // Elimino los que se han borrado
        for(Video video : oldVideos)
        {
            if (this.isCancelled()) return true;
            setMessage(MessageFormat.format(strVideoRemove, video.getName()));
            if (!removeVideo(video))
                errorFound = true;
        }
        
        // Fin del proceso
        try { dbManager.disconnect(); }
        catch (SQLException ex) { logger.log(Level.SEVERE, "No se ha podido desconectar de la base de datos", ex.getMessage()); }
        
        setProgress(100);
        logger.log(Level.INFO, "Proceso VideoAddTask finalizado");
        return !errorFound;
    }

    /**
     * Obtiene la configuración de la base de datos.
     * @return Verdadero si ha obtenido la información correctamente
     */
    private boolean getConfiguration()
    {
        // Obtengo configuracion
        try
        {
            IMAGE_PATH = new File(dbManager.getProperty(DbManager.DbProperty.FRAMES_PATH));
        }
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "No se puede obtener la ruta donde hay que guardar las imagenes: {0}", ex.getMessage());
            errorMessage.append(bundle.getString("error.processing.wrong_path_0"));
            errorMessage.append("\n");
            return false;
        }

        try
        {
            IMAGE_WIDTH    = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.IMAGE_SIZE_X));
            IMAGE_HEIGHT   = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.IMAGE_SIZE_Y));
            WAVELET_WIDTH  = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.HAAR_SIZE_X));
            WAVELET_HEIGHT = Integer.valueOf(dbManager.getProperty(DbManager.DbProperty.HAAR_SIZE_Y));
        }
        catch(Exception ex)
        {
            logger.log(Level.SEVERE, "No se puede obtener el tamaño de la imagen objetivo: {0}", ex.getMessage());
            errorMessage.append(bundle.getString("error.processing.wrong_size_0"));
            errorMessage.append("\n");
            return false;
        }
        return true;
    }

    /**
     * <p>Añade un vídeo a la base de datos.</p>
     * <p>Esta tarea realiza basicamente los siguientes pasos: 
     *      <ol>
     *          <li>Genera imágenes correspondientes a los frames del vídeo.</li>
     *          <li>Procesa cada uno de los frames usando un filtro wavelet</li>
     *          <li>Añade cada uno de los frames procesados a la base de datos</li>
     *      </ol>
     * </p>
     * @param video Vídeo a añadir
     * @return Verdadero si se ha añadido correctamente el vídeo
     */
    private boolean addVideo(final Video video)
    {
        try
        {
            Date now = new Date();

            if (!dbManager.existsVideo(video))
            {
                logger.log(Level.INFO, "Generando hash del video {0}", video.getName());
                video.setHash(Hash.getHash(video.getFile()));
                increaseProgress(step);
                timeHash += new Date().getTime() - now.getTime();
                now = new Date();

                logger.log(Level.INFO, "Obteniendo frames del video {0}", video.getName());
                video.generateImages(IMAGE_PATH, IMAGE_WIDTH, IMAGE_HEIGHT);
                increaseProgress(step);
                timeFrames += new Date().getTime() - now.getTime();
                now = new Date();

                logger.log(Level.INFO, "Procesando imagenes del video: {0}", video.getName());
                video.generateFilteredImages(WAVELET_WIDTH, WAVELET_HEIGHT);
                increaseProgress(step);
                timeProccess += new Date().getTime() - now.getTime();
                now = new Date();

                logger.log(Level.INFO, "Añadiendo el video {0} a la base de datos", video.getName());
                dbManager.addVideo(video);
                dbManager.disconnect();
                increaseProgress(step);
                timeDb += new Date().getTime() - now.getTime();
                now = new Date();
            }
            else
            {
                logger.log(Level.WARNING, "El video {0} ya existe en la base de datos. No se hace nada", video.getName());
                errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_exists_0"), video.getName()));
                errorMessage.append("\n");
                return false;
            }
        }
        catch (NoSuchAlgorithmException ex)
        {
            logger.log(Level.SEVERE, "No se puede calcular un hash para el video: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_hash_0"), video.getName()));
            errorMessage.append("\n");
            return false;
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "Error de lectura: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_read_0"), video.getName()));
            errorMessage.append("\n");
            return false;
        }
        catch (InterruptedException ex)
        {
            logger.log(Level.SEVERE, "Error: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_interrupted_0"), video.getName()));
            errorMessage.append("\n");
            return false;
        }
        catch(SQLException ex)
        {
            logger.log(Level.SEVERE, "Error de base de datos al añadir video: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_database_0"), video.getName()));
            errorMessage.append("\n");
            return false;
        }
        catch(ClassNotFoundException ex)
        {
            logger.log(Level.SEVERE, "Error de base de datos al añadir video: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_database_0"), video.getName()));
            errorMessage.append("\n");
            return false;
        }
        return true;
    }

    /**
     * <p>Elimina un vídeo de la base de datos.</p>
     * <p>Se realizan dos pasos:
     *      <ol>
     *          <li>Se elimina los frames del vídeo del disco</li>
     *          <li>Se elimina el video y los frames de la base de datos</li>
     *      </ol>
     * </p>
     * 
     * @param video Video a eliminar
     * @return Verdadero si no ha habido problemas
     */
    private boolean removeVideo(Video video)
    {
        boolean errorFound = false;
        logger.log(Level.INFO, "Eliminando video {0}", video.getName());

        try
        {
            String image_path = dbManager.getProperty(DbManager.DbProperty.FRAMES_PATH);
            image_path += File.separator + video.getHash();

            File imagesFolder = new File(image_path);
            for (File f : imagesFolder.listFiles())
            {
                f.delete();
            }
            imagesFolder.delete();
        }
        catch (Exception ex)
        {
            logger.log(Level.WARNING, "No se pueden eliminar las imagenes del disco: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.images_delete_0"), video.getName()));
            errorMessage.append("\n");
            errorFound = true;
        }

        try
        {
            dbManager.removeVideo(video);
        }
        catch(Exception ex)
        {
            logger.log(Level.SEVERE, "Error de base de datos al eliminar video: {0}", ex.getMessage());
            errorMessage.append(MessageFormat.format(bundle.getString("error.processing.video_deletedb_0"), video.getName()));
            errorMessage.append("\n");
            errorFound = true;
        }
        increaseProgress(step);
        return !errorFound;
    }
    

    /**
     * Devuelve el mensaje de error producido en la llamada al metodo <code>
     * doInBackground</code>
     * @return Mensaje de error
     */
    public String getError()
    {
        return errorMessage.toString();
    }

    /**
     * Incrementa el progreso de la tarea.
     * @param rise Incremento
     */
    private void increaseProgress(final int rise)
    {
        setProgress(getProgress() + rise);
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
