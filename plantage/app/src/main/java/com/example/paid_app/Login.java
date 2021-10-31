package com.example.paid_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button login;
    TextView forgot, register;
    String Email, Password;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    GoogleSignInClient signInClient;
    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginID);
        password = findViewById(R.id.loginpassword);
        forgot = findViewById(R.id.forgot);
        register = findViewById(R.id.register);
        login = findViewById(R.id.logbutton);
//        google = findViewById(R.id.google);

        mAuth = FirebaseAuth.getInstance();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("936209577490-r3ve1eikliadlse4riuk8virko7f9fe0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this,gso);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);

        if(signInAccount != null || mAuth.getCurrentUser() != null){
            startActivity(new Intent(this,Home.class));
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserDetails();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());

                new AlertDialog.Builder(Login.this)
                        .setTitle("Reset Password")
                        .setMessage("Enter your mail to receive the reset link:")
                        .setView(resetMail)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                String mail = resetMail.getText().toString();
                                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(Login.this,"Reset Link sent to Mail", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Login.this,"Reset Link not sent to Mail"+ e.getMessage() , Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(R.drawable.check)
                        .show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Signup.class));
                finish();
            }
        });

//        google.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent sign = signInClient.getSignInIntent();
//                startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
//            }
//        });
    }

    private void checkUserDetails() {
        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();

        if(Email.isEmpty()){
            email.setError("This is compulsory");
            email.requestFocus();
            return;
        }

        if(Password.isEmpty()){
            password.setError("This is compulsory");
            password.requestFocus();
            return;
        }

        if(Password.length()<8){
            password.setError("Enter correct password");
            password.requestFocus();
            return;
        }

        Progress_bar();

        mAuth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this,Home.class));
                    finish();
                }
                else{
                    Toast.makeText(Login.this, task.getException().getLocalizedMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void Progress_bar(){
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN_IN_CODE){
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                /*AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Your Google Account is Connected to Our Application.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),Home.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, e+"", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                });*/
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }
}