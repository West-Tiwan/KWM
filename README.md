# KWM — Kotlin Window Manager for Windows

A lightweight **tiling window manager for Windows** written in **Kotlin** using **JNA** and **Compose Desktop**.

KWM automatically arranges application windows into a tiling layout and provides workspace management, keyboard-driven window control, and a real-time status bar.

The goal of this project is to bring a **Linux-style tiling workflow** (similar to i3, XMonad, or GlazeWM) to Windows while keeping the implementation simple and hackable.

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
│   - window discovery
│   - tiling logic
│   - workspace system
│   - hotkey handling
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

The window manager engine does **not depend on the UI**.

---

# Tech Stack

Language:

* Kotlin

Libraries:

* **JNA (Java Native Access)** — Win32 API integration
* **Compose Desktop** — status bar UI
* **OSHI** — system statistics (CPU, RAM, battery)

---

# How It Works

KWM listens to Windows events using:

```
SetWinEventHook
```

Events captured include:

* window creation
* window destruction
* foreground changes

When events occur, KWM:

1. updates workspace window lists
2. recalculates the tiling layout
3. applies new window positions using Win32 APIs

---

# Building

## Requirements

* JDK 17+
* Gradle
* Windows

## Clone the repository

```
git clone https://github.com/<your-username>/kwm
cd kwm
```

## Run the project

```
./gradlew :launcher:run
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

## WindowController

Applies tiling layouts by calling Win32 APIs such as:

```
SetWindowPos
MoveWindow
ShowWindow
```

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
* Windows only

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