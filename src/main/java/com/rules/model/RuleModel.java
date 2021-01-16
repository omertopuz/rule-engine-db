package com.rules.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Data
public class RuleModel {

    private Map<String, Object> fields;

    public static int toInt(Object val){
        return Optional.ofNullable(val)
                .map(v-> Integer.parseInt(v.toString()))
                .orElseThrow(()->new RuntimeException(val + " is null. Can not be converted to integer"));
    }

    public static double toDouble(Object val){
        return Optional.ofNullable(val)
                .map(v-> Integer.parseInt(v.toString()))
                .orElseThrow(()->new RuntimeException(val + " is null. Can not be converted to double"));
    }

    public static LocalDateTime toDate(Object val){
        return toDate(val,DateTimeFormatter.ISO_DATE_TIME);
    }

    public static LocalDateTime toDate(Object val, DateTimeFormatter formatter){
        return Optional.ofNullable(val)
                .map(v->LocalDateTime.parse(v.toString(),formatter))
                .orElseThrow(()->new RuntimeException(val + " is null. Can not be converted to date"));
    }

    public static Map<String, Object> toMap(Object val){
        return Optional.ofNullable(val)
                .map(v-> (Map<String, Object>)val)
                .orElseThrow(()->new RuntimeException(val + " is null. Can not be converted to map"));
    }

    public Object get(String key){
        return fields.get(key);
    }

    public Object put(String key,Object value){
        return fields.put(key,value);
    }
}
