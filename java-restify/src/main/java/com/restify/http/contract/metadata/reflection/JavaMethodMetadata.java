package com.restify.http.contract.metadata.reflection;

import java.lang.reflect.Type;
import java.util.Optional;

import com.restify.http.contract.Header;
import com.restify.http.contract.Headers;
import com.restify.http.contract.Method;
import com.restify.http.contract.Path;

public class JavaMethodMetadata {

	private final java.lang.reflect.Method javaMethod;
	private final Path path;
	private final Method httpMethod;
	private final Header[] headers;

	public JavaMethodMetadata(java.lang.reflect.Method javaMethod) {
		this.javaMethod = javaMethod;

		this.path = Optional.ofNullable(javaMethod.getAnnotation(Path.class))
				.orElseThrow(() -> new IllegalArgumentException("Method " + javaMethod + " does not have a @Path annotation"));

		this.httpMethod = Optional.ofNullable(new JavaAnnotationScanner(javaMethod).scan(Method.class))
				.orElseThrow(() -> new IllegalArgumentException("Method " + javaMethod + " does not have a @Method annotation"));

		this.headers = Optional.ofNullable(javaMethod.getAnnotation(Headers.class))
				.map(Headers::value)
					.orElseGet(() -> new JavaAnnotationScanner(javaMethod).scanAll(Header.class));
	}

	public Path path() {
		return path;
	}

	public Method httpMethod() {
		return httpMethod;
	}

	public java.lang.reflect.Method javaMethod() {
		return javaMethod;
	}

	public Header[] headers() {
		return headers;
	}

	public Type returnType(Class<?> rawType) {
		return new JavaTypeResolver(rawType).returnTypeOf(javaMethod);
	}
}
