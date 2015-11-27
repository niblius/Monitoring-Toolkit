package com.slaruva.sollertiamonitoring;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by enterix on 11/26/2015.
 */
public class CreateTaskDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.what_type_of_task)
                .setItems(R.array.tasks_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i;
                        Context context = ((Dialog) dialog).getContext();
                        switch (which) {
                            case 0:
                                i = new Intent(context, CreatePortCheckActivity.class);
                                break;
                            default:
                                i = new Intent(context, MainActivity.class);
                        }
                        startActivity(i);
                    }
                });
        return builder.create();
    }
}
