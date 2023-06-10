package org.kgs.quicksave;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FileUtil {
    public static void copyDir(File file, File file1) {
        if (!file.isDirectory()) {
            return;
        }
        if (!file1.exists()) {
            file1.mkdir();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    copyDir(f, new File(file1.getPath(), f.getName()));
                } else if (f.isFile()) {
                    copyFile(f, new File(file1.getPath(), f.getName()));
                }
            }
        }
    }

    public static void deleteFile(File dirFile) {
        // 如果dir对应的文件不存在，则退出
        if (!dirFile.exists()) {
            return;
        }

        if (dirFile.isFile()) {
            dirFile.delete();
        } else {

            for (File file : Objects.requireNonNull(dirFile.listFiles())) {
                deleteFile(file);
            }
        }
    }

    private static void copyFile(File file, File file1) {
        try (FileInputStream fis = new FileInputStream(file); FileOutputStream fos = new FileOutputStream(file1)) {
            byte[] bys = new byte[2048];
            int len;
            while ((len = fis.read(bys)) != -1) {
                fos.write(bys, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
