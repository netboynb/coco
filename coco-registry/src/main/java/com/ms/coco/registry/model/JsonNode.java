package com.ms.coco.registry.model;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年09月29日 上午01:40:10
 */
public abstract class JsonNode {

    private static final Gson baseGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    @Override
    public String toString() {
        return baseGson.toJson(this);
    }

    protected abstract Gson nodeValueGson();

    public String toNodeJsonValue() {
        return nodeValueGson().toJson(this);
    }

    public byte[] toNodeJsonBinary() {
        return toNodeJsonValue().getBytes(Charsets.UTF_8);
    }

    public static class FileNameExclusionStrategy implements ExclusionStrategy {

        private Set<String> skipFields = new HashSet<>();

        public FileNameExclusionStrategy(String... skipFields) {
            for (String skipField : skipFields) {
                this.skipFields.add(skipField);
            }
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return skipFields.contains(f.getName());
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

}
