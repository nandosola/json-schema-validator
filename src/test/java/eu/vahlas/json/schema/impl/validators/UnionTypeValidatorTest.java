/**
 * Copyright (C) 2010 Nicolas Vahlas <nico@vahlas.eu>
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package eu.vahlas.json.schema.impl.validators;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

public class UnionTypeValidatorTest {

    private final String json = "\"test\"";  // a string
    private final String json2 = "42"; //an integer

    private final String schema1 =
                         "{\"type\": [" + "\"integer\", " + "\"boolean\"" + "]" + "}";
    private final String schema2 =
                         "{\"type\" : [" + "{\"type\": \"integer\"}," + "{\"type\": \"boolean\"}" + "]" + "}";
    private final String schema3 =
                         "{\"type\" : [" + "{\"type\": \"integer\"}," + "\"boolean\"" + "]" + "}";
    private final String schema4 =
                         "{\"type\": [" + "\"string\", " + "\"null\"" + "]" + "}";
    private ObjectMapper mapper;
    private JsonNode jsonNode;
    private JsonNode jsonNodeInt;

    public UnionTypeValidatorTest() throws IOException {
        mapper = new ObjectMapper();
        jsonNode = mapper.readTree(json);
        jsonNodeInt = mapper.readTree(json2);
    }

    @Test
    public void testUnionTypeWithStrings() throws IOException {
        JsonNode schemaNode = mapper.readTree(schema1);

        UnionTypeValidator v = new UnionTypeValidator(schemaNode.get("type"));
        List<String> errors = v.validate(jsonNode, "$");

        assertThat(errors.get(0), is("$: string found, but [integer, boolean] is required"));
    }

    @Test
    public void testUnionTypeWithObjects() throws IOException {
        JsonNode schemaNode = mapper.readTree(schema2);

        UnionTypeValidator v = new UnionTypeValidator(schemaNode.get("type"));
        List<String> errors = v.validate(jsonNode, "$");

        assertThat(errors.get(0), is("$: string found, but [integer, boolean] is required"));
    }

    @Test
    public void testUnionTypeMixed() throws IOException {
        JsonNode schemaNode = mapper.readTree(schema3);

        UnionTypeValidator v = new UnionTypeValidator(schemaNode.get("type"));
        List<String> errors = v.validate(jsonNode, "$");

        assertThat(errors.get(0), is("$: string found, but [integer, boolean] is required"));

    }

    @Test
    public void testUnionTypeWithStringAndNullAgainstInteger() throws IOException {
        JsonNode schemaNode = mapper.readTree(schema4);

        UnionTypeValidator v = new UnionTypeValidator(schemaNode.get("type"));
        List<String> errors = v.validate(jsonNodeInt, "$");

        assertThat(errors.size(), is(1));
        assertThat(errors.get(0), is("$: integer found, but [string, null] is required"));

    }
}
