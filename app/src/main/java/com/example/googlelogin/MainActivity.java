package com.example.googlelogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {
SignInButton signInButton;
TextView status ,uid;
ImageView profileimage;
Button signout, disconnect;
public  static final int INNR = 900;
   private GoogleSignInAccount account;
   FirebaseAuth firebaseAuth;

private GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectxml();
        firebaseAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("587347558458-ee6ns6334gn780lb6fe6s6ovldb2jfhi.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this,gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intetn = googleSignInClient.getSignInIntent();
                startActivityForResult(intetn,INNR);
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                googleSignInClient.signOut()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "sign out succesful", Toast.LENGTH_SHORT).show();
                                updateui(firebaseAuth.getCurrentUser());
                                }else{

                                }
                            }
                        });
            }
        });
                 disconnect.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         firebaseAuth.signOut();
                              googleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                  public void onComplete(@NonNull Task<Void> task) {
                                      updateui(firebaseAuth.getCurrentUser());
                                  }
                              });


                     }
                 });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    if (requestCode ==INNR && data != null){
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            account = task.getResult(ApiException.class);
        } catch (ApiException e) {
            e.printStackTrace();
            updateui(null);
        }
 firebaseuthwithgoogle(account.getIdToken());


    }else{
        Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
    }

    }

    private void firebaseuthwithgoogle(String idToken) {
        AuthCredential googlecredential = GoogleAuthProvider.getCredential(idToken,null);
        firebaseAuth.signInWithCredential(googlecredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Log in sucessful", Toast.LENGTH_SHORT).show();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateui(user);


                }else{
                    Toast.makeText(MainActivity.this, "No log in", Toast.LENGTH_SHORT).show();

                    updateui(null);


                }
            }
        });
    }

    private void updateui(FirebaseUser user) {
       if (user != null){

           status.setText(user.getEmail());
           uid.setText(user.getDisplayName());
           String url = user.getPhotoUrl().toString();
           Glide.with(getApplicationContext()).load(url).into(profileimage);
           signInButton.setVisibility(View.GONE);
           signout.setVisibility(View.VISIBLE);
           disconnect.setVisibility(View.VISIBLE);



       }else {

           status.setText("user not sign in ");
           uid.setText(null);
           profileimage.setImageResource(R.drawable.ic_launcher_background);
           signInButton.setVisibility(View.VISIBLE);
           signout.setVisibility(View.GONE);
           disconnect.setVisibility(View.GONE);
       }
    }

    private void connectxml() {
        signInButton = findViewById(R.id.googlesignbutton);
        status = findViewById(R.id.userstatus);
        uid = findViewById(R.id.userdetail);
        signout = findViewById(R.id.signout);
        disconnect = findViewById(R.id.discoonect);
         profileimage = findViewById(R.id.imagevew);
    }
}