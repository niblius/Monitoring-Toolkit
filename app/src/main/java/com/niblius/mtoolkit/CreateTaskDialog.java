package com.niblius.mtoolkit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.niblius.mtoolkit.integrity.CreateIntegrityActivity;
import com.niblius.mtoolkit.ping.CreatePingActivity;
import com.niblius.mtoolkit.portcheck.CreatePortCheckActivity;

public class CreateTaskDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.what_type_of_task)
                .setItems(R.array.tasks_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = null;
                        Context context = ((Dialog) dialog).getContext();
                        switch (which) {
                            case 0:
                                i = new Intent(context, CreatePortCheckActivity.class);
                                break;
                            case 1:
                                i = new Intent(context, CreatePingActivity.class);
                                break;
                            case 2:
                                i = new Intent(context, CreateIntegrityActivity.class);
                                break;
                            default:
                                // i = new Intent(context, MainActivity.class);
                                break;
                        }
                        if (i != null)
                            startActivity(i);
                    }
                });
        return builder.create();
    }
}
