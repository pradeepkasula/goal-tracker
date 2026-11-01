package com.goaltracker.jaxrs;

import jakarta.ws.rs.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * JAX-RS has no standard @PATCH annotation (unlike @GET/@POST/@PUT/@DELETE), so it's defined here
 * the way the spec intends custom verbs to be added: a meta-annotation on {@link HttpMethod}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PATCH")
public @interface PATCH {
}
