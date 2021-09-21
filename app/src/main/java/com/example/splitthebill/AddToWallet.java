package com.example.splitthebill;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class AddToWallet extends DialogFragment {

    private FirebaseAuth mAuth;
    private static final String TAG = "AddToWallet";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mview = inflater.inflate(R.layout.fragment_add_to_wallet, container, false);
        EditText editTextValueAddToWallet = mview.findViewById(R.id.editTextValueAddToWallet);
        Button buttonAddToWallet = mview.findViewById(R.id.buttonAddToWallet);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        buttonAddToWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer valueAddToWallet = Integer.parseInt(editTextValueAddToWallet.getText().toString());
                final DocumentReference sfDocRef = db.collection("users").document("users-child").collection(mAuth.getCurrentUser().getUid()).document("userInfo");

                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        // Note: this could be done without a transaction
                        //       by updating the population using FieldValue.increment()
                        double newAccountBalance = snapshot.getDouble("accountbalance") + valueAddToWallet;
                        transaction.update(sfDocRef, "accountbalance", newAccountBalance);

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction success!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                            }
                        });
                getDialog().dismiss();
            }
        });


        return  mview;
    }
}