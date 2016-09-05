package com.restify.http.client.converter.form.multipart;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.junit.Before;
import org.junit.Test;

import com.restify.http.client.converter.SimpleHttpRequestMessage;
import com.restify.http.contract.MultipartFile;
import com.restify.http.contract.MultipartFile.ContentType;

public class MultipartFormFileObjectMessageConverterTest {

	private MultipartFormFileObjectMessageConverter converter;

	private MultipartFile multipartFile;

	private SimpleHttpRequestMessage httpRequestMessage;
	private ByteArrayOutputStream output;

	private File file;

	@Before
	public void setup() throws IOException {
		converter = new MultipartFormFileObjectMessageConverter(new SimpleMultipartFormBoundaryGenerator("myBoundary"));

		file = File.createTempFile("myTextFile", ".txt");

		OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(file));
		fileWriter.write("file content...line 1");
		fileWriter.write("\n");
		fileWriter.write("file content...line 2");
		fileWriter.flush();
		fileWriter.close();

		output = new ByteArrayOutputStream();
		httpRequestMessage = new SimpleHttpRequestMessage(output);
	}

	@Test
	public void shouldSerializeMultipartFileObjectAsFormMultipartData() throws IOException {
		multipartFile = MultipartFile.create("myFile", file);

		String body = "------myBoundary"
				+ "\r\n"
				+ "Content-Disposition: form-data; name=\"myFile\"; filename=\"" + file.getName() + "\""
				+ "\r\n"
				+ "Content-Type: text/plain"
				+ "\r\n"
				+ "\r\n"
				+ "file content...line 1"
				+ "\n"
				+ "file content...line 2"
				+ "\r\n"
				+ "\r\n"
				+ "------myBoundary--";

		converter.write(multipartFile, httpRequestMessage);

		assertEquals(body, output.toString());
	}

	@Test
	public void shouldSerializeMultipartFileObjectWithPathAsFormMultipartData() throws IOException {
		multipartFile = MultipartFile.create("myFileAsPath", file.toPath());

		String body = "------myBoundary"
				+ "\r\n"
				+ "Content-Disposition: form-data; name=\"myFileAsPath\"; filename=\"" + file.getName() + "\""
				+ "\r\n"
				+ "Content-Type: text/plain"
				+ "\r\n"
				+ "\r\n"
				+ "file content...line 1"
				+ "\n"
				+ "file content...line 2"
				+ "\r\n"
				+ "\r\n"
				+ "------myBoundary--";

		converter.write(multipartFile, httpRequestMessage);

		assertEquals(body, output.toString());
	}

	@Test
	public void shouldSerializeMultipartFileObjectWithInputStreamAsFormMultipartData() throws IOException {
		multipartFile = MultipartFile.create("myFileAsInputStream", "whateverFileName", ContentType.of("text/plain"),
				new FileInputStream(file));

		String body = "------myBoundary"
				+ "\r\n"
				+ "Content-Disposition: form-data; name=\"myFileAsInputStream\"; filename=\"whateverFileName\""
				+ "\r\n"
				+ "Content-Type: text/plain"
				+ "\r\n"
				+ "\r\n"
				+ "file content...line 1"
				+ "\n"
				+ "file content...line 2"
				+ "\r\n"
				+ "\r\n"
				+ "------myBoundary--";

		converter.write(multipartFile, httpRequestMessage);

		assertEquals(body, output.toString());
	}
}
