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
package eu.vahlas.json.schema;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import eu.vahlas.json.schema.impl.JacksonSchemaProvider;

public class JSONSchemaTest {

	public JSONSchemaTest() {

	}

	@Test
	public void testCard() throws Exception {
		// Jackson parsing API: the ObjectMapper can be provided
		// and configured differently depending on the application
		ObjectMapper mapper = new ObjectMapper();

		// Allows to retrieve a JSONSchema object on various sources
		// supported by the ObjectMapper provided
		JSONSchemaProvider schemaProvider = new JacksonSchemaProvider(mapper);

		// Retrieves a JSON Schema object based on a URL
                URL url = getClass().getResource("/card-schema.json");
		JSONSchema schema = schemaProvider.getSchema(url);

		// Validates a JSON Instance object stored in a file against the schema
		InputStream instanceIS = getClass().getResourceAsStream("/card.json");
		List<String> errors = schema.validate(instanceIS);

		assertThat(errors.size(), is(10));
		// YES THIS IS A REF !!!!
                // commented-out the line below because the good folks at json-schema.org changed geo.longitude
                // not to be required...
		//assertThat(errors.get(0), is("$.geo.longitude: is missing and it is not optional"));

                // Important: only "date" and "date-time" formats are supported for the moment
                assertThat(errors.get(0), is("$.logo: unknown: unknown or unimplemented format type"));
                assertThat(errors.get(1), is("$.tel.value: unknown: unknown or unimplemented format type"));
                assertThat(errors.get(2), is("$.foo: integer found, but [string, null] is required"));
                assertThat(errors.get(3), is("$.photo: unknown: unknown or unimplemented format type"));
                assertThat(errors.get(4), is("$.url: unknown: unknown or unimplemented format type"));
                assertThat(errors.get(5), is("$.email: string found, object expected"));
                assertThat(errors.get(6), is("$.email.value: is missing and it is not optional"));
                assertThat(errors.get(7), is("$.email.type: is missing and it is not optional"));
                // $.email.value format validation is skipped because type validations fail
                // TODO: (low priority) if node.isTextual, then apply fmt validations
                // and get some meaningful error
                assertThat(errors.get(8), is("$.sound: unknown: unknown or unimplemented format type"));
                assertThat(errors.get(9), is("$.town: the presence of this property requires that state also be present"));
	}
}
