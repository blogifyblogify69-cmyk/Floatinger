# Floatinger — Two-App QA Controller

Floatinger is the controller APK for a two-APK QA/test prototype.

## Workflow

1. Open Floatinger.
2. Set Target A value, trigger value, and delay.
3. Grant the user-approved Display over other apps capability.
4. Show the floating F icon.
5. Open the companion Virtual QA Test APK.
6. Tap ACTIVE or STOP from the floating controller.

The companion test APK owns its own countdown and test buttons. Floatinger sends only the configured test command to that dedicated QA package.

## Current capabilities

- Target A setting (default 1.50)
- Trigger setting (default 15)
- Delay setting (default 22 seconds)
- Floating controller icon
- ACTIVE / STOP controls
- Two-app command channel to the dedicated Virtual QA Test APK
- No root
- No APK cloning
- No third-party app inspection
- No wagering automation

Android application overlays use SYSTEM_ALERT_WINDOW and require the user to grant the overlay capability in Settings. TYPE_APPLICATION_OVERLAY is the supported overlay window type for regular apps on modern Android.

## Companion project

https://github.com/blogifyblogify69-cmyk/Auto-cliker

This repository is intentionally a test harness rather than a controller for arbitrary installed applications.
