package ru.vsu.cs.masalkin.service.enums;

public enum ServiceCommands {
    START("/start"),
    HELP("/help"),
    REGISTRATION("/registration"),
    MENU("/menu"),
    TOGGLE_NOTIFICATION("/toggle_notification"),
    SEMESTER_LIST("/semester_list"),
    STUDENT_INFO("/student_info"),
    ABOUT_BOT("/about_bot");

    private final String cmd;

    ServiceCommands(String cmd){
        this.cmd = cmd;
    }

    @Override
    public String toString() {
        return cmd;
    }

    public boolean equals(String cmd) {
        return this.toString().equals(cmd);
    }
}
