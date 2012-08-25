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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Selector de ficheros basado en JFileChooser que añade previsualizacion
 * de imagenes.
 * @author David Escribano García
 */
public class ImageChooser extends JFileChooser
{

    /**
     * Crea un nuevo dialogo para seleccionar imagenes.
     * @param imageExtensions Extensiones de ficheros que son consideradas imagenes
     */
    public ImageChooser(final String[] imageExtensions)
    {
        super();

        FileChooserPreview fcp = new FileChooserPreview(this, imageExtensions);
        this.addPropertyChangeListener(fcp);
        this.setAccessory(fcp);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        dimension.height = dimension.height - 100;
        dimension.width = dimension.width - 100;
        this.setPreferredSize(dimension);

        this.setFileFilter(
            new FileFilter()
            {
                @Override
                public boolean accept(File pathname)
                {
                    if (pathname.isDirectory())
                        return true;

                    String filename = pathname.getName().toLowerCase();
                    for (String extension : imageExtensions)
                    {
                        if (filename.toLowerCase().endsWith(extension))
                            return true;
                    }
                    return false;
                }

                @Override
                public String getDescription()
                {
                    return "Imagenes";
                }
            }
        );

    }
}
