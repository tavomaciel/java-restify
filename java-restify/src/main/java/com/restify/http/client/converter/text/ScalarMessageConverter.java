package com.restify.http.client.converter.text;

import static com.restify.http.metadata.Preconditions.isTrue;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.restify.http.client.HttpRequestMessage;
import com.restify.http.client.HttpResponseMessage;
import com.restify.http.client.RestifyHttpMessageReadException;
import com.restify.http.client.RestifyHttpMessageWriteException;
import com.restify.http.client.converter.HttpMessageReader;
import com.restify.http.client.converter.HttpMessageWriter;

public class ScalarMessageConverter implements HttpMessageReader<Object>, HttpMessageWriter<Object> {

	private static final String TEXT_PLAIN = "text/plain";

	private static final Set<Type> SCALAR_TYPES = new HashSet<>();

	private final TextPlainMessageConverter textPlainMessageConverter = new TextPlainMessageConverter();

	static {
		SCALAR_TYPES.add(byte.class);
		SCALAR_TYPES.add(short.class);
		SCALAR_TYPES.add(int.class);
		SCALAR_TYPES.add(long.class);
		SCALAR_TYPES.add(float.class);
		SCALAR_TYPES.add(double.class);
		SCALAR_TYPES.add(boolean.class);
		SCALAR_TYPES.add(char.class);
		SCALAR_TYPES.add(Byte.class);
		SCALAR_TYPES.add(Short.class);
		SCALAR_TYPES.add(Integer.class);
		SCALAR_TYPES.add(Long.class);
		SCALAR_TYPES.add(Float.class);
		SCALAR_TYPES.add(Double.class);
		SCALAR_TYPES.add(Boolean.class);
		SCALAR_TYPES.add(Character.class);
	}

	@Override
	public String contentType() {
		return TEXT_PLAIN;
	}

	@Override
	public boolean canRead(Type type) {
		return SCALAR_TYPES.contains(type);
	}

	@Override
	public Object read(HttpResponseMessage httpResponseMessage, Type expectedType)
			throws RestifyHttpMessageReadException {

		String responseAsString = textPlainMessageConverter.read(httpResponseMessage, String.class);

		return ScalarType.of(expectedType).convert(responseAsString);
	}

	@Override
	public boolean canWrite(Class<?> type) {
		return SCALAR_TYPES.contains(type);
	}

	@Override
	public void write(Object body, HttpRequestMessage httpRequestMessage) throws RestifyHttpMessageWriteException {
		String bodyAsString = body.toString();

		try {
			httpRequestMessage.output().write(bodyAsString.getBytes(httpRequestMessage.charset()));
			httpRequestMessage.output().flush();
			httpRequestMessage.output().close();

		} catch (IOException e) {
			throw new RestifyHttpMessageWriteException(e);
		}
	}

	private enum ScalarType {
		BYTE(byte.class, Byte.class) {
			@Override
			public Object convert(String source) {
				return Byte.valueOf(source);
			}
		},
		SHORT(short.class, Short.class) {
			@Override
			public Object convert(String source) {
				return Short.valueOf(source);
			}
		},
		INTEGER(int.class, Integer.class) {
			@Override
			public Object convert(String source) {
				return Integer.valueOf(source);
			}
		},
		LONG(long.class, Long.class) {
			@Override
			public Object convert(String source) {
				return Long.valueOf(source);
			}
		},
		FLOAT(float.class, Float.class) {
			@Override
			public Object convert(String source) {
				return Float.valueOf(source);
			}
		},
		DOUBLE(double.class, Double.class) {
			@Override
			public Object convert(String source) {
				return Double.valueOf(source);
			}
		},
		BOOLEAN(boolean.class, Boolean.class) {
			@Override
			public Object convert(String source) {
				return Boolean.valueOf(source);
			}
		},
		CHARACTER(char.class, Character.class) {
			@Override
			public Object convert(String source) {
				isTrue(source.length() == 1, "Expected body response of length 1 for Character conversion, "
						+ "but the length is " + source.length() + ".");

				return source.charAt(0);
			}
		};

		private final Set<Type> types;

		private ScalarType(Type primitiveType, Type wrapperType) {
			this.types = new HashSet<>(Arrays.asList(primitiveType, wrapperType));
		}

		public abstract Object convert(String source);

		public static ScalarType of(Type type) {
			return Arrays.stream(ScalarType.values())
				.filter(s -> s.types.contains(type))
					.findFirst()
						.orElseThrow(() -> new IllegalArgumentException("Unsupported type [" + type + "]. "
								+ "Is a primitive type?"));
		}
	}
}
