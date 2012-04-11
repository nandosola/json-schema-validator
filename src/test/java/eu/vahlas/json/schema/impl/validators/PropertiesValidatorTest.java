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

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.vahlas.json.schema.impl.JSONValidator;

public class PropertiesValidatorTest {
	private final String schema = "{" +
		"\"type\" : \"object\"," +
		"\"properties\": {" +
			"\"p1\": {\"type\":\"string\"}, " +
			"\"p2\": {\"type\":\"number\", \"optional\": true}" +
			"}" +
		"}";
	private final String json1 = 
								"{" +
									"\"p1\":\"p1 with p2\"," +
									"\"p2\": 12" +
								"}";
	private final String json2 = 
								"{" +
									"\"p1\": \" p1 alone\"" +
								"}";
	private final String json3 = 
								"{" +
									"\"p2\": 12" +
								"}";
	private ObjectMapper mapper;
	private PropertiesValidator v;
	
	public PropertiesValidatorTest() throws Exception {
		mapper = new ObjectMapper();
		
		JsonNode schemaNode = mapper.readTree(schema);
		
		JsonNode ppNode = schemaNode.get(PropertiesValidator.PROPERTY);
		
		v = new PropertiesValidator(ppNode, mapper);
	}
	
	@Test
	public void testValidateSuccess() throws Exception{
		
		JsonNode json1Node = mapper.readTree(json1);
		
		List<String> errors =  v.validate(json1Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void testValidateSuccessOptional() throws Exception {
		JsonNode json2Node = mapper.readTree(json2);
		
		List<String> errors =  v.validate(json2Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void testValidateFailureOptional() throws Exception {
		JsonNode json3Node = mapper.readTree(json3);
		
		List<String> errors =  v.validate(json3Node, JSONValidator.AT_ROOT);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$.p1: is missing and it is not optional"));
	}
}
