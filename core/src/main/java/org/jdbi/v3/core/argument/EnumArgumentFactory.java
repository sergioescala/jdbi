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

import java.lang.reflect.Type;
import java.util.Optional;
import org.jdbi.v3.core.Enums;
import org.jdbi.v3.core.argument.internal.StatementBinder;
import org.jdbi.v3.core.argument.internal.strategies.LoggableBinderArgument;
import org.jdbi.v3.core.config.ConfigRegistry;

class EnumArgumentFactory implements ArgumentFactory {
    @Override
    public Optional<Argument> build(Type expectedType, Object rawValue, ConfigRegistry config) {
        if (rawValue instanceof Enum) {
            Enum<?> enumValue = (Enum<?>) rawValue;

            StatementBinder<Enum<?>> binder = config.get(Enums.class).enumsHandledByName()
                ? (p, i, v) -> p.setString(i, v.name())
                : (p, i, v) -> p.setInt(i, v.ordinal());

            return Optional.of(new LoggableBinderArgument<>(enumValue, binder));
        } else {
            return Optional.empty();
        }
    }
}
