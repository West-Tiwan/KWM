# KWM — Kotlin Window Manager

A lightweight **tiling window manager for Windows and macOS** written in **Kotlin** using **JNA** and **Compose Desktop**.

KWM automatically arranges application windows into a tiling layout and provides workspace management, keyboard-driven window control, and a real-time status bar.

The goal of this project is to bring a **Linux-style tiling workflow** (similar to i3, XMonad, or GlazeWM) to Windows and macOS while keeping the implementation simple and hackable. It works fully offline.

KWM detects the operating system at startup and loads the matching platform backend, so the same build runs on both.

---

# Features

## Window Tiling

Windows are automatically arranged in a tiling layout.

Current layout:

```
1 window  → fullscreen
2 windows → left / right
3 windows → left + top-right + bottom-right
4 windows → left + 3 stacked right
```

Windows are dynamically re-tiled when:

* a new window appears
* a window closes
* windows are reordered
* workspaces change

---

## Workspaces

KWM supports **multiple workspaces**.

Each workspace maintains its own window list.

Switching workspace will:

* hide windows from the previous workspace
* restore windows from the new workspace
* re-tile them
* focus the primary window

---

## Keyboard Window Control

Windows can be reordered or moved using hotkeys.

Operations supported:

* Promote window to master
* Move window up/down in stack
* Move window to another workspace
* Switch workspaces

Hotkeys use **Alt** on Windows and **Option** on macOS:

| Hotkey | Action |
| --- | --- |
| `Alt + ←` | Promote focused window to master |
| `Alt + ↑` / `Alt + ↓` | Move focused window up / down the stack |
| `Alt + 1…5` | Switch to workspace 1–5 |
| `Alt + Shift + 1…5` | Move focused window to workspace 1–5 |
| `Alt + Shift + Q` | Quit KWM |

---

## Focus Follows Mouse

KWM can optionally enable **focus-follows-mouse**.

When the cursor moves over a window, that window receives focus without changing the tiling order.

---

## Status Bar

A top bar built with **Compose Desktop** displays real-time information.

Left side:

* Workspace indicators

Center:

* Current date
* Current time

Right side:

* CPU usage
* RAM usage
* Battery level

Example layout:

```
[1] [2] [3] [4] [5]       Fri Feb 21 23:18       CPU 17%  RAM 43%  BAT 92%
```

---

# Architecture

The project is organized into **multiple modules**.

```
KWM
│
├─ core
│   Window management engine
│   - tiling logic
│   - workspace system
│   - hotkey bindings
│   └─ platform
│       Platform interface + OS detection
│       ├─ windows   Win32: window discovery, events, hotkeys
│       └─ macos     Accessibility API, CoreGraphics event tap
│
├─ bar
│   Compose Desktop UI
│   - workspace indicators
│   - system stats
│   - clock
│
└─ launcher
    Application entry point
    Starts both WM and bar
```

Design principle:

```
core  → window manager engine
bar   → UI only
launcher → connects both
```

The window manager engine does **not depend on the UI**, and the tiling and workspace logic does **not depend on the OS**. Everything OS-specific sits behind the `Platform` interface.

---

# Tech Stack

Language:

* Kotlin

Libraries:

* **JNA (Java Native Access)** — Win32, Accessibility, CoreGraphics and CoreFoundation APIs
* **Compose Desktop** — status bar UI
* **OSHI** — system statistics (CPU, RAM, battery)

---

# How It Works

## Windows

KWM listens to Windows events using:

```
SetWinEventHook
```

Events captured include:

* window creation
* window destruction
* foreground changes

Hotkeys are registered with `RegisterHotKey`, and windows are moved with `SetWindowPos`.

## macOS

* **Window discovery:** `CGWindowListCopyWindowInfo` finds apps with windows on screen, and the Accessibility API lists their standard windows (skipping sheets, panels, minimized and full screen windows)
* **Tiling:** windows are moved and resized by setting their `AXPosition` and `AXSize` attributes
* **Window changes:** polled every 250 ms, since macOS has no system-wide equivalent of `SetWinEventHook`
* **Hotkeys:** a `CGEventTap` swallows matching key presses, so `Option + 1` doesn't also type `¡`
* **Workspaces:** macOS can't hide a single window, so windows on other workspaces are parked in the bottom-right corner of the screen
* **Status bar:** sits below the menu bar, and KWM runs without a Dock icon

## Both

When events occur, KWM:

1. updates workspace window lists
2. recalculates the tiling layout
3. applies new window positions through the platform backend

---

# Building

## Requirements

* JDK 17+
* Gradle (the wrapper runs its daemon on JDK 21 and downloads one if needed, so newer default JDKs work)
* Windows 10/11 or macOS

## Clone the repository

```
git clone https://github.com/<your-username>/kwm
cd kwm
```

## Run the project

```
./gradlew :launcher:run
```

### macOS permissions

KWM needs **Accessibility** access to move other apps' windows and listen for hotkeys. On first launch macOS shows a prompt; allow it under **System Settings → Privacy & Security → Accessibility** for the app you launched KWM from (Terminal, iTerm or your IDE). KWM waits until access is granted.

## Run the tests

```
./gradlew :core:test
```

---

# Key Components

## WindowManager

Responsible for:

* tiling layout
* window promotion
* window movement
* workspace switching

---

## WorkspaceManager

Maintains window lists for each workspace.

Handles:

* window insertion
* window removal
* workspace switching
* window swapping

---

## Platform

Interface between the window manager and the OS. `Platform.create()` picks the backend for the detected OS.

* **WindowsPlatform** — `SetWinEventHook`, `RegisterHotKey`, `SetWindowPos`, `ShowWindow`
* **MacPlatform** — Accessibility API, `CGEventTap`, `CGWindowListCopyWindowInfo`

---

## SystemStatService

Provides system statistics for the bar using **OSHI**.

Updates:

* CPU usage
* RAM usage
* battery state
* clock

---

## BarApp

Compose Desktop application responsible for rendering the top bar.

Displays:

* workspace state
* system stats
* time/date

---

# Current Limitations

* Single monitor support
* Fixed tiling layout
* No configuration file yet
* No animated transitions
* macOS: only windows on the current Space are managed
* macOS: window changes are polled rather than event-driven

---

# Roadmap

Planned features.

## Window Manager

* multi-monitor support
* configurable layouts
* smart gaps
* floating windows
* layout switching

## UI

* window titles in bar
* system tray integration
* weather widget
* notification area

## Configuration

* config file for keybinds
* layout configuration
* workspace naming

## Performance

* replace polling with event-driven mouse focus
* improve window detection filtering

---

# Inspiration

This project is inspired by:

* GlazeWM
* DWM
* yabai

---

# Contributing

Contributions are welcome.

Possible areas:

* layout algorithms
* multi-monitor support
* animation improvements
* better window detection
* UI improvements

---

# License

MIT License

---

# Author

**Mohit Singh**

Computer Science Student

IIIT Sonepat 2022-26