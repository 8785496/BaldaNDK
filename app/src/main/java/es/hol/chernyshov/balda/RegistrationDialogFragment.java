package es.hol.chernyshov.balda;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

// http://developer.android.com/intl/ru/guide/topics/ui/dialogs.html
public class RegistrationDialogFragment extends DialogFragment {

    public interface RegistrationDialogListener {
        void onRegistrationDialogPositiveClick(DialogFragment dialog, String username, String password, String repeatPassword);
    }

    RegistrationDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (RegistrationDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement RegistrationDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.dialog_registration, null);
        builder.setView(layoutView)
                .setPositiveButton(R.string.label_registration, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editUsername = (EditText) layoutView.findViewById(R.id.editUsername);
                        EditText editPassword = (EditText) layoutView.findViewById(R.id.editPassword);
                        EditText editRepeatPassword = (EditText) layoutView.findViewById(R.id.editRepeatPassword);
                        String username = editUsername.getText().toString();
                        String password = editPassword.getText().toString();
                        String repeatPassword = editRepeatPassword.getText().toString();
                        mListener.onRegistrationDialogPositiveClick(RegistrationDialogFragment.this, username, password, repeatPassword);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                });
        return builder.create();
    }
}
