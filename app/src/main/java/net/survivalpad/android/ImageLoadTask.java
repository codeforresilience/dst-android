package net.survivalpad.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

class ImageLoadTask extends AsyncTask<ImageLoadTask.Container, Void, Bitmap> {

    private Container container;

    @Override
    protected Bitmap doInBackground(Container... params) {
        container = params[0];

        return BitmapFactory.decodeFile(container.file.getAbsolutePath());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            container.imageView.setImageBitmap(bitmap);
            container.imageView.setVisibility(View.VISIBLE);
        }
    }

    public static class Container {
        public final ImageView imageView;
        public final File file;

        Container(ImageView iv, File f) {
            imageView = iv;
            file = f;
        }
    }

}
