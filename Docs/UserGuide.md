# FitnessONE User Guide

FitnessONE is a **desktop application for fitness coaches to manage multiple athletes and their training sessions**, optimized for use via a **Command Line Interface (CLI)**. If you can type fast, FitnessONE can help you manage your athletes' training programs more efficiently than traditional GUI apps.

--------------------------------------------------------------------------------------------------------------------

## Table of Contents
* [Quick Start](#quick-start)
* [Features](#features)
  * [Athlete Management](#athlete-management)
    * [Adding a new athlete: `/newathlete`](#adding-a-new-athlete-newathlete)
    * [Viewing athlete details: `/viewathlete`](#viewing-athlete-details-viewathlete)
    * [Listing all athletes: `/listathletes`](#listing-all-athletes-listathletes)
    * [Flagging an athlete: `/flagathlete`](#flagging-an-athlete-flagathlete)
    * [Deleting an athlete: `/deleteathlete`](#deleting-an-athlete-deleteathlete)
  * [Session Management](#session-management)
    * [Adding a new session: `/newsession`](#adding-a-new-session-newsession)
    * [Viewing all sessions: `/viewsessions`](#viewing-all-sessions-viewsessions)
    * [Updating session notes: `/updatesessionnote`](#updating-session-notes-updatesessionnote)
    * [Completing a session: `/completesession`](#completing-a-session-completesession)
    * [Undoing session completion: `/undosession`](#undoing-session-completion-undosession)
    * [Deleting a session: `/deletesession`](#deleting-a-session-deletesession)
  * [Exercise Management](#exercise-management)
    * [Adding a new exercise: `/newexercise`](#adding-a-new-exercise-newexercise)
    * [Viewing all exercises: `/viewexercises`](#viewing-all-exercises-viewexercises)
    * [Completing an exercise: `/completeexercise`](#completing-an-exercise-completeexercise)
    * [Undoing exercise completion: `/undoexercise`](#undoing-exercise-completion-undoexercise)
    * [Deleting an exercise: `/deleteexercise`](#deleting-an-exercise-deleteexercise)
  * [Other Features](#other-features)
    * [Viewing the leaderboard: `/leaderboard`](#viewing-the-leaderboard-leaderboard)
    * [Viewing help: `/help`](#viewing-help-help)
    * [Exiting the program: `bye`](#exiting-the-program-bye)
    * [Saving the data](#saving-the-data)
* [FAQ](#faq)
* [Command Summary](#command-summary)

--------------------------------------------------------------------------------------------------------------------

## Quick Start

1. Ensure you have Java `17` or above installed on your computer.

2. Download the latest `FitnessONE.jar` file from [here](https://github.com/AY2526S1-CS2113-W14-1/tp/releases).

3. Copy the file to the folder you want to use as the home folder for FitnessONE.

4. Open a command terminal, navigate (`cd`) into the folder you put the jar file in, and use the `java -jar FitnessONE.jar` command to run the application.

5. Type a command in the terminal and press Enter to execute it. For example:
   * `/help` : Shows a list of all available commands.
   * `/newathlete John Doe` : Adds a new athlete named "John Doe".
   * `/listathletes` : Lists all athletes.
   * `bye` : Exits the app.

6. Refer to the [Features](#features) below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

**:information_source: Notes about the command format:**<br>

* Words in `<UPPER_CASE>` are the parameters to be supplied by you.<br>
  e.g. in `/newathlete <ATHLETE_NAME>`, `<ATHLETE_NAME>` is a parameter which can be used as `/newathlete John Doe`.

* Items in square brackets are optional.<br>
  e.g. `<ATHLETE_NAME> [COLOR]` can be used as `John Doe red` or as `John Doe`.

* Parameters must be in the specified order.<br>
  e.g. if the command specifies `/newexercise <ATHLETE_ID>  <SESSION_ID> <Description> <SETS> <REPS>`, the order must be followed.

* **ID Formats:**
  * Athlete ID: 4 digits (e.g., `0001`)
  * Session ID: 3 digits (e.g., `001`)
  * Exercise ID: 2 digits (e.g., `01`)

* Extraneous parameters for commands that do not take parameters (such as `/help`, `/listathletes`, and `bye`) will cause an error.

**All outputs are displayed in lowercase, regardless of how the input is entered (e.g., uppercase or mixed case)**

</div>
--------------------------------------------------------------------------------------------------------------------

### Athlete Management

#### Adding a new athlete: `/newathlete`

Creates a new athlete profile with a specified name. The system automatically assigns a unique 4-digit athlete ID.

**Format:** `/newathlete <ATHLETE_NAME>`

**Example:**
```
/newathlete Jonas Hardwell
```

**Expected output:**
```
____________________________________________________________
Athlete added:
     [  ] jonas hardwell (0001)
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Viewing athlete details: `/viewathlete`

Displays detailed information about a specific athlete, including all their sessions and exercises.

**Format:** `/viewathlete <ATHLETE_ID> `

**Example:**
```
/viewathlete 0001
```

**Expected output:**
```
____________________________________________________________
    Athlete Name: jonas hardwell
    Sessions: 2
    List:
       Session 1. | Notes: chest day
       Exercises:
          1. | Notes: bench press | sets x reps: 5 x 3
          2. | Notes: cable press | sets x reps: 5 x 15
       Session 2. | Notes: leg day
       Exercises:
          1. | Notes: squat | sets x reps: 5 x 5
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Listing all athletes: `/listathletes`

Displays a list of all registered athletes with their IDs and priority flags.

**Format:** `/listathletes`

**Example:**
```
/listathletes
```

**Expected output:**
```
____________________________________________________________
    1. [RED] jonas hardwell (0001)
    2. [YELLOW] john doe (0002)
    3. [ ] jonathan well (0003)

____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Flagging an athlete: `/flagathlete`

Assigns a color flag to an athlete for priority tracking.

**Format:** `/flagathlete <ATHLETE_ID>  <COLOR>`

**Supported colors:** `red`, `yellow`, `green`

**Example:**
```
/flagathlete 0001 red
```

**Expected output:**
```
____________________________________________________________
Athlete 0001 flagged as: RED
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Deleting an athlete: `/deleteathlete`

Deletes an athlete and all associated data (sessions and exercises).

⚠️ **Caution:**
This action cannot be undone. All data associated with the athlete will be permanently deleted.

**Format:** `/deleteathlete <ATHLETE_ID> `

**Example:**
```
/deleteathlete 0001
```

**Expected output:**
```
____________________________________________________________
Deleted athlete with ID 0001
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

### Session Management

#### Adding a new session: `/newsession`

Creates a new training session for a specific athlete.

**Format:** `/newsession <ATHLETE_ID>  <Session Notes>`

**Example:**
```
/newsession 0001 Leg day workout
```

**Expected output:**
```
____________________________________________________________
New session created:
Athlete Name: jonas hardwell | ID: 0001

Session ID: 001
Session Description: leg day workout
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Viewing all sessions: `/viewsessions`

Displays all sessions for a specific athlete, showing completion status, date, and notes.

**Format:** `/viewsessions <ATHLETE_ID> `

**Example:**
```
/viewsessions 0001
```

**Expected output:**
```
____________________________________________________________
Athlete ID: 0001 | Name: jonas hardwell

Status |  Session ID   | Date                   | Notes
1. [ ] |  Session: 001 | Date: 29-10-2025 08:00 | morning session
2. [X] |  Session: 002 | Date: 29-10-2025 10:00 | pre training warmup
3. [ ] |  Session: 003 | Date: 29-10-2025 15:00 | post training cool down
____________________________________________________________
```

💡 **Tip:**
`[X]` indicates a completed session, while `[ ]` indicates an incomplete session.

--------------------------------------------------------------------------------------------------------------------

#### Updating session notes: `/updatesessionnote`

Updates the training notes for a specific session.

**Format:** `/updatesessionnote <ATHLETE_ID>  <SESSION_ID> <New Notes>`

**Example:**
```
/updatesessionnote 0001 001 Upper body strength training
```

**Expected output:**
```
____________________________________________________________
Successfully updated Athlete (ID: 0001) session: 001 with notes: upper body strength training.
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Completing a session: `/completesession`

Marks a session as completed.

**Format:** `/completesession <ATHLETE_ID>  <SESSION_ID>`

**Example:**
```
/completesession 0001 001
```

**Expected output:**
```
____________________________________________________________
Session (ID: 001) completed by jonas hardwell (ID: 0001).
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Undoing session completion: `/undosession`

Marks a previously completed session as not completed.

**Format:** `/undosession <ATHLETE_ID>  <SESSION_ID>`

**Example:**
```
/undosession 0001 001
```

**Expected output:**
```
____________________________________________________________
Session (ID: 001) has been marked as not completed for johnas hardwell (ID: 0002).
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Deleting a session: `/deletesession`

Deletes a specific session and all its associated exercises.

⚠️ **Caution:**
This action cannot be undone. All exercises in this session will also be deleted.

**Format:** `/deletesession <ATHLETE_ID>  <SESSION_ID>`

**Example:**
```
/deletesession 0001 003
```

**Expected output:**
```
____________________________________________________________
Session 001 deleted for 0002
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

### Exercise Management

#### Adding a new exercise: `/newexercise`

Adds a new exercise to a specific session with sets and reps.

**Format:** `/newexercise <ATHLETE_ID>  <SESSION_ID> <Exercise Description> <SETS> <REPS>`

**Example:**
```
/newexercise 0001 001 Bench Press 5 10
```

**Expected output:**
```
____________________________________________________________
New exercise created!

Athlete (ID): 0001
Athlete name: jonas hardwell
Session (ID): 001 | Date: 03-11-2025 13:23
Session Description: chest day

Exercise (ID): 01
Exercise Description: bench press
sets x reps: 5 x 10
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Viewing all exercises: `/viewexercises`

Displays all exercises in a specific session.

**Format:** `/viewexercises <ATHLETE_ID>  <SESSION_ID>`

**Example:**
```
/viewexercises 0001 001
```

**Expected output:**
```
____________________________________________________________
Athlete ID: 0001
Athlete Name: jonas hardwell
Session ID: 001 | 03-11-2025 13:12

Session Note: Chest day
Exercise List: 3
1. [X] ID: 01 bench press 5 x 10
2. [ ] ID: 02 cable fly 3 x 12
3. [ ] ID: 03 push ups 3 x 15
____________________________________________________________
```

💡 **Tip:** `[X]` indicates a completed exercise, while `[ ]` indicates an incomplete exercise.

--------------------------------------------------------------------------------------------------------------------

#### Completing an exercise: `/completeexercise`

Marks an exercise as completed.

**Format:** `/completeexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`

**Example:**
```
/completeexercise 0001 001 01
```

**Expected output:**
```
____________________________________________________________
Exercise (ID: 01) completed by jonas hardwell (ID: 0001).
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Undoing exercise completion: `/undoexercise`

Marks a previously completed exercise as not completed.

**Format:** `/undoexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`

**Example:**
```
/undoexercise 0001 001 01
```

**Expected output:**
```
____________________________________________________________
Exercise (ID: 01), Session (ID: 001) has been marked as not completed for jonas hardwell (ID: 0001).
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Deleting an exercise: `/deleteexercise`

Deletes a specific exercise from a session.

⚠️ **Caution:** This action cannot be undone.

**Format:** `/deleteexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`

**Example:**
```
/deleteexercise 0001 001 02
```

**Expected output:**
```
____________________________________________________________
Deleted exercise (ID: 02) from session 001 for jonas hardwell (ID: 0001).
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

### Other Features

#### Viewing the leaderboard: `/leaderboard`

Displays a ranked list of all athletes based on their achievement scores (completed exercises and sessions).

**Format:** `/leaderboard`

**Example:**
```
/leaderboard
```

**Expected output:**
```
____________________________________________________________
athleteName     athleteId score
    ____________________________________________________________
    jonas hardwell  0001      15
    john doe        0002      10
    jane smith      0003      5

____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Viewing help: `/help`

Shows a comprehensive list of all available commands with their formats.

**Format:** `/help`

**Example:**
```
/help
```

**Expected output:**
```
____________________________________________________________
FitnessONE - Available Commands

=== ATHLETE COMMANDS ===
/newathlete <ATHLETE_NAME> - Create a new athlete
/viewathlete <ATHLETE_ID>  - View athlete details
/listathletes - List all athletes
/deleteathlete <ATHLETE_ID>  - Delete an athlete
/flagathlete <ATHLETE_ID>  <COLOR> - Flag an athlete

=== SESSION COMMANDS ===
/newsession <ATHLETE_ID>  <NOTES> - Create a new session
/deletesession <ATHLETE_ID>  <SESSION_ID> - Delete a session
/completesession <ATHLETE_ID>  <SESSION_ID> - Complete a session
/viewsessions <ATHLETE_ID>  - View all sessions
/updatesessionnote <ATHLETE_ID>  <SESSION_ID> <NOTES> - Update notes
/undosession <ATHLETE_ID>  <SESSION_ID> - Undo completion

=== EXERCISE COMMANDS ===
/newexercise <ATHLETE_ID>  <SESSION_ID> <Description> <SETS> <REPS> - Create exercise
/deleteexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID> - Delete exercise
/viewexercises <ATHLETE_ID>  <SESSION_ID> - View all exercises
/completeexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID> - Complete exercise
/undoexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID> - Undo completion

=== OTHER COMMANDS ===
/help - Show this help message
/leaderboard - Show the leaderboard
bye - Exit the application

Tip: Each command has its own help. Try using an incorrect format to see details!
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Exiting the program: `bye`

Exits FitnessONE and saves all data automatically.

**Format:** `bye`

**Example:**
```
bye
```

**Expected output:**
```
____________________________________________________________
Bye. Hope to see you again soon!
____________________________________________________________
```

--------------------------------------------------------------------------------------------------------------------

#### Saving the data

FitnessONE data is saved in the hard disk automatically after any command that changes the data. There is no need to save manually.

The data file is located at `[JAR file location]/data/athletes_export.txt`.

💡 **Tip:** 
FitnessONE creates the data folder and file automatically if they don't exist.

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q: How do I transfer my data to another computer?**  
A: Install FitnessONE on the other computer and replace the empty `athletes_export.txt` file it creates with the file from your previous FitnessONE home folder (located in the `data` directory).

**Q: Can I edit the data file directly?**  
A: Advanced users can edit the `athletes_export.txt` file directly, but this is not recommended. If the file format becomes invalid, FitnessONE will start with an empty data file. Always back up your data file before editing.

**Q: What happens if I delete an athlete?**  
A: All sessions and exercises associated with that athlete will be permanently deleted. This action cannot be undone.

**Q: Why are athlete IDs always 4 digits?**  
A: FitnessONE uses fixed-length IDs for consistency and to make it easier to reference athletes in commands. The system supports up to 9,999 athletes.

**Q: Can I have multiple athletes with the same name?**  
A: Yes, each athlete is uniquely identified by their athlete ID, not their name. You can have multiple athletes with the same name.

--------------------------------------------------------------------------------------------------------------------

## Command Summary

| Action                   | Command                                                               | Example                                  |
|--------------------------|-----------------------------------------------------------------------|------------------------------------------|
| **Add athlete**          | `/newathlete <ATHLETE_NAME>`                                          | `/newathlete Jonas Hardwell`             |
| **View athlete**         | `/viewathlete <ATHLETE_ID> `                                          | `/viewathlete 0001`                      |
| **List athletes**        | `/listathletes`                                                       | `/listathletes`                          |
| **Flag athlete**         | `/flagathlete <ATHLETE_ID>  <COLOR>`                                  | `/flagathlete 0001 red`                  |
| **Delete athlete**       | `/deleteathlete <ATHLETE_ID> `                                        | `/deleteathlete 0001`                    |
| **Add session**          | `/newsession <ATHLETE_ID>  <NOTES>`                                   | `/newsession 0001 Leg day`               |
| **View sessions**        | `/viewsessions <ATHLETE_ID> `                                         | `/viewsessions 0001`                     |
| **Update session notes** | `/updatesessionnote <ATHLETE_ID>  <SESSION_ID> <NOTES>`               | `/updatesessionnote 0001 001 Upper body` |
| **Complete session**     | `/completesession <ATHLETE_ID>  <SESSION_ID>`                         | `/completesession 0001 001`              |
| **Undo session**         | `/undosession <ATHLETE_ID>  <SESSION_ID>`                             | `/undosession 0001 001`                  |
| **Delete session**       | `/deletesession <ATHLETE_ID>  <SESSION_ID>`                           | `/deletesession 0001 001`                |
| **Add exercise**         | `/newexercise <ATHLETE_ID>  <SESSION_ID> <Description> <SETS> <REPS>` | `/newexercise 0001 001 Bench Press 5 10` |
| **View exercises**       | `/viewexercises <ATHLETE_ID>  <SESSION_ID>`                           | `/viewexercises 0001 001`                |
| **Complete exercise**    | `/completeexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`          | `/completeexercise 0001 001 01`          |
| **Undo exercise**        | `/undoexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`              | `/undoexercise 0001 001 01`              |
| **Delete exercise**      | `/deleteexercise <ATHLETE_ID>  <SESSION_ID> <EXERCISE_ID>`            | `/deleteexercise 0001 001 01`            |
| **View leaderboard**     | `/leaderboard`                                                        | `/leaderboard`                           |
| **View help**            | `/help`                                                               | `/help`                                  |
| **Exit**                 | `bye`                                                                 | `bye`                                    |