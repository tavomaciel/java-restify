/*******************************************************************************
 *
 * MIT License
 *
 * Copyright (c) 2016 Tiago de Freitas Lima
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *******************************************************************************/
package com.github.ljtfreitas.restify.http.netflix.client.call.exec;

import java.util.Optional;

import com.github.ljtfreitas.restify.http.client.call.EndpointCall;
import com.github.ljtfreitas.restify.http.client.call.exec.EndpointCallExecutable;
import com.github.ljtfreitas.restify.http.contract.metadata.EndpointMethod;
import com.github.ljtfreitas.restify.http.contract.metadata.reflection.JavaType;
import com.netflix.hystrix.HystrixCommand;

class HystrixCommandEndpointCallExecutable<T, R> implements EndpointCallExecutable<HystrixCommand<T>, R> {

	private final HystrixCommand.Setter hystrixMetadata;
	private final EndpointMethod endpointMethod;
	private final EndpointCallExecutable<T, R> delegate;
	private final Object fallback;

	public HystrixCommandEndpointCallExecutable(HystrixCommand.Setter hystrixMetadata, EndpointMethod endpointMethod,
			EndpointCallExecutable<T, R> delegate) {

		this(hystrixMetadata, endpointMethod, delegate, null);
	}

	public HystrixCommandEndpointCallExecutable(HystrixCommand.Setter hystrixMetadata, EndpointMethod endpointMethod,
			EndpointCallExecutable<T, R> delegate, Object fallback) {

		this.hystrixMetadata = hystrixMetadata;
		this.endpointMethod = endpointMethod;
		this.delegate = delegate;
		this.fallback = fallback;
	}

	@Override
	public JavaType returnType() {
		return delegate.returnType();
	}

	@Override
	public HystrixCommand<T> execute(EndpointCall<R> call, Object[] args) {
		return new HystrixCommand<T>(hystrixMetadata()) {
			@Override
			protected T run() throws Exception {
				return delegate.execute(call, args);
			}

			protected T getFallback() {
				return fallback == null ? super.getFallback() : doFallback();
			}

			private T doFallback() {
				HystrixCommandFallback hystrixCommandFallback = Optional.ofNullable(fallback)
						.map(f -> new HystrixCommandFallback(endpointMethod, args, fallback))
							.orElse(null);

				HystrixCommand<T> value = hystrixCommandFallback.run();
				return value.execute();
			};
		};
	}

	private HystrixCommand.Setter hystrixMetadata() {
		return Optional.ofNullable(hystrixMetadata)
				.orElseGet(() -> buildHystrixMetadata());
	}

	private HystrixCommand.Setter buildHystrixMetadata() {
		return new HystrixCommandMetadataFactory(endpointMethod).create();
	}
}
