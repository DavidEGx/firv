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

package es.davideg.uned.ii.proyecto;

import es.davideg.uned.ii.proyecto.gui.MainForm;
import java.awt.Frame;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Herramienta para la búsqueda de imágenes similares en bases de datos
 * de fusión.
 * @author David Escribano García
 * @version 0.3
 * @see <a href="http://www.uned.es">UNED</a>
 * @see <a href="http://www.ciemat.es">CIEMAT</a>
 * @see <a href="http://www.dia.uned.es/pfc/ciemat.pdf">Descripción del proyecto</a>
 */
public class Main
{
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * <p>Inicio de la aplicacion.</p>
     * <p>Se prepara la configuración de la aplicación y se inicia la interfaz
     * grafica</p>
     * @param args
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InterruptedException
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException
    {
        ConfigurationManager.initConfigurationManager();
        configureLog();

        logger.info("Iniciando programa");
        MainForm form = new MainForm();
        form.setExtendedState(Frame.MAXIMIZED_BOTH);
        form.setVisible(true);
    }

    /**
     * Configura el log del programa.
     * El log establecido será el que figure bajo la clave system.log.level
     * del fichero de configuración (configuration.properties).
     * Si se detecta cualquier tipo de error se establecerá el máximo
     * nivel de log (Level.ALL)
     */
    private static void configureLog()
    {
        // Leo el nivel de log del ficherod e configuración
        String strLevel = ConfigurationManager.getProperty("system.log.level");

        // Establezo el nivel de log que figura en el fichero de configuración
        Level level = null;
        try
        {
            level = Level.parse(strLevel);
        }
        catch (Exception ex)
        {
            level = Level.WARNING;
            logger.warning("Nivel de log incorrecto en el fichero de configuración. Se establecerá el nivel máximo.");
        }
        Logger.getLogger("").setLevel(level);
        for (Handler hd : Logger.getLogger("").getHandlers())
        {
            hd.setLevel(level);
        }
        logger.log(Level.FINE, "El nivel de log de la aplicación ha sido establecido a {0}", level.getName());
    }

}
