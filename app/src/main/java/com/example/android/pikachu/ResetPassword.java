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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ResetPassword extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText old_pass, new_pass, cnfrm_pass;
    private String Old_pass, New_Pass, Cnfrm_pass;
    private String pass, Collection;
    private Button change_pass;
    private TextInputLayout inputLayoutPasswordOld, inputLayoutPasswordNew, inputLayoutPasswordCnfrm;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        final String Email = currentUser.getEmail();
        Collection = "donars";
        old_pass = findViewById(R.id.rstPassword_old);
        new_pass = findViewById(R.id.rstPassword_new);
        cnfrm_pass = findViewById(R.id.rstPassword_cnfrm);

        inputLayoutPasswordOld = findViewById(R.id.input_layout_password_old);
        inputLayoutPasswordNew = findViewById(R.id.input_layout_password_new);
        inputLayoutPasswordCnfrm = findViewById(R.id.input_layout_password_cnfrm);

        change_pass = findViewById(R.id.rstPassword_button);
        change_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (old_pass.getText().toString().trim().isEmpty()) {
                    inputLayoutPasswordOld.setError(getString(R.string.err_msg));
                } else if (new_pass.getText().toString().trim().isEmpty()) {
                    inputLayoutPasswordNew.setError(getString(R.string.err_msg));
                } else if (cnfrm_pass.getText().toString().trim().isEmpty()) {
                    inputLayoutPasswordCnfrm.setError(getString(R.string.err_msg));
                } else {
                    old_pass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    new_pass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    cnfrm_pass.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    getPassword(Email);
                }
            }
        });
    }

    public void getPassword(final String Email) {
        progressDialog.setMessage("Updating Password");
        progressDialog.show();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Old_pass = old_pass.getText().toString().trim();
            New_Pass = new_pass.getText().toString().trim();
            Cnfrm_pass = cnfrm_pass.getText().toString().trim();
            DocumentReference documentReference = db.collection(Collection).document(Email);
            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    pass = documentSnapshot.getString("Password");
                    if (pass.compareTo(Old_pass) == 0) {
                        if (New_Pass.compareTo(Cnfrm_pass) == 0) {
                            user.updatePassword(Cnfrm_pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    updatePass(Cnfrm_pass, Email);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(ResetPassword.this, "Error#151", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ResetPassword.this, "Password do not match", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ResetPassword.this, "Wrong Old Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ResetPassword.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void updatePass(String password, String Email) {
        DocumentReference documentReference = db.collection(Collection).document(Email);
        documentReference.update("Password", password).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(ResetPassword.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(ResetPassword.this, TabLayoutActivity.class));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
}
