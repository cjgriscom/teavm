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
package org.teavm.idea.debug;

import com.intellij.debugger.ui.breakpoints.JavaLineBreakpointType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.java.debugger.breakpoints.properties.JavaLineBreakpointProperties;
import org.teavm.debugging.Breakpoint;
import org.teavm.debugging.Debugger;

public class TeaVMLineBreakpointHandler extends XBreakpointHandler<XLineBreakpoint<JavaLineBreakpointProperties>> {
    private Debugger innerDebugger;
    private VirtualFileManager vfs;
    private ProjectFileIndex fileIndex;

    @SuppressWarnings("unchecked")
    public TeaVMLineBreakpointHandler(Project project, Debugger innerDebugger) {
        super(JavaLineBreakpointType.class);
        this.innerDebugger = innerDebugger;
        vfs = VirtualFileManager.getInstance();
        fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
    }

    @Override
    public void registerBreakpoint(@NotNull XLineBreakpoint<JavaLineBreakpointProperties> breakpoint) {
        VirtualFile virtualFile = vfs.findFileByUrl(breakpoint.getFileUrl());
        if (virtualFile == null) {
            return;
        }
        VirtualFile root = fileIndex.getSourceRootForFile(virtualFile);
        if (root == null) {
            return;
        }
        String path = relativePath(root, virtualFile);
        if (path == null) {
            return;
        }

        Breakpoint innerBreakpoint = innerDebugger.createBreakpoint(path, breakpoint.getLine() + 1);
        breakpoint.putUserData(TeaVMDebugProcess.INNER_BREAKPOINT_KEY, innerBreakpoint);
    }

    @Nullable
    private String relativePath(@NotNull VirtualFile ancestor, @NotNull VirtualFile descendant) {
        List<String> parts = new ArrayList<>();
        while (!ancestor.equals(descendant)) {
            if (descendant == null) {
                return null;
            }
            parts.add(descendant.getName());
            descendant = descendant.getParent();
        }

        Collections.reverse(parts);
        return StringUtil.join(parts, "/");
    }

    @Override
    public void unregisterBreakpoint(@NotNull XLineBreakpoint<JavaLineBreakpointProperties> breakpoint,
            boolean temporary) {
        Breakpoint innerBreakpoint = breakpoint.getUserData(TeaVMDebugProcess.INNER_BREAKPOINT_KEY);
        if (innerBreakpoint != null) {
            breakpoint.putUserData(TeaVMDebugProcess.INNER_BREAKPOINT_KEY, null);
            innerBreakpoint.destroy();
        }
    }
}