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

package eu.vahlas.json.schema.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class JacksonSchemaTest {
	private final String schema = 
			"{" +
				"\"title\": \"this is a test title ...\"," +
				"\"description\": \"this is a test description ... \"," +
				"\"type\": \"string\"," +
				"\"enum\": [\"one\", \"two\", \"three\"]," +
				"\"optional\": false" +
			"}";
	
	private final String schema2 = 
			"{" +
				"\"type\": \"object\"," +
				"\"properties\": {" +
					"\"p1\": {\"type\": \"string\", \"pattern\": \"[A-Z]{2}-[0-9]{5}\"}," +
					"\"p2\": {\"type\": \"integer\", \"enum\":[1,2,3,4,5]}," +
					"\"p3\": {" +
						"\"type\": \"array\"," +
						"\"items\" : {\"type\": \"string\", \"minLength\": 5, \"maxLength\": 10}," +
						"\"minItems\": 1," +
						"\"maxItems\": 2," +
						"\"uniqueItems\": false" +
					"}," +
					"\"p4\": {\"type\": \"boolean\", \"requires\": \"p2\", \"optional\": true}," +
					"\"p5\": {\"type\": \"number\", \"minimum\": 0, \"minimumCanEqual\": false, \"maximum\": 20.0}" +
				"}," +
				"\"additionalProperties\": false" +
			"}";
	
	private final String json1 = "\"two\"";
	private final String json2 = "\"five\"";
	private final String json3 = 
			"{" +
				"\"p1\": \"GR-17124\"," +
				"\"p2\": 4," +
				"\"p3\": [\"Megalou\", \"Spilaioy\", \"a\"]," +
				"\"p5\": 19" +
			"}";
	
	private ObjectMapper mapper;
	private JacksonSchema jschema;
	private JacksonSchema jschema2;
	
	public JacksonSchemaTest() throws Exception {
		mapper = new ObjectMapper();
		
		JsonNode schemaNode = mapper.readTree(schema);
		jschema = new JacksonSchema(mapper, schemaNode);
		
		JsonNode schemaNode2 = mapper.readTree(schema2);
		jschema2 = new JacksonSchema(mapper, schemaNode2);
	}
	
	@Test
	public void testNew() throws Exception {
		JsonNode schemaNode = mapper.readTree(schema);
		JacksonSchema jschema = new JacksonSchema(mapper, schemaNode);
		List<JSONValidator> validators = jschema.validators;
		assertThat(validators.get(1).getClass().getName(), is("eu.vahlas.json.schema.impl.validators.NoOpValidator") );
		assertThat(validators.get(2).getClass().getName(), is("eu.vahlas.json.schema.impl.validators.TypeValidator") );
		assertThat(validators.get(3).getClass().getName(), is("eu.vahlas.json.schema.impl.validators.EnumValidator") );
	}
	
	@Test
	public void testValidateSimpleSuccess() throws Exception {
		List<String> errors = jschema.validate(json1);
		assertThat(errors.size(), is(0));
	}
	
	@Test
	public void testValidateSimpleFailure() throws Exception {
		List<String> errors = jschema.validate(json2);
		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), is("$: does not have a value in the enumeration [one, two, three]"));
	}
	
	@Test
	public void testValidateComplexSuccess() throws Exception {
		List<String> errors = jschema2.validate(json3);
		assertThat(errors.size(), is(2));
		assertThat(errors.get(0), is("$.p3[2]: must be at least 5 characters long"));
		assertThat(errors.get(1), is("$.p3: there must be a maximum of 2 items in the array"));
	}
	
}
