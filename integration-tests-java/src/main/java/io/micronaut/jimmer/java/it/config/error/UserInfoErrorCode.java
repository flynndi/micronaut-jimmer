package io.micronaut.jimmer.java.it.config.error;

import org.babyfish.jimmer.error.ErrorFamily;
import org.babyfish.jimmer.error.ErrorField;

@ErrorFamily
public enum UserInfoErrorCode {
    @ErrorField(name = "illegalChars", type = Character.class, list = true)
    ILLEGAL_USER_NAME,

    PASSWORD_TOO_SHORT,

    PASSWORDS_NOT_SAME
}
