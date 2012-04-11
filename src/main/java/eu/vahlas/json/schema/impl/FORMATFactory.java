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
package eu.vahlas.json.schema.impl;

import eu.vahlas.json.schema.FORMAT;
import eu.vahlas.json.schema.JSONSchemaException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.codehaus.jackson.JsonNode;

public class FORMATFactory {

    //Formats for SimpleDateFormat
    private static String DATETIME_FMT = "yyyy-MM-dd'T'HH:mm:ssz";
    private static String DATE_FMT = "yyyy-MM-dd";
    private static String ZULU = "Z";
    private static String GMT = "GMT";
    private static String GMT_UTC_OFFSET = "GMT-00:00";

    /**
     * Translates the "format" property of the
     * <code>org.codehaus.jackson.JsonNode<code>
     * passed into one of the formats defined in the paragraph 5.20 of the JSON schema specification.
     *
     * @param the node containing the "format" property to translate.
     * @return the format as defined by the JSON Schema specification
     */
    public static FORMAT getFormat(JsonNode node) {

        FORMAT fmt = FORMAT.UNKNOWN;
        JsonNode formatNode = null;

        if (node.isTextual()) {  //comes from JacksonSchema
            formatNode = node;
        } else {
            if (node.isObject()) {
                formatNode = node.get("format");
            }
        }

        if (formatNode == null) {
            throw new JSONSchemaException("Invalid schema provided: property format is not defined!");
        } else {
            if (formatNode.isTextual()) {
                String format = formatNode.getTextValue();

                if ("date-time".equals(format)) {
                    fmt = FORMAT.DATE_TIME;
                } else {
                    if ("date".equals(format)) {
                        fmt = FORMAT.DATE;
                    }
                }
            }
        }
        return fmt;
    }

    /**
     * Translates the format of a
     * <code>org.codehaus.jackson.JsonNode</code> passed into a format as defined in the paragraph 5.20 of the JSON
     * Schema specification.<br/> <br/> This method returns the "real" format of the node passed.
     *
     *
     * @return the JSON Schema format of this node
     */
    public static FORMAT getNodeFormat(JsonNode node) {

        FORMAT fmt = FORMAT.UNKNOWN;

        if (null != node) {

            if (node.isTextual()) {
                String propertyValue = node.getTextValue();
                if (isValidDateTime(propertyValue)) {
                    fmt = FORMAT.DATE_TIME;
                } else {
                    if (isValidDate(propertyValue)) {
                        fmt = FORMAT.DATE;
                    }
                }
            }
        }
        return fmt;
    }

    private static boolean isValidDateTime(String inStr) {
        return isParseableAsDateWithFmt(inStr, DATETIME_FMT);
    }

    private static boolean isValidDate(String inStr) {
        return isParseableAsDateWithFmt(inStr, DATE_FMT);
    }

    private static boolean isParseableAsDateWithFmt(String inStr, String dateFmt) {
        if (inStr == null) {
            return false;
        }
        // Set the format to use as a constructor argument.
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFmt);
        dateFormat.setLenient(false);

        if (dateFmt.equals(DATETIME_FMT)) {  //ISO-8601
            //SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks things a bit.
            if (inStr.endsWith(ZULU)) {
                inStr = inStr.substring(0, inStr.length() - 1) + GMT_UTC_OFFSET;
            } else {
                int inset = 6;
                try {
                    String s0 = inStr.substring(0, inStr.length() - inset);
                    String s1 = inStr.substring(inStr.length() - inset, inStr.length());
                    inStr = s0 + GMT + s1;
                } catch (java.lang.StringIndexOutOfBoundsException obe) {
                    return false;
                }
            }
        }

        try {
            //parse the inDate parameter
            dateFormat.parse(inStr.trim());
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }
}
