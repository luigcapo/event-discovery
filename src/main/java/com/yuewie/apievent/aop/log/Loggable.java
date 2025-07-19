package com.yuewie.apievent.aop.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD}) // S'applique aux classes ET aux méthodes
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * Si false, le logging sera complètement désactivé pour cette portée.
     */
    boolean active() default true;
    /**
     * Si true, les arguments de la méthode seront inclus dans les logs (niveau TRACE).
     */
    boolean logParams() default false;
}
