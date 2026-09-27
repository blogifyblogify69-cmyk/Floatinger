# Floatinger

Floatinger is a **virtual Android test-environment prototype**.

## Current prototype

- Built-in test screen with a 30→1 countdown.
- User-configurable trigger value (default 15).
- ACTIVE / STOP controls.
- Target A and Target B demo controls.
- Deterministic test flow: when the countdown reaches 15, B is triggered once; after 22 seconds, A is triggered.
- The same visible 15 cannot retrigger until the countdown changes away from 15.
- No root, no hidden Android APIs, no third-party APK cloning, and no betting/wagering automation.

## Important architecture note

A normal Android application cannot simply make a transparent, full clone of an arbitrary installed APK inside itself. A real app-container/virtualization product requires a much larger isolation architecture and has compatibility/security limitations.

For external apps, Android AccessibilityService can read accessibility window content and draw overlays, but it must be explicitly enabled by the user. Google Play also has additional disclosure and automation requirements. This prototype therefore keeps the automation inside a controlled test screen.

## Planned legal QA architecture

For an app the developer owns or is authorized to test:

1. Select the package under test.
2. Explicitly enable the required Android capability.
3. Restrict inspection to the selected package.
4. Read accessible text where available.
5. Use screenshot/OCR only where the test owner has authorization and the platform permits it.
6. Show a visible floating controller with ACTIVE / STOP.
7. Require human-defined deterministic rules.
8. Stop when the selected app is no longer foreground.
9. Keep an audit/status view so the user can see what the test system is doing.

This repository does **not** implement automatic actions against the betting interface shown in the supplied screenshot.
