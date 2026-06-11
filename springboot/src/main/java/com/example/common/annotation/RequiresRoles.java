package com.example.common.annotation;

import com.example.common.enums.Logical;
import com.example.common.enums.RoleEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRoles {
    RoleEnum[] value();

    Logical logical() default Logical.OR;
}