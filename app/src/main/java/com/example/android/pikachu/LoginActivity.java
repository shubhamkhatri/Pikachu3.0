package com.example.android.pikachu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText pass;
    private Button loginButton;
    private TextView signupp;
    private TextView forgetPass, skip;
    private ProgressDialog progressDialog;
    private TextInputLayout inputLayoutPassword;
    private FirebaseAuth firebaseAuth;
    private LoginPreferences loginPreferences;
    private SkipPreferences skipPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPreferences = new LoginPreferences(this);
        if (loginPreferences.DonarLaunch() == 1) {
            Intent intent = new Intent(LoginActivity.this, TabLayoutActivity.class);
            intent.putExtra("fragment id", 0);
            startActivity(intent);
            finish();
        }
        skipPreferences = new SkipPreferences(this);
        if (skipPreferences.SkipLaunch() == 1) {
            Intent intent = new Intent(LoginActivity.this, TabLayoutActivity.class);
            intent.putExtra("fragment id", 0);
            startActivity(intent);
            finish();
        }

        username = findViewById(R.id.lgEmail);
        pass = findViewById(R.id.lgPassword);
        loginButton = findViewById(R.id.lgLogin);
        signupp = findViewById(R.id.sign_up);
        forgetPass = findViewById(R.id.forget_pass);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        skip = (TextView) findViewById(R.id.skip_login);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipPreferences.setLaunch(1);
                finish();
                Intent i = new Intent(LoginActivity.this, TabLayoutActivity.class);
                i.putExtra("fragment id", 0);
                startActivity(i);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().trim().isEmpty()) {
                    username.setError(getString(R.string.err_msg));
                } else if (pass.getText().toString().trim().isEmpty()) {
                    inputLayoutPassword.setError(getString(R.string.err_msg));
                } else {
                    username.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    pass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    inputLayoutPassword.setErrorEnabled(false);
                    validate(username.getText().toString().trim(), pass.getText().toString().trim());
                }
            }
        });

        signupp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, AddDonar.class));
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(LoginActivity.this, ForgetPassActivity.class));
            }
        });

    }

    public void validate(final String user, String password) {
        progressDialog.setMessage("Authentication in progress");
        progressDialog.show();
        progressDialog.setCancelable(false);
        firebaseAuth.signInWithEmailAndPassword(user, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    checkEmailVerification();
                } else {
                    progressDialog.dismiss();
                    Toast toast = Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    public void checkEmailVerification() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Boolean company_emailflag = firebaseUser.isEmailVerified();
        if (company_emailflag) {
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            finish();
            loginPreferences.setLaunch(1);
            Intent intent = new Intent(LoginActivity.this, TabLayoutActivity.class);
            intent.putExtra("fragment id", 0);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Verify your email!!!", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
        }
    }
}
