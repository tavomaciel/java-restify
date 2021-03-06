package com.github.ljtfreitas.restify.http.client.message.converter.octet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.github.ljtfreitas.restify.http.client.request.HttpRequestMessage;
import com.github.ljtfreitas.restify.http.client.request.SimpleHttpRequestMessage;
import com.github.ljtfreitas.restify.http.client.response.HttpResponseMessage;
import com.github.ljtfreitas.restify.http.client.response.SimpleHttpResponseMessage;

public class OctetInputStreamMessageConverterTest {

	private OctetInputStreamMessageConverter converter;

	@Before
	public void setup() {
		converter = new OctetInputStreamMessageConverter();
	}

	@Test
	public void shouldCanReadWhenTypeIsInputStream() {
		assertTrue(converter.canRead(InputStream.class));
	}

	@Test
	public void shouldNotCanReadWhenTypeNotIsInputStream() {
		assertFalse(converter.canRead(String.class));
	}

	@Test
	public void shouldReadHttpResponseToInputStream() {
		String body = "response";

		HttpResponseMessage httpResponseMessage = new SimpleHttpResponseMessage(new ByteArrayInputStream(body.getBytes()));

		InputStream stream = converter.read(httpResponseMessage, InputStream.class);

		BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));

		String output = buffer.lines().collect(Collectors.joining("\n"));

		assertEquals(body, output);
	}

	@Test
	public void shouldCanWriteWhenTypeIsInputStream() {
		assertTrue(converter.canWrite(InputStream.class));
	}

	@Test
	public void shouldNotCanWriteWhenTypeNotIsInputStream() {
		assertFalse(converter.canWrite(String.class));
	}

	@Test
	public void shouldWriteInputStreamBodyToOutputStream() {
		String body = "request body";

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		HttpRequestMessage httpRequestMessage = new SimpleHttpRequestMessage(outputStream);

		converter.write(new ByteArrayInputStream(body.getBytes()), httpRequestMessage);

		String output = new String(outputStream.toByteArray());

		assertEquals(body, output);
	}
}
