/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.ballerinalang.util.codegen;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code AnnotationAttachmentInfo} represents a Ballerina annotation attachment.
 *
 * @since 0.87
 */
public class AnnotationAttachmentInfo {

    protected String pkgPath;
    protected String name;

    protected int pkgPathCPIndex;
    protected int nameCPIndex;

    private Map<String, AnnotationAttributeValue> attributeValueMap = new HashMap<>();

    public AnnotationAttachmentInfo(String pkgPath, int pkgPathCPIndex, String name, int nameCPIndex) {
        this.pkgPath = pkgPath;
        this.pkgPathCPIndex = pkgPathCPIndex;
        this.name = name;
        this.nameCPIndex = nameCPIndex;
    }

    public void addAnnotationAttribute(String name, AnnotationAttributeValue attributeValue) {
        attributeValueMap.put(name, attributeValue);
    }

    public AnnotationAttributeValue getAnnotationAttributeValue(String name) {
        return attributeValueMap.get(name);
    }

    public String getPkgPath() {
        return pkgPath;
    }

    public String getName() {
        return name;
    }

    public Map<String, AnnotationAttributeValue> getAttributeValueMap() {
        return attributeValueMap;
    }
}
