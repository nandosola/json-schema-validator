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

import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;

public class FormatValidatorTest {
	private final String schema1 = "{\"type\":\"string\", \"format\":\"date-time\"}";
	private final String schema2 = "{\"type\":\"string\", \"format\":\"date\"}";
        private final String schema3 = "{\"type\":[\"string\",\"null\"], \"format\":\"date\"}" ;
        private final String schema4 = "{\"type\":\"string\", \"optional\":true, \"format\":\"date-time\"}";
        private final String schema5 = "{\"type\":[\"string\",\"null\"], \"optional\":true, \"format\":\"date\"}";
	private final String json1 =
								"{" +
									"\"p1\":\"2012-04-09T09:23:42Z\"," +
									"\"p2\": \"2012-04-09\", " +
                                                                        "\"p3\": null, " +
                                                                        "\"p4\": 42, " +
                                                                        "\"p5\": \"nonsense\" " +
								"}";
	private ObjectMapper mapper;
	private FormatValidator v1;
        private FormatValidator v2;
        private FormatValidator v3;
        private FormatValidator v4;
        private FormatValidator v5;
        private JsonNode jsonNode1;


	public FormatValidatorTest() throws Exception {
		mapper = new ObjectMapper();

		JsonNode schemaNode1 = mapper.readTree(schema1);
                JsonNode schemaNode2 = mapper.readTree(schema2);
                JsonNode schemaNode3 = mapper.readTree(schema3);
                JsonNode schemaNode4 = mapper.readTree(schema4);
                JsonNode schemaNode5 = mapper.readTree(schema5);

                jsonNode1 = mapper.readTree(json1);

                v1 = new FormatValidator(schemaNode1);
                v2 = new FormatValidator(schemaNode2);
                v3 = new FormatValidator(schemaNode3);
                v4 = new FormatValidator(schemaNode4);
                v5 = new FormatValidator(schemaNode5);
	}

	@Test
	public void testValidateSuccess() throws Exception{
                List<String> errors1 =  v1.validate(jsonNode1.get("p1"), "$.p1");
		assertThat(errors1.size(), is(0));
                List<String> errors2 =  v2.validate(jsonNode1.get("p2"), "$.p2");
		assertThat(errors2.size(), is(0));
                List<String> errors3 =  v3.validate(jsonNode1.get("p3"), "$.p3");
		assertThat(errors3.size(), is(0));
                List<String> errors4 =  v3.validate(jsonNode1.get("p2"), "$.p2");
		assertThat(errors4.size(), is(0));
                List<String> errors5 =  v4.validate(jsonNode1.get("p1"), "$.p1");
		assertThat(errors5.size(), is(0));
                List<String> errors6 =  v4.validate(jsonNode1.get("p42"), "$.42");
		assertThat(errors6.size(), is(0));
                List<String> errors7 =  v5.validate(jsonNode1.get("p2"), "$.p2");
		assertThat(errors7.size(), is(0));
                List<String> errors8 =  v5.validate(jsonNode1.get("p42"), "$.42");
		assertThat(errors8.size(), is(0));
	}


	@Test
	public void testValidateFailure() throws Exception {
                List<String> errors1 =  v1.validate(jsonNode1.get("p2"), "$.p2");
		assertThat(errors1.size(), is(1));
                assertThat(errors1.get(0), is("$.p2: incorrect format, date-time expected"));
                List<String> errors2 =  v1.validate(jsonNode1.get("p3"), "$.p3");
		assertThat(errors2.size(), is(1));
                assertThat(errors2.get(0), is("$.p3: incorrect format, date-time expected"));
                List<String> errors3 =  v1.validate(jsonNode1.get("p4"), "$.p4");
		assertThat(errors3.size(), is(1));
                assertThat(errors3.get(0), is("$.p4: incorrect format, date-time expected"));
                List<String> errors4 =  v1.validate(jsonNode1.get("p5"), "$.p5");
		assertThat(errors4.size(), is(1));
                assertThat(errors4.get(0), is("$.p5: incorrect format, date-time expected"));

                List<String> errors5 =  v2.validate(jsonNode1.get("p1"), "$.p1");
		assertThat(errors5.size(), is(1));
                assertThat(errors5.get(0), is("$.p1: incorrect format, date expected"));
                List<String> errors6 =  v2.validate(jsonNode1.get("p3"), "$.p3");
		assertThat(errors6.size(), is(1));
                assertThat(errors6.get(0), is("$.p3: incorrect format, date expected"));
                List<String> errors7 =  v2.validate(jsonNode1.get("p4"), "$.p4");
		assertThat(errors7.size(), is(1));
                assertThat(errors7.get(0), is("$.p4: incorrect format, date expected"));
                List<String> errors8 =  v2.validate(jsonNode1.get("p5"), "$.p5");
		assertThat(errors8.size(), is(1));
                assertThat(errors8.get(0), is("$.p5: incorrect format, date expected"));

                List<String> errors9 =  v3.validate(jsonNode1.get("p1"), "$.p1");
		assertThat(errors9.size(), is(1));
                assertThat(errors9.get(0), is("$.p1: incorrect format, date expected"));
                List<String> errors11 =  v3.validate(jsonNode1.get("p4"), "$.p4");
		assertThat(errors11.size(), is(1));
                assertThat(errors11.get(0), is("$.p4: incorrect format, date expected"));
                List<String> errors12 =  v3.validate(jsonNode1.get("p5"), "$.p5");
		assertThat(errors12.size(), is(1));
                assertThat(errors12.get(0), is("$.p5: incorrect format, date expected"));

                List<String> errors13 =  v4.validate(jsonNode1.get("p2"), "$.p2");
		assertThat(errors13.size(), is(1));
                assertThat(errors13.get(0), is("$.p2: incorrect format, date-time expected"));
                List<String> errors14 =  v4.validate(jsonNode1.get("p3"), "$.p3");
		assertThat(errors14.size(), is(1));
                assertThat(errors14.get(0), is("$.p3: incorrect format, date-time expected"));
                List<String> errors15 =  v4.validate(jsonNode1.get("p4"), "$.p4");
		assertThat(errors15.size(), is(1));
                assertThat(errors15.get(0), is("$.p4: incorrect format, date-time expected"));
                List<String> errors16 =  v4.validate(jsonNode1.get("p5"), "$.p5");
		assertThat(errors16.size(), is(1));
                assertThat(errors16.get(0), is("$.p5: incorrect format, date-time expected"));

                List<String> errors17 =  v5.validate(jsonNode1.get("p1"), "$.p1");
		assertThat(errors17.size(), is(1));
                assertThat(errors17.get(0), is("$.p1: incorrect format, date expected"));
                List<String> errors18 =  v5.validate(jsonNode1.get("p4"), "$.p4");
		assertThat(errors18.size(), is(1));
                assertThat(errors18.get(0), is("$.p4: incorrect format, date expected"));
                List<String> errors19 =  v5.validate(jsonNode1.get("p5"), "$.p5");
		assertThat(errors19.size(), is(1));
                assertThat(errors19.get(0), is("$.p5: incorrect format, date expected"));
	}
}
