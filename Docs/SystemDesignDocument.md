# RUST RUNNER [V.0.5]

---
# System Design Document (SDD)

> This is an up-to-date snapshot of the current design of RUST RUNNER, as of the 5th December, 2025.
> This document is a summarized understanding of
> the architecture, classes, and how the system interact with itself done by us, ***Group 16***.

---
## Developers
- `Rayan Ahmad`    : `RayanAhmad123`
- `Philip Hasson`  : `ChangIkJoong`
- `Nadir Morabeth` :  ` `
- `Oscar Jöjk`     : ` `
- `Janna`          : ` `

---
## 1. Architectural Overview

The game is meant to be structured around **MVC architecture** with additional
patterns. The **MVC** pattern in our architecture consiting of:
- **Model**
  - `GameModel` : The central **GAME** model for our project, implementing the model for our game.
  - `Player`, `Level`, `LevelManager` are also part of it, with entity classes under `entities`
  directory and the level design and management in the `Levels` directory.
- **View**
  - `GameView` : Renders only the game environment, HUD, pause overlay, and transition.
  - `GamePanel`, `GameWindow` : The Swing UI components, and window setup for the game.
  - Main Menu, leaderboard and level-selection are renderers under `main.states`, abstracted with the `GameBaseState` using
    the **State pattern**.
- **Controller**
  - `Game` : The main **Controller** unit and game loop, controlling and composing together the
  **Model** and **View**, along with the different states and observers in the game.
  - `GameBaseState` : An abstract of the concrete states implemented including the `PlayingState`, `MenuState`,
    `LeaderboardState`, `LevelSelectState`. These emulate the **State pattern**, also communicating directly
  with other Objects such as the **Singleton Pattern** of `AudioController`, enriching the system
  with audible haptic feedback and sound.
  - `KeyboardInputs`, `MouseInputs` and `inputs.commands.*` use a **Command pattern**
    for input handling.
  - `events` : Directory includes several **Observer Pattern** Event Listeners, which
  are meant to be implemented to act as interfaces between `Game` and different sub-classes.

### 1.1 High-level components

- `MainClass`
  - creates `Game` (main controller)
- `Game`
  - owns `GameModel`, `GameView`, `LevelManager`, `Player`, `AudioController`
  - owns view adapter: `GamePanel`, `GameWindow`
  - manages the current `GameBaseState` implementation
  - acts as the central **Controller** hub, will be listening to low-level model events
    (via listeners on `Player`) and communicating with higher-level
    game events (via `GameEventListener` and `LevelCompletedListener`).
- `GameModel`
  - references `Player`, `LevelManager`
  - exposes getters for data through the view (`GameView`) and controller (`Game`)
- `GameView`
  - reads from `GameModel` and draws via `Graphics`
- `LevelManager`
  - owns the collection of different `Level` objects
  - uses `LoadSave` and `LevelConfigLoader` to build levels from resources
  - exposes `loadNextLevel()` and `resetToFirstLevel()` used by `GameModel` and `Game`.
- `AudioController`
  - singleton that loads and plays music and sound effects (via JavaFX `MediaPlayer`/`AudioClip`)
- `inputs/KeyboardInputs` & `inputs/MouseInputs`
  - map user events to `Command` objects that call into `Game`/`Player`

---

## 2. Core Class Diagram (Overview) //TODO INSERT UMLS

Here are a few UML-style class diagrams for the core architecture. They are not complete
and have been abstracted to easier understand the key responsibilities and relations in our application software.

Relationships (summary):

- `MainClass` -> `Game` (creates)
- `Game` -> `GameModel`, `GameView`, `LevelManager`, `Player`, `AudioController`, `GamePanel`, `GameWindow`, concrete states

- `GameModel` -> `Player`, `LevelManager`
- `GameView` -> `GameModel`
- `LevelManager` -> [`Level`]s
- `KeyboardInputs` / `MouseInputs` -> `Command` -> `Game` | `Player` | UI (state)

- To be continued:
- `Game` -> `GameEventListener` for high-level death/level-completion notifications to views/services
- `Game` -> `LevelCompletedListener` (default listener in `Game` that updates the score file via `LoadSave.appendToScoreFile` and forwards to `GameEventListener`)
- `Player` -> `PlayerEventListener` (implemented by `Game`) for death notifications

---

## 3. MVC Responsibilities

### 3.1 Model Layer

**Key classes:**
