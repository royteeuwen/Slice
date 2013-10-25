package com.cognifide.slice.core.internal.provider;

/*
 * #%L
 * Slice - Core
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2012 Cognifide Limited
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cognifide.slice.api.context.ContextProvider;
import com.cognifide.slice.api.context.ContextScope;
import com.cognifide.slice.api.execution.ExecutionContextStack;
import com.cognifide.slice.api.provider.ClassToKeyMapper;
import com.cognifide.slice.api.provider.ModelProvider;
import com.cognifide.slice.core.internal.execution.ExecutionContextImpl;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * This class creates object or list of objects of given injectable type using Guice injector.
 * 
 * @author Witold Szczerba
 * @author Rafał Malinowski
 */
public class SliceModelProvider implements ModelProvider {

	private static final Logger LOG = LoggerFactory.getLogger(SliceModelProvider.class);

	private final Injector injector;

	private final ContextScope contextScope;

	private final ContextProvider contextProvider;

	private final ClassToKeyMapper classToKeyMapper;

	private final ExecutionContextStack currentExecutionContext;

	/**
	 * {@inheritDoc}
	 */
	@Inject
	public SliceModelProvider(final Injector injector, final ContextScope contextScope,
			final ClassToKeyMapper classToKeyMapper, final ExecutionContextStack currentExecutionContext) {
		this.injector = injector;
		this.contextScope = contextScope;
		this.contextProvider = contextScope.getContextProvider();
		this.classToKeyMapper = classToKeyMapper;
		this.currentExecutionContext = currentExecutionContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T> T get(final Class<T> type, final String path) {
		/*
		 * This method is not synchronized on purpose, the only case where concurrency issues may occur is
		 * when the contentPathContext variable is used from different threads, and since the
		 * CurrentPathContext is request scoped it would mean multiple threads in same request which then is
		 * against servlet specification.
		 */
		ExecutionContextImpl executionItem = new ExecutionContextImpl(path);
		LOG.debug("creating new instance of " + type.getName() + " from " + path);
		return get(type, executionItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T get(Class<T> type, Resource resource) {
		ExecutionContextImpl executionItem = new ExecutionContextImpl(resource);
		LOG.debug("creating new instance of " + type.getName() + " from resource: " + resource);
		return get(type, executionItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(String className, String path) throws ClassNotFoundException {
		final Key<?> key = classToKeyMapper.getKey(className);
		if (null == key) {
			throw new ClassNotFoundException("key for class " + className + " not found");
		}
		ExecutionContextImpl executionItem = new ExecutionContextImpl(path);
		LOG.debug("creating new instance for " + key.toString() + " from " + path);
		return get(key, executionItem);
	}

	@SuppressWarnings("unchecked")
	private <T> T get(Class<T> type, ExecutionContextImpl executionItem) {
		return (T) get(Key.get(type), executionItem);
	}

	private Object get(Key<?> key, ExecutionContextImpl executionItem) {
		final ContextProvider oldContextProvider = contextScope.getContextProvider();
		contextScope.setContextProvider(contextProvider);

		if ((executionItem.getResource() == null) && (executionItem.getPath() != null)) {
			executionItem.setPath(currentExecutionContext.getAbsolutePath(executionItem.getPath()));
		}

		currentExecutionContext.push(executionItem);
		try {
			return injector.getInstance(key);
		} finally {
			currentExecutionContext.pop();
			contextScope.setContextProvider(oldContextProvider);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T> List<T> getList(final Class<T> type, final Iterator<String> paths) {
		final ArrayList<T> result = new ArrayList<T>();

		if (null == paths) {
			return result;
		}

		while (paths.hasNext()) {
			final String path = paths.next();
			final T model = get(type, path);
			result.add(model);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <T> List<T> getList(final Class<T> type, final String[] paths) {
		if (null == paths) {
			return new ArrayList<T>();
		}
		return getList(type, Arrays.asList(paths).iterator());
	}

}
