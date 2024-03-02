package com.socialmeli2.be_java_hisp_w25_g11.utils.messages;

import lombok.Getter;

@Getter
public enum SuccessMessages {
    DEFAULT_MESSAGE("Operación realizada exitosamente"),
    SUCCESFUL_FOLLOW_ACTION("El usuario ha empezado a seguir al vendedor con ID #d"),
    SUCCESFUL_UNFOLLOW_ACTION("El usuario ha dejado de seguir al vendedor con ID #d");

    private final String message;

    SuccessMessages(String message) {
        this.message = message;
    }

    public static String build(SuccessMessages message, Object... args) {
        if (args.length == 0) {
            return message.getMessage();
        }

        return String.format(message.getMessage(), args);
    }
}
