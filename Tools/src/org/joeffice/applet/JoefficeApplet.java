/*
 * Copyright 2013 - Japplis - All rights reserved.
 * Proprietary code, do not copy / re-use this code without the agreement of Japplis.
 */
package org.joeffice.applet;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * Applet to start Joeffice in a browser.
 * Test at http://www.joeffice.net/test123.html
 *
 * @author Anthony Goubard - Japplis
 */
public final class JoefficeApplet extends JApplet {

    private ClassLoader loader;
    private JFrame mainWindow;

    @Override
    public void init() {
        boolean pageValid = checkDocument();
        if (!pageValid) return;
        File destinationDir = copyFiles();
        File joefficeDir = new File(destinationDir, "joeffice");
        setNetbeansProperties(destinationDir, joefficeDir);
        loader = createClassLoader(joefficeDir);
    }

    private boolean checkDocument() {
        String documentBase = getDocumentBase().toString();
        return documentBase.startsWith("http://www.japplis.com/") || documentBase.startsWith("http://www.joeffice.net/") ||
                documentBase.startsWith("http://japplis.com/") || documentBase.startsWith("http://joeffice.net/");
    }

    private File copyFiles() {
        File destinationDir = new File(System.getProperty("user.home"), ".japplis");
        File destinationZip = new File(destinationDir, "Joeffice.zip");
        JProgressBar downloadProgressBar = initUI(destinationZip);
        try {
            System.out.println("Downloading Joeffice...");
            FileUtils.copyURL(new URL("http://www.joeffice.net/joeffice.zip"), destinationZip);
            System.out.println("Joeffice downloaded...");
            //FileUtils.copyURL(new File("c:/Java/projects/joeffice/dist/Joeffice.zip").toURI().toURL(), destinationZip);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        downloadProgressBar.setIndeterminate(false);

        return destinationDir;
    }

    private JProgressBar initUI(File destinationZip) {
        JLabel message1 = new JLabel();
        message1.setFont(Font.decode(Font.MONOSPACED + "-BOLD-24"));
        message1.setOpaque(false);
        JLabel message2 = new JLabel();
        message2.setFont(Font.decode(Font.MONOSPACED + "-PLAIN-16"));
        message2.setOpaque(false);
        JProgressBar downloadProgressBar = new JProgressBar();
        downloadProgressBar.setIndeterminate(true);
        downloadProgressBar.setOpaque(false);
        if (destinationZip.exists()) {
            message1.setText("Starting Joeffice");
        } else {
            message1.setText("First Start of Joeffice.");
            message2.setText("The first start can take a couple of minutes.");
        }

        JPanel messagePanel = new JPanel(new GridLayout(3, 1));
        messagePanel.setOpaque(false);
        messagePanel.add(message1);
        messagePanel.add(message2);
        messagePanel.add(downloadProgressBar);
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(messagePanel);
        getContentPane().setBackground(Color.WHITE);
        return downloadProgressBar;
    }

    private void setNetbeansProperties(File destinationDir, File joefficeDir) {
        File netbeansUserDir = new File(destinationDir, "joeffice_user_data");
        if (!netbeansUserDir.exists()) {
            netbeansUserDir.mkdirs();
        }
        System.setProperty("netbeans.user", netbeansUserDir.getAbsolutePath());
        System.setProperty("netbeans.home", new File(joefficeDir, "platform").getAbsolutePath());
        System.setProperty("netbeans.default_userdir_root", "");
        String netbeansDirs = new File(joefficeDir, "joeffice").getAbsolutePath() + File.pathSeparator +
                new File(joefficeDir, "ide").getAbsolutePath() + File.pathSeparator +
                new File(joefficeDir, "nb").getAbsolutePath();
        System.setProperty("netbeans.dirs", netbeansDirs);
        //System.setProperty("org.netbeans.core.WindowSystem.show", "false");
    }

    private ClassLoader createClassLoader(File joefficeHome) {
        List<URL> jars = new ArrayList<>();
        File platformHome = new File(joefficeHome, "platform");
        File platformLib = new File(platformHome, "lib");
        File platformCore = new File(platformHome, "core");
        jars.addAll(FileUtils.getJarsFromDir(new File(platformLib, "patches")));
        jars.addAll(FileUtils.getJarsFromDir(platformLib));
        jars.addAll(FileUtils.getJarsFromDir(new File(platformLib, "locale")));
        jars.addAll(FileUtils.getJarsFromDir(platformCore));
        jars.addAll(FileUtils.getJarsFromDir(new File(platformCore, "locale")));
        URLClassLoader loader = new URLClassLoader(jars.toArray(new URL[jars.size()]));
        System.setSecurityManager(null);
        return loader;
    }

    @Override
    public void start() {
        try {
            executeMain();
            mainWindow = getMainWindow();
            putWindowInApplet();

        } catch (Exception ex) {
            System.out.println("error start " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
        System.out.println("started");
    }

    private void executeMain() throws Exception {
        Class mainClass = loader.loadClass("org.netbeans.Main");
        //Class mainClass = loader.loadClass("org.netbeans.core.startup.Main");
        String[] args = {"--branding", "joeffice"};
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) args);
        Method finishInitializationMethod = mainClass.getMethod("finishInitialization");
        finishInitializationMethod.invoke(null);
    }

    private JFrame getMainWindow() throws Exception {
        ClassLoader loader2 = Thread.currentThread().getContextClassLoader();
        Class windowManagerClass = loader2.loadClass("org.openide.windows.WindowManager");
        Method getDefaultMethod = windowManagerClass.getMethod("getDefault");
        Object defaultWindowManager = getDefaultMethod.invoke(null);
        Class windowManagerClass2 = loader2.loadClass(defaultWindowManager.getClass().getName());
        Method getMainWindowMethod = windowManagerClass2.getMethod("getMainWindow");
        JFrame mainWindow = (JFrame) getMainWindowMethod.invoke(defaultWindowManager);
        return mainWindow;
    }

    private void putWindowInApplet() {
        mainWindow.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent we) {
                setRootPane(mainWindow.getRootPane());
                mainWindow.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                mainWindow.setVisible(false);
                doLayout();
            }
        });
    }

    @Override
    public void stop() {
        if (mainWindow != null) {
            mainWindow.dispose();
            try {
                ClassLoader loader2 = Thread.currentThread().getContextClassLoader();
                Class topSecurityManagerClass = loader2.loadClass("org.netbeans.TopSecurityManager");
                Method exitMethod = topSecurityManagerClass.getMethod("exit", Integer.TYPE);
                exitMethod.invoke(null, 0);
            } catch (Exception ex) {
                System.out.println("error stop " + ex.getMessage());
                ex.printStackTrace(System.out);
            }
        }
    }

    @Override
    public String getAppletInfo() {
        String info = "Joeffice Applet version 0.5\n"
                + "(c) Copyright 2013 Japplis\n"
                + "e-mail : info@japplis.com\n"
                + "URL : http://www.joeffice.com/";
        return info;
    }

    public static void main(String[] args) {
        JoefficeApplet applet = new JoefficeApplet();
        JFrame testFrame = new JFrame("Test applet");
        applet.init();
        testFrame.setContentPane(applet.getContentPane());
        testFrame.pack();
        testFrame.setVisible(true);
        applet.start();
    }
}