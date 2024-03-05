package com.socialmeli2.be_java_hisp_w25_g11.utils.messages;

import lombok.Getter;

@Getter
public enum ErrorMessages {
    NON_EXISTENT_USER("No existe un usuario con ID #%d"),
    NON_EXISTENT_BUYER("No existe un comprador con ID %d"),
    NON_EXISTENT_SELLER("No existe un vendedor con ID #%d"),
    INVALID_DATE_ORDER_ARGUMENT("Argumento invalido (order debe ser DATE_ASC o DATE_DESC)"),
    INVALID_NAME_ORDER_ARGUMENT("Argumento invalido (order debe ser NAME_ASC o NAME_DESC)"),
    USER_INFORMATION_NOT_FOUND("No se pudo encontrar información del usuario con ID #%d"),
    SELLER_INFORMATION_NOT_FOUND("No se pudo encontrar la información del vendedor con ID #%d"),
    USER_TO_FOLLOW_MUST_BE_SELLER("El usuario a seguir debe ser un vendedor"),
    USER_TO_UNFOLLOW_MUST_BE_SELLER("El usuario a dejar de seguir debe ser un vendedor"),
    USER_CANNOT_FOLLOW_HIMSELF("El usuario no se puede seguir a si mismo"),
    USER_CANNOT_UNFOLLOW_HIMSELF("El usuario no se puede dejar de seguir a si mismo"),
    USER_ALREADY_FOLLOWS_SELLER("El usuario ya sigue al vendedor con ID #%d"),
    USER_DOES_NOT_FOLLOW_SELLER("El usuario no sigue al vendedor con ID #%d"),
    USER_CANNOT_HAVE_FOLLOWERS("El ID #%d pertenece a un usuario que no puede tener seguidores"),
    USER_CANNOT_CREATE_POSTS("El ID #%d pertenece a un usuario que no puede crear publicaciones"),
    LIST_USER_INFO_NOT_FOUND("No se encontró la información del usuario con ID #%d"),
    BUYER_DOES_NOT_FOLLOW_SELLER("El comprador con ID #%d no sigue al vendedor con ID #%d"),
    SELLER_DOES_NOT_FOLLOW_SELLER("El vendedor con ID #%d no sigue al vendedor con ID #%d");

    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    public static String build(ErrorMessages message, Object... args) {
        if (args.length == 0) {
            return message.getMessage();
        }

        return String.format(message.getMessage(), args);
    }
}
