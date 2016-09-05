package com.slaruva.sollertiamonitoring.integrity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slaruva.sollertiamonitoring.R;
import com.slaruva.sollertiamonitoring.SharedMenuFragment;
import com.slaruva.sollertiamonitoring.TaskBasicActivity;

import java.io.File;
import java.io.FileOutputStream;

public class IntegrityLogActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SharedMenuFragment sharedMenu;
    private Integrity task;
    private IntegrityLog log;
    private TextView responseView;

    private static final String TAG = "IntegrityLogActivity";

    private void initToolbar(Bundle savedInstanceState) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(task.getIp());
        setSupportActionBar(toolbar);
        ActionBar mActionBar;
        if ((mActionBar = getSupportActionBar()) != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (savedInstanceState == null) {
            sharedMenu = new SharedMenuFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(sharedMenu, SharedMenuFragment.TAG);
            transaction.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integrity_log);

        long tId = getIntent().getExtras().getLong(TaskBasicActivity.TASK_ID_TAG);
        task = Integrity.findById(Integrity.class, tId);
        long lId = getIntent().getExtras().getLong(IntegrityActivity.TASK_LOG_ID_TAG);
        log = IntegrityLog.findById(IntegrityLog.class, lId);

        initToolbar(savedInstanceState);

        responseView = (TextView) findViewById(R.id.highlighted_response);
        responseView.setText(log.getResponse());
    }

    public void onCopy(View v) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Integrity response",
                log.getResponse());
        clipboard.setPrimaryClip(clip);

        Toast t = Toast.makeText(getApplicationContext(),
                getString(R.string.server_responce_copied),
                Toast.LENGTH_SHORT);
        t.show();
    }

    public void onSaveToFile(View v) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return;
        }

        if (!isExternalStorageWritable()) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.not_able_to_save),
                    Toast.LENGTH_LONG).show();
            return;
        }
        String filename = Long.toString(log.getDatetime()) + ".txt";
        File logfile = new File(getLogsStorageDir(), filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(logfile);
            outputStream.write(log.getResponse().getBytes());
            outputStream.close();
            Toast t = Toast.makeText(getApplicationContext(),
                    getString(R.string.response_saved) + " " + logfile.getPath(),
                    Toast.LENGTH_LONG);
            t.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onSaveToFile(null);
                } else {
                    // do nothing
                }
                return;
            }
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getLogsStorageDir() {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "Sollertia");
        if (!file.exists() && !file.mkdirs())
            Log.e(TAG, "Directory not created " + file.getPath());
        return file;
    }
}
