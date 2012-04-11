/**
 * Copyright (C) 2010 Nicolas Vahlas <nico@vahlas.eu>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.vahlas.json.schema.impl.validators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.vahlas.json.schema.impl.JSONValidator;

public class UniqueItemsValidatorTest {
	public final String schema = 
		"{" +
			"\"type\": \"array\"," +
			"\"uniqueItems\": true" +
		"}";
	
	public final String json1 = "[1,2,3]";
	public final String json2 = "[1,2,1]";
	
	private ObjectMapper mapper;
	private UniqueItemsValidator v;
	
	public UniqueItemsValidatorTest() throws Exception {
		mapper = new ObjectMapper();
		v = new UniqueItemsValidator(mapper.readTree(schema).get(UniqueItemsValidator.PROPERTY));
	}
	
	@Test
	public void validateSuccess() throws Exception{
		List<String> errors = v.validate(mapper.readTree(json1), JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void validateFailure() throws Exception{
		List<String> errors = v.validate(mapper.readTree(json2), JSONValidator.AT_ROOT);
		
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$: the items in the array must be unique"));
	}
}
