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
import org.jdbi.v3.core.config.ConfigRegistry;

class EnumArgumentFactory implements ArgumentFactory {
    @Override
    public Optional<Argument> build(Type expectedType, Object rawValue, ConfigRegistry config) {
        boolean isEnum = expectedType instanceof Class && ((Class<?>) expectedType).isEnum();
        if (!isEnum) {
            return Optional.empty();
        }

        boolean byName = config.get(Enums.class).enumsHandledByName();
        Arguments arguments = config.get(Arguments.class);

        if (rawValue == null) {
            return byName
                ? arguments.findFor(String.class, null)
                : arguments.findFor(Integer.class, null);
        }

        Enum<?> enumValue = (Enum<?>) rawValue;

        return byName
            ? arguments.findFor(String.class, enumValue.name())
            : arguments.findFor(Integer.class, enumValue.ordinal());
    }
}
