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

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.vahlas.json.schema.impl.JSONValidator;


public class ItemsValidatorTest {
	private final String schema1 = 
				"{" +
					"\"type\": \"array\"," +
					"\"items\": {\"type\": \"string\"}" +
				"}";
	
	private final String schema2 = 
				"{" +
					"\"type\": \"array\"," +
					"\"items\": [{\"type\": \"string\"}, {\"type\": \"integer\"}]" +
				"}";
	
	private final String json1 = 
				"[\"one\", \"two\", \"three\"]";
	
	private final String json2 = 
				"[\"one\", true, \"three\", 4]";
	
	private final String json3 = 
				"[\"one\", 2]";
	
	private final String json4 = 
				"[\"one\", \"two\"]";
	
	private ObjectMapper mapper;
	private ItemsValidator v1;
	private ItemsValidator v2;
	
	public ItemsValidatorTest() throws Exception {
		mapper = new ObjectMapper();
		JsonNode schemaNode1 = mapper.readTree(schema1);
		JsonNode schemaNode2 = mapper.readTree(schema2);
		v1 = new ItemsValidator(schemaNode1.get(ItemsValidator.PROPERTY));
		v2 = new ItemsValidator(schemaNode2.get(ItemsValidator.PROPERTY));
	}
	
	@Test
	public void testSimpleSchemaSuccess() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode jsonNode1 = mapper.readTree(json1);
		errors = v1.validate(jsonNode1, JSONValidator.AT_ROOT);
		assertThat("Test 1", errors.size(), is(0));
	}
	
	@Test
	public void testSimpleSchemaFailure() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode jsonNode2 = mapper.readTree(json2);
		errors = v1.validate(jsonNode2, JSONValidator.AT_ROOT);
		assertThat("Test 2", errors.size(), is(2));
		assertThat(errors.get(0), is("$[1]: boolean found, string expected"));
		assertThat(errors.get(1), is("$[3]: integer found, string expected"));
	}
	
	@Test
	public void testTupleSchemaSuccess() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode jsonNode3 = mapper.readTree(json3);
		errors = v2.validate(jsonNode3, JSONValidator.AT_ROOT);
		assertThat("Test 3", errors.size(), is(0));
	}
	
	@Test
	public void testTupleSchemaFailure() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode jsonNode4 = mapper.readTree(json4);
		errors = v2.validate(jsonNode4, JSONValidator.AT_ROOT);
		assertThat("Test 4", errors.size(), is(1));
		assertThat(errors.get(0), is("$[1]: string found, integer expected"));
	}
	
	@Test
	public void testTupleSchemaOutOfBounds() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode jsonNode2 = mapper.readTree(json2);
		errors = v2.validate(jsonNode2, JSONValidator.AT_ROOT);
		assertThat("Test 5", errors.size(), is(3));
		assertThat(errors.get(0), is("$[1]: boolean found, integer expected"));
		assertThat(errors.get(1), is("$[2]: no validator found at this index"));
		assertThat(errors.get(2), is("$[3]: no validator found at this index"));
	}
	
}
