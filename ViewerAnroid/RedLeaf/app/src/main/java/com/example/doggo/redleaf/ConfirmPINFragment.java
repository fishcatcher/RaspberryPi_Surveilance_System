package com.example.doggo.redleaf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class ConfirmPINFragment extends DialogFragment {

    public interface ConfirmPINPositive {
        void handlePositiveButtonFromConfirmPIN(String pin);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        //
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View confirmPINView = inflater.inflate(R.layout.fragment_confirm_pin, null);

        //
        builder.setView(confirmPINView)
                //builder.setView(inflater.inflate(confirm_pin, null))
                .setCancelable(true)
                .setPositiveButton(R.string.proceed_confirm_PIN_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        final EditText pinText = (EditText) confirmPINView.findViewById(R.id.pinText);
                        final ConfirmPINPositive getInterface = (ConfirmPINPositive)getActivity();
                        getInterface.handlePositiveButtonFromConfirmPIN(pinText.getText().toString());

                    }
                })
                .setNegativeButton(R.string.cancel_confirm_PIN_string, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
