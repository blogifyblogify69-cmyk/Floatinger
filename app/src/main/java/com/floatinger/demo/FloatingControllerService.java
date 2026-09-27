package com.floatinger.demo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FloatingControllerService extends Service {
    public static final String ACTION_ACTIVE = "com.floatinger.demo.ACTION_ACTIVE";
    public static final String ACTION_STOP = "com.floatinger.demo.ACTION_STOP";
    private static final String TEST_PACKAGE = "com.autoclicker.virtualtest";

    private WindowManager windowManager;
    private TextView bubble;
    private LinearLayout menu;
    private WindowManager.LayoutParams bubbleParams;
    private String targetA = "1.50";
    private String trigger = "15";
    private String delay = "22";
    private boolean active;

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String a = intent.getStringExtra(MainActivity.KEY_TARGET_A);
            String t = intent.getStringExtra(MainActivity.KEY_TRIGGER);
            String d = intent.getStringExtra(MainActivity.KEY_DELAY);
            if (a != null) targetA = a;
            if (t != null) trigger = t;
            if (d != null) delay = d;
        }

        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }

        if (bubble == null) createOverlay();
        return START_STICKY;
    }

    private void createOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        bubble = new TextView(this);
        bubble.setText("F");
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(20);
        bubble.setGravity(Gravity.CENTER);

        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(Color.rgb(30, 136, 229));
        bubble.setBackground(bg);

        bubbleParams = new WindowManager.LayoutParams(
                64, 64,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        bubbleParams.gravity = Gravity.TOP | Gravity.END;
        bubbleParams.x = 18;
        bubbleParams.y = 180;

        bubble.setOnClickListener(v -> toggleMenu());

        bubble.setOnTouchListener(new View.OnTouchListener() {
            private int downX, downY;
            private float downRawX, downRawY;

            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    downX = bubbleParams.x;
                    downY = bubbleParams.y;
                    downRawX = e.getRawX();
                    downRawY = e.getRawY();
                    return false;
                }
                if (e.getAction() == MotionEvent.ACTION_MOVE) {
                    bubbleParams.x = downX - (int)(e.getRawX() - downRawX);
                    bubbleParams.y = downY + (int)(e.getRawY() - downRawY);
                    windowManager.updateViewLayout(bubble, bubbleParams);
                }
                return false;
            }
        });

        windowManager.addView(bubble, bubbleParams);
    }

    private void toggleMenu() {
        if (menu != null) {
            windowManager.removeView(menu);
            menu = null;
            return;
        }

        menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(10, 10, 10, 10);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(25, 26, 30));
        bg.setCornerRadius(18);
        menu.setBackground(bg);

        TextView config = item(
                "A = " + targetA + "\nTrigger = " + trigger +
                "\nDelay = " + delay + "s");

        TextView activeButton = item("ACTIVE");
        TextView stopButton = item("STOP");
        TextView closeButton = item("CLOSE");

        menu.addView(config);
        menu.addView(activeButton);
        menu.addView(stopButton);
        menu.addView(closeButton);

        activeButton.setOnClickListener(v -> {
            active = true;
            sendToTestApp(ACTION_ACTIVE);
            closeMenu();
        });

        stopButton.setOnClickListener(v -> {
            active = false;
            sendToTestApp(ACTION_STOP);
            closeMenu();
        });

        closeButton.setOnClickListener(v -> closeMenu());

        WindowManager.LayoutParams p = new WindowManager.LayoutParams(
                260, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        p.gravity = Gravity.TOP | Gravity.END;
        p.x = 18;
        p.y = bubbleParams.y + 72;
        windowManager.addView(menu, p);
    }

    private TextView item(String text) {
        TextView v = new TextView(this);
        v.setText(text);
        v.setTextColor(Color.WHITE);
        v.setTextSize(15);
        v.setPadding(18, 14, 18, 14);
        return v;
    }

    private void sendToTestApp(String action) {
        Intent i = new Intent(action);
        i.setPackage(TEST_PACKAGE);
        i.putExtra(MainActivity.KEY_TARGET_A, targetA);
        i.putExtra(MainActivity.KEY_TRIGGER, parseInt(trigger, 15));
        i.putExtra(MainActivity.KEY_DELAY, parseInt(delay, 22));
        sendBroadcast(i);
    }

    private int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value); }
        catch (Exception ignored) { return fallback; }
    }

    private void closeMenu() {
        if (menu != null) {
            windowManager.removeView(menu);
            menu = null;
        }
    }

    @Override public void onDestroy() {
        closeMenu();
        if (bubble != null && windowManager != null) {
            windowManager.removeView(bubble);
            bubble = null;
        }
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }
}
