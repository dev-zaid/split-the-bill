package com.example.splitthebill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class signUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
// ...
// Initialize Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        EditText editTextetUserName = findViewById(R.id.editTextUserName);
        EditText editTextUserEmail = findViewById(R.id.editTextUserEmail);
        EditText editTextUserPassword = findViewById(R.id.editTextUserPassword);
        EditText editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        EditText editTextMoneyAvailable = findViewById(R.id.editTextAvailableMoney);
        Button btRegister = findViewById(R.id.btSignUpComplete);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        String userName, userEmail, userPassword, confirmPassword;





         btRegister.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View view) {
                 String userName = editTextetUserName.getText().toString();
                 String userEmail = editTextUserEmail.getText().toString();
                 String userPassword = editTextUserPassword.getText().toString();
                 String confirmPassword = editTextConfirmPassword.getText().toString();

                 Integer userMoney = Integer.parseInt(editTextMoneyAvailable.getText().toString());


                 if(userPassword.equals(confirmPassword)){
                     Log.d(null,"BOTH ARE EQUAL");
                     mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                             .addOnCompleteListener(signUp.this, new OnCompleteListener<AuthResult>() {
                                 @Override
                                 public void onComplete(@NonNull Task<AuthResult> task) {
                                     if (task.isSuccessful()) {
                                         // Sign in success, update UI with the signed-in user's information
                                         Log.d(null, "createUserWithEmail:success");
                                         FirebaseUser user = mAuth.getCurrentUser();
                                         Map<String, Object> users = new HashMap<>();
                                         users.put("name", userName);
                                         users.put("accountbalance", userMoney);
                                         users.put("email",userEmail);


                                         db.collection("users").document("users-child").collection(mAuth.getCurrentUser().getUid()).document("userInfo")
                                                 .set(users)
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         Log.d(null, "DocumentSnapshot successfully written!");
                                                     }
                                                 })
                                                 .addOnFailureListener(new OnFailureListener() {
                                                     @Override
                                                     public void onFailure(@NonNull Exception e) {
                                                         Log.w(null, "Error writing document", e);
                                                     }
                                                 });

                                         updateUI(user);
                                     } else {
                                         // If sign in fails, display a message to the user.
                                         Log.w(null, "createUserWithEmail:failure", task.getException());
                                         Toast.makeText(signUp.this, "Authentication failed.",
                                                 Toast.LENGTH_SHORT).show();
                                     }
                                 }
                             });


                 }

                 else{

                     Toast.makeText(getApplicationContext(),"Passwords do not match",
                             Toast.LENGTH_LONG).show();
                     return;

                 }


             }
         });

    }

    public void updateUI(FirebaseUser currentUser){
        Intent profileIntent = new Intent(this,MainActivity.class);
        profileIntent.putExtra("email",currentUser.getEmail());
        startActivity(profileIntent);
    }


}