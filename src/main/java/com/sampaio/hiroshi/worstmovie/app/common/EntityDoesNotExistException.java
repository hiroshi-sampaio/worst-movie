package com.sampaio.hiroshi.worstmovie.app.common;

public class EntityDoesNotExistException extends BaseException {

    public EntityDoesNotExistException() {
        super("Can't update non-existing entity!");
    }
}
