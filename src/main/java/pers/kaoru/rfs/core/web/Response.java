package pers.kaoru.rfs.core.web;

import pers.kaoru.rfs.core.Error;

import java.util.TreeMap;

public class Response {

    private ResponseCode code;

    private final TreeMap<String, String> headers;

    public Response() {
        headers = new TreeMap<>();
    }

    public ResponseCode getCode() {
        return code;
    }

    public void setCode(ResponseCode code) {
        this.code = code;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setError(Error error){
        setHeader("error", error.name());
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public final TreeMap<String, String> getHeaders() {
        return headers;
    }
}
