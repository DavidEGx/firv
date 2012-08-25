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

package es.davideg.uned.ii.proyecto.gui.lib;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

/**
 * Proporciona una vista para un selector de ficheros en el que se previsualizan
 * las imágenes.
 * @author David Escribano García
 */
public class FileChooserView extends FileView
{
    private int width;
    private int height;
    private Pattern images_pattern;

    /**
     * Crea un nuevo objecto FileChooserView.
     * @param imageExtensions Lista de imágenes que se previsualizará
     * @param width Ancho de la previsualización
     * @param height Alto de la previsualización
     */
    public FileChooserView(final String[] imageExtensions
                         , final int width
                         , final int height)
    {
        this.width = width;
        this.height = height;

        StringBuffer sb = new StringBuffer(".*(");
        for (String extension : imageExtensions)
        {
            sb.append(extension);
            sb.append('|');
        }
        sb = sb.replace(sb.length(), sb.length(), ")");
        images_pattern = Pattern.compile(sb.toString());
    }

    /**
     * Devuelve el icono con la previsualización de un fichero.
     * @param file Fichero a previsualizar
     * @return Icono
     */
    @Override
    public Icon getIcon(final File file)
    {
        if (file.isDirectory())
        {
            return null;
        }

        String filename = file.getName().toLowerCase();
        Matcher isImage = images_pattern.matcher(filename);
        if (!isImage.matches())
        {
            // No es imagen
            return null;
        }

        // Es imagen, cargo icono
        try
        {
            Image originalImage = ImageIO.read(file);
            Image resizedImage  = originalImage.getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
            return new ImageIcon(resizedImage);
        }
        catch (IOException ex)
        {
            return null;
        }
    }
}
