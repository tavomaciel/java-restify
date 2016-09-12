package com.restify.http.contract;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.restify.http.metadata.EndpointMethodParameterSerializer;
import com.restify.http.metadata.EndpointMethodQueryParameterSerializer;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter
public @interface QueryParameter {

	String value() default "";

	Class<? extends EndpointMethodParameterSerializer> serializer() default EndpointMethodQueryParameterSerializer.class;
}