/*
 * Copyright (C) 2014 Disaster Survival Toolbox Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
