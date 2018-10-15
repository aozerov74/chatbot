package com.chatbot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utility methods for working with JSON.
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER;

    static {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JsonOrgModule());

        MAPPER = mapper;
    }

    private JsonUtils() {
    }

    /**
     * Get a pre-configured Jackson <code>ObjectMapper</code>.
     *
     * @return  the <code>ObjectMapper</code>
     */
    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    /**
     * Convert an object to the equivalent <code>JSONObject</code>.
     *
     * @param obj   the object to be converted
     *
     * @return  the <code>JSONObject</code>
     */
    public static JSONObject toJson(Object obj) {
        return JsonUtils.getMapper().convertValue(obj, JSONObject.class);
    }

    /**
     * Convert an object to the equivalent <code>JSONArray</code>.
     *
     * @param obj   the list to be converted
     *
     * @return  the <code>JSONArray</code>
     */
    public static JSONArray toJsonArray(List obj) {
        return JsonUtils.getMapper().convertValue(obj, JSONArray.class);
    }

    /**
     * Format the given date in ISO format.
     *
     * @param date  the date to be formatted
     *
     * @return  the formatted date
     */
    public static String formatDate(Date date) {
        assert date != null;

        return date.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT);
    }

}

