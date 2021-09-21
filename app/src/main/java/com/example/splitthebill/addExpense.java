package com.example.splitthebill;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addExpense extends DialogFragment {

    Integer noOfShares = 1;
    private FirebaseAuth mAuth;
    private static final String TAG = "Transaction";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        NumberPicker numpicker = (NumberPicker) view.findViewById(R.id.noOfShare);
        numpicker.setMinValue(1);
        numpicker.setMaxValue(10);

        EditText editTextTitle = (EditText) view.findViewById(R.id.editTextTitle);
        EditText editTextBillAmount = (EditText)view.findViewById(R.id.editTextBillAmount);
        EditText editTextNote = (EditText) view.findViewById(R.id.editTextNote);

        Button buttonAddFriend = (Button) view.findViewById(R.id.btAddFriend);
        Button buttonAddExpense = (Button) view.findViewById(R.id.btAddExpense);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer billAmount = Integer.parseInt(editTextBillAmount.getText().toString());
                String title = editTextTitle.getText().toString();
                String note = editTextNote.getText().toString();
                Integer noOfShares = numpicker.getValue();
                Integer sharePerPerson = billAmount/noOfShares;
                Calendar currentTime = Calendar.getInstance();




                final DocumentReference sfDocRef = db.collection("users").document("users-child").collection(mAuth.getCurrentUser().getUid()).document("userInfo");
                final DocumentReference activity = db.collection("users").document("users-child").collection(mAuth.getCurrentUser().getUid()).document("activity");
                db.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(sfDocRef);

                        // Note: this could be done without a transaction
                        //       by updating the population using FieldValue.increment()
                        double newAccountBalance = snapshot.getDouble("accountbalance") - sharePerPerson;
                        transaction.update(sfDocRef, "accountbalance", newAccountBalance);

                        // Success
                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction success!");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmSS");
                        String uniqueTrasactionID = sdf.format(System.currentTimeMillis());
                        //String uniqueTrasactionID = ""+currentTime.get(Calendar.YEAR)+currentTime.get(Calendar.MONTH)+currentTime.get(Calendar.DATE)+currentTime.get(Calendar.HOUR)+currentTime.get(Calendar.MINUTE)+currentTime.get(Calendar.SECOND);
                        String activityArray[] = {title,editTextBillAmount.getText().toString(),Calendar.getInstance().getTime().toString(),sharePerPerson.toString(),note,"expense"};
                        Map<String, Object> activityMap = new HashMap<>();
                        activityMap.put(uniqueTrasactionID, Arrays.asList(activityArray));
                        activity.set(activityMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
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

        return view;
    }

}