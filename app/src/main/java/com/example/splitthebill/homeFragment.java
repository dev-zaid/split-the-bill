package com.example.splitthebill;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class homeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private FirebaseAuth mAuth;
    Button btAddExpense, btAddToWallet;
    TextView textViewAvailableBalance;
    private View mview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_home, container, false);
        btAddExpense = (Button)mview.findViewById(R.id.addexpense);
        textViewAvailableBalance = (TextView)mview.findViewById(R.id.tvAvailableAmount);
        btAddToWallet = (Button)mview.findViewById(R.id.btAddMoney);

        btAddExpense.setOnClickListener(this);
        btAddToWallet.setOnClickListener(this);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        final DocumentReference docRef = db.collection("users").document("users-child").collection(mAuth.getCurrentUser().getUid()).document("userInfo");

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    String stringAccountBalance = snapshot.get("accountbalance").toString();
                    Double doubleAccountBalance = Double.parseDouble(stringAccountBalance);
                    Integer intAccountBalance = (int) Math.round(doubleAccountBalance);
                    String accountBalance = String.valueOf(intAccountBalance);
                    textViewAvailableBalance.setText(accountBalance);
                    Log.d(TAG, "Current data: " + snapshot.get("accountbalance"));
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }

        });

        return mview;
    }

    @Override
    public void onClick(View v){
        FragmentManager manager = getFragmentManager();
        switch (v.getId()){
            case R.id.addexpense:
                addExpense mydialog = new addExpense();
                mydialog.show(manager,null);
                break;

            case R.id.btAddMoney:
                AddToWallet Add = new AddToWallet();
                Add.show(manager,null);
                break;

            default:
                break;
        }

    }
}