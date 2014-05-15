package net.survivalpad.android.util;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import net.survivalpad.android.entity.DisasterType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Locale;

public class FileUtils {
    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    private static final String DIR = "repo";

    public static String load(File file) {
        StringBuffer sb = new StringBuffer();

        BufferedReader br = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));
            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line)
                        .append('\n');
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }

        return sb.toString();
    }

    public static File getArticleDir(Context context) {
        return new File(context.getDir(DIR, Context.MODE_PRIVATE), "articles");
    }

    public static File getArticleImage(Context context, String fileName) {
        File dir = new File(getArticleDir(context), "images");
        return getLocalizedFile(context, dir, fileName);
    }

    public static File getDisasterTypesJson(Context context) {
        return getLocalizedFile(context, new File(context.getDir(DIR, Context.MODE_PRIVATE), "disaster_types"), "data.json");
    }

    public static Drawable getDisasterTypesDrawable(Context context, DisasterType disasterType) {
        File file = getLocalizedFile(context,
                new File(new File(context.getDir(DIR, Context.MODE_PRIVATE), "disaster_types"), "images"),
                disasterType.getIcon());

        Log.d(TAG, "file = " + file.getAbsolutePath());

        try {
            return BitmapDrawable.createFromStream(new FileInputStream(file), disasterType.getName());
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    private static File getLocalizedFile(Context context, File path, String name) {
        Locale loc = context.getResources().getConfiguration().locale;
        String lang = loc.getLanguage();

        String extension = name.substring(name.lastIndexOf('.'));
        String nameWithoutExtension = name.substring(0, name.lastIndexOf(extension));

        File file = new File(path, nameWithoutExtension + "-" + lang + extension);

        if (file.exists()) {
            return file;
        }

        file = new File(path, name);
        if (file.exists()) {
            return file;
        }

        return null;
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
