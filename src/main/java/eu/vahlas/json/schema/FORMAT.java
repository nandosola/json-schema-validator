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
package eu.vahlas.json.schema;

/**
 * Translates
 * <code>JsonNode</code> type into formats as defined in the paragraph 5.20 of the specification<br/> <br/> Allowed node
 * types according to the specification are:<br/> &nbsp;date, time, utc-millisec, regex, color, style, phone, uri,
 * email<br/> ip-address, ipv6, street-address, locality, region, postal-code, country.<br/> - Additional custom formats
 * may be defined with a URL to a definition of the format.<br/> - Any valid MIME media type.
 *
 * <br/> Two types were added as a help for the implementation:<br/> &nbsp;unknown and union<br/> <br/> <i>Specification
 * document:</i><br/> <a
 * href="http://tools.ietf.org/pdf/draft-zyp-json-schema-02.txt">http://tools.ietf.org/pdf/draft-zyp-json-schema-02.txt</a>
 *
 * @author nando@abstra.cc
 */
public enum FORMAT {

    /**
     * Type "date-time" as defined by the paragraph 5.20 of the JSON Schema specification
     */
    DATE_TIME("date-time"),
    /**
     * Type "date" as defined by the paragraph 5.20 of the JSON Schema specification
     */
    DATE("date"),

    UNKNOWN("unknown");

    private String format;

    /**
     * Constructed with a string as a parameter in order to allow pretty printing in the error messages
     *
     * @param typeStr the name of the type
     */
    private FORMAT(String formatStr) {
        format = formatStr;
    }

    /**
     * Returns the name of the format as defined in the paragraph 5.20 of the specification
     *
     * @return the name of the format
     */
    @Override
    public String toString() {
        return format;
    }
}
