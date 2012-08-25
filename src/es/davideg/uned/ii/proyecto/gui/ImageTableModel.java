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

package es.davideg.uned.ii.proyecto.gui;

import es.davideg.uned.ii.proyecto.image.DatabaseImage;
import es.davideg.uned.ii.proyecto.image.filter.ResizeFilter;
import es.davideg.uned.ii.proyecto.video.Video;
import java.awt.Image;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * <p>Modelo que representa un listado de imágenes.</p>
 * <p>El modelo esta formado por las siguientes columnas:
 *      <ul>
 *          <li>Video      : Video al que pertence la imagen</li>
 *          <li>Nombre     : Nombre de la imagen</li>
 *          <li>Frame      : Número de frame de la imagen dentro del vídeo</li>
 *          <li>Diferencia : Diferencia de la imagen respecto a la imagen buscada</li>
 *          <li>image      : Representación grafica de la propia imagen</li>
 *      </ul>
 * </p>
 * @author David Escribano García
 */
public class ImageTableModel extends DefaultTableModel
{
    private static final Logger logger = Logger.getLogger(ImagePanel.class.getName());
    
    /**
     * Crea un nuevo ImageTableModel.
     */
    public ImageTableModel()
    {
        super(null, new String[] {"row"});
        final ResourceBundle bundle = java.util.ResourceBundle.getBundle("configuration/language");
        super.addColumn(bundle.getString("imagepanel.video"));
        super.addColumn(bundle.getString("imagepanel.imagename"));
        super.addColumn(bundle.getString("imagepanel.frame"));
        super.addColumn(bundle.getString("imagepanel.difference"));
        super.addColumn(bundle.getString("imagepanel.image"));
    }

    /**
     * Añade una nueva imagen.
     * @param itr Datos de la imagen
     * @param width Ancho para la imagen mostrada
     * @param height Alto para la imagen mostrada
     */
    public void addRow(final ImageTableRow itr, final int width, final int height)
    {
        final Object[] row = new Object[6];
        row[0] = itr;
        row[1] = itr.getVideo().getName();
        row[2] = itr.getImage().getFile().getName();
        row[3] = itr.getImage().getFrameNumber();
        row[4] = itr.getDifference();
        final Image originalImage;
        try
        {
            originalImage = itr.getImage().getImage();
            ResizeFilter resize = new ResizeFilter();
            resize.setDimensions(width, height, true);
            final Image resizedImage = resize.filter(originalImage, null);
            row[5] = new javax.swing.ImageIcon(resizedImage);
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "No se puede procesar la imagen: {0}", ex.getMessage());
            row[5] = null;
        }
        super.addRow(row);
    }

    /**
     * Comprueba si una celda es editable.
     * @param rowIndex Fila
     * @param mColIndex Columna
     * @return Falso, las celdas no son editables
     */
    @Override
    public boolean isCellEditable(final int rowIndex, final int mColIndex)
    {
        return false;
    }

    /**
     * Devuelve el tipo de datos de una columna.
     * @param column Columna
     * @return Clase
     */
    @Override
    public Class getColumnClass(final int column)
    {
        switch (column)
        {
            case 0 : return ImageTableRow.class;
            case 1 : return String.class;
            case 2 : return String.class;
            case 3 : return Long.class;
            case 4 : return Long.class;
            case 5 : return javax.swing.ImageIcon.class;
            default : return Object.class;
        }
    }

    /**
     * Obtiene al que pertence la imagen de una fila.
     * @param row Fila
     * @return Video
     */
    public Video getVideo(final int row)
    {
        ImageTableRow itr = (ImageTableRow)getValueAt(row, 0);
        return itr.getVideo();
    }

    /**
     * Obtiene la imagen que se encuentra en una fila.
     * @param row Fila
     * @return imagen
     */
    public DatabaseImage getImage(final int row)
    {
        ImageTableRow itr = (ImageTableRow)getValueAt(row, 0);
        return itr.getImage();
    }

    /**
     * Representa una imagen dentro de la tabla de imágenes encontradas.
     */
    public class ImageTableRow
    {
        private Video video;
        private DatabaseImage image;
        private long difference;

        /**
         * Devuelve el video al que pertence la imagen.
         * @return Video
         */
        public Video getVideo()
        {
            return video;
        }

        /**
         * Indica el video al que pertenece la imagen.
         * @param video 
         */
        public void setVideo(final Video video)
        {
            this.video = video;
        }

        /**
         * Devuelve la imagen.
         * @return Imagen
         */
        public DatabaseImage getImage()
        {
            return image;
        }

        /**
         * Estable la imagen.
         * @param image 
         */
        public void setImage(final DatabaseImage image)
        {
            this.image = image;
        }

        /**
         * Devuelve la diferencia respecto a la imagen buscada.
         * @return Diferencia
         */
        public long getDifference()
        {
            return difference;
        }

        /**
         * Establece la diferencia respecto a la imagen buscada.
         * @param difference 
         */
        public void setDifference(final long difference)
        {
            this.difference = difference;
        }
    }
}