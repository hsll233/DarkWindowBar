# Minecraft 26.2 unofficial port

Upstream: https://github.com/txnimc/DarkWindowBar
Author: Txni
Upstream revision: f1fdad3c7434bc95332d29fc4a52c0c9bbd6f7dc
Port date: 2026-09-26
License: unchanged; see LICENSE.md. This is not an official upstream release.

## Changes

- Preserve the upstream JNA DwmApi interface and dark-title-bar implementation.
- Use Minecraft 26.2 Window.handle() instead of Window.getWindow().
- Apply styling through Fabric CLIENT_STARTED on the game thread, replacing the constructor mixin.
- Remove legacy OS version checks and the resize-based repaint workaround.
- On Windows 11, explicitly set a black caption and white text; older Windows 10 versions fall back to dark mode where supported.
- Enable by default; config/darkwindowbar.properties accepts enabled=false (restart required).
- Cosmetic errors are caught so they do not block game startup.
- Do not change global themes, FPS, window size, shaders or focus.

## Build

Run ./build-local.ps1 with PowerShell 7 and JDK 21+. The game needs Java 25.
The standalone script downloads pinned compile dependencies and verifies SHA-256.
The original multiplatform template remains for upstream reference; only the three port classes and selected resources are included by this script.
Output: build-local/darkwindowbar-fabric-26.2-1.0.0-port.1.jar.

## Verification

- Standalone build with downloaded dependencies completed successfully.
- A hidden Windows test window accepted all three DWM attributes; the dark-mode attribute read back as enabled. Caption/text attributes are set-only on Windows.
- Startup listener registration succeeded with the installed Minecraft 26.2 and Fabric lifecycle event module.
- Actual appearance across Windows versions and themes is not covered by automated Minecraft GUI tests.
