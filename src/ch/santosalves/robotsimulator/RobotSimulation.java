/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.santosalves.robotsimulator;

import ch.santosalves.robotsimulator.helpers.FileHelper;
import ch.santosalves.robotsimulator.robots.api.GenericRobot;
import ch.santosalves.robotsimulator.robots.api.RobotPositionAndDirection;
import ch.santosalves.robotsimulator.helpers.Vector2D;
import ch.santosalves.robotsimulator.playground.TileType;
import ch.santosalves.robotsimulator.playground.api.MultiLayeredPlayground;
import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import ch.santosalves.robotsimulator.inputs.RobotSensorInput;
import java.awt.Point;
import java.io.InputStreamReader;
import java.util.Set;
import javax.swing.JOptionPane;
import jsyntaxpane.syntaxkits.JavaSyntaxKit;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

/**
 *
 * @author Sergio
 */
public class RobotSimulation extends javax.swing.JFrame {
    private static final Logger LOGGER = Logger.getLogger(RobotSimulation.class.getPackage().getName());
    protected static final String ROBOTS_PACKAGE = "ch.santosalves.robotsimulator.robots";
    protected static final String ROBOTS_PATH = ROBOTS_PACKAGE.replace(".", "/");

    /**
     * Creates new form RobotSimulation
     */
    public RobotSimulation() {
        initComponents();
        initJCodeEditorComponent();
        initRobotPlugins();
        initJcbTilesType();

        loadAvailableRobotAlgorithms();

        updatePlayGroundBoundsTextFields();
        loadLayoutsAndUpdatePlaygroundsComboBox();
        loadAvailableSensorsList();
    }

    List<String> availableAlgorithms = new ArrayList<>();

    private void loadAvailableRobotAlgorithms() {
        try {
            Files.list(new File(FileHelper.getPluginsDirectory(ROBOTS_PATH)).toPath())
                    .filter((Path x) -> x.toFile().getAbsolutePath().endsWith(".java"))
                    .map((Path x) -> x.getName(x.getNameCount() - 1).toString().replace(".java", "").replace("Robot", ""))
                    .forEach(x -> jcAvailableAlgorithms.addItem(x));
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initRobotPlugins() {
        try {
            Files.list(new File(FileHelper.getPluginsDirectory(ROBOTS_PATH)).toPath())
                    .filter((Path t) -> t.toFile().getAbsolutePath().endsWith(".class") && !t.toFile().getAbsolutePath().contains("$"))
                    .forEach((Path x) -> {
                        try {
                            URL[] urls = {x.toFile().toURI().toURL()};
                            URLClassLoader urlClsL = new URLClassLoader(urls);
                            Class<?> c = urlClsL.loadClass(ROBOTS_PACKAGE + "." + x.getName(x.getNameCount() - 1).toString().replace(".class", ""));
                            jcRobots.addItem((GenericRobot) c.newInstance());
                        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private final List<RobotSensorInput> sensorsAvailable = new ArrayList<>();
    private final List<RobotSensorInput> sensorsToUse = new ArrayList<>();
    private GenericRobot gr;

    private void loadAvailableSensorsList() {
        // Initialization of robots combobox
        Reflections reflections = new Reflections(ROBOTS_PATH, new SubTypesScanner());
        Set<Class<? extends RobotSensorInput>> modules = reflections.getSubTypesOf(RobotSensorInput.class);

        for (Class<? extends RobotSensorInput> module : modules) {
            try {
                sensorsAvailable.add((RobotSensorInput) module.newInstance());
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        sensorsListAvailable.setListData(sensorsAvailable.toArray());

        //should be possible to add other sensorsToUse in homedir/appgroupdir/appdir/... folder        
    }

    private void loadLayoutsAndUpdatePlaygroundsComboBox() {
        // initialization of playgrounds list in playgrounds configuration tab
        try (Stream<Path> files = Files.list(new File(FileHelper.getLayoutsDirectory()).toPath())) {
            files.forEach((x) -> {
                jcbPlaygrounds.addItem(x.getName(x.getNameCount() - 1).toString().replace(".xml", ""));
                jcbPlaygroundSelection.addItem(x.getName(x.getNameCount() - 1).toString().replace(".xml", ""));
            });
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        jcbPlaygroundSelection.setModel(jcbPlaygrounds.getModel());
    }

    private void initJcbTilesType() {
        for (TileType t : TileType.values()) {
            jcbTilesType.addItem(t);
        }
    }

    private void updatePlayGroundBoundsTextFields() {
        updateJTGridWidth();
        updateJTGridHeight();
        updateJtTileSize();
    }

    private void updateJtTileSize() {
        jtTileSize.setText(tiledPlaygroundPanel1.getTileSize() + "");
    }

    private void updateJTGridHeight() {
        jtGridHeight.setText(tiledPlaygroundPanel1.getPlaygroundHeight() + "");
    }

    private void updateJTGridWidth() {
        jtGridWidth.setText(tiledPlaygroundPanel1.getPlaygroundWidth() + "");
    }

    private void initJCodeEditorComponent() {
        JavaSyntaxKit.initKit();
        jPanel2.setLayout(new BorderLayout());
        codeEditor = new JEditorPane();
        JScrollPane scrPane = new JScrollPane(codeEditor);
        jPanel2.add(scrPane, BorderLayout.CENTER);
        jPanel2.doLayout();

        //updateCodeEditorContent(previousRobotName, algorithmName.getText());
    }

    private void updateCodeEditorContent(String previousName, String newName) {
        final StringBuilder sb = new StringBuilder();

        if (previousName == null) {
            codeEditor.setContentType("text/java");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(RobotSimulation.class.getResourceAsStream("templates/robot_template.tpl")))) {
                br.lines().forEach((x) -> sb.append(x + "\r\n"));
                int index = 0;
                while ((index = sb.indexOf("{{RobotName}}", index)) != -1) {
                    sb.replace(index, index + 13, newName);
                    //index = sb.indexOf("{{RobotName}}");
                    index += 13;
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            sb.delete(0, sb.length());
            sb.append(codeEditor.getText());

            int index = 0;

            while ((index = sb.indexOf("Robot" + previousName, index)) != -1) {
                sb.replace(index, index + ("Robot" + previousName).length(), "Robot" + newName);
                //index = sb.indexOf("Robot"+previousName);
                index += ("Robot" + previousName).length();
            }
        }

        codeEditor.setText(sb.toString());
        ///previousRobotName = newName;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jDialog1 = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        simulationPlayground = new ch.santosalves.robotsimulator.playground.TiledPlaygroundPanel();
        jPanel13 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jcbPlaygroundSelection = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jcRobots = new javax.swing.JComboBox();
        jbAddRobot = new javax.swing.JButton();
        jbRemoveRobot = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        initialX = new javax.swing.JTextField();
        initialY = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        tiledPlaygroundPanel1 = new ch.santosalves.robotsimulator.playground.TiledPlaygroundPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jtGridWidth = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jtGridHeight = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jtTileSize = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        jcbPlaygrounds = new javax.swing.JComboBox();
        jbSaveLayout = new javax.swing.JButton();
        jbLoadLayout = new javax.swing.JButton();
        typeSelectorPane = new javax.swing.JPanel();
        jcbTilesType = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jbCompile = new javax.swing.JButton();
        jcAvailableAlgorithms = new javax.swing.JComboBox();
        jbSave = new javax.swing.JButton();
        jbAddNew = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sensorsListToUse = new javax.swing.JList();
        jToggleButton1 = new javax.swing.JToggleButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        sensorsListAvailable = new javax.swing.JList();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        compilationResultsList = new javax.swing.JList();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/santosalves/robotsimulator/Bundle"); // NOI18N
        setTitle(bundle.getString("RobotSimulation.title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(735, 628));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setSize(new java.awt.Dimension(800, 600));

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(735, 580));

        jPanel5.setMinimumSize(new java.awt.Dimension(730, 580));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        simulationPlayground.setPlaygroundHeight(0);
        simulationPlayground.setPlaygroundWidth(0);
        simulationPlayground.setTileSize(0);

        javax.swing.GroupLayout simulationPlaygroundLayout = new javax.swing.GroupLayout(simulationPlayground);
        simulationPlayground.setLayout(simulationPlaygroundLayout);
        simulationPlaygroundLayout.setHorizontalGroup(
            simulationPlaygroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 505, Short.MAX_VALUE)
        );
        simulationPlaygroundLayout.setVerticalGroup(
            simulationPlaygroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 552, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel5.add(simulationPlayground, gridBagConstraints);

        jPanel13.setMinimumSize(new java.awt.Dimension(250, 250));
        jPanel13.setPreferredSize(new java.awt.Dimension(250, 400));

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel12.border.title"))); // NOI18N
        jPanel12.setMinimumSize(new java.awt.Dimension(250, 63));

        jcbPlaygroundSelection.setEditable(true);
        jcbPlaygroundSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbPlaygroundSelectionActionPerformed(evt);
            }
        });

        jButton5.setText(bundle.getString("RobotSimulation.jButton5.text")); // NOI18N
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbPlaygroundSelection, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbPlaygroundSelection, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel15.border.title"))); // NOI18N
        jPanel15.setMinimumSize(new java.awt.Dimension(250, 125));

        jcRobots.setEditable(true);

        jbAddRobot.setText(bundle.getString("RobotSimulation.jbAddRobot.text")); // NOI18N
        jbAddRobot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbAddRobotMouseClicked(evt);
            }
        });

        jbRemoveRobot.setText(bundle.getString("RobotSimulation.jbRemoveRobot.text")); // NOI18N
        jbRemoveRobot.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbRemoveRobotMouseClicked(evt);
            }
        });

        jLabel5.setText(bundle.getString("RobotSimulation.jLabel5.text")); // NOI18N

        initialX.setText(bundle.getString("RobotSimulation.initialX.text")); // NOI18N
        initialX.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                initialXMouseExited(evt);
            }
        });

        initialY.setText(bundle.getString("RobotSimulation.initialY.text")); // NOI18N

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcRobots, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initialX, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(initialY))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jbAddRobot)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbRemoveRobot)))
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcRobots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(initialY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(initialX, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbAddRobot)
                    .addComponent(jbRemoveRobot))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel20.setMinimumSize(new java.awt.Dimension(250, 250));

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 246, Short.MAX_VALUE)
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel16.border.title"))); // NOI18N
        jPanel16.setMinimumSize(new java.awt.Dimension(250, 60));

        jButton4.setText(bundle.getString("RobotSimulation.jButton4.text")); // NOI18N
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel20, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel5.add(jPanel13, gridBagConstraints);

        jTabbedPane1.addTab(bundle.getString("RobotSimulation.jPanel5.TabConstraints.tabTitle"), jPanel5); // NOI18N

        jPanel1.setMinimumSize(new java.awt.Dimension(730, 580));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 900));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        tiledPlaygroundPanel1.setAllowEdition(true);
        tiledPlaygroundPanel1.setPlaygroundHeight(15);
        tiledPlaygroundPanel1.setPlaygroundWidth(15);
        tiledPlaygroundPanel1.setPreferredSize(new java.awt.Dimension(130, 130));
        tiledPlaygroundPanel1.setTileSize(20);

        javax.swing.GroupLayout tiledPlaygroundPanel1Layout = new javax.swing.GroupLayout(tiledPlaygroundPanel1);
        tiledPlaygroundPanel1.setLayout(tiledPlaygroundPanel1Layout);
        tiledPlaygroundPanel1Layout.setHorizontalGroup(
            tiledPlaygroundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );
        tiledPlaygroundPanel1Layout.setVerticalGroup(
            tiledPlaygroundPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 542, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(tiledPlaygroundPanel1, gridBagConstraints);

        jPanel4.setMinimumSize(new java.awt.Dimension(250, 0));
        jPanel4.setPreferredSize(new java.awt.Dimension(250, 935));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel10.border.title"))); // NOI18N

        jLabel2.setLabelFor(jtGridWidth);
        jLabel2.setText(bundle.getString("RobotSimulation.jLabel2.text")); // NOI18N

        jtGridWidth.setText(bundle.getString("RobotSimulation.jtGridWidth.text")); // NOI18N
        jtGridWidth.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtGridWidthFocusLost(evt);
            }
        });

        jLabel3.setLabelFor(jtGridHeight);
        jLabel3.setText(bundle.getString("RobotSimulation.jLabel3.text")); // NOI18N

        jtGridHeight.setText(bundle.getString("RobotSimulation.jtGridHeight.text")); // NOI18N
        jtGridHeight.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtGridHeightFocusLost(evt);
            }
        });

        jLabel4.setLabelFor(jtTileSize);
        jLabel4.setText(bundle.getString("RobotSimulation.jLabel4.text")); // NOI18N

        jtTileSize.setText(bundle.getString("RobotSimulation.jtTileSize.text")); // NOI18N
        jtTileSize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jtTileSizeFocusLost(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jtGridWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jtTileSize)
                            .addComponent(jtGridHeight))))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jtGridWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jtGridHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtTileSize, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel11.border.title"))); // NOI18N

        jcbPlaygrounds.setEditable(true);

        jbSaveLayout.setText(bundle.getString("RobotSimulation.jbSaveLayout.text")); // NOI18N
        jbSaveLayout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbSaveLayoutMouseClicked(evt);
            }
        });

        jbLoadLayout.setText(bundle.getString("RobotSimulation.jbLoadLayout.text")); // NOI18N
        jbLoadLayout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbLoadLayoutMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jcbPlaygrounds, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jbSaveLayout)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbLoadLayout)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbPlaygrounds, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbSaveLayout)
                    .addComponent(jbLoadLayout))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        typeSelectorPane.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.typeSelectorPane.border.title"))); // NOI18N

        jcbTilesType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcbTilesTypeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout typeSelectorPaneLayout = new javax.swing.GroupLayout(typeSelectorPane);
        typeSelectorPane.setLayout(typeSelectorPaneLayout);
        typeSelectorPaneLayout.setHorizontalGroup(
            typeSelectorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, typeSelectorPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbTilesType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        typeSelectorPaneLayout.setVerticalGroup(
            typeSelectorPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(typeSelectorPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcbTilesType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(typeSelectorPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeSelectorPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 254, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel1.add(jPanel4, gridBagConstraints);

        jTabbedPane1.addTab(bundle.getString("RobotSimulation.jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel6.setMinimumSize(new java.awt.Dimension(730, 580));
        jPanel6.setPreferredSize(new java.awt.Dimension(1070, 900));
        jPanel6.setLayout(new java.awt.GridBagLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(800, 900));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 495, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 463, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jPanel2, gridBagConstraints);

        jPanel7.setMinimumSize(new java.awt.Dimension(250, 100));
        jPanel7.setPreferredSize(new java.awt.Dimension(250, 962));

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel17.border.title"))); // NOI18N

        jbCompile.setText(bundle.getString("RobotSimulation.jbCompile.text")); // NOI18N
        jbCompile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbCompileMouseClicked(evt);
            }
        });

        jcAvailableAlgorithms.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jcAvailableAlgorithmsItemStateChanged(evt);
            }
        });

        jbSave.setText(bundle.getString("RobotSimulation.jbSave.text")); // NOI18N
        jbSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbSaveMouseClicked(evt);
            }
        });

        jbAddNew.setText(bundle.getString("RobotSimulation.jbAddNew.text")); // NOI18N
        jbAddNew.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jbAddNewMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcAvailableAlgorithms, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addComponent(jbAddNew)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbSave)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jbCompile, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jcAvailableAlgorithms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jbSave)
                    .addComponent(jbAddNew)
                    .addComponent(jbCompile))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RobotSimulation.jPanel19.border.title"))); // NOI18N

        sensorsListToUse.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                sensorsListToUseMouseDragged(evt);
            }
        });
        sensorsListToUse.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sensorsListToUseMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(sensorsListToUse);

        jToggleButton1.setLabel(bundle.getString("RobotSimulation.jToggleButton1.label")); // NOI18N
        jToggleButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton1MouseClicked(evt);
            }
        });

        sensorsListAvailable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sensorsListAvailableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(sensorsListAvailable);

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE)
            .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(143, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jPanel7, gridBagConstraints);

        jPanel18.setPreferredSize(new java.awt.Dimension(0, 50));
        jPanel18.setLayout(new java.awt.GridBagLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(23, 69));

        compilationResultsList.setMinimumSize(new java.awt.Dimension(0, 90));
        compilationResultsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                compilationResultsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(compilationResultsList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel18.add(jScrollPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel6.add(jPanel18, gridBagConstraints);

        jTabbedPane1.addTab(bundle.getString("RobotSimulation.jPanel6.TabConstraints.tabTitle"), jPanel6); // NOI18N

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void compilationResultsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_compilationResultsListMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2) {

            ListItemWithSelectionStuff item = ((ListItemWithSelectionStuff) compilationResultsList.getSelectedValue());
            codeEditor.setSelectionColor(Color.red);
            codeEditor.getCaret().setSelectionVisible(true);

            int ss = (int) (item.getSelectionStart() - item.getLine());
            int ee = (int) (item.getSelectionEnd() + (item.getSelectionStart() == item.getSelectionEnd() ? 1 : 0) - item.getLine());

            codeEditor.setCaretPosition(ss);
            codeEditor.moveCaretPosition(ee);
        }
    }//GEN-LAST:event_compilationResultsListMouseClicked

    private void sensorsListAvailableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sensorsListAvailableMouseClicked
        if (evt.getClickCount() > 1) {
            sensorsToUse.add((RobotSensorInput) sensorsListAvailable.getSelectedValue());
            sensorsListToUse.setListData(sensorsToUse.toArray());
        }
    }//GEN-LAST:event_sensorsListAvailableMouseClicked

    private void jToggleButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton1MouseClicked
        String s = "appendInputs() {";
        String e = "}//end appendInputs";

        int startIndex = codeEditor.getText().indexOf(s);
        int endIndex = codeEditor.getText().indexOf(e);

        StringBuilder sb = new StringBuilder(codeEditor.getText().substring(0, startIndex + s.length() + 1));

        // all the inputs stuff
        sensorsToUse.stream().forEach((RobotSensorInput x) -> sb.append("getInputs().add(new ").append(x.getClass().getCanonicalName()).append("());\n"));
        sb.append(codeEditor.getText().substring(endIndex));

        codeEditor.setText(sb.toString());

    }//GEN-LAST:event_jToggleButton1MouseClicked

    private void sensorsListToUseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sensorsListToUseMouseClicked
        if (evt.getClickCount() > 1) {
            sensorsListToUse.remove(sensorsListToUse.getSelectedIndex());
        }
    }//GEN-LAST:event_sensorsListToUseMouseClicked

    private void sensorsListToUseMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sensorsListToUseMouseDragged

    }//GEN-LAST:event_sensorsListToUseMouseDragged

    private void jbCompileMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbCompileMouseClicked
        final String fname = "Robot" + jcAvailableAlgorithms.getSelectedItem() + ".java";

        //creates and save the file
        try (FileWriter fw = new FileWriter(FileHelper.getPlugin(ROBOTS_PATH, fname))) {
            fw.write(codeEditor.getText());
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        //creates the compiler
        JavaCompiler jc = javax.tools.ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        boolean success = false;
        List<ListItemWithSelectionStuff> listData = new ArrayList<>();

        //compiles
        try (StandardJavaFileManager fileManager = jc.getStandardFileManager(diagnostics, null, null)) {
            final String f = FileHelper.getPlugin(ROBOTS_PATH, fname).getAbsolutePath();            
            final List<String> filesToCompile = Arrays.asList(f);            
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(filesToCompile);
            JavaCompiler.CompilationTask task = jc.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
            
            success = task.call();
            
            diagnostics.getDiagnostics().stream().forEach((diagnostic) -> {
                //Error code and line
                String message = "[ " + diagnostic.getKind() + "] " + diagnostic.getCode() + "@" + diagnostic.getLineNumber() + ": " + diagnostic.getMessage(null);
                listData.add(new ListItemWithSelectionStuff(message, diagnostic.getLineNumber(), diagnostic.getStartPosition(), diagnostic.getEndPosition()));
            });

            listData.add(new ListItemWithSelectionStuff((success ? "Compiled with success" : "Compilation errors found"), 0, 0, 0));
            compilationResultsList.setListData(listData.toArray());
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbCompileMouseClicked

    private void jcbTilesTypeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcbTilesTypeItemStateChanged
        tiledPlaygroundPanel1.setDefaultTile((TileType) evt.getItem());
    }//GEN-LAST:event_jcbTilesTypeItemStateChanged

    private void jbLoadLayoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbLoadLayoutMouseClicked
        final String fname = jcbPlaygrounds.getSelectedItem() + ".xml";
        try (XMLDecoder dec = new XMLDecoder(new FileInputStream(FileHelper.getLayout("", fname)))){
            tiledPlaygroundPanel1.setPlayground((MultiLayeredPlayground) dec.readObject());            
            //update fields
            updatePlayGroundBoundsTextFields();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbLoadLayoutMouseClicked

    private void jbSaveLayoutMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbSaveLayoutMouseClicked
        final String fname = jcbPlaygrounds.getEditor().getItem().toString() + ".xml";

        //Save Playground model;
        try (XMLEncoder enc = new XMLEncoder(new FileOutputStream(FileHelper.getLayout("", fname)))) {                
            enc.writeObject(tiledPlaygroundPanel1.getPlayground());

            if (jcbPlaygrounds.getSelectedIndex() == -1) {
                jcbPlaygrounds.addItem(jcbPlaygrounds.getEditor().getItem());
                jcbPlaygrounds.setSelectedItem(jcbPlaygrounds.getEditor().getItem());
            }
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jbSaveLayoutMouseClicked

    private void jtTileSizeFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtTileSizeFocusLost
        tiledPlaygroundPanel1.setTileSize(new Integer(jtTileSize.getText()));
    }//GEN-LAST:event_jtTileSizeFocusLost

    private void jtGridHeightFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtGridHeightFocusLost
        tiledPlaygroundPanel1.setPlaygroundHeight(new Integer(jtGridHeight.getText()));
    }//GEN-LAST:event_jtGridHeightFocusLost

    private void jtGridWidthFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtGridWidthFocusLost
        tiledPlaygroundPanel1.setPlaygroundWidth(new Integer(jtGridWidth.getText()));
    }//GEN-LAST:event_jtGridWidthFocusLost

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        gr.start();
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        final String fname = jcbPlaygroundSelection.getSelectedItem() + ".xml";
        
        try (XMLDecoder dec = new XMLDecoder(new FileInputStream(FileHelper.getLayout("", fname)))) {
            simulationPlayground.setPlayground((MultiLayeredPlayground) dec.readObject());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton5MouseClicked

    private void jcbPlaygroundSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbPlaygroundSelectionActionPerformed

    }//GEN-LAST:event_jcbPlaygroundSelectionActionPerformed

    private void jbRemoveRobotMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbRemoveRobotMouseClicked
        simulationPlayground.remove(gr);
        simulationPlayground.repaint();
    }//GEN-LAST:event_jbRemoveRobotMouseClicked

    private void jbAddRobotMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbAddRobotMouseClicked
        final int ts = simulationPlayground.getTileSize();
        final Point p = new Point(Integer.parseInt(initialX.getText()), Integer.parseInt(initialY.getText()));
        final Vector2D d = new Vector2D(0, 1);

        //Create an instance of the robot
        gr = (GenericRobot) jcRobots.getSelectedItem();

        //To be refactored
        gr.setWorldPrintingSupport(jPanel20);

        //Adds the robot to the playground
        simulationPlayground.add(gr);

        gr.setPlayground(simulationPlayground.getPlayground());
        gr.setPosition(new RobotPositionAndDirection(p, d));
        gr.setBounds(ts * p.x, ts * p.y, ts, ts);
        gr.updateRobot();

        gr.setVisible(true);

        //Do a refresh
        simulationPlayground.repaint();
    }//GEN-LAST:event_jbAddRobotMouseClicked

    private void initialXMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_initialXMouseExited
        final int ts = simulationPlayground.getTileSize();
        final Point p = new Point(Integer.parseInt(initialX.getText()), Integer.parseInt(initialY.getText()));

        if (gr != null) {
            gr.setBounds(ts * p.x, ts * p.y, ts, ts);
        }
    }//GEN-LAST:event_initialXMouseExited

    private void jbAddNewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbAddNewMouseClicked
        String robotName = (String) JOptionPane.showInputDialog(
                this,
                "Give a valid name to the Robot (Only letters):",
                "Create new Robot Dialog",
                JOptionPane.PLAIN_MESSAGE);

        updateCodeEditorContent(null, robotName);

        saveRobot(robotName);
    }//GEN-LAST:event_jbAddNewMouseClicked

    private void saveRobot(String name) {
        final String fname = "Robot" + name + ".java";

        //creates and save the file
        try (FileWriter fw = new FileWriter(FileHelper.getPlugin(ROBOTS_PATH, fname))) {
            fw.write(codeEditor.getText());
            
            jcAvailableAlgorithms.setSelectedItem(name);

            if (!jcAvailableAlgorithms.getSelectedItem().equals(name)) {
                jcAvailableAlgorithms.addItem(name);
                jcAvailableAlgorithms.setSelectedItem(name);
            }
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void jbSaveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jbSaveMouseClicked
        saveRobot((String) jcAvailableAlgorithms.getSelectedItem());
    }//GEN-LAST:event_jbSaveMouseClicked

    private void jcAvailableAlgorithmsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jcAvailableAlgorithmsItemStateChanged
        final String robotName = "Robot" + (String) jcAvailableAlgorithms.getSelectedItem() + ".java";
        
        codeEditor.setContentType("text/java");
        StringBuffer sb;
        
        sb = new StringBuffer();

        try (BufferedReader br = new BufferedReader(new FileReader(FileHelper.getPlugin(ROBOTS_PATH, robotName)))) {
            br.lines().forEach((x) -> sb.append(x + "\r\n"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RobotSimulation.class.getName()).log(Level.SEVERE, null, ex);
        }

        codeEditor.setText(sb.toString());
    }//GEN-LAST:event_jcAvailableAlgorithmsItemStateChanged

    public static class ListItemWithSelectionStuff {

        private String message;
        private long line;
        private long selectionStart;
        private long selectionEnd;

        public ListItemWithSelectionStuff() {
        }

        public ListItemWithSelectionStuff(String message, long line, long start, long end) {
            this.message = message;
            this.line = line;
            this.selectionEnd = end;
            this.selectionStart = start;
        }

        /**
         * @return the message
         */
        protected String getMessage() {
            return message;
        }

        /**
         * @param message the message to set
         */
        protected void setMessage(String message) {
            this.message = message;
        }

        /**
         * @return the line
         */
        protected long getLine() {
            return line;
        }

        /**
         * @param line the line to set
         */
        protected void setLine(long line) {
            this.line = line;
        }

        /**
         * @return the selectionStart
         */
        protected long getSelectionStart() {
            return selectionStart;
        }

        /**
         * @param selectionStart the selectionStart to set
         */
        protected void setSelectionStart(long selectionStart) {
            this.selectionStart = selectionStart;
        }

        /**
         * @return the selectionEnd
         */
        protected long getSelectionEnd() {
            return selectionEnd;
        }

        /**
         * @param selectionEnd the selectionEnd to set
         */
        protected void setSelectionEnd(long selectionEnd) {
            this.selectionEnd = selectionEnd;
        }

        @Override
        public String toString() {
            return message;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RobotSimulation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RobotSimulation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RobotSimulation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RobotSimulation.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RobotSimulation().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JList compilationResultsList;
    private javax.swing.JTextField initialX;
    private javax.swing.JTextField initialY;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JButton jbAddNew;
    private javax.swing.JButton jbAddRobot;
    private javax.swing.JButton jbCompile;
    private javax.swing.JButton jbLoadLayout;
    private javax.swing.JButton jbRemoveRobot;
    private javax.swing.JButton jbSave;
    private javax.swing.JButton jbSaveLayout;
    private javax.swing.JComboBox jcAvailableAlgorithms;
    private javax.swing.JComboBox jcRobots;
    private javax.swing.JComboBox jcbPlaygroundSelection;
    private javax.swing.JComboBox jcbPlaygrounds;
    private javax.swing.JComboBox jcbTilesType;
    private javax.swing.JTextField jtGridHeight;
    private javax.swing.JTextField jtGridWidth;
    private javax.swing.JTextField jtTileSize;
    private javax.swing.JList sensorsListAvailable;
    private javax.swing.JList sensorsListToUse;
    private ch.santosalves.robotsimulator.playground.TiledPlaygroundPanel simulationPlayground;
    private ch.santosalves.robotsimulator.playground.TiledPlaygroundPanel tiledPlaygroundPanel1;
    private javax.swing.JPanel typeSelectorPane;
    // End of variables declaration//GEN-END:variables
    private JEditorPane codeEditor;
}
