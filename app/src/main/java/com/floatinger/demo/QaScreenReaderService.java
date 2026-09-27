package com.floatinger.demo;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.Intent;

public class QaScreenReaderService extends AccessibilityService {
    private static final String TEST_PACKAGE = "com.autoclicker.virtualtest";
    public static final String ACTION_TEXT_UPDATE = "com.floatinger.demo.ACTION_QA_TEXT_UPDATE";

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null ||
                !TEST_PACKAGE.contentEquals(event.getPackageName())) return;

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) return;

        StringBuilder text = new StringBuilder();
        collect(root, text);
        Intent i = new Intent(ACTION_TEXT_UPDATE);
        i.setPackage(getPackageName());
        i.putExtra("text", text.toString().trim());
        sendBroadcast(i);
        root.recycle();
    }

    private void collect(AccessibilityNodeInfo node, StringBuilder out) {
        if (node == null) return;
        CharSequence value = node.getText();
        if (value != null && value.length() > 0) {
            if (out.length() > 0) out.append(" | ");
            out.append(value);
        }
        for (int n = 0; n < node.getChildCount(); n++) collect(node.getChild(n), out);
    }

    @Override public void onInterrupt() {}
}
