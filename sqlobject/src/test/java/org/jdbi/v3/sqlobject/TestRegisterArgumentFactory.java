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
package org.jdbi.v3.sqlobject;

import java.lang.reflect.Type;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.argument.ArgumentFactory;
import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.rule.H2DatabaseRule;
import org.jdbi.v3.sqlobject.config.RegisterArgumentFactory;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRegisterArgumentFactory {
    @Rule
    public H2DatabaseRule dbRule = new H2DatabaseRule().withPlugin(new SqlObjectPlugin());
    private Jdbi db;

    @Before
    public void setUp() {
        db = dbRule.getJdbi();
    }

    @Test
    public void testSingleAnnotation() {
        db.useExtension(Waffle.class, w -> {
            w.insert(1, new Name("Brian", "McCallister"));

            assertThat(w.findName(1)).isEqualTo("Brian McCallister");
        });
    }

    @Test
    public void testMultipleAnnotations() {
        db.useExtension(ShortStack.class, s -> {
            s.insert(1, new Name("George", "Takei"));

            assertThat(s.findName(1)).isEqualTo("George Takei");
        });
    }

    @RegisterArgumentFactory(NameAF.class)
    public interface Waffle {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@Bind("id") int id, @Bind("name") Name name);

        @SqlQuery("select name from something where id = :id")
        String findName(@Bind("id") int id);
    }

    @RegisterArgumentFactory(NameAF.class)
    @RegisterArgumentFactory(LazyAF.class)
    public interface ShortStack {
        @SqlUpdate("insert into something (id, name) values (:id, :name)")
        void insert(@Bind("id") int id, @Bind("name") Name name);

        @SqlQuery("select name from something where id = :id")
        String findName(@Bind("id") int id);
    }

    public static class LazyAF implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type type, Object value, ConfigRegistry config) {
            return Optional.empty();
        }
    }

    public static class NameAF implements ArgumentFactory {
        @Override
        public Optional<Argument> build(Type expectedType, Object value, ConfigRegistry config) {
            if (expectedType == Name.class || value instanceof Name) {
                Name nameValue = (Name) value;
                return Optional.of((position, statement, ctx1) -> statement.setString(position, nameValue.getFullName()));
            }
            return Optional.empty();
        }
    }

    @RequiredArgsConstructor
    @ToString
    public static class Name {
        private final String first;
        private final String last;

        public String getFullName() {
            return first + " " + last;
        }
    }

}
