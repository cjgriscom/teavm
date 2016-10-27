/*
 *  Copyright 2016 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.idea;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeaVMFacetType extends FacetType<TeaVMFacet, TeaVMFacetConfiguration> {
    public static final FacetTypeId<TeaVMFacet> TYPE_ID = new FacetTypeId<>("teavm-js");
    private static final Icon icon = IconLoader.getIcon("/teavm-16.png");

    public TeaVMFacetType() {
        super(TYPE_ID, "teavm-js", "TeaVM (JS)");
    }

    @Override
    public TeaVMFacetConfiguration createDefaultConfiguration() {
        return null;
    }

    @Override
    public TeaVMFacet createFacet(@NotNull Module module, String name, @NotNull TeaVMFacetConfiguration configuration,
            @Nullable Facet underlyingFacet) {
        return null;
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return moduleType instanceof JavaModuleType;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return icon;
    }
}
