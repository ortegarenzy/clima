package com.renzyortega.clima.dialog;

import android.app.Dialog;
import android.app.DialogFragment;

import android.content.DialogInterface;

import android.os.Bundle;

import android.support.v7.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;

import com.renzyortega.clima.R;

public class AboutDialog extends DialogFragment {

	public static AboutDialog getInstance() {
		return new AboutDialog();
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.about_dialog_layout, null);
		AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle("About");
		dialog.setView(v, 100, 0, 100, 0);
		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
		return dialog.create();
	}
}