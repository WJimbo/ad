package com.zz9158.app.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by tangyongx on 26/11/2018.
 */

public class GsonUtil {
    public static Gson gson = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            }
            return new JsonPrimitive(src);
        }
    }).registerTypeAdapter(Float.class, new JsonSerializer<Float>() {
        @Override
        public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == src.longValue()) {
                return new JsonPrimitive(src.longValue());
            }
            return new JsonPrimitive(src);
        }
    }).registerTypeAdapter(Boolean.class, new JsonSerializer<Boolean>() {
        @Override
        public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
            if ("true".equals(src.toString())) {
                return new JsonPrimitive(true);
            }else{
                return new JsonPrimitive(false);
            }
//            return new JsonPrimitive(src);
        }
    }).registerTypeAdapter(
            new TypeToken<TreeMap<String, Object>>() {
            }.getType(),
            new JsonDeserializer<TreeMap<String, Object>>() {
                @Override
                public TreeMap<String, Object> deserialize(
                        JsonElement json, Type typeOfT,
                        JsonDeserializationContext context) throws JsonParseException {

                    TreeMap<String, Object> treeMap = new TreeMap<>();
                    JsonObject jsonObject = json.getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                    for (Map.Entry<String, JsonElement> entry : entrySet) {
                        Object valueObj = entry.getValue();
                        if (valueObj instanceof String) {
                            if (valueObj == null || "null".equals(valueObj)) {
                                valueObj = "";
                            }
                        }
                        treeMap.put(entry.getKey(), valueObj);
                    }
                    return treeMap;
                }
            }).registerTypeAdapter(String.class, new JsonSerializer<String>() {
        @Override
        public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return new JsonPrimitive("");
            }
            return new JsonPrimitive(src);
        }
    }).serializeNulls().create();
    public static Map<String,Object> objectToMap(Object object){
        String jsonStr = GsonUtil.gson.toJson(object);
        TreeMap<String, Object> map =
                GsonUtil.gson.fromJson(jsonStr, new TypeToken<TreeMap<String, Object>>() {
                }.getType());
        return map;
    }
    public static boolean validate(String jsonStr) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(jsonStr);
        } catch (Exception e) {
            return false;
        }
        if (jsonElement == null) {
            return false;
        }
        if (!jsonElement.isJsonObject()) {
            return false;
        }
        return true;
    }
}
