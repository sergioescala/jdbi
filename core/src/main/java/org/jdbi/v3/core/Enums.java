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
package org.jdbi.v3.core;

import org.jdbi.v3.core.config.JdbiConfig;

public class Enums implements JdbiConfig<Enums> {
    private boolean handleEnumsByName = true;

    public Enums() {}

    private Enums(Enums other) {
        this.handleEnumsByName = other.handleEnumsByName;
    }

    /**
     * Applies to both binding and mapping.
     */
    public boolean enumsHandledByName() {
        return handleEnumsByName;
    }

    /**
     * Applies to both binding and mapping.
     */
    public boolean enumsHandledByOrdinal() {
        return !handleEnumsByName;
    }

    /**
     * Applies to both binding and mapping.
     */
    public void handleEnumsByName() {
        this.handleEnumsByName = true;
    }

    /**
     * Applies to both binding and mapping.
     */
    public void handleEnumsByOrdinal() {
        this.handleEnumsByName = false;
    }

    @Override
    public Enums createCopy() {
        return new Enums(this);
    }
}
