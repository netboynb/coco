package com.ms.coco.model;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * @author wanglin/netboy
 * @version 创建时间：2016年8月12日 下午7:17:15
 * @func
 */
public class FileNameExclusionStrategy implements ExclusionStrategy {

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
