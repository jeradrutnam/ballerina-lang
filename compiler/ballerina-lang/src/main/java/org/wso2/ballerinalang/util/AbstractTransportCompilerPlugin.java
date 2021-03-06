/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.wso2.ballerinalang.util;

import org.ballerinalang.compiler.plugins.AbstractCompilerPlugin;
import org.ballerinalang.model.tree.FunctionNode;
import org.wso2.ballerinalang.compiler.semantics.analyzer.Types;
import org.wso2.ballerinalang.compiler.semantics.model.SymbolTable;
import org.wso2.ballerinalang.compiler.semantics.model.types.BType;
import org.wso2.ballerinalang.compiler.semantics.model.types.BUnionType;
import org.wso2.ballerinalang.compiler.tree.BLangFunction;
import org.wso2.ballerinalang.compiler.util.CompilerContext;

import java.util.LinkedHashSet;

/**
 * {@code AbstractTransportCompilerPlugin} provides convenient superclass
 * for standard library compiler plugin implementations.
 *
 * @since 0.990.0
 */
public abstract class AbstractTransportCompilerPlugin extends AbstractCompilerPlugin {

    private BType errorOrNil = null;
    private Types types = null;

    public boolean isResourceReturnsErrorOrNil(FunctionNode functionNode) {
        BLangFunction resource = (BLangFunction) functionNode;
        return types.isAssignable(resource.symbol.getReturnType(), errorOrNil);
    }

    @Override
    public void setCompilerContext(CompilerContext context) {
        this.types = Types.getInstance(context);
        SymbolTable symbolTable = SymbolTable.getInstance(context);
        LinkedHashSet<BType> memberTypes = new LinkedHashSet<>();
        memberTypes.add(symbolTable.errorType);
        memberTypes.add(symbolTable.nilType);
        this.errorOrNil = new BUnionType(null, memberTypes, true);
    }
}
