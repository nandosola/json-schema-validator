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

public class AdditionalPropertiesValidatorTest {
	private final String schema1 = 
			"{" +
				"\"type\": \"object\"," +
				"\"properties\": {" +
					"\"p1\": {\"type\":\"string\"}, " +
					"\"p2\": {\"type\":\"number\"}" +
					"}," +
				"\"additionalProperties\" : false" +
			"}";
	
	private final String json1 = 
			"{" +
				"\"p1\": \"property 1\"," +
				"\"p2\": 12" +
			"}";
	
	private final String json2 = 
			"{" +
				"\"p1\": \"property 1\"," +
				"\"p2\": 12," +
				"\"p3\": \"should not be here\"" +
			"}";
	
	private final String schema2 = 
			"{" +
				"\"type\": \"object\"," +
				"\"properties\": {" +
					"\"p1\": {\"type\":\"string\"}, " +
					"\"p2\": {\"type\":\"number\"}" +
					"}," +
				"\"additionalProperties\": {" +
					"\"type\": \"string\"," +
					"\"pattern\": \"[A-Z]{3}\"" +
					"}" +
			"}";
	
	private final String json3 = 
			"{" +
				"\"p1\": \"property 1\"," +
				"\"p2\": 12," +
				"\"p3\": \"ABC\"" +
			"}";
	
	private final String json4 = 
			"{" +
				"\"p1\": \"property 1\"," +
				"\"p2\": 12," +
				"\"p3\": false" +
			"}";
	
	private ObjectMapper mapper;
	private AdditionalPropertiesValidator v1;
	private AdditionalPropertiesValidator v2;
	
	public AdditionalPropertiesValidatorTest() throws Exception {
		mapper = new ObjectMapper();
		JsonNode schemaNode1 = mapper.readTree(schema1);
		JsonNode ppNode1 = schemaNode1.get(PropertiesValidator.PROPERTY);
		JsonNode addPpNode1 = schemaNode1.get(AdditionalPropertiesValidator.PROPERTY);
		v1 = new AdditionalPropertiesValidator(ppNode1, addPpNode1);
		
		JsonNode schemaNode2 = mapper.readTree(schema2);
		JsonNode ppNode2 = schemaNode2.get(PropertiesValidator.PROPERTY);
		JsonNode addPpNode2 = schemaNode2.get(AdditionalPropertiesValidator.PROPERTY);
		v2 = new AdditionalPropertiesValidator(ppNode2, addPpNode2);
		
	}
	
	@Test
	public void testAdditionalNotAllowedSuccess() throws Exception{
		List<String> errors = new ArrayList<String>();
		
		JsonNode json1Node = mapper.readTree(json1);
		errors = v1.validate(json1Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void testAdditionalNotAllowedFailure() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode json2Node = mapper.readTree(json2);
		errors = v1.validate(json2Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$.p3: is not defined in the schema and the schema does not allow additional properties"));
	}
	
	@Test
	public void testAdditionalAllowedSuccess() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode json3Node = mapper.readTree(json3);
		errors = v2.validate(json3Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void testAdditionalAllowedFailure() throws Exception {
		List<String> errors = new ArrayList<String>();
		
		JsonNode json4Node = mapper.readTree(json4);
		errors = v2.validate(json4Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(2));
		assertThat(errors.get(0), is("$.p3: boolean found, string expected"));
		assertThat(errors.get(1), is("$.p3: cannot match a boolean against a regex pattern ([A-Z]{3})"));
		
	}
	
}
