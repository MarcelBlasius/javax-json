/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.stream.JsonGenerationException;
import javax.json.stream.JsonGenerator;

import junit.framework.TestCase;

/**
 * {@link JsonGenerator} tests
 *
 * @author Jitendra Kotamraju
 */
public class JsonGeneratorTest extends TestCase {
    public JsonGeneratorTest(String testName) {
        super(testName);
    }

    public void testObjectWriter() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        testObject(generator);
        generator.close();
        writer.close();

        JsonReader reader = new JsonReader(new StringReader(writer.toString()));
        JsonObject person = reader.readObject();
        JsonObjectTest.testPerson(person);
    }

    public void testObjectStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonGenerator generator = Json.createGenerator(out);
        testObject(generator);
        generator.close();
        out.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        JsonReader reader = new JsonReader(in);
        JsonObject person = reader.readObject();
        JsonObjectTest.testPerson(person);
        reader.close();
        in.close();
    }

    static void testObject(JsonGenerator generator) throws Exception {
        generator
                .writeStartObject()
                .write("firstName", "John")
                .write("lastName", "Smith")
                .write("age", 25)
                .writeStartObject("address")
                .write("streetAddress", "21 2nd Street")
                .write("city", "New York")
                .write("state", "NY")
                .write("postalCode", "10021")
                .writeEnd()
                .writeStartArray("phoneNumber")
                .writeStartObject()
                .write("type", "home")
                .write("number", "212 555-1234")
                .writeEnd()
                .writeStartObject()
                .write("type", "fax")
                .write("number", "646 555-4567")
                .writeEnd()
                .writeEnd()
                .writeEnd();
    }

    public void testArray() throws Exception {
        Writer sw = new StringWriter();
        JsonGenerator generator = Json.createGenerator(sw);
        generator
                .writeStartArray()
                .writeStartObject()
                .write("type", "home")
                .write("number", "212 555-1234")
                .writeEnd()
                .writeStartObject()
                .write("type", "fax")
                .write("number", "646 555-4567")
                .writeEnd()
                .writeEnd();
        generator.close();
        System.out.println(sw.toString());
    }

    // tests JsonGenerator when JsonValue is used for generation
    public void testJsonValue() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator
                .writeStartObject()
                .write("firstName", "John")
                .write("lastName", "Smith")
                .write("age", 25)
                .write("address", JsonBuilderTest.buildAddress())
                .write("phoneNumber", JsonBuilderTest.buildPhone())
                .writeEnd();
        generator.close();
        writer.close();

        JsonReader reader = new JsonReader(new StringReader(writer.toString()));
        JsonObject person = reader.readObject();
        JsonObjectTest.testPerson(person);
    }

    public void testArrayString() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartArray().write("string").writeEnd();
        generator.close();
        writer.close();

        assertEquals("[\"string\"]", writer.toString());
    }

    public void testEscapedString() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartArray().write("\u0000").writeEnd();
        generator.close();
        writer.close();

        assertEquals("[\"\\u0000\"]", writer.toString());
    }

    public void testEscapedString1() throws Exception {
        String expected = "\u0000\u00ff";
        StringWriter sw = new StringWriter();
        JsonGenerator generator = Json.createGenerator(sw);
        generator.writeStartArray().write("\u0000\u00ff").writeEnd();
        generator.close();
        sw.close();

        JsonReader jr = new JsonReader(new StringReader(sw.toString()));
        JsonArray array = jr.readArray();
        String got = array.getString(0);
        jr.close();

        assertEquals(expected, got);
    }

    public void testGeneratorEquals() throws Exception {
        StringWriter sw = new StringWriter();
        JsonGenerator generator = Json.createGenerator(sw);
        generator.writeStartArray()
                .write(JsonValue.TRUE)
                .write(JsonValue.FALSE)
                .write(JsonValue.NULL)
                .write(Integer.MAX_VALUE)
                .write(Long.MAX_VALUE)
                .write(Double.MAX_VALUE)
                .write(Integer.MIN_VALUE)
                .write(Long.MIN_VALUE)
                .write(Double.MIN_VALUE)
                .writeEnd();

        JsonReader reader = new JsonReader(new StringReader(sw.toString()));
        JsonArray expected = reader.readArray();
        reader.close();

        JsonArray actual = new JsonArrayBuilder()
                .add(JsonValue.TRUE)
                .add(JsonValue.FALSE)
                .add(JsonValue.NULL)
                .add(Integer.MAX_VALUE)
                .add(Long.MAX_VALUE)
                .add(Double.MAX_VALUE)
                .add(Integer.MIN_VALUE)
                .add(Long.MIN_VALUE)
                .add(Double.MIN_VALUE)
                .build();

        assertEquals(expected, actual);
    }

    public void testPrettyObjectWriter() throws Exception {
        StringWriter writer = new StringWriter();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGenerator generator = Json.createGeneratorFactory(config)
                .createGenerator(writer);
        testObject(generator);
        generator.close();
        writer.close();

        JsonReader reader = new JsonReader(new StringReader(writer.toString()));
        JsonObject person = reader.readObject();
        JsonObjectTest.testPerson(person);
    }

    public void testPrettyObjectStream() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonGenerator generator = Json.createGeneratorFactory(config)
                .createGenerator(out);
        testObject(generator);
        generator.close();
        out.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        JsonReader reader = new JsonReader(in);
        JsonObject person = reader.readObject();
        JsonObjectTest.testPerson(person);
        reader.close();
        in.close();
    }

    public void testGenerationException1() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();
        try {
            generator.writeStartObject();
            fail("Expected JsonGenerationException, writeStartObject() cannot be called more than once");
        } catch (JsonGenerationException je) {
            // Expected exception
        }
    }

    public void testGenerationException2() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();
        try {
            generator.writeStartArray();
            fail("Expected JsonGenerationException, writeStartArray() is valid in no context");
        } catch (JsonGenerationException je) {
            // Expected exception
        }
    }

    public void testGenerationException3() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        try {
            generator.close();
            fail("Expected JsonGenerationException, no JSON is generated");
        } catch (JsonGenerationException je) {
            // Expected exception
        }
    }

    public void testGenerationException4() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartArray();
        try {
            generator.close();
            fail("Expected JsonGenerationException, writeEnd() is not called");
        } catch (JsonGenerationException je) {
            // Expected exception
        }
    }

    public void testGenerationException5() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();
        try {
            generator.close();
            fail("Expected JsonGenerationException, writeEnd() is not called");
        } catch (JsonGenerationException je) {
            // Expected exception
        }
    }

    public void testGeneratorArrayDouble() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartArray();
        try {
            generator.write(Double.NaN);
            fail("JsonGenerator.write(Double.NaN) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        try {
            generator.write(Double.POSITIVE_INFINITY);
            fail("JsonGenerator.write(Double.POSITIVE_INIFINITY) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        try {
            generator.write(Double.NEGATIVE_INFINITY);
            fail("JsonGenerator.write(Double.NEGATIVE_INIFINITY) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        generator.writeEnd();
        generator.close();
    }

    public void testGeneratorObjectDouble() throws Exception {
        StringWriter writer = new StringWriter();
        JsonGenerator generator = Json.createGenerator(writer);
        generator.writeStartObject();
        try {
            generator.write("foo", Double.NaN);
            fail("JsonGenerator.write(String, Double.NaN) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        try {
            generator.write("foo", Double.POSITIVE_INFINITY);
            fail("JsonGenerator.write(String, Double.POSITIVE_INIFINITY) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        try {
            generator.write("foo", Double.NEGATIVE_INFINITY);
            fail("JsonGenerator.write(String, Double.NEGATIVE_INIFINITY) should produce NumberFormatException");
        } catch (NumberFormatException ne) {
            // expected
        }
        generator.writeEnd();
        generator.close();
    }
}
