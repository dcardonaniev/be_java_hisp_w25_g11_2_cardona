package com.socialmeli2.be_java_hisp_w25_g11.utils.messages;

import lombok.Getter;

@Getter
public class ValidationMessages {
    public static final String DATE_CANNOT_BE_EMPTY = "La fecha no puede estar vacía";
    public static final String PRICE_CANNOT_BE_EMPTY = "El precio no puede estar vacío";
    public static final String PRICE_LIMIT = "El precio máximo por producto es de 10.000.000";
    public static final String USER_ID_CANNOT_BE_EMPTY = "El id del usuario no puede estar vacío";
    public static final String USER_ID_MUST_BE_GREATER_THAN_ZERO = "El id del usuario debe ser mayor a cero";
    public static final String USER_NAME_CANNOT_BE_EMPTY = "El nombre del usuario no puede estar vacío";
    public static final String USER_NAME_SIZE_LIMIT = "El nombre del usuario no puede superar los 15 caracteres";
    public static final String POST_ID_CANNOT_BE_EMPTY = "El id de la publicación no puede estar vacío";
    public static final String POST_ID_MUST_BE_GREATER_THAN_ZERO = "El id de la publicación debe ser mayor a cero";
    public static final String PRODUCT_ID_CANNOT_BE_EMPTY = "El id del producto no puede estar vacío";
    public static final String PRODUCT_ID_MUST_BE_GREATER_THAN_ZERO = "El id del producto debe ser mayor a cero";
    public static final String PRODUCT_NAME_CANNOT_BE_EMPTY = "El nombre del producto no puede estar vacío";
    public static final String PRODUCT_NAME_SIZE_LIMIT = "La longitud del nombre del producto no puede superar los 40 caracteres";
    public static final String PRODUCT_NAME_CONTAINS_SPECIAL_CHARACTERS = "El nombre del producto no puede poseer caracteres especiales";
    public static final String CATEGORY_CANNOT_BE_EMPTY = "La categoria no puede estar vacío";
    public static final String TYPE_CANNOT_BE_EMPTY = "El tipo no puede estar vacío";
    public static final String TYPE_SIZE_LIMIT = "La longitud del tipo no puede superar los 15 caracteres";
    public static final String TYPE_CONTAINS_SPECIAL_CHARACTERS = "El tipo no puede poseer caracteres especiales";
    public static final String BRAND_CANNOT_BE_EMPTY = "La marca no puede estar vacía";
    public static final String BRAND_SIZE_LIMIT = "La longitud del tipo no puede superar los 25 caracteres";
    public static final String BRAND_CONTAINS_SPECIAL_CHARACTERS = "La marca no puede poseer caracteres especiales";
    public static final String COLOR_CANNOT_BE_EMPTY = "El color no puede estar vacío";
    public static final String COLOR_SIZE_LIMIT = "La longitud del color no puede superar los 15 caracteres";
    public static final String COLOR_CONTAINS_SPECIAL_CHARACTERS = "El color no puede poseer caracteres especiales";
    public static final String NOTES_SIZE_LIMIT = "La longitud de las notas no puede superar los 80 caracteres";
    public static final String NOTES_CONTAIN_SPECIAL_CHARACTERS = "Las notas no pueden poseer caracteres especiales";
}
