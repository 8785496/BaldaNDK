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
public class LoginDialogFragment extends DialogFragment {

    public interface LoginDialogListener {
        void onLoginDialogPositiveClick(DialogFragment dialog, String username, String password);
    }

    LoginDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(layoutView)
                .setPositiveButton(R.string.label_login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editUsername = (EditText) layoutView.findViewById(R.id.editUsername);
                        EditText editPassword = (EditText) layoutView.findViewById(R.id.editPassword);
                        String username = editUsername.getText().toString();
                        String password = editPassword.getText().toString();
                        mListener.onLoginDialogPositiveClick(LoginDialogFragment.this, username, password);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {}
                });
        return builder.create();
    }
}
