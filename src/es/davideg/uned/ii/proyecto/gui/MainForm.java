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

import es.davideg.uned.ii.proyecto.gui.lib.ImageChooser;
import es.davideg.uned.ii.proyecto.gui.lib.FileDrop;
import es.davideg.uned.ii.proyecto.ConfigurationManager;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import es.davideg.uned.ii.proyecto.db.DbManager;
import es.davideg.uned.ii.proyecto.gui.lib.ButtonTabComponent;
import es.davideg.uned.ii.proyecto.image.ImageComparation;
import es.davideg.uned.ii.proyecto.image.ImageTask;
import es.davideg.uned.ii.proyecto.image.SearchedImage;
import es.davideg.uned.ii.proyecto.video.Video;
import es.davideg.uned.ii.proyecto.video.VideoTask;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Formulario principal de la aplicacion</p>
 * <p>A través de este formulario se acceden a todas las funcionalidades de la
 * apliación:
 *      <ul>
 *          <li>Crear una nueva base de datos</li>
 *          <li>Abrir una base de datos existente</li>
 *          <li>Añadir videos a la base de datos</li>
 *          <li>Eliminar videos</li>
 *          <li>Buscar imágenes</li>
 *      </ul>
 * </p>
 * 
 * @author David Escribano García
 */
public final class MainForm extends javax.swing.JFrame implements PropertyChangeListener, LocaleChangeListener
{
    private DbManager dbManager = new DbManager();
    private static final Logger logger = Logger.getLogger(MainForm.class.getName());
    private final Dimension FILECHOOSER_DIMENSION = new Dimension(800, 500);
    private boolean AM_I_PROCESSING = false;
    private VideoTask videoTask;
    private ImageTask imageTask;
    
    /**
     * Crea el formulario principal MainForm
     */
    public MainForm()
    {
        logger.info("Iniciando componentes");
        initComponents();

        // Codigo para permitir arrastrar videos
        FileDrop.Listener fileDropListener =
            new FileDrop.Listener()
            {
                String[] extensions = ConfigurationManager.getProperty("image.extension").split(",");

                @Override
                public void filesDropped(File[] files)
                {
                    if (searchButton.isEnabled())
                    {
                        imageSearch(files[0]);
                    }
                }
            };
        new FileDrop(null, searchButton, fileDropListener);

        // Abro la ultima base de datos usada
        logger.info("Obteniendo la configuracion de la ultima base de datos abierta");
        String lastDb = ConfigurationManager.getProperty("system.db");
        if (lastDb != null && !lastDb.equals(""))
        {
            if (new File(lastDb).exists())
            {
                logger.log(Level.INFO, "Abriendo base de datos {0}", lastDb);
                openDb(new File(lastDb));
            }
            else
            {
                logger.log(Level.WARNING, "No se puede abria la base de datos. Fichero {0} no disponible", lastDb);
                lastDb = null;
            }
        }

        logger.info("Configurando idioma de la interfaz");
        Locale locale = new Locale(ConfigurationManager.getProperty("system.language")
                                 , ConfigurationManager.getProperty("system.country"));
        configureLanguage(locale);

        // Habilito los controles
        logger.info("Habilitando controles");
        enableControls();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        videoTableMenu = new javax.swing.JPopupMenu();
        videoMenuOpen = new javax.swing.JMenuItem();
        progressBar = new javax.swing.JProgressBar();
        mainToolBar = new javax.swing.JToolBar();
        toolNewDb = new javax.swing.JButton();
        toolOpenDb = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        toolConfiguration = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        videoAdd = new javax.swing.JButton();
        videoDel = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        runButton = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        searchButton = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        panelBusqueda = new javax.swing.JTabbedPane();
        panelVideos = new es.davideg.uned.ii.proyecto.gui.VideoPanel();
        jToolBar1 = new javax.swing.JToolBar();
        cancelButton = new javax.swing.JButton();
        menubar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuNewDb = new javax.swing.JMenuItem();
        menuOpenDb = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        menuConfiguration = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        menuExit = new javax.swing.JMenuItem();
        menuVideo = new javax.swing.JMenu();
        menuNewVideo = new javax.swing.JMenuItem();
        menuDeleteVideo = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        menuOpenVideo = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        menuRunVideo = new javax.swing.JMenuItem();
        menuImage = new javax.swing.JMenu();
        menuImageSearch = new javax.swing.JMenuItem();
        menuLanguage = new javax.swing.JMenu();
        menuLangSpanish = new javax.swing.JMenuItem();
        menuLangEnglish = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        menuHelpAbout = new javax.swing.JMenuItem();

        videoMenuOpen.setText("Ver video");
        videoMenuOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoMenuOpenActionPerformed(evt);
            }
        });
        videoTableMenu.add(videoMenuOpen);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Herramienta para la búsqueda de imágenes similares en bases de datos de fusión");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        progressBar.setStringPainted(true);

        mainToolBar.setBorder(null);
        mainToolBar.setFloatable(false);
        mainToolBar.setRollover(true);
        mainToolBar.setOpaque(false);

        toolNewDb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_newdb.png"))); // NOI18N
        toolNewDb.setToolTipText("Crea una nueva base de datos");
        toolNewDb.setFocusable(false);
        toolNewDb.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolNewDb.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolNewDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolNewDbActionPerformed(evt);
            }
        });
        mainToolBar.add(toolNewDb);

        toolOpenDb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_opendb.png"))); // NOI18N
        toolOpenDb.setToolTipText("Abre una base de datos existente");
        toolOpenDb.setFocusable(false);
        toolOpenDb.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolOpenDb.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolOpenDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolOpenDbActionPerformed(evt);
            }
        });
        mainToolBar.add(toolOpenDb);
        mainToolBar.add(jSeparator1);

        toolConfiguration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_configure.png"))); // NOI18N
        toolConfiguration.setFocusable(false);
        toolConfiguration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolConfiguration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolConfigurationActionPerformed(evt);
            }
        });
        mainToolBar.add(toolConfiguration);
        mainToolBar.add(jSeparator8);

        videoAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_add.png"))); // NOI18N
        videoAdd.setToolTipText("Añade un video");
        videoAdd.setEnabled(false);
        videoAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoAddActionPerformed(evt);
            }
        });
        mainToolBar.add(videoAdd);

        videoDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_remove.png"))); // NOI18N
        videoDel.setToolTipText("Elimina un video");
        videoDel.setEnabled(false);
        videoDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                videoDelActionPerformed(evt);
            }
        });
        mainToolBar.add(videoDel);
        mainToolBar.add(jSeparator2);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_run.png"))); // NOI18N
        runButton.setToolTipText("Procesa los cambios marcados");
        runButton.setEnabled(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(runButton);
        mainToolBar.add(jSeparator3);

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icon_search.png"))); // NOI18N
        searchButton.setToolTipText("Busca una imagen en la base de datos");
        searchButton.setEnabled(false);
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        mainToolBar.add(searchButton);
        mainToolBar.add(jSeparator9);

        panelBusqueda.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelBusquedaMouseClicked(evt);
            }
        });
        panelBusqueda.addTab("Lista de videos", panelVideos);

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setOpaque(false);

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/stop.png"))); // NOI18N
        cancelButton.setEnabled(false);
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(cancelButton);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("configuration/language"); // NOI18N
        menuFile.setText(bundle.getString("menu.file")); // NOI18N

        menuNewDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        menuNewDb.setText(bundle.getString("menu.file.new")); // NOI18N
        menuNewDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewDbActionPerformed(evt);
            }
        });
        menuFile.add(menuNewDb);

        menuOpenDb.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        menuOpenDb.setText(bundle.getString("menu.file.open")); // NOI18N
        menuOpenDb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenDbActionPerformed(evt);
            }
        });
        menuFile.add(menuOpenDb);
        menuFile.add(jSeparator6);

        menuConfiguration.setText(bundle.getString("menu.file.configuration")); // NOI18N
        menuConfiguration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuConfigurationActionPerformed(evt);
            }
        });
        menuFile.add(menuConfiguration);
        menuFile.add(jSeparator7);

        menuExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        menuExit.setText(bundle.getString("menu.file.exit")); // NOI18N
        menuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuExitActionPerformed(evt);
            }
        });
        menuFile.add(menuExit);

        menubar.add(menuFile);

        menuVideo.setText(bundle.getString("menu.video")); // NOI18N

        menuNewVideo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        menuNewVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menu_video_add.png"))); // NOI18N
        menuNewVideo.setText(bundle.getString("menu.video.new")); // NOI18N
        menuNewVideo.setEnabled(false);
        menuNewVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuNewVideoActionPerformed(evt);
            }
        });
        menuVideo.add(menuNewVideo);

        menuDeleteVideo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuDeleteVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menu_video_remove.png"))); // NOI18N
        menuDeleteVideo.setText(bundle.getString("menu.video.delete")); // NOI18N
        menuDeleteVideo.setEnabled(false);
        menuDeleteVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuDeleteVideoActionPerformed(evt);
            }
        });
        menuVideo.add(menuDeleteVideo);
        menuVideo.add(jSeparator4);

        menuOpenVideo.setText(bundle.getString("menu.video.open")); // NOI18N
        menuOpenVideo.setEnabled(false);
        menuOpenVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuOpenVideoActionPerformed(evt);
            }
        });
        menuVideo.add(menuOpenVideo);
        menuVideo.add(jSeparator5);

        menuRunVideo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        menuRunVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menu_video_run.png"))); // NOI18N
        menuRunVideo.setText(bundle.getString("menu.video.run")); // NOI18N
        menuRunVideo.setEnabled(false);
        menuRunVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRunVideoActionPerformed(evt);
            }
        });
        menuVideo.add(menuRunVideo);

        menubar.add(menuVideo);

        menuImage.setText(bundle.getString("menu.image")); // NOI18N
        menuImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImageActionPerformed(evt);
            }
        });

        menuImageSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        menuImageSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/menu_image_search.png"))); // NOI18N
        menuImageSearch.setText(bundle.getString("menu.image.search")); // NOI18N
        menuImageSearch.setEnabled(false);
        menuImageSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuImageSearchActionPerformed(evt);
            }
        });
        menuImage.add(menuImageSearch);

        menubar.add(menuImage);

        menuLanguage.setText(bundle.getString("menu.language")); // NOI18N

        menuLangSpanish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flag_spain.gif"))); // NOI18N
        menuLangSpanish.setText(bundle.getString("menu.language.spanish")); // NOI18N
        menuLangSpanish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLangSpanishActionPerformed(evt);
            }
        });
        menuLanguage.add(menuLangSpanish);

        menuLangEnglish.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/flag_uk.gif"))); // NOI18N
        menuLangEnglish.setText(bundle.getString("menu.language.english")); // NOI18N
        menuLangEnglish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuLangEnglishActionPerformed(evt);
            }
        });
        menuLanguage.add(menuLangEnglish);

        menubar.add(menuLanguage);

        menuHelp.setText(bundle.getString("menu.help")); // NOI18N

        menuHelpAbout.setText(bundle.getString("menu.help.about")); // NOI18N
        menuHelpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuHelpAboutActionPerformed(evt);
            }
        });
        menuHelp.add(menuHelpAbout);

        menubar.add(menuHelp);

        setJMenuBar(menubar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBusqueda, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 961, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(mainToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 431, Short.MAX_VALUE)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mainToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBusqueda, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelBusqueda.getAccessibleContext().setAccessibleName(bundle.getString("videolist.title")); // NOI18N

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Evento disparado cuando se pusla el boton de busqueda
     * @param evt
     */
    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        menuImageSearchActionPerformed(evt);
    }//GEN-LAST:event_searchButtonActionPerformed

    /**
     * Evento para abrir una base de datos existente.
     * @param evt
     */
    private void menuOpenDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenDbActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(FILECHOOSER_DIMENSION);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(
            new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory() || pathname.getName().endsWith(".sqlite");
                }

                @Override
                public String getDescription() {
                    return "Base de datos sqlite";
                }

            }
        );
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File db = fc.getSelectedFile();
            openDb(db);
        }
    }//GEN-LAST:event_menuOpenDbActionPerformed

    /**
     * Abre una base de datos.
     * @param f Fichero que contiene la base de datos sqlite
     */
    public void openDb(File f)
    {
        try
        {
            // Abro la base de datos y recargo la lista de videos
            dbManager.setConnectionString(f);
            reloadVideoList();

            // Establezco el titulo de la ventana
            ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
            this.setTitle(f.getName()  +  " - " + bundle.getString("app.title"));

            // Indico la bdd con ultima base de datos abierta
            ConfigurationManager.setProperty("system.db", f.getAbsolutePath());

            // Habilito los controles
            enableControls();
        }
        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, "No se ha podido abrir la base de datos: {0}", ex.getMessage());
        }
        catch (ClassNotFoundException ex)
        {
            logger.log(Level.SEVERE, "No se ha podido abrir la base de datos: {0}", ex.getMessage());
        }
    }

    /**
     * Recarga la lista de videos.
     * @throws SQLException
     * @throws ClassCastException
     */
    private void reloadVideoList() throws SQLException, ClassNotFoundException
    {
        try
        {
            dbManager.disconnect();
        }
        catch (SQLException ex)
        {
            logger.log(Level.SEVERE, "No se puede recargar la lista de videos");
            logger.log(Level.INFO, "Excepcion: {0}", ex.getMessage());
            throw ex;
        }
        panelVideos.setVideos(dbManager.getVideoList());
    }

    /**
     * Evento al pulsar el boton para añadir un video
     * @param evt Evento
     */
    private void videoAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoAddActionPerformed
        menuNewVideoActionPerformed(evt);
    }//GEN-LAST:event_videoAddActionPerformed

    /**
     * <p>Se invoca cuando cambia alguna propiedad.</p>
     * <p>En particular es llamado cuando se selecciona un vídeo para habilitar
     * los controles correspondientes (por ejemplo el botón de borrado)</p>
     * @param evt Evento
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (VideoPanel.EVENT_CLICK.equals(evt.getPropertyName()))
        {
            if (!AM_I_PROCESSING)
                enableControls();
        }
    }

    /**
     * Evento disparado al pulsar sobre el boton procesar.
     * @param evt Evento
     */
    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        menuRunVideoActionPerformed(evt);
    }//GEN-LAST:event_runButtonActionPerformed

    /**
     * Evento disparado al pulsar sobre el boton de eliminar video.
     * @param evt
     */
    private void videoDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoDelActionPerformed
        menuDeleteVideoActionPerformed(evt);
    }//GEN-LAST:event_videoDelActionPerformed

    /**
     * Evento disparado al pulsar sobre el boton de abrir base de datos.
     * @param evt Evento
     */
    private void toolOpenDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolOpenDbActionPerformed
        menuOpenDbActionPerformed(evt);
    }//GEN-LAST:event_toolOpenDbActionPerformed

    /**
     * <p>Añade uno o varios videos al listado de videos.</p>
     * <p>Se muestra un dialogo para seleccionar ficheros, los videos seleccionados
     * se añadiran a listado de videos y quedaran marcados como pendientes para
     * añadir en base de datos.</p>
     * @param evt Evento
     */
    private void menuNewVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewVideoActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(FILECHOOSER_DIMENSION);
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new java.io.File(ConfigurationManager.getProperty("video.path")));

        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(
            new FileFilter()
            {
                @Override
                public boolean accept(File pathname)
                {

                    if (pathname.isDirectory())
                        return true;

                    String[] extensions = ConfigurationManager.getProperty("video.extension").split(",");
                    for(String extension : extensions)
                    {
                        if (pathname.getName().endsWith(extension))
                            return true;
                    }
                    return false;
                }

                @Override
                public String getDescription()
                {
                    return "Archivos de video";
                }
            }
        );
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            panelVideos.addVideos(fc.getSelectedFiles());
            enableControls();
        }
    }//GEN-LAST:event_menuNewVideoActionPerformed

    /**
     * Elimina los videos seleccionados del listado de videos. Estos videos
     * quedan marcados para eliminar en base de datos.
     * @param evt Evento
     */
    private void menuDeleteVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuDeleteVideoActionPerformed
        
        panelVideos.deleteSelectedVideos();
        enableControls();
    }//GEN-LAST:event_menuDeleteVideoActionPerformed

    /**
     * Añade y/o elimina en la base de datos los videos que previamente hayan
     * sido marcados para añadir o eliminar.
     * @param evt Evento
     */
    private void menuRunVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRunVideoActionPerformed
        
        final List<Video> newVideos = panelVideos.getModel().getNewVideos();
        final List<Video> deletedVideos = panelVideos.getModel().getDeletedVideos();

        videoTask = new VideoTask(dbManager, newVideos, deletedVideos);        
        videoTask.addPropertyChangeListener(this.new TaskListener(videoTask));
        videoTask.execute();
    }//GEN-LAST:event_menuRunVideoActionPerformed

    /**
     * <p>Busqueda de una imagen en la base de datos.</p>
     * <p>Se muestra un dialogo para seleccionar un fichero correspondiente
     * a una imagen y se buscan imagenes similares en la base de datos.
     * Se crea una nueva pestaña con los resultados</p>
     * @param evt
     */
    private void menuImageSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImageSearchActionPerformed
        final JFileChooser fc = new ImageChooser(ConfigurationManager.getProperty("image.extension").split(","));
        fc.setCurrentDirectory(new File(ConfigurationManager.getProperty("image.path")));
        final int returnVal = fc.showOpenDialog(this);
        if (returnVal == ImageChooser.APPROVE_OPTION)
        {
            imageSearch(fc.getSelectedFile());
        }
        enableControls();
    }//GEN-LAST:event_menuImageSearchActionPerformed

    /**
     * Busca una imagen en la base de datos.
     * @param imageFile Imagen a buscar
     */
    private void imageSearch(final File imageFile)
    {
        imageTask = new ImageTask(dbManager, imageFile);
        imageTask.addPropertyChangeListener(this.new TaskListener(imageTask));
        imageTask.execute();
    }

    /**
     * Abre el video seleccionado.
     * @param evt
     */
    private void menuOpenVideoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuOpenVideoActionPerformed
        panelVideos.openSelectedVideo();
    }//GEN-LAST:event_menuOpenVideoActionPerformed

    /**
     * Abre el video seleccionado.
     * @param evt
     */
    private void videoMenuOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_videoMenuOpenActionPerformed
        menuOpenVideoActionPerformed(evt);
    }//GEN-LAST:event_videoMenuOpenActionPerformed

    /**
     * Evento lanzado al pulsar sobre el boton de crear nueva base de datos.
     * @param evt
     */
    private void toolNewDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolNewDbActionPerformed
        menuNewDbActionPerformed(evt);
    }//GEN-LAST:event_toolNewDbActionPerformed

    /**
     * Abre el dialogo para crear una nueva base de datos.
     * @param evt
     */
    private void menuNewDbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuNewDbActionPerformed
        NewDbDialog newDbDialog = new NewDbDialog(this, true);
        newDbDialog.setLocationRelativeTo(null);
        newDbDialog.setVisible(true);
        File dbFile = newDbDialog.getConnection();
        if (dbFile != null)
            openDb(dbFile);
    }//GEN-LAST:event_menuNewDbActionPerformed

    private void panelBusquedaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelBusquedaMouseClicked
        if (evt.isAltDown())
        {
            evt.getComponent().setVisible(true);
        }
    }//GEN-LAST:event_panelBusquedaMouseClicked

    /**
     * Evento disparado al cerrar el formulario
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try
        {
            ConfigurationManager.save();
        }
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "No se puede guardar la configuracion: {0}", ex.getMessage());
        }
    }//GEN-LAST:event_formWindowClosing

    private void menuImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuImageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_menuImageActionPerformed

    /**
     * Evento disparado al seleccionar el idioma español en el menu.
     * @param evt
     */
    private void menuLangSpanishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLangSpanishActionPerformed
        configureLanguage(new Locale("es", "ES"));
    }//GEN-LAST:event_menuLangSpanishActionPerformed

    /**
     * Evento disparado al seleccionar el idioma inglés en el menu.
     * @param evt
     */
    private void menuLangEnglishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuLangEnglishActionPerformed
        configureLanguage(new Locale("en", "US"));
    }//GEN-LAST:event_menuLangEnglishActionPerformed

    private void menuHelpAboutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuHelpAboutActionPerformed
    {//GEN-HEADEREND:event_menuHelpAboutActionPerformed
        final JDialog aboutDialog = new About(this, true);
        aboutDialog.setLocationRelativeTo(null);
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_menuHelpAboutActionPerformed

    private void menuConfigurationActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuConfigurationActionPerformed
    {//GEN-HEADEREND:event_menuConfigurationActionPerformed
        JDialog myConfig = new ConfigurationDialog(this, true);
        myConfig.setLocationRelativeTo(null);
        myConfig.setVisible(true);
    }//GEN-LAST:event_menuConfigurationActionPerformed

    private void menuExitActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_menuExitActionPerformed
    {//GEN-HEADEREND:event_menuExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_menuExitActionPerformed

    private void toolConfigurationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolConfigurationActionPerformed
        menuConfigurationActionPerformed(evt);
    }//GEN-LAST:event_toolConfigurationActionPerformed

    /**
     * Cancela la tarea que se esta ejecutando actualmente.
     * @param evt 
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (imageTask != null)
            imageTask.cancel(true);
        if (videoTask != null)
            videoTask.cancel(true);
        cancelButton.setEnabled(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Configura el idioma de la aplicacion.
     * @param locale Idioma
     */
    private void configureLanguage(Locale locale)
    {
        ConfigurationManager.setProperty("system.language", locale.getLanguage());
        ConfigurationManager.setProperty("system.country", locale.getCountry());
        ConfigurationManager.setLocale(locale);
        onLocaleChange();
    }

    /**
     * <p>Habilita o deshabilita los campos del formulario en función del estado de
     * la aplicación.</p>
     * <p>Por ejemplo, si no hay ninguna base de datos abierta los botones de
     * añadir y eliminar videos asi como el de busqueda estarán deshabilitados.</p>
     */
    private void enableControls()
    {
        toolNewDb.setEnabled(true);
        toolOpenDb.setEnabled(true);
        toolConfiguration.setEnabled(true);
        menuFile.setEnabled(true);
        menuVideo.setEnabled(true);
        menuImage.setEnabled(true);
        menuLanguage.setEnabled(true);
        menuHelp.setEnabled(true);
        cancelButton.setEnabled(false);

        if (dbManager.isConnected())
        {
            // Conexion con base de datos, habilito botones para añadir y buscar
            panelVideos.setEnabled(true);
            videoAdd.setEnabled(true);
            searchButton.setEnabled(true);
            menuNewVideo.setEnabled(true);
            menuImageSearch.setEnabled(true);
        }
        else
        {
            // No hay conexion, deshabilito botones para añadir y buscar
            panelVideos.setEnabled(false);
            videoAdd.setEnabled(false);
            searchButton.setEnabled(false);
            menuNewVideo.setEnabled(false);
            menuImageSearch.setEnabled(false);
        }

        VideoTableModel vtm = panelVideos.getModel();
        if (vtm.hasChanged())
        {
            // Ha habido algun cambio, habilito el botón para procesar cambios
            runButton.setEnabled(true);
            menuRunVideo.setEnabled(true);
        }
        else
        {
            // Sin cambios, deshabilito del botónd ejecutar
            runButton.setEnabled(false);
            menuRunVideo.setEnabled(false);
        }

        if (panelVideos.getSelectedVideos().size() > 0)
        {
            // Algun video seleccionado, habilito botones de borrado y visualización
            videoDel.setEnabled(true);
            menuDeleteVideo.setEnabled(true);
            menuOpenVideo.setEnabled(true);
        }
        else
        {
            // Ningún video seleccionado, deshabilito botones de borrado y visualización
            videoDel.setEnabled(false);
            menuDeleteVideo.setEnabled(false);
            menuOpenVideo.setEnabled(false);
        }
    }

    /**
     * <p>Deshabilita todos los controles del formulario.</p>
     * <p>Se debe llamar cuando se va a realizar algún proceso durante el cual
     * no queremos que el usuario toque nada.</p>
     * <p>Para volver a activar los controles hay que llamar a <tt>enableControls</tt></p>
     */
    private void disableControls()
    {
        for(Component component : mainToolBar.getComponents())
        {
            component.setEnabled(false);
        }
        for(Component component : menubar.getComponents())
        {
            component.setEnabled(false);
        }
        panelVideos.setEnabled(false);
    }
    
    /**
     * Cambia el idioma del formulario
     */
    @Override
    public void onLocaleChange()
    {
        ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
        this.setTitle(dbManager.getDbName()  +  " - " + bundle.getString("app.title"));

        // Textos del menu
        menuFile.setText(bundle.getString("menu.file"));
        menuNewDb.setText(bundle.getString("menu.file.new"));
        menuOpenDb.setText(bundle.getString("menu.file.open"));
        menuConfiguration.setText(bundle.getString("menu.file.configuration"));
        menuExit.setText(bundle.getString("menu.file.exit"));
        menuVideo.setText(bundle.getString("menu.video"));
        menuNewVideo.setText(bundle.getString("menu.video.new"));
        menuDeleteVideo.setText(bundle.getString("menu.video.delete"));
        menuOpenVideo.setText(bundle.getString("menu.video.open"));
        menuRunVideo.setText(bundle.getString("menu.video.run"));
        menuImage.setText(bundle.getString("menu.image"));
        menuImageSearch.setText(bundle.getString("menu.image.search"));
        menuLanguage.setText(bundle.getString("menu.language"));
        menuLangSpanish.setText(bundle.getString("menu.language.spanish"));
        menuLangEnglish.setText(bundle.getString("menu.language.english"));
        menuHelp.setText(bundle.getString("menu.help"));
        menuHelpAbout.setText(bundle.getString("menu.help.about"));

        // Para el acceso con teclado (alt)
        menuFile.setMnemonic(bundle.getString("menu.file.mnemonic").charAt(0));
        menuNewDb.setMnemonic(bundle.getString("menu.file.new.mnemonic").charAt(0));
        menuOpenDb.setMnemonic(bundle.getString("menu.file.open.mnemonic").charAt(0));
        menuConfiguration.setMnemonic(bundle.getString("menu.file.configuration.mnemonic").charAt(0));
        menuVideo.setMnemonic(bundle.getString("menu.video.mnemonic").charAt(0));
        menuNewVideo.setMnemonic(bundle.getString("menu.video.new.mnemonic").charAt(0));
        menuDeleteVideo.setMnemonic(bundle.getString("menu.video.delete.mnemonic").charAt(0));
        menuOpenVideo.setMnemonic(bundle.getString("menu.video.open.mnemonic").charAt(0));
        menuRunVideo.setMnemonic(bundle.getString("menu.video.run.mnemonic").charAt(0));
        menuImage.setMnemonic(bundle.getString("menu.image.mnemonic").charAt(0));
        menuImageSearch.setMnemonic(bundle.getString("menu.image.search.mnemonic").charAt(0));
        menuLanguage.setMnemonic(bundle.getString("menu.language.mnemonic").charAt(0));
        menuLangSpanish.setMnemonic(bundle.getString("menu.language.spanish.mnemonic").charAt(0));
        menuLangEnglish.setMnemonic(bundle.getString("menu.language.english.mnemonic").charAt(0));
        menuHelp.setMnemonic(bundle.getString("menu.help.mnemonic").charAt(0));
        menuHelpAbout.setMnemonic(bundle.getString("menu.help.about.mnemonic").charAt(0));
        
        panelVideos.onLocaleChange();

        // Iconos
        toolNewDb.setToolTipText(bundle.getString("toolbar.db.new"));
        toolOpenDb.setToolTipText(bundle.getString("toolbar.db.open"));
        videoAdd.setToolTipText(bundle.getString("toolbar.video.add"));
        videoDel.setToolTipText(bundle.getString("toolbar.video.delete"));
        runButton.setToolTipText(bundle.getString("toolbar.video.run"));
        searchButton.setToolTipText(bundle.getString("toolbar.image.search"));
        cancelButton.setToolTipText(bundle.getString("toolbar.cancel"));

        panelBusqueda.setTitleAt(0, bundle.getString("videolist.title"));
    }

    /**
     * Cambia el idioma de la aplicación.
     * @param ae 
     */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        onLocaleChange();
    }

    /**
     * <p>Clase interna encargada de gestionar el progreso de la barra de estado
     * cuando se esta realizando una tarea.</p>
     * <p>Se escucharán los eventos generados por una tarea <tt>SwingWorker</tt> y
     * se actualizará el progreso en función de eso.</p>
     */
    private class TaskListener implements PropertyChangeListener
    {
        private SwingWorker task;
        
        /**
         * Crea un nuevo TaskListener.
         * @param task Tarea sobre la que se esta informando.
         */
        public TaskListener(SwingWorker task)
        {
            this.task = task;
        }

        /**
         * <p>Se ejecuta cuando una propiedad cambia.</p>
         * <p>Realiza las actualizaciones apropiedas en la interfaz en función
         * de la propiedad que haya cambiado</p>
         * @param evt Evento procesado
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt)
        {
            if ("state".equals(evt.getPropertyName()))
            {
                final SwingWorker.StateValue state = (SwingWorker.StateValue) evt.getNewValue();

                if (state.equals(SwingWorker.StateValue.STARTED))
                {
                    disableControls();
                    mainToolBar.setEnabled(true);
                    cancelButton.setEnabled(true);
                    progressBar.setValue(0);
                    AM_I_PROCESSING = true;
                }
                else if(state.equals(SwingWorker.StateValue.DONE))
                {
                    if (task instanceof VideoTask)
                        done((VideoTask)task);
                    else if (task instanceof ImageTask)
                        done((ImageTask)task);

                    AM_I_PROCESSING = false;
                    task = null;
                }
            }
            else if ("progress".equals(evt.getPropertyName()))
            {
                final int progress = (Integer)evt.getNewValue();
                progressBar.setValue(progress);
            }
            else if ("message".equals(evt.getPropertyName()))
            {
                final String message = (String)evt.getNewValue();
                progressBar.setString(message);
            }
        }

        /**
         * Método ejecutado cuando finaliza la tarea <tt>VideoTask</tt>.
         */
        private void done(VideoTask task)
        {
            boolean result;
            String errorMessage = "";

            try
            {
                result = (Boolean)task.get();
                errorMessage = task.getError();
            }
            catch (CancellationException ex)
            {
                result = false;
            }
            catch (Exception ex)
            {
                result = false;
                errorMessage = ex.getMessage();
                logger.log(Level.SEVERE, null, ex);
            }

            // Muestro mensaje de finalizacion o de error
            ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
            if (result)
                JOptionPane.showMessageDialog(null, bundle.getString("processing_1"), bundle.getString("processing_0"), JOptionPane.INFORMATION_MESSAGE);
            else if (task.isCancelled())
                JOptionPane.showMessageDialog(null, bundle.getString("processing_3"), bundle.getString("processing_2"), JOptionPane.WARNING_MESSAGE);
            else
                JOptionPane.showMessageDialog(null, errorMessage, bundle.getString("error.processing_0"), JOptionPane.ERROR_MESSAGE);

            // Habilito los controles
            enableControls();
            progressBar.setValue(0);
            progressBar.setString("");

            // Recargo la lista de videos
            try { reloadVideoList(); }
            catch (SQLException ex)
            {
                logger.log(Level.SEVERE, "No se ha podido recargar la lista de videos: {0}", ex.getMessage());
            }
            catch (ClassNotFoundException ex)
            {
                logger.log(Level.SEVERE, "No se ha podido recargar la lista de videos: {0}", ex.getMessage());
            }
        }

        /**
         * Método ejecutado cuando finaliza la tarea <tt>ImageTask</tt>.
         */
        private void done(ImageTask task)
        {
            // Muestro los resultados
            ResourceBundle bundle = ResourceBundle.getBundle("configuration/language");
            SearchedImage image = task.getSearchedImage();
            Vector<ImageComparation> comparationResults = task.getCompartionResults();
            
            final ImagePanel imgPanel = new ImagePanel();
            final String title = MessageFormat.format(bundle.getString("imagepanel.search"), image.getFile().getName());
            panelBusqueda.addTab(title, imgPanel);
            panelBusqueda.setTabComponentAt(panelBusqueda.getTabCount() - 1, new ButtonTabComponent(panelBusqueda));
            panelBusqueda.setSelectedIndex(panelBusqueda.getTabCount() - 1);
            imgPanel.setComparationResults(comparationResults);
            imgPanel.setSearchedImage(image);
            
            // Compruebo errores
            boolean result;
            try { result = (Boolean)task.get(); }
            catch (Exception ex) { result = false; }

            if (!result && !task.isCancelled())
                JOptionPane.showMessageDialog(null
                                           , bundle.getString("error.processing.image_1")
                                           , bundle.getString("error.processing.image_0")
                                           , JOptionPane.ERROR_MESSAGE);
                

            // Habilito los controles
            enableControls();
            progressBar.setValue(0);
            progressBar.setString("");
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JMenuItem menuConfiguration;
    private javax.swing.JMenuItem menuDeleteVideo;
    private javax.swing.JMenuItem menuExit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem menuHelpAbout;
    private javax.swing.JMenu menuImage;
    private javax.swing.JMenuItem menuImageSearch;
    private javax.swing.JMenuItem menuLangEnglish;
    private javax.swing.JMenuItem menuLangSpanish;
    private javax.swing.JMenu menuLanguage;
    private javax.swing.JMenuItem menuNewDb;
    private javax.swing.JMenuItem menuNewVideo;
    private javax.swing.JMenuItem menuOpenDb;
    private javax.swing.JMenuItem menuOpenVideo;
    private javax.swing.JMenuItem menuRunVideo;
    private javax.swing.JMenu menuVideo;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JTabbedPane panelBusqueda;
    private es.davideg.uned.ii.proyecto.gui.VideoPanel panelVideos;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton runButton;
    private javax.swing.JButton searchButton;
    private javax.swing.JButton toolConfiguration;
    private javax.swing.JButton toolNewDb;
    private javax.swing.JButton toolOpenDb;
    private javax.swing.JButton videoAdd;
    private javax.swing.JButton videoDel;
    private javax.swing.JMenuItem videoMenuOpen;
    private javax.swing.JPopupMenu videoTableMenu;
    // End of variables declaration//GEN-END:variables

}


