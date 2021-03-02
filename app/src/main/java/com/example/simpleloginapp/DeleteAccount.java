package com.example.simpleloginapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccount extends AppCompatDialogFragment {

    private EditText password;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private AuthCredential authCredential;
    private Context context;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_delete, null);
        builder.setView(view);
        builder.setMessage("To continue, please re-enter your password:");

        // delete account using password
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String confirmPassword = password.getText().toString();

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseAuth = FirebaseAuth.getInstance();

                if (!confirmPassword.isEmpty()) {
                    authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), confirmPassword);
                    firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        firebaseAuth.signOut();
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        // cancel
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        password = view.findViewById(R.id.etPassword);
        return builder.create();
    }

    // called once the fragment is associated with its activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            super.onAttach(context);
            this.context = context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

}