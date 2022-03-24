/*
 * Copyright (c) COMU GmbH - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly
 * prohibited Proprietary and confidential.
 */
package de.seinab.backend.datatable.resolver;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface DataTablesEditorRequestParam
{
    String value() default "";
}
