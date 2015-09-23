package de.interoberlin.lymbo.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static int BUFFER = 1024 * 1024 * 30;

    // --------------------
    // Methods
    // --------------------

    /**
     * Extracts a zip file into a give target directory
     *
     * @param zipFile   zip file to extract
     * @param targetDir target directory
     */
    public static void unzip(File zipFile, File targetDir) {
        // Create target dir if not existant
        if (!targetDir.exists()) {
            if (!targetDir.mkdirs())
                return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(zipFile);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();

                // Create dir of entry if necessary
                if (name.contains("/")) {
                    int lastSlash = name.lastIndexOf("/");
                    if (lastSlash > -1) {
                        String entryDirName = name.substring(0, lastSlash);
                        File entryDir = new File(targetDir.getPath() + entryDirName);

                        if (!entryDir.exists()) {
                            if (!entryDir.mkdirs())
                                continue;
                        }
                    }
                }

                // Write file
                FileOutputStream fileOutputStream = new FileOutputStream(targetDir.getPath() + name);
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    fileOutputStream.write(c);
                }

                zipInputStream.closeEntry();
                fileOutputStream.close();
            }
            zipInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Zips a list a list of files in a zip files
     *
     * @param files   list of files
     * @param zipFile zip file
     */
    public static void zip(List<File> files, File zipFile) {
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

                ZipEntry entry = new ZipEntry(fileName.substring(fileName.lastIndexOf("\\") + 1));
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