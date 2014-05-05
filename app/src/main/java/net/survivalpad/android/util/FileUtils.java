package net.survivalpad.android.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    private FileUtils() {
    }

    public static void copy(File from, File to) throws FileNotFoundException {
        if (!from.exists()) {
            throw new FileNotFoundException();
        }
        InputStream is = new FileInputStream(from);
        copy(is, to);
    }

    public static void copy(InputStream is, File to) throws FileNotFoundException {

        OutputStream os = new FileOutputStream(to);

        try {
            byte[] buff = new byte[512];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
