package com.slaruva.sollertiamonitoring.integrity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.slaruva.sollertiamonitoring.CreateTaskActivity;
import com.slaruva.sollertiamonitoring.R;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Activity responsible for creating new tasks of type: Integrity
 */
public class CreateIntegrityActivity extends CreateTaskActivity {
    private static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    String escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_integrity);

        initToolbar(savedInstanceState);
    }

    /**
     * Callback method for button "Create", checks validation and
     * indicates what fields are invalid
     * @param view current context
     */
    public void onCreateIntegrity(View view) {
        Integrity task = new Integrity();

        EditText ipField = (EditText)findViewById(R.id.ip);
        String ip = ipField.getText().toString();

        EditText regexpField = (EditText)findViewById(R.id.regexp);
        String regexp = regexpField.getText().toString();

        TextView errorView = (TextView)findViewById(R.id.error_view);

        if(!task.setRegexp(regexp)) {
            errorView.setText(R.string.regexp_not_valid);
            return;
        }

        if(!task.setIp(ip)) {
            errorView.setText(getString(R.string.invalid_ip));
            return;
        }

        task.save();
        this.finish();
    }

    public void onGenerate(View v) {
        EditText source = (EditText)findViewById(R.id.generate);
        EditText regexpField = (EditText)findViewById(R.id.regexp);

        String input = source.getText().toString();
        regexpField.setText(".*" + escapeSpecialRegexChars(input) + ".*");
    }
}
