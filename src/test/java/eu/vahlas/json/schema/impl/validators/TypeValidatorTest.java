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

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.vahlas.json.schema.impl.JSONValidator;

public class TypeValidatorTest {

	private final String json1 = "\"Test 1\"";
	private final String json2 = "false";
	private final String json3 = "null";
	private final String schema1 = "{\"type\" : \"boolean\"}";
	private final String schema2 = "{\"type\": [ {\"type\": \"boolean\"}, \"number\" ]}";
	private final String schema3 = "{\"type\": \"any\"}";
	private final String schema4 = "{\"type\": \"null\"}";
        private final String schema5 = "{\"type\": [ \"boolean\", \"number\" ]}";
        private final String schema6 = "{\"type\": [ {\"type\": \"boolean\"}, {\"type\": \"number\"} ]}";

	private ObjectMapper mapper;

	private JsonNode jsonNode1;
	private JsonNode jsonNode2;
	private JsonNode jsonNode3;

	public TypeValidatorTest() throws IOException {
		mapper = new ObjectMapper();
		jsonNode1 = mapper.readTree(json1);
		jsonNode2 = mapper.readTree(json2);
		jsonNode3 = mapper.readTree(json3);
	}

	@Test
	public void testType() throws IOException {
		JsonNode schemaNode = mapper.readTree(schema1);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode1, "$");

		assertThat(errors.get(0), is("$: string found, boolean expected"));

		errors = v.validate(jsonNode2, "$");
		assertThat(errors.size(), is(0));
	}

	@Test
	public void testAnyType() throws Exception {
		JsonNode schemaNode = mapper.readTree(schema3);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode1, "$");

		assertThat(errors.size(), is(0));
	}

	@Test
	public void testNullType() throws Exception {
		JsonNode schemaNode = mapper.readTree(schema4);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode3, "$");

		assertThat(errors.size(), is(0));
	}

	@Test
	public void testNumberType() throws Exception {
		// TODO: check that number type includes integer type as described in the spec
		assertThat(0, is(0));
	}

	@Test
	public void testUnionTypeUnbalancedNestedTypeAttrs() throws IOException {
		JsonNode schemaNode = mapper.readTree(schema2);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode1, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$: string found, but [boolean, number] is required"));
	}
        @Test
	public void testUnionTypeNoNestedTypeAttrs() throws IOException {
		JsonNode schemaNode = mapper.readTree(schema5);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode1, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$: string found, but [boolean, number] is required"));
	}
        @Test
	public void testUnionTypeBalancedNestedTypeAttrs() throws IOException {
		JsonNode schemaNode = mapper.readTree(schema6);

		TypeValidator v = new TypeValidator(schemaNode);
		List<String> errors = v.validate(jsonNode1, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$: string found, but [boolean, number] is required"));
	}
}
