package com.itcrowd.blogosphere.server.payload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiResponseBuilder {
    public static <T> ResponseEntity<ApiResponseData<T>> makeGeneric(T data, HttpStatus status){

        var status_string = status == HttpStatus.OK ? "OK" : "ERROR";

        var res = new ApiResponseData<>(data, status_string);

        return new ResponseEntity<>(res,status);
    }

    public static <T> ResponseEntity<Object> make(T data, HttpStatus status){

        var status_string = status == HttpStatus.OK ? "OK" : "ERROR";

        var res = new ApiResponseData<>(data, status_string);

        return new ResponseEntity<>(res,status);
    }

    public static ResponseEntity<?> error(String data){
        return error(data,HttpStatus.BAD_REQUEST);
    }

    public static <T> ResponseEntity<?> error(T data, HttpStatus status){
        return error(List.of(data),status);
    }

    public static <T> ResponseEntity<?> error(List<T> data, HttpStatus status){
        Map<String, List<T>> body = new HashMap<>();
        body.put("errors",data);

        return makeGeneric(body,status);
    }

    public static ResponseEntity<ApiResponseData<String>> ok(String data){
        return makeGeneric(data,HttpStatus.OK);
    }

    public static <T> ResponseEntity<ApiResponseData<T>> ok(T data){
        return makeGeneric(data,HttpStatus.OK);
    }
}
