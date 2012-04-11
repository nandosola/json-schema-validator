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

import eu.vahlas.json.schema.FORMAT;
import eu.vahlas.json.schema.TYPE;
import eu.vahlas.json.schema.impl.FORMATFactory;
import eu.vahlas.json.schema.impl.JSONValidator;
import eu.vahlas.json.schema.impl.TYPEFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatValidator implements JSONValidator, Serializable {

    private static final long serialVersionUID = -637068450453946642L;
    private static final Logger LOG = LoggerFactory.getLogger(FormatValidator.class);
    public static final String PROPERTY = "format";
    private FORMAT schemaFormat;
    private boolean isOptionalProperty = false;
    private boolean isNullableProperty = false;

    public FormatValidator(JsonNode schemaNode) {
        schemaFormat = FORMATFactory.getFormat(schemaNode);

        JsonNode typeNode = schemaNode.get("type");
        // TODO: (low priority) move nodeType to FORMATFactory
        // It could be useful for better error messages
        if (null != typeNode) {
            TYPE schemaType = TYPEFactory.getType(typeNode);
            if (schemaType.equals(TYPE.NULL)){
                isNullableProperty = true;
            } else {
                if (schemaType.equals(TYPE.UNION)){
                    TYPE[] types = TYPEFactory.getUnionType(schemaNode);
                    if(TYPE.unionContains(types, TYPE.NULL)){
                        isNullableProperty = true;
                    }
                }
            }
        }

        JsonNode optionalNode = schemaNode.get("optional");
        if (null != optionalNode) {
            if (optionalNode.isBoolean()) {
                isOptionalProperty = optionalNode.getBooleanValue();
            }
        }
    }

    @Override
    public List<String> validate(JsonNode node, String at) {
        LOG.debug("validate( " + node + ", " + at + ")");
        return validate(node, null, at);
    }

    @Override
    public List<String> validate(JsonNode node, JsonNode parent, String at) {
        LOG.debug("validate( " + node + ", " + parent + ", " + at + ")");
        List<String> errors = new ArrayList<String>();

        if ((null == node && isOptionalProperty) || (node.isNull() && isNullableProperty)) {
            return errors;
        }

        FORMAT nodeFormat = FORMATFactory.getNodeFormat(node);

        if (!schemaFormat.equals(FORMAT.UNKNOWN)) {

            if (nodeFormat != schemaFormat) {
                errors.add(at + ": incorrect format, " + schemaFormat + " expected");
            }
        } else {
            errors.add(at + ": " + schemaFormat + ": unknown or unimplemented format type");
        }

        return errors;
    }
}
