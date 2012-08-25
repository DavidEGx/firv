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

import es.davideg.uned.ii.proyecto.video.Video;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

/**
 * Modelo de tabla que contiene una lista de videos
 * @author David Escribano García
 */
public class VideoTableModel extends DefaultTableModel
{

    final static int IMAGE_COLUMN = 0;
    final static int NAME_COLUMN  = 1;
    final static int HASH_COLUMN  = 2;
    final static int VIDEO_COLUMN = 3;
    final static int NEW_COLUMN   = 4;
    final static int DEL_COLUMN   = 5;

    /**
     * Constructor por defecto
     */
    public VideoTableModel()
    {
        super(null, new String[] { ""});
        final ResourceBundle bundle = java.util.ResourceBundle.getBundle("configuration/language");
        super.addColumn(bundle.getString("videopanel.name"));
        super.addColumn(bundle.getString("videopanel.hash"));
        super.addColumn("Video");
        super.addColumn("New");
        super.addColumn("Deleted");
    }

    /**
     * Añade un nuevo video al modelo
     * @param video Video que se añade
     */
    public void addRow(Video video)
    {
        this.addRow(new Object[] {null, video.getName(), video.getHash(), video, false, false});
    }

    /**
     * Elimina todas las filas del modelo
     */
    public void clear()
    {
        while(this.getRowCount() > 0)
        {
            this.removeRow(0);
        }
    }

    /**
     * Devuelve el video correspondiente a la fila recibida como
     * parametro
     * @param row
     * @return Video
     */
    public Video getVideo(int row)
    {
        return (Video)getValueAt(row, VIDEO_COLUMN);
    }
    
    public List<Video> getNewVideos()
    {
        List newVideos = new ArrayList();
        for(int row = 0; row < this.getRowCount(); row++)
        {
            if (this.isNew(row))
            {
                Video video = this.getVideo(row);
                newVideos.add(video);
            }
        }
        return newVideos;
    }
    
    public List<Video> getDeletedVideos()
    {
        List deletedVideos = new ArrayList();
        for(int row = 0; row < this.getRowCount(); row++)
        {
            if (this.isDeleted(row))
            {
                Video video = this.getVideo(row);
                deletedVideos.add(video);
            }
        }
        return deletedVideos;
    }

    /**
     * Identifica el tipo de dato existente en cada columna
     * @param column Columna que se quiere consultar
     * @return Clase correspondiente a la columna
     */
    @Override
    public Class getColumnClass(int column)
    {
        switch (column)
        {
            case IMAGE_COLUMN : return javax.swing.ImageIcon.class;
            case NAME_COLUMN  : return String.class;
            case HASH_COLUMN  : return String.class;
            case VIDEO_COLUMN : return Video.class;
            case NEW_COLUMN   : return Boolean.class;
            case DEL_COLUMN   : return Boolean.class;
            default : return Object.class;
        }
    }

    /**
     * Comprueba si una fila es nueva
     * @param row Fila a comprobar
     * @return Verdadero si la fila es nueva
     */
    public boolean isNew(int row)
    {
        Object isnew = this.getValueAt(row, NEW_COLUMN);
        if (isnew != null && (Boolean)isnew == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Comprueba si una fila esta marcada para eliminar
     * @param row Fila a comprobar
     * @return Verdadero si la fila esta marcada para eliminar
     */
    public boolean isDeleted(int row)
    {
        Object isdeleted = this.getValueAt(row, DEL_COLUMN);
        if (isdeleted != null && (Boolean)isdeleted == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Marca una fila como nueva en el modelo de datos
     * @param row Fila a marcar
     * @param isNew Indica si la fila es nueva o no
     */
    public void setNew(int row, boolean isNew)
    {
        this.setValueAt(isNew, row, NEW_COLUMN);
        if (isNew)
        {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/icon_video_new.png"));
            this.setValueAt(icon, row, IMAGE_COLUMN);
        }
        else
        {
            this.setValueAt(null, row, IMAGE_COLUMN);
        }
    }

    /**
     * Marca una fila en el modelo para eliminar
     * @param row Fila a marcar
     * @param isDeleted Indica se marca para eliminar o para mantener
     */
    public void setDeleted(int row, boolean isDeleted)
    {
        this.setValueAt(isDeleted, row, DEL_COLUMN);
        if (isDeleted)
        {
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/icon_video_remove.png"));
            this.setValueAt(icon, row, IMAGE_COLUMN);
        }
        else
        {
            this.setValueAt(null, row, IMAGE_COLUMN);
        }
    }

    /**
     * Comprueba si se ha modificado el modelo
     * @return True si el modelo ha sido modificado
     */
    public boolean hasChanged()
    {
        for(int row = 0; row < this.getRowCount(); row++)
        {
            if (isDeleted(row) || isNew(row))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @return Devuelve el numero de videos nuevos en el modelo
     */
    public int getNewCount()
    {
        int count = 0;
        for (int row = 0; row < this.getRowCount(); row++)
        {
            if (isNew(row))
                count++;
        }
        return count;
    }

    /**
     * @return Devuelve el numero de videos eliminados en el modelo
     */
    public int getDeletedCount()
    {
        int count = 0;
        for (int row = 0; row < this.getRowCount(); row++)
        {
            if (isDeleted(row))
                count++;
        }
        return count;
    }

    /**
     * Comprueba si una celda es editable.
     * Se devuelve siempre falso ya que no se permite la edicion de celdas
     * @param row
     * @param column
     * @return False
     */
    @Override
    public boolean isCellEditable(int row, int column)
    {
        return false;
    }
}
