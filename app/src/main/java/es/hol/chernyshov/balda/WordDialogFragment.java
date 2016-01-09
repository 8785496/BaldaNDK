package es.hol.chernyshov.balda;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class WordDialogFragment extends DialogFragment {
    public interface WordDialogListener {
        public void onWordDialogPositiveClick(DialogFragment dialog, String word);
        public void onWordDialogNegativeClick(DialogFragment dialog);
    }

    WordDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (WordDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WordDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View layoutView = inflater.inflate(R.layout.dialog_word, null);
        builder.setView(layoutView)
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editWord = (EditText) layoutView.findViewById(R.id.editWord);
                        String word = editWord.getText().toString();
                        mListener.onWordDialogPositiveClick(WordDialogFragment.this, word);
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onWordDialogNegativeClick(WordDialogFragment.this);
                    }
                });
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mListener.onWordDialogNegativeClick(WordDialogFragment.this);
    }
}
