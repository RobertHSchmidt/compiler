/*
 * Copyright 2016, Hridesh Rajan, Robert Dyer, Farheen Sultana
 *                 Iowa State University of Science and Technology,
 *                 and Bowling Green State University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package boa.test.datagen;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/*
 * @author sfarheen
 * @author rdyer
 */
public class TestInstanceMethodReference extends BaseTest {
	@Test
	public void methodReference() {
		assertEquals(parseWrapped("obj::compareByName;"),
			"{\n" +
		    "   \"namespaces\": [\n" +
		    "      {\n" +
		    "         \"name\": \"\",\n" +
		    "         \"declarations\": [\n" +
		    "            {\n" +
		    "               \"name\": \"t\",\n" +
		    "               \"kind\": \"CLASS\",\n" +
		    "               \"methods\": [\n" +
		    "                  {\n" +
		    "                     \"name\": \"m\",\n" +
		    "                     \"return_type\": {\n" +
		    "                        \"kind\": \"OTHER\",\n" +
		    "                        \"name\": 0\n" +
			"                     },\n" +
			"                     \"statements\": [\n" +
		    "                        {\n" +
		    "                           \"kind\": \"BLOCK\"\n" +
		    "                        }\n" +
		    "                     ]\n" +
		    "                  }\n" +
		    "               ]\n" +
		    "            }\n" +
		    "         ]\n" +
		    "      }\n" +
		    "   ]\n" +
		    "}" 
		);
	}
}
