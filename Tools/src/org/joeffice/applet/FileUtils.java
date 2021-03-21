package org.joeffice.applet;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
//import org.apache.tools.ant.AntClassLoader;

/**
 * Class containing utilities methods to copy and unzip files.
 *
 * @author Anthony Goubard - Japplis
 */
public class FileUtils {

    public static void copyURL(URL url, File destination) throws IOException {
        URLConnection connection = url.openConnection();
        long urlDate = connection.getLastModified();
        if (!destination.exists() || destination.lastModified() < urlDate) {
            copyRemoteBinaryFile(connection, destination);
            if (destination.getName().endsWith(".zip")) {
                unzipArchive(destination, destination.getParentFile());
            }
        }
    }

    public static void copyRemoteBinaryFile(URLConnection connection, File localFile) throws IOException {
        BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
        FileOutputStream out = new FileOutputStream(localFile);
        copyStream(in, out);
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        try {
            int count = 0;
            byte[] buffer = new byte[65536];
            int bufferLength = in.read(buffer);
            while (bufferLength != -1) {
                count++;
                out.write(buffer, 0, bufferLength);
                bufferLength = in.read(buffer);
                if (count % 16 == 0) {
                    System.out.println("Downloaded " + (count / 16) + " MB");
                }
            }
        } finally {
            out.close();
            in.close();
        }
    }

    public static void unzipArchive(File archive, File outputDir) throws IOException {
        ZipFile zipfile = new ZipFile(archive);
        for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File outputFile = unzipEntry(zipfile, entry, outputDir);
            if (!outputFile.isDirectory() && (outputFile.getName().indexOf('.') < 1 || outputFile.getName().endsWith(".sh"))) {
                outputFile.setExecutable(true);
            }
        }
    }

    private static File unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir) throws IOException {
        if (entry.isDirectory()) {
            File outputFile = new File(outputDir, entry.getName());
            outputFile.mkdirs();
            return outputFile;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        copyStream(inputStream, outputStream);
        return outputFile;
    }

    /**
     * Load the files included in the specified class loader.
     */
    /*public static boolean loadDir(ClassLoader loader, File dir) {
        if (dir == null) {
            return false;
        }
        if (!dir.exists()) {
            System.err.println("Library dir " + dir.getAbsolutePath() + " not found.");
            return false;
        }
        String[] libJars = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        for (int i = 0; i < libJars.length; i++) {
            File newJar = new File(dir, libJars[i]);
            ((AntClassLoader) loader).addPathComponent(newJar);
        }
        return true;
    }*/

    public static List<URL> getJarsFromDir(File dir) {
        List<URL> foundJars = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) return foundJars;
        try (DirectoryStream<Path> jars = Files.newDirectoryStream(dir.toPath(), "*.jar")) {
            for (Path jar: jars) {
                foundJars.add(jar.toUri().toURL());
            }
        } catch (IOException ex) {
            System.out.println("error getJarsFromDir " + ex.getMessage());
            ex.printStackTrace(System.out);
        }
        return foundJars;
    }
}