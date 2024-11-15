package com.itcrowd.blogosphere.server.payload;

import lombok.Data;

@Data
public class ApiResponseData<T> {

    T data;

    String status;

    public ApiResponseData(T data, String status_string) {
        this.data = data;
        this.status = status_string;
    }
}
