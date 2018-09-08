package lk.ac.sjp.aurora.phasetwo.Dialogs;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import lk.ac.sjp.aurora.phasetwo.PostDetectorView;

public class TeamRecognizedDialog extends DialogFragment {
    private String message;
    private Context context;
    private Activity activity;

    public TeamRecognizedDialog() {
        context = getContext();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getMessaage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, PostDetectorView.class);
                        intent.putExtra("lk.ac.sjp.aurora.TEAM_NAME", "Team A");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(activity, PostDetectorView.class);
                        intent.putExtra("lk.ac.sjp.aurora.TEAM_NAME", "Team B");
                        startActivity(intent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
