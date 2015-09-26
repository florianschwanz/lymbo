package de.interoberlin.lymbo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static int BUFFER = 1024 * 1024 * 30;

    // --------------------
    // Methods
    // --------------------

    public static void unzip(File file, File targetDir) throws IOException {
        ZipFile zf = new ZipFile(file.getAbsoluteFile());
        Enumeration e = zf.entries();

        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            FileOutputStream fout = new FileOutputStream(targetDir + "/" + ze.getName());
            InputStream in = zf.getInputStream(ze);
            for (int c = in.read(); c != -1; c = in.read()) {
                fout.write(c);
            }
            in.close();
            fout.close();
        }
    }

    /**
     * Extracts a zip file into a give target directory
     *
     * @param inputStream input stream of zip file to extract
     * @param targetDir   target directory
     */
    public static void unzip(InputStream inputStream, File targetDir) {
        // Create target dir if not existent
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                return;
            }
        }

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        FileOutputStream fout;
        try {
            fout = new FileOutputStream(targetDir);
            BufferedInputStream in = new BufferedInputStream(zipInputStream);
            BufferedOutputStream out = new BufferedOutputStream(fout);

            byte b[] = new byte[BUFFER];
            int n;
            while ((n = in.read(b, 0, BUFFER)) >= 0) {
                out.write(b, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("FOO unzip end");
    }

    /**
     * Zips a list a list of files in a zip files
     *
     * @param files   list of files
     * @param zipFile zip file
     */
    public static void zip(File[] files, File zipFile) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (File f : files) {
                String fileName = f.getPath();

                FileInputStream fi = new FileInputStream(f);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(fileName.substring(fileName.lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void zip(File directory, File zipFile) {

        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFile);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            List<File> files = getFilesFromDir(directory);

            for (File f : files) {
                String fileName = f.getPath();

                System.out.println("FOO zip " + fileName);

                FileInputStream fi = new FileInputStream(f);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(fileName.substring(fileName.lastIndexOf(directory.getName())));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a list of files contained in the directory @param{dir}
     *
     * @param dir directory to look for files in
     * @return list of files
     */
    public static List<File> getFilesFromDir(File dir) {
        List<File> files = new ArrayList<>();

        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                files.addAll(getFilesFromDir(file));
            }
        }

        return files;
    }
}
