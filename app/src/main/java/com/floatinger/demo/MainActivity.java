package com.floatinger.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    public static final String KEY_TARGET_A = "target_a";
    public static final String KEY_TRIGGER = "trigger";
    public static final String KEY_DELAY = "delay";

    private EditText targetA;
    private EditText trigger;
    private EditText delay;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        showSetup();
    }

    private TextView label(String text, int size) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setPadding(16, 12, 16, 12);
        return v;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        return b;
    }

    private void showSetup() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(16, 17, 20));

        TextView title = label("FLOATINGER", 30);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        root.addView(label(
                "Two-app QA controller prototype\n\n" +
                "Configure the test rule here, then show the floating controller. " +
                "The controller communicates only with the built-in Virtual QA Test app.",
                16));

        root.addView(label("Target A value", 15));
        targetA = new EditText(this);
        targetA.setText(getPreferences(0).getString(KEY_TARGET_A, "1.50"));
        targetA.setTextColor(Color.WHITE);
        targetA.setHintTextColor(Color.GRAY);
        targetA.setHint("1.50");
        root.addView(targetA);

        root.addView(label("Trigger value", 15));
        trigger = new EditText(this);
        trigger.setText(getPreferences(0).getString(KEY_TRIGGER, "15"));
        trigger.setInputType(2);
        trigger.setTextColor(Color.WHITE);
        root.addView(trigger);

        root.addView(label("Delay after Target B (seconds)", 15));
        delay = new EditText(this);
        delay.setText(getPreferences(0).getString(KEY_DELAY, "22"));
        delay.setInputType(2);
        delay.setTextColor(Color.WHITE);
        root.addView(delay);

        Button save = button("SAVE SETTINGS");
        root.addView(save);
        save.setOnClickListener(v -> saveSettings());

        Button floating = button("SAVE + SHOW FLOATING ICON");
        root.addView(floating);
        floating.setOnClickListener(v -> {
            saveSettings();
            showFloatingController();
        });

        Button overlaySettings = button("OPEN OVERLAY PERMISSION");
        root.addView(overlaySettings);
        overlaySettings.setOnClickListener(v -> {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(i);
        });

        setContentView(root);
    }

    private void saveSettings() {
        getPreferences(0).edit()
                .putString(KEY_TARGET_A, targetA.getText().toString().trim())
                .putString(KEY_TRIGGER, trigger.getText().toString().trim())
                .putString(KEY_DELAY, delay.getText().toString().trim())
                .apply();
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private void showFloatingController() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this,
                    "Grant 'Display over other apps' permission first.",
                    Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())));
            return;
        }

        Intent service = new Intent(this, FloatingControllerService.class);
        service.putExtra(KEY_TARGET_A, getPreferences(0).getString(KEY_TARGET_A, "1.50"));
        service.putExtra(KEY_TRIGGER, getPreferences(0).getString(KEY_TRIGGER, "15"));
        service.putExtra(KEY_DELAY, getPreferences(0).getString(KEY_DELAY, "22"));
        startService(service);

        Toast.makeText(this,
                "Floating controller is ready. Open the Virtual QA Test app next.",
                Toast.LENGTH_LONG).show();
    }
}
