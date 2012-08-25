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

import es.davideg.uned.ii.proyecto.image.filter.ResizeFilter;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileView;


/**
 * Panel para previsualizacion de ficheros que puede ser añadido a un
 * JFileChooser.
 * @author David Escribano García
 */
public class FileChooserPreview extends javax.swing.JPanel implements PropertyChangeListener
{
    private static final int PREV_SIZE = (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.40);
    private int width, height;
    private Image image;
    private Color bg;
    private JFileChooser chooser;
    private String[] imageExtensions;

    /**
     * Crea un nuevo FileChooserPreview.
     * @param chooser JFileChooser al que se añade el panel de previsualizacion creado
     * @param imageExtensions Extensiones de imagenes que se previsualizan
     */
    public FileChooserPreview(final JFileChooser chooser
                            , final String[] imageExtensions)
    {
        initComponents();
        this.imageExtensions = imageExtensions;
        setPreferredSize(new Dimension(PREV_SIZE, -1));
        bg = getBackground();

        this.chooser = chooser;
        FileView view = new FileChooserView(imageExtensions, sizeSelector.getValue(), sizeSelector.getValue());
        chooser.setFileView(view);
    }

    /**
     * Cambia la imagen mostrada cuando se selecciona otra imagen diferente.
     * @param e Evento
     */
    @Override
    public void propertyChange(PropertyChangeEvent e)
    {
        String propertyName = e.getPropertyName();
        
        // Make sure we are responding to the right event.
        if (propertyName.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
        {
            File selection = (File)e.getNewValue();
            String name;
            
            if (selection == null)
                return;
            else
                name = selection.getAbsolutePath();
            
            /*
             * Make reasonably sure we have an image format that AWT can
             * handle so we don't try to draw something silly.
             */
            if (name != null)
            {
                for (String extension: imageExtensions)
                {
                    if (name.toLowerCase().endsWith(extension))
                    {
                        try
                        {
                            image = ImageIO.read(selection);
                            ResizeFilter resize = new ResizeFilter();
                            resize.setDimensions(getWidth(), getHeight(), true);
                            image = resize.filter(image, null);
                            width = image.getWidth(this);
                            height = image.getHeight(this);
                            break;
                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
                repaint();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        g.setColor(bg);
        
        /*
         * If we don't do this, we will end up with garbage from previous
         * images if they have larger sizes than the one we are currently
         * drawing. Also, it seems that the file list can paint outside
         * of its rectangle, and will cause odd behavior if we don't clear
         * or fill the rectangle for the accessory before drawing. This might
         * be a bug in JFileChooser.
         */
        g.fillRect(0, 0, PREV_SIZE, getHeight());
        g.drawImage(image
                  , getWidth() / 2 - width / 2 + 5
                  , getHeight() / 2 - height / 2
                  , this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sizeSelector = new javax.swing.JSlider();

        sizeSelector.setMaximum(500);
        sizeSelector.setMinimum(10);
        sizeSelector.setValue(100);
        sizeSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                sizeSelectorMouseReleased(evt);
            }
        });
        sizeSelector.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSelectorStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sizeSelector, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sizeSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(276, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void sizeSelectorStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSelectorStateChanged
    int size = sizeSelector.getValue();
    FileView view = new FileChooserView(imageExtensions, size, size);
    chooser.setFileView(view);
    chooser.repaint();
}//GEN-LAST:event_sizeSelectorStateChanged

private void sizeSelectorMouseReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_sizeSelectorMouseReleased
{//GEN-HEADEREND:event_sizeSelectorMouseReleased
    chooser.updateUI();
}//GEN-LAST:event_sizeSelectorMouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider sizeSelector;
    // End of variables declaration//GEN-END:variables
}
