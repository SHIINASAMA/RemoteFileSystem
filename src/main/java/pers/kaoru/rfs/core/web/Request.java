package pers.kaoru.rfs.core.web;

import java.util.TreeMap;

public class Request {

    private RequestMethod mode;

    private final TreeMap<String, String> headers;

    public Request() {
        headers = new TreeMap<>();
    }

    public RequestMethod getMethod() {
        return mode;
    }

    public void setMethod(RequestMethod mode) {
        this.mode = mode;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key){
        return headers.get(key);
    }

    public final TreeMap<String, String> getHeaders() {
        return headers;
    }
}