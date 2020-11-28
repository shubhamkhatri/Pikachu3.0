/*
 *  Copyright (c) 2020 Pikachu(shubham khatri). All rights reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.example.android.pikachu;

import android.content.Context;
import android.content.SharedPreferences;

public class LoginPreferences {
    private static final String LAUNCH = "Launch";
    private static final String PREFERENCE = "MyPref";
    int MODE = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor spEditor;
    private Context context;

    public LoginPreferences(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences(PREFERENCE, MODE);
        spEditor = sharedPreferences.edit();
    }

    public void setLaunch(int value) {
        spEditor.putInt(LAUNCH, value);
        spEditor.apply();
    }

    public int DonarLaunch() {
        return sharedPreferences.getInt(LAUNCH, 0);
    }
}
