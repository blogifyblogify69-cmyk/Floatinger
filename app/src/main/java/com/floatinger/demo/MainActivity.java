package com.floatinger.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView timer;
    private TextView status;
    private int value = 30;
    private boolean active = false;
    private boolean triggeredForCurrent15 = false;
    private Runnable ticker;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        showHome();
    }

    private TextView label(String text, int size) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextSize(size);
        v.setTextColor(Color.WHITE);
        v.setPadding(20, 14, 20, 14);
        return v;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        return b;
    }

    private void showHome() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(16,17,20));

        TextView title = label("FLOATINGER", 30);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView note = label(
            "Virtual test environment\n\n" +
            "This demo does not clone or control third-party apps. " +
            "It provides the same workflow against a built-in test screen.",
            16);
        root.addView(note);

        Button setup = button("OPEN AUTOMATION SETUP");
        root.addView(setup);
        setup.setOnClickListener(v -> showSetup());

        Button demo = button("OPEN TEST SCREEN");
        root.addView(demo);
        demo.setOnClickListener(v -> showDemoScreen());

        setContentView(root);
    }

    private void showSetup() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 30, 24, 24);
        root.setBackgroundColor(Color.rgb(16,17,20));

        root.addView(label("AUTOMATION SETUP", 26));
        root.addView(label("Trigger timer", 15));

        EditText trigger = new EditText(this);
        trigger.setHint("15");
        trigger.setInputType(2);
        trigger.setText("15");
        root.addView(trigger);

        root.addView(label("After trigger: B → wait 22 seconds → A", 16));

        Button save = button("SAVE");
        root.addView(save);
        save.setOnClickListener(v -> {
            Toast.makeText(this, "Saved for virtual test screen", Toast.LENGTH_SHORT).show();
            showDemoScreen();
        });

        Button back = button("BACK");
        root.addView(back);
        back.setOnClickListener(v -> showHome());

        setContentView(root);
    }

    private void showDemoScreen() {
        active = false;
        triggeredForCurrent15 = false;
        value = 30;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(12, 20, 12, 12);
        root.setBackgroundColor(Color.rgb(35, 32, 44));

        TextView header = label("VIRTUAL TEST SCREEN", 20);
        header.setGravity(Gravity.CENTER);
        root.addView(header);

        timer = label("30", 72);
        timer.setGravity(Gravity.CENTER);
        timer.setBackgroundColor(Color.rgb(30, 70, 90));
        root.addView(timer, new LinearLayout.LayoutParams(-1, 0, 1));

        status = label("STOPPED", 18);
        status.setGravity(Gravity.CENTER);
        root.addView(status);

        LinearLayout targets = new LinearLayout(this);
        targets.setGravity(Gravity.CENTER);
        Button a = button("TARGET A");
        Button b = button("TARGET B");
        targets.addView(a);
        targets.addView(b);
        root.addView(targets);

        LinearLayout controls = new LinearLayout(this);
        Button activate = button("ACTIVE");
        Button stop = button("STOP");
        Button back = button("HOME");
        controls.addView(activate);
        controls.addView(stop);
        controls.addView(back);
        root.addView(controls);

        activate.setOnClickListener(v -> startAutomation());
        stop.setOnClickListener(v -> stopAutomation());
        back.setOnClickListener(v -> { stopAutomation(); showHome(); });

        setContentView(root);

        a.setOnClickListener(v -> status.setText("Target A pressed (demo)"));
        b.setOnClickListener(v -> status.setText("Target B pressed (demo)"));

        startCountdown(a, b);
    }

    private void startCountdown(Button a, Button b) {
        if (ticker != null) handler.removeCallbacks(ticker);
        ticker = new Runnable() {
            @Override public void run() {
                if (value < 1) value = 30;
                timer.setText(String.valueOf(value));
                observeTimer(value, a, b);
                value--;
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(ticker);
    }

    private void observeTimer(int current, Button a, Button b) {
        if (!active) return;

        if (current != 15) {
            triggeredForCurrent15 = false;
            return;
        }

        if (triggeredForCurrent15) return;
        triggeredForCurrent15 = true;

        status.setText("15 detected → Target B");
        b.performClick();

        handler.postDelayed(() -> {
            if (!active) return;
            status.setText("22 seconds elapsed → Target A");
            a.performClick();
        }, 22_000L);
    }

    private void startAutomation() {
        active = true;
        status.setText("ACTIVE — watching test timer");
    }

    private void stopAutomation() {
        active = false;
        if (ticker != null) {
            handler.removeCallbacks(ticker);
            ticker = null;
        }
        status.setText("STOPPED");
    }

    @Override protected void onDestroy() {
        stopAutomation();
        super.onDestroy();
    }
}
