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

package es.davideg.uned.ii.proyecto.db;

import es.davideg.uned.ii.proyecto.ConfigurationManager;
import es.davideg.uned.ii.proyecto.image.DatabaseImage;
import es.davideg.uned.ii.proyecto.image.SearchedImage;
import es.davideg.uned.ii.proyecto.video.Video;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Gestion la conexion con la base de datos</p>
 * 
 * <p>La base de datos de la aplicación será una base de datos sqlite con la
 * siguiente estructura: <br />
 * <code>
 *  <ul>
 *      <li>configuration: Configuración de la base de datos</li>
 *          <ul>
 *              <li>property: Nombre de la propiedad</li>
 *              <li>value: Valor de la propiedad</li>
 *          </ul>
 *      <li>videos: Lista de vídeos en la base de datos</li>
 *          <ul>
 *              <li>video_hash: Identificador único del vídeo</li>
 *              <li>video_name: Nombre del vídeo</li>
 *              <li>video_path: Ruta donde esta el vídeo</li>
 *          </ul>
 *      <li>images: Lista de imagenes en la base de datos</li>
 *          <ul>
 *              <li>image_haar: Valor numerico que representa la imagen</li>
 *              <li>image_number: Posición que ocupa la imagen dentro del vídeo</li>
 *              <li>video_hash: Video al que pertenece la imagen</li>
 *              <li>image_path: Ruta donde se encuentra la imagen</li>
 *          </ul>
 *  </ul>
 * </code></p>
 * @author David Escribano García
 */
public class DbManager
{

    private String connectionString;
    private Connection conn;
    private static final Logger logger = Logger.getLogger(DbManager.class.getName());

    /**
     * Listado de propiedades admitido en la base de datos
     */
    public enum DbProperty
    {
        VIDEO_PROCESSOR, FRAMES_PATH, IMAGE_SIZE_X, IMAGE_SIZE_Y, HAAR_SIZE_X, HAAR_SIZE_Y;
    };

    /**
     * Conecta con la base de datos definida en <code>connectionString</code>
     * @return Conexion con la base de datos
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private Connection connect() throws ClassNotFoundException, SQLException
    {
        if (conn == null || conn.isClosed())
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(connectionString);
        }
        return conn;
    }

    /**
     * Realiza la desconexion de la base de datos
     * @throws SQLException
     */
    public void disconnect() throws SQLException
    {
        if (conn != null && !conn.isClosed())
        {
            conn.close();
        }
    }

    @Override
    public void finalize() throws Throwable
    {
        if (conn != null && !conn.isClosed())
        {
            conn.close();
        }
        super.finalize();
    }

    /**
     * Crea una base de datos nueva.
     * Al crear la base de datos se ejecutan los comandos que figuran
     * en el fichero configuration/SQLite.sql
     * @throws ClassNotFoundException
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void createDb() throws ClassNotFoundException, SQLException, FileNotFoundException, IOException
    {
        conn = connect();

        // Creo las tablas
        Statement stat = conn.createStatement();
        String[] ddlStatements = ConfigurationManager.getSQLInit();
        for(String currentStat : ddlStatements)
        {
            if (currentStat != null && !currentStat.matches("^(\\n|\\s|\\t)*$"))
                stat.addBatch(currentStat);
        }
        stat.executeBatch();
        stat.close();
    }

    /**
     * Establece una propiedad de la aplicacion.
     * Las propiedades se almacenan en la tabla configuration de la base de
     * datos.
     * @param property Nombre de la propiedad
     * @param value Valor de la propiedad
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void setProperty(DbProperty property, String value) throws ClassNotFoundException, SQLException
    {
        conn = connect();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO configuration (property, value) VALUES (?,?)");
        ps.setString(1, property.toString());
        ps.setString(2, value);
        ps.execute();
        ps.close();
    }

    /**
     * Obtiene el valor de una propiedad desde la base de datos
     * @param property Nombre de la propiedad
     * @return Valor de la propiedad
     */
    public String getProperty(DbProperty property) throws ClassNotFoundException, SQLException
    {
        String value = null;
        conn = connect();
        PreparedStatement ps = conn.prepareStatement("SELECT value FROM configuration WHERE property = ?");
        ps.setString(1, property.toString());
        ResultSet rs = ps.executeQuery();
        if (rs.next())
        {
            value = rs.getString("value");
        }
        rs.close();
        if (value == null)
        {
            throw new SQLException("Propiedad no encontrada: " + property);
        }
        return value;
    }

    /**
     * Añade un video a la base de datos
     * @param video Video que se va a añadir
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void addVideo(Video video) throws ClassNotFoundException, SQLException
    {
        // Para que sean mas rapidos los inserts se hacen en bloques de 500
        final int maxInserts = 499;

        conn = connect();

        // Añado la instruccion para insertar el video
        Statement stat = conn.createStatement();
        stat.addBatch("INSERT INTO videos (video_hash, video_name, video_path)" +
                     " VALUES ('" + video.getHash() + "','" + video.getName() + "','" + video.getFile().getAbsolutePath() + "')");

        // Añado las instrucciones para insertar las imagenes
        int i = 0;
        StringBuffer currentInsert = new StringBuffer("INSERT INTO images (video_hash, image_number, image_haar, image_path) ");
        List<DatabaseImage> frames = video.getImages();
        for(DatabaseImage img : frames)
        {
            currentInsert.append(" SELECT '");
            currentInsert.append(video.getHash());
            currentInsert.append("', ");
            currentInsert.append(img.getFrameNumber());
            currentInsert.append(", '");
            currentInsert.append(img.getWaveletValue());
            currentInsert.append("', '");
            currentInsert.append(img.getFile().getPath());
            currentInsert.append("' union all ");
            if (i == maxInserts)
            {
                stat.addBatch(currentInsert.toString().replaceAll(" union all $", ""));
                currentInsert = new StringBuffer("INSERT INTO images (video_hash, image_number, image_haar, image_path) ");
                i = 0;
            }
            else
            {
                i++;
            }
        }
        if (i > 0)
        {
            stat.addBatch(currentInsert.toString().replaceAll(" union all $", ""));
        }

        // Ejecuto los inserts
        stat.executeBatch();
        stat.close();
    }

    /**
     * Elimina un video de la base de datos
     * @param video Video que se va a eliminar
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public void removeVideo(Video video) throws ClassNotFoundException, SQLException
    {
        conn = connect();

        // Borro las imagenes
        PreparedStatement ps = conn.prepareStatement("DELETE FROM images" +
                                                     " WHERE video_hash = ?");
        ps.setString(1, video.getHash());
        ps.execute();
        ps.close();

        // Borro el video
        ps = conn.prepareStatement("DELETE FROM videos" +
                                   " WHERE video_hash = ?");
        ps.setString(1, video.getHash());
        ps.execute();
        ps.close();
    }

    /**
     * Comprueba si existe un video en la base de datos
     * @param video
     * @return Verdadero si el video existe en la bdd, falso si no existe
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public boolean existsVideo(Video video) throws SQLException, ClassNotFoundException
    {
        boolean existe = false;

        conn = connect();

        PreparedStatement ps = conn.prepareStatement("SELECT video_hash" +
                                                     "  FROM videos" +
                                                     " WHERE video_hash = ?");
        ps.setString(1, video.getHash());
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            existe = rs.getBoolean(1);
        }
        return existe;
    }

    /**
     * Busca una imagen en la base de datos.
     * Se devolveran aquellas imagenes que tengan un mismo valor para la
     * transformada wavelet aplicada.
     * @param img Imagen a buscar en la base de datos
     * @return Lista de videos que contienen las imagenes encontradas.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public List<Video> searchImage(SearchedImage img) throws SQLException, ClassNotFoundException
    {
        int IMAGE_INTERVAL;
        try
        {
            IMAGE_INTERVAL = Integer.parseInt(ConfigurationManager.getProperty("image.consecutive"));
        }
        catch(Exception ex)
        {
            IMAGE_INTERVAL = 25;
        }
        List<Video> resultList = new ArrayList<Video> ();
        long count_wavelet = 0;
        long count_images = 0;

        conn = connect();
        PreparedStatement ps = conn.prepareStatement("   SELECT v.video_hash" +
                                                     "        , video_name" +
                                                     "        , video_path" +
                                                     "        , image_number" +
                                                     "        , image_path " +
                                                     "     FROM images i" +
                                                     "     join videos v on v.video_hash = i.video_hash" +
                                                     "    WHERE image_haar = ?" +
                                                     " ORDER BY v.video_hash" +
                                                     "        , image_number");
        ps.setString(1, img.getWaveletValue().toString());
        ResultSet rs = ps.executeQuery();
        String previous_hash = null, current_hash = null;
        int last_frame_added = -1, current_frame = -1;
        int consecutive_count = 0;

        Video video = null;
        while (rs.next())
        {
            count_wavelet++;
            current_hash  = rs.getString("video_hash");
            current_frame = rs.getInt("image_number");

            if (!current_hash.equals(previous_hash))
            {
                // Cambio de video
                if (video != null)
                {
                    resultList.add(video);
                }
                video = new Video(rs.getString("video_name"), new File(rs.getString("video_path")), current_hash);
                previous_hash  = current_hash;
                
                final File file = new File(rs.getString("image_path"));
                final DatabaseImage image = new DatabaseImage(file
                                                            , current_frame
                                                            , null);
                video.addImage(image);
                count_images++;
                
                last_frame_added = current_frame;
                consecutive_count = 1;
            }
            else
            {
                // Mismo video
                if (current_frame != last_frame_added + consecutive_count || consecutive_count > IMAGE_INTERVAL)
                {
                    final File file = new File(rs.getString("image_path"));
                    final DatabaseImage image = new DatabaseImage(file
                                                                , current_frame
                                                                , null);
                    video.addImage(image);
                    count_images++;

                    last_frame_added = current_frame;
                    consecutive_count = 1;
                }
                else
                {
                    consecutive_count++;
                }
            }
        }
        if (video != null)
        {
            resultList.add(video);
        }
        logger.log(Level.INFO, "Se han encontrado {0} imagenes con la misma wavelet.", count_wavelet);
        logger.log(Level.INFO, "Se procesaran {0} imagenes.", count_images);
        return resultList;
    }

    /**
     * Devuelve la lista de videos existentes
     * @return Lista de videos
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public List<Video> getVideoList() throws ClassNotFoundException, SQLException
    {
        List videoList = new ArrayList();
        conn = connect();

        PreparedStatement ps = conn.prepareStatement("   SELECT video_hash" +
                                                     "        , video_name" +
                                                     "        , video_path" +
                                                     "        , value || '/' || video_hash as frames_path" +
                                                     "     FROM videos" +
                                                     "     cross join configuration" +
                                                     "    WHERE property = ?" +
                                                     " ORDER BY video_name");
        ps.setString(1, DbProperty.FRAMES_PATH.toString());
        ResultSet rs = ps.executeQuery();
        while (rs.next())
        {
            videoList.add(new Video(rs.getString("video_name")
                                  , new File(rs.getString("video_path"))
                                  , rs.getString("video_hash")
                                  , new File(rs.getString("frames_path"))
                         )
            );
        }
        return videoList;
    }

    /**
     * Devuelve la cadena de conexión usada
     * @return Cadena de conexión
     */
    public String getConnectionString()
    {
        return connectionString;
    }

    /**
     * Estable la cadena de conexion
     * @param connectionString Cadena de conexion
     */
    public void setConnectionString(String connectionString)
    {
        this.connectionString = connectionString;
    }

    /**
     * Estable la cadena de conexion
     * @param f Fichero donde esta la base de datos
     */
    public void setConnectionString(File f)
    {
        this.connectionString = "jdbc:sqlite:" + f.getAbsolutePath();
    }

    /**
     * Comprueba si hay establecida una conexion
     * @return Verdadero si hay conexion, falso en caso contrario
     */
    public boolean isConnected()
    {
        if (connectionString == null)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /**
     * Devuelve el nombre de la base de datos a la que se esta conectado
     * @return Nombre de la base de datos
     */
    public String getDbName()
    {
        try
        {
            File f = new File(connectionString);
            return f.getName();
        }
        catch(Exception ex)
        {
            return "";
        }
    }
}
