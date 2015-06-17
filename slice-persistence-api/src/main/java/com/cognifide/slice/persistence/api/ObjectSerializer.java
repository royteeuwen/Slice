/*-
 * #%L
 * Slice - Persistence API
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
package com.cognifide.slice.persistence.api;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;

import aQute.bnd.annotation.ConsumerType;

/**
 * Serializes an object into resource. Unlike the {@link FieldSerializer} it doesn't have access to the field
 * information.
 * 
 * @author Tomasz Rękawek
 *
 */
@ConsumerType
public interface ObjectSerializer extends Serializer {

	/**
	 * Serializer will return {@code true} if it's able to handle such class
	 * 
	 * @param objectClass
	 * @return
	 */
	boolean accepts(Class<?> objectClass);

	/**
	 * Serializes an object into repository
	 * 
	 * @param propertyName Name of the property
	 * @param fieldValue Value to serialize
	 * @param parent Resource to save the field
	 * @param ctx Serialization context
	 * @throws PersistenceException
	 */
	void serialize(String propertyName, Object object, Resource parent, SerializerContext ctx)
			throws PersistenceException;

}
