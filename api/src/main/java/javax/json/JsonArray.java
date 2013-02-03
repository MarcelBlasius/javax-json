/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package javax.json;

import java.util.List;

/**
 * {@code JsonArray} class represents an immutable JSON array value
 * (an ordered sequence of zero or more values).
 * It also provides unmodifiable list view to the JSON array values.
 *
 * <p>A JsonArray instance can be created from a input source using
 * {@link JsonReader#readArray()}. For example:
 * <pre><code>
 * JsonReader jsonReader = new JsonReader(...);
 * JsonArray array = jsonReader.readArray();
 * jsonReader.close();
 * </code></pre>
 *
 * It can also be built from scratch using a {@link JsonArrayBuilder}.
 *
 * <p>For example 1: An empty JSON array can be built as follows:
 * <pre><code>
 * JsonArray array = new JsonArrayBuilder().build();
 * </code></pre>
 *
 * For example 2: The following JSON
 * <pre><code>
 * [
 *     { "type": "home", "number": "212 555-1234" },
 *     { "type": "fax", "number": "646 555-4567" }
 * ]
 * </code></pre>
 * can be built using :
 * <pre><code>
 * JsonArray value = new JsonArrayBuilder()
 *     .add(new JsonObjectBuilder()
 *         .add("type", "home")
 *         .add("number", "212 555-1234"))
 *     .add(new JsonObjectBuilder()
 *         .add("type", "fax")
 *         .add("number", "646 555-4567"))
 *     .build();
 * </code></pre>
 *
 * {@code JsonArray} can be written to JSON as follows:
 * <pre><code>
 * JsonArray arr = ...;
 * JsonWriter writer = new JsonWriter(...)
 * writer.writeArray(arr);
 * writer.close();
 * </code></pre>
 *
 * {@code JsonArray} values can be {@link JsonObject}, {@link JsonArray},
 * {@link JsonString}, {@link JsonNumber}, {@link JsonValue#TRUE},
 * {@link JsonValue#FALSE}, {@link JsonValue#NULL}. These values can be
 * accessed using various accessor methods.
 *
 * <p>In the above example 2, home number "212 555-1234" can be got using:
 * <pre><code>
 * JsonObject home = array.getValue(0, JsonObject.class);
 * String number = home.getStringValue("number");
 * </code></pre>
 *
 * This list object provides read-only access to the JSON array data,
 * and attempts to modify the list, whether direct or via its collection
 * views, result in an {@code UnsupportedOperationException}.
 *
 * @author Jitendra Kotamraju
 */
public interface JsonArray extends JsonStructure, List<JsonValue> {

    /**
     * Returns the value at the specified position in this JSON array values.
     * This is just a convenience method for {@code (T) get(index)} to get
     * the value.
     *
     * @param index index of the value to return
     * @param clazz value class
     * @return the value at the specified position in this array values
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to the type T
     */
    <T extends JsonValue> T get(int index, Class<T> clazz);

    /**
     * A convenience method for
     * {@code getValue(index, JsonString.class).getValue()}
     *
     * @param index index of the JsonString value
     * @return the String value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to JsonString
     */
    String getString(int index);

    /**
     * Returns the {@code String} value of {@code JsonString} at the specified
     * position in this JSON array values. If {@code JsonString} is found,
     * its {@link javax.json.JsonString#getValue()} is returned. Otherwise,
     * the specified default value is returned.
     *
     * @param index index of the JsonString value
     * @return the String value at the specified position in this array,
     * or the specified default value
     */
    String getString(int index, String defaultValue);

    /**
     * A Convenience method for
     * {@code getValue(index, JsonNumber.class).getIntValue()}
     *
     * @param index index of the JsonNumber value
     * @return the int value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to JsonNumber
     */
    int getInt(int index);

    /**
     * Returns the int value of {@code JsonNumber} at specified position in
     * this JSON array values. If {@code JsonNumber} is found,
     * its {@link javax.json.JsonNumber#intValue()} is returned. Otherwise,
     * the specified default value is returned.
     *
     * @param index index of the JsonNumber value
     * @return the int value at the specified position in this array,
     * or the specified default value
     */
    int getInt(int index, int defaultValue);

    /**
     * Returns the boolean value for the specified position in this array.
     * If the value at the specified position is JsonValue.TRUE, then returns
     * true. If the value at the specified position is JsonValue.FALSE, then
     * returns false.
     *
     * @param index index of the JSON boolean value
     * @return the boolean value at the specified position in this array
     * @throws IndexOutOfBoundsException if the index is out of range
     * @throws ClassCastException if the value at the specified position is not
     * assignable to JsonValue.TRUE or JsonValue.FALSE
     */
    boolean getBoolean(int index);

    /**
     * Returns the boolean value for the specified position in this array.
     * If the value at the specified position is JsonValue.TRUE, then returns
     * true. If the value at the specified position is JsonValue.FALSE, then
     * returns false. Otherwise, the specified default value is returned.
     *
     * @param index index of the JSON boolean value
     * @return the boolean value at the specified position in this array,
     * or the specified default value
     */
    boolean getBoolean(int index, boolean defaultValue);

}
