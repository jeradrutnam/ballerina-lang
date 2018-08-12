/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.persistence.serializable.serializer.providers.bvalue;

import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.persistence.serializable.serializer.BValueDeserializer;
import org.ballerinalang.persistence.serializable.serializer.BValueSerializer;
import org.ballerinalang.persistence.serializable.serializer.SerializationBValueProvider;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Provide mapping between {@link BMap} and {@link BValue} representation of it.
 */
public class BMapBValueProvider implements SerializationBValueProvider<BMap> {
    @Override
    public String typeName() {
        return BMap.class.getSimpleName();
    }

    @Override
    public Class<?> getType() {
        return BMap.class;
    }

    @Override
    public BValue toBValue(BMap bMap, BValueSerializer serializer) {
            LinkedHashMap implMap = bMap.getMap();
            BValue serialized = serializer.toBValue(implMap, implMap.getClass());
            return BValueProviderHelper.wrap(typeName(), serialized);
    }

    @Override
    @SuppressWarnings("unchecked")
    public BMap toObject(BValue bValue, BValueDeserializer bValueDeserializer) {
        if (bValue instanceof BMap) {
            BMap<String, BValue> wrapper = (BMap<String, BValue>) bValue;
            if (BValueProviderHelper.isWrapperOfType(wrapper, typeName())) {
                BMap payload = (BMap) BValueProviderHelper.getPayload(wrapper);
                HashMap deserializedMap = (HashMap) bValueDeserializer.deserialize(payload, HashMap.class);
                BMap bMap = new BMap();
                bMap.getMap().putAll(deserializedMap);
                return bMap;
            }
        }
        throw BValueProviderHelper.deserializationIncorrectType(bValue, typeName());
    }
}
