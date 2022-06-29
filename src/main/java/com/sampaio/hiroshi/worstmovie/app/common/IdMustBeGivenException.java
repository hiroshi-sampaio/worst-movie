package com.sampaio.hiroshi.worstmovie.app.common;

public class IdMustBeGivenException extends BaseException {

    public IdMustBeGivenException() {
        super("ID must be specified when updating entities!");
    }
}
