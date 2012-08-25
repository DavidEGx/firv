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

import es.davideg.uned.ii.proyecto.ConfigurationManager;
import es.davideg.uned.ii.proyecto.image.ImageProcessor;
import es.davideg.uned.ii.proyecto.crypt.Hash;
import es.davideg.uned.ii.proyecto.image.DatabaseImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Clase que representa a un video.
 * Divide un video en frames y aplica un filtro a cada uno de ellos.
 * @author David Escribano García
 */
public final class Video
{

    private String name;            // Nombre del video
    private File file;              // Fichero de video
    private String hash;            // Hash que identifica univocamente al video
    private File framesDir;         // Directorio temporal donde se guardan las imagenes
    private Vector<DatabaseImage> images = new Vector<DatabaseImage>();      // Imagenes a las que ya se han aplicado los filtros
    private static final Logger logger = Logger.getLogger(Video.class.getName());

    /**
     * Crea un video
     * @param f Fichero con el video a procesar
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Video(final File f) throws NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        this(f.getName(), f);
    }

    /**
     * Crea un video
     * @param name Nombre del video
     * @param f Fichero con el video a procesar
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Video(final String name, final File f) throws NoSuchAlgorithmException, FileNotFoundException, IOException
    {
        this(name, f, Hash.getHash(f));
    }


    public Video(final String name, final File f, final String hash)
    {
        this.setName(name);
        this.setFile(f);
        this.setHash(hash);
    }

    public Video(final String name, final File f, final String hash, final File framesDir)
    {
        this(name, f, hash);
        this.setFramesDir(framesDir);
    }


    /**
     * Asigna el nombre al video
     * @param name Nombre del video
     */
    private void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @return Devuelve el nombre del video
     */
    public String getName()
    {
        return name;
    }

    /**
     * Indica el fichero de video que se va a procesar
     * @param f video a procesar
     */
    private void setFile(final File f)
    {
        this.file = f;
    }

    /**
     * @return Fichero que se esta procesando
     */
    public File getFile()
    {
        return file;
    }

    /**
     * Establece un hash unico que identifica al video
     */
    public void setHash(final String hash)
    {
        this.hash = hash;
    }

    /**
     * @return Hash que identifica al video
     */
    public String getHash()
    {
        return hash;
    }

    public void addImage(final DatabaseImage image)
    {
        images.add(image);
    }

    /**
     * Obtiene los frames de un video y los guarda como imagenes. Se
     * guardan en una carpeta cuyo nombre es el hash del video
     * ubicada dentro del directorio recibido como parametro.
     * @param tmpDir Directorio donde se van a colocar los frames
     * @param width Anchura de las imagenes generadas
     * @param height Altura de las imagenes generadas
     * @return Carpeta donde se encuentran los frames del video
     * @throws IOException
     * @throws InterruptedException
     */
    public File generateImages(final File tmpDir, final int width, final int height) throws IOException, InterruptedException
    {
    
        class StreamPrinter implements Runnable
        {
            InputStream is;
            String type;
            
            public StreamPrinter(InputStream is, String type)
            {
                this.is = is;
                this.type = type;
            }
            
            @Override
            public void run()
            {
                try
                {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String line = null;
                    while ((line = br.readLine()) != null)
                        logger.log(Level.INFO, "{0}>{1}", new Object[]{type, line});
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }


        // Creo la carpeta donde se van a guardar las imagenes
        this.framesDir = new File(Matcher.quoteReplacement(tmpDir.getAbsoluteFile() + File.separator + this.getHash()));
        this.getFramesDir().mkdirs();

        // Proceso el video
        Process p = Runtime.getRuntime().exec(
            new String[]
            {
                  "ffmpeg"
                , "-i"
                , Matcher.quoteReplacement(file.getAbsolutePath())
                ,"-pix_fmt"
                , "gray"
                , "-s"
                , width + "x" + height
                , "-threads"
                , "2"
                , "-an"
                , Matcher.quoteReplacement(getFramesDir().getAbsolutePath() + File.separator + "output%d.bmp")
            }
        );
        StreamPrinter stdStream = new StreamPrinter(p.getInputStream(), "FFMPEG");
        StreamPrinter errStream = new StreamPrinter(p.getErrorStream(), "FFMPEG");

        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(stdStream);
        exec.submit(errStream);
        exec.shutdown();

        int exitVal = p.waitFor();

        logger.log(Level.INFO, "Ffmpeg ExitValue: {0}", exitVal);

        return this.getFramesDir();
    }

    /**
     * Procesa cada uno de los frames del video aplicando a cada imagen
     * un filtro haar. Las imagenes resultantes quedan almacenadas en
     * images.
     */
    public void generateFilteredImages(final int width, final int height)
    {
        int maxThreads;
        try
        {
            maxThreads = Integer.parseInt(ConfigurationManager.getProperty("system.thread.filter"));
        }
        catch(Exception ex)
        {
            maxThreads = 1;
            logger.log(Level.WARNING, "No se puede obtener el numero maximo de threads: {0}", ex.getMessage());
        }
        logger.log(Level.INFO, "Se usaran {0} threads para procesar las imagenes", maxThreads);
        
        ExecutorService exec = Executors.newFixedThreadPool(maxThreads);

        for(File f : this.getFramesDir().listFiles())
        {
            int position = Integer.valueOf(f.getName().replaceAll("\\D", ""));
            exec.execute(new ImageProcessor(f, position, width, height, images));
        }
        exec.shutdown();
        try
        {
            exec.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
        }
        catch (InterruptedException ex)
        {
            logger.log(Level.SEVERE, "Error al procesar las imagenes: {0}", ex.getMessage());
        }
    }

    /**
     * @return Lista con los frames que componen el video
     */
    public List<DatabaseImage> getImages()
    {
        return images;
    }

    /**
     * @return Cadena con la descripión del video
     */
    @Override
    public String toString()
    {
        return getName() + " [Hash: "+ getHash() + "]";
    }

    public void setFramesDir(File directory)
    {
        this.framesDir = directory;
    }

    /**
     * @return the tmpDir
     */
    public File getFramesDir()
    {
        return framesDir;
    }

    
    @Override
    public boolean equals(Object v2)
    {
        if (v2 == null)
            return false;
        if (!(v2 instanceof Video))
            return false;
        
        String v1hash = this.getHash();
        String v2hash = ((Video)v2).getHash();
        return (v2hash.equals(v1hash));
    }

    @Override
    public int hashCode()
    {
        int myhash = 7;
        myhash = 73 * myhash + (this.hash != null ? this.hash.hashCode() : 0);
        return myhash;
    }
}
