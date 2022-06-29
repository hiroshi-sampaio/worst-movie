package com.sampaio.hiroshi.worstmovie.app.common;

public class IdMustBeEmptyException extends BaseException {

    public IdMustBeEmptyException() {
        super("ID is auto generated for new entities!");
    }
}
