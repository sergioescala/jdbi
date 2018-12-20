/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.argument;

import org.jdbi.v3.core.Enums;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestEnumArguments {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule();

    @Test
    public void testDefaultValue() {
        dbRule.getJdbi().useHandle(h -> {
            h.createUpdate("create table enums(value varchar)").execute();

            h.createUpdate("insert into enums(value) values(:enum)")
                .bind("enum", TestEnum.FOO)
                .execute();

            String inserted = h.createQuery("select value from enums")
                .mapTo(String.class)
                .findOnly();

            assertThat(inserted).isEqualTo(TestEnum.FOO.name());
        });
    }

    @Test
    public void testOrdinalValue() {
        dbRule.getJdbi().useHandle(h -> {
            h.getConfig(Enums.class).handleEnumsByOrdinal();

            h.createUpdate("create table enums(value int)").execute();

            h.createUpdate("insert into enums(value) values(:enum)")
                .bind("enum", TestEnum.FOO)
                .execute();

            Integer inserted = h.createQuery("select value from enums")
                .mapTo(Integer.class)
                .findOnly();

            assertThat(inserted).isEqualTo(TestEnum.FOO.ordinal());
        });
    }

    @Test
    public void testNull() {
        dbRule.getJdbi().useHandle(h -> {
            h.createUpdate("create table enums(value varchar)").execute();

            h.createUpdate("insert into enums(value) values(:enum)")
                .bindByType("enum", null, TestEnum.class)
                .execute();

            String inserted = h.createQuery("select value from enums")
                .mapTo(String.class)
                .findOnly();

            assertThat(inserted).isNull();
        });
    }

    private enum TestEnum {
        FOO, BAR
    }
}
