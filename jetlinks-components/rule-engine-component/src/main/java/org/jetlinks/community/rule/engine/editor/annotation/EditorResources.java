package org.jetlinks.community.rule.engine.editor.annotation;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EditorResources {

    EditorResource [] resources();

}
