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

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

public abstract class BaseEditorFragment<T extends BaseEditorFragment.Listener> extends Fragment {

    T listener;

    public abstract void publish();

    public abstract void setImage(String fileName);

    public abstract void setImageBitmap(Bitmap bitmap);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (T) activity;
    }

    public interface Listener {
        public void onTakePictureClicked(BaseEditorFragment fragment);
    }

}
