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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private Button send;
    private FirebaseAuth firebaseAuth;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        firebaseAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.fpEmail);
        send = findViewById(R.id.fp_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialog();
                editTextEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                String email = editTextEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgetPassActivity.this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
                } else {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(ForgetPassActivity.this, "Password reset mail sent", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgetPassActivity.this, LoginActivity.class));
                            } else {
                                loadingDialog.dismissDialog();
                                Toast.makeText(ForgetPassActivity.this, "Error!!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }
    public void setDialog() {
        loadingDialog = new LoadingDialog(ForgetPassActivity.this);
        loadingDialog.startLoadingDialog();
    }
}