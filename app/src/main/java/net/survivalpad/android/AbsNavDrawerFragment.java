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
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;

import net.survivalpad.android.util.DbManager;

public abstract class AbsNavDrawerFragment<T extends AbsNavDrawerFragment.IFragmentListener> extends Fragment {

    public abstract CharSequence getTitle();

    public abstract int getMenuId();

    T mListener;

    SQLiteDatabase mDb;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (T) activity;
        mListener.onAttach(getTitle(), getMenuId());

        mDb = new DbManager(activity, DbManager.FILE_NAME, null).getWritableDatabase();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    public interface IFragmentListener {

        public void onAttach(CharSequence title, int menuId);
    }

}
