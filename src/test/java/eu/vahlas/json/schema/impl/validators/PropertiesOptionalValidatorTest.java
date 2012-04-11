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

public class PropertiesOptionalValidatorTest {
	private final String schema1 = "{" +
		"\"type\" : \"object\"," +
		"\"properties\": {" +
			"\"id\": {\"type\":\"integer\"}," +
			"\"_id\": {\"type\":\"string\"}," +
                        "\"foo_id\": {\"type\":\"integer\", \"optional\": true}," +
                        "\"barid\": {\"type\":\"string\", \"optional\": true}," +
                        "\"baz_id\": {\"type\":[\"integer\",\"null\"]}" +
			"}" +
		"}";

       private final String schema2 = "{" +
		"\"type\" : \"object\"," +
		"\"properties\": {" +
			"\"id\": {\"type\":\"integer\", \"optional\": false}, " +
			"\"_id\": {\"type\":\"string\", \"optional\": false}, " +
                        "\"foo_id\": {\"type\":\"integer\", \"optional\": true}, " +
                        "\"barid\": {\"type\":\"string\", \"optional\": true}, " +
                        "\"baz_id\": {\"type\":[\"integer\",\"null\"], \"optional\": false}" +
			"}" +
		"}";

       private final String schema3 = "{" +
		"\"type\" : \"object\"," +
		"\"properties\": {" +
			"\"id\": {\"type\":\"integer\", \"optional\": true}," +
			"\"_id\": {\"type\":\"string\", \"optional\": true}," +
                        "\"foo_id\": {\"type\":\"integer\", \"optional\": true}," +
                        "\"barid\": {\"type\":\"string\", \"optional\": false}," +
                        "\"baz_id\": {\"type\":[\"integer\",\"null\"], \"optional\": false}" +
			"}" +
		"}";




	private final String json1 =
								"{" +
									"\"id\": 42," +
									"\"_id\": \"deadc00ff33b4b3\"," +
                                                                        "\"baz_id\": 1337" +
								"}";
	private final String json2 =
								"{" +
                                                                        "\"baz_id\": null," +
                                                                        "\"barid\": \"pineapple\"" +
								"}";
        private final String json3 =
								"{" +
                                                                        "\"baz_id\": 42," +
                                                                        "\"foo_id\": 123" +
								"}";
        private final String json4 =
								"{" +
									"\"id\": 42," +
									"\"_id\": \"deadc00ff33b4b3\"," +
                                                                        "\"baz_id\": 1337," +
                                                                        "\"barid\": \"goodbye\"" +
								"}";

	private ObjectMapper mapper;
	private PropertiesValidator v1;
       	private PropertiesValidator v2;
        private PropertiesValidator v3;

	public PropertiesOptionalValidatorTest() throws Exception {
		mapper = new ObjectMapper();

		JsonNode schemaNode1 = mapper.readTree(schema1);
		JsonNode ppNode1 = schemaNode1.get(PropertiesValidator.PROPERTY);
		v1 = new PropertiesValidator(ppNode1, mapper);

                JsonNode schemaNode2 = mapper.readTree(schema2);
		JsonNode ppNode2 = schemaNode2.get(PropertiesValidator.PROPERTY);
		v2 = new PropertiesValidator(ppNode2, mapper);

                JsonNode schemaNode3 = mapper.readTree(schema3);
		JsonNode ppNode3 = schemaNode3.get(PropertiesValidator.PROPERTY);
		v3 = new PropertiesValidator(ppNode3, mapper);

	}

	@Test
	public void testValidateSuccess() throws Exception{

		JsonNode json1Node = mapper.readTree(json1);
                JsonNode json2Node = mapper.readTree(json2);
                JsonNode json4Node = mapper.readTree(json4);

		List<String> errors_1 =  v1.validate(json1Node, JSONValidator.AT_ROOT);
		assertThat(errors_1.size(), is(0));
                List<String> errors_2 =  v1.validate(json4Node, JSONValidator.AT_ROOT);
		assertThat(errors_2.size(), is(0));

                List<String> errors_3 =  v2.validate(json1Node, JSONValidator.AT_ROOT);
		assertThat(errors_3.size(), is(0));
                List<String> errors_4 =  v2.validate(json4Node, JSONValidator.AT_ROOT);
		assertThat(errors_4.size(), is(0));

                List<String> errors_5 =  v3.validate(json2Node, JSONValidator.AT_ROOT);
		assertThat(errors_5.size(), is(0));
                List<String> errors_6 =  v3.validate(json4Node, JSONValidator.AT_ROOT);
		assertThat(errors_6.size(), is(0));

	}

	@Test
	public void testValidateFailure() throws Exception {
		JsonNode json1Node = mapper.readTree(json1);
                JsonNode json2Node = mapper.readTree(json2);
                JsonNode json3Node = mapper.readTree(json3);
                JsonNode json4Node = mapper.readTree(json4);

		List<String> errors_1 =  v1.validate(json2Node, JSONValidator.AT_ROOT);
		assertThat(errors_1.size(), is(2));
		assertThat(errors_1.get(0), is("$.id: is missing and it is not optional"));
                assertThat(errors_1.get(1), is("$._id: is missing and it is not optional"));

                List<String> errors_2 =  v1.validate(json3Node, JSONValidator.AT_ROOT);
		assertThat(errors_2.size(), is(2));
		assertThat(errors_2.get(0), is("$.id: is missing and it is not optional"));
                assertThat(errors_2.get(1), is("$._id: is missing and it is not optional"));

                List<String> errors_3 =  v2.validate(json2Node, JSONValidator.AT_ROOT);
		assertThat(errors_3.size(), is(2));
		assertThat(errors_3.get(0), is("$.id: is missing and it is not optional"));
                assertThat(errors_3.get(1), is("$._id: is missing and it is not optional"));

                List<String> errors_4 =  v2.validate(json3Node, JSONValidator.AT_ROOT);
		assertThat(errors_4.size(), is(2));
		assertThat(errors_4.get(0), is("$.id: is missing and it is not optional"));
                assertThat(errors_4.get(1), is("$._id: is missing and it is not optional"));

                List<String> errors_5 =  v3.validate(json1Node, JSONValidator.AT_ROOT);
		assertThat(errors_5.size(), is(1));
		assertThat(errors_5.get(0), is("$.barid: is missing and it is not optional"));

                List<String> errors_6 =  v3.validate(json3Node, JSONValidator.AT_ROOT);
		assertThat(errors_6.size(), is(1));
		assertThat(errors_6.get(0), is("$.barid: is missing and it is not optional"));
	}
}
