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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

/**
 * <p>Gestión de la configuración de la aplicación.</p>
 * <p>La configuración consiste una serie de parejas clave-valor que son
 * almacenadas en un fichero de texto plano en
 * <code>~/.firv/configuration.properties</code></p>
 * <p></p>
 * @author David Escribano García
 */
public class ConfigurationManager
{
    private static String propertiesDefault = "configuration/configuration.properties";
    private static String sqlDefault = "configuration/SQLite.sql";
    private static File propertiesFile;
    private static Properties properties = new Properties();

    /**
     * Lee los ficheros de configuración de la aplicación y carga sus propiedades
     * @throws IOException 
     */
    public static void initConfigurationManager() throws IOException
    {
        // Obtengo la version del fichero de configuracion embebido
        properties.clear();
        properties.load(ClassLoader.getSystemResourceAsStream(propertiesDefault));
        final int program_version = Integer.valueOf(properties.getProperty("system.version"));

        // Obtengo el fichero de configuracion de la carpeta del usuario
        String userdir = System.getProperties().getProperty("user.home");
        File confdir = new File(userdir + File.separator + ".firv");
        if (!confdir.exists())
        {
            confdir.mkdir();
        }
        propertiesFile = new File(confdir + File.separator + "configuration.properties");

        if (propertiesFile.exists() && propertiesFile.isFile())
        {
            // Si existe cargo las propiedades y comparo la version
            properties.clear();
            properties.load(new FileInputStream(propertiesFile));
            final String properties_version = properties.getProperty("system.version");
            if (properties_version == null || program_version > Integer.valueOf(properties_version))
            {
                properties.clear();
                properties.load(ClassLoader.getSystemResourceAsStream(propertiesDefault));
            }

        }
    }

    /**
     * Obtiene el script para la generación de una nueva base de datos.
     * @return Codigo SQL para generar base de datos
     * @throws IOException 
     */
    public static String[] getSQLInit() throws IOException
    {
        StringBuilder script = new StringBuilder();
        BufferedReader input =  new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(sqlDefault)));
        
        String line;
        while ((line = input.readLine()) != null)
        {
            script.append(line);
            script.append("\n");
        }
        return script.toString().split(";");
    }

    /**
     * Obtiene el valor de una propiedad.
     * @param key Clave de la propiedad
     * @return Valor de la propiedad 
     */
    public static String getProperty(final String key)
    {
        String value = null;
        try
        {
            value = properties.getProperty(key);
        }
        finally
        {
            if (value == null)
                value = "";
        }
        return value;
    }

    /**
     * Establece el valor de una propiedad de la aplicación.
     * @param key Clave de la propiedad
     * @param value Valor de la propiedad
     */
    public static void setProperty(String key, String value)
    {
        properties.setProperty(key, value);
    }

    /**
     * Establece la configuración regional de la aplicación.
     * @param locale Configuración regional
     */
    public static void setLocale(Locale locale)
    {
        Locale.setDefault(locale);

    }

    /**
     * Guarda las propiedades modificadas en el disco.
     * @throws IOException 
     */
    public static void save() throws IOException
    {
        FileOutputStream out = new FileOutputStream(propertiesFile);
        properties.store(out, "");
        out.close();
    }

}
