package inputs.commands;

/**
 * Command interface for input actions, "COMMAND PATTERN"
 * https://refactoring.guru/design-patterns/command
 * IMPORTANT! We have to create a new press and release for every
 * new keyboard input we do.
 */
public interface Command {
    void execute();
}
