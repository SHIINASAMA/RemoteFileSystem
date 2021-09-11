package pers.kaoru.rfs.core.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WebUtils {

    public static void WriteRequest(Socket socket, Request request) throws IOException {
        var out = socket.getOutputStream();
        out.write((request.getMethod().toString() + "\r\n").getBytes(StandardCharsets.UTF_8));
        for (var header : request.getHeaders().entrySet()) {
            out.write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public static Request ReadRequest(Socket socket) throws IOException {
        Request request = new Request();
        var in = socket.getInputStream();
        var reader = new BufferedReader(new InputStreamReader(in));

        String str = reader.readLine();
        switch (str) {
            case "LIST_SHOW":
                request.setMethod(RequestMethod.LIST_SHOW);
                break;
            case "REMOVE":
                request.setMethod(RequestMethod.REMOVE);
                break;
            case "COPY":
                request.setMethod(RequestMethod.COPY);
                break;
            case "MOVE":
                request.setMethod(RequestMethod.MOVE);
                break;
            case "MAKE_DIRECTORY":
                request.setMethod(RequestMethod.MAKE_DIRECTORY);
                break;
            case "DOWNLOAD":
                request.setMethod(RequestMethod.DOWNLOAD);
                break;
            case "UPLOAD":
                request.setMethod(RequestMethod.UPLOAD);
                break;
            case "VERIFY":
                request.setMethod(RequestMethod.VERIFY);
                break;
            default:
                request.setMethod(RequestMethod.ERROR);
                break;
        }
        while (!(str = reader.readLine()).equals("")) {
            var substr = str.split(": ");
            request.setHeader(substr[0], substr[1]);
        }

        return request;
    }

    public static void WriteResponse(Socket socket, Response response) throws IOException {
        var out = socket.getOutputStream();
        out.write((response.getCode() + "\r\n").getBytes(StandardCharsets.UTF_8));
        for (var header : response.getHeaders().entrySet()) {
            out.write((header.getKey() + ": " + header.getValue() + "\r\n").getBytes(StandardCharsets.UTF_8));
        }
        out.write("\r\n".getBytes(StandardCharsets.UTF_8));
    }

    public static Response ReadResponse(Socket socket) throws IOException {
        Response response = new Response();
        var in = socket.getInputStream();
        var reader = new BufferedReader(new InputStreamReader(in));

        String str = reader.readLine();
        switch (str) {
            case "OK":
                response.setCode(ResponseCode.OK);
                break;
            case "FAIL":
                response.setCode(ResponseCode.FAIL);
                break;
            default:
                response.setCode(ResponseCode.ERROR);
                break;
        }

        while (!(str = reader.readLine()).equals("")) {
            var substr = str.split(": ");
            response.setHeader(substr[0], substr[1]);
        }

        return response;
    }

    /// 懒得处理 token 过期问题了
    private static final long TokenExpireDate = 60L * 60 * 24 * 365 * 100; // 100 年
    private static final String TokenSecret = "pers.kaoru.rfs.core.web.WebUtils";

    public static String MakeToken(String name, String password) {
        Date date = new Date(System.currentTimeMillis() + TokenExpireDate);

        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        String token = JWT.create()
                .withHeader(headers)
                .withClaim("username", name)
                .withClaim("password", password)
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(TokenSecret));

        return token;
    }

    public static Map<String, String> VerifyToken(String token) {
        try {
            Map<String, String> headers = new TreeMap<>();
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TokenSecret)).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            String name = decodedJWT.getClaim("username").asString();
            String pwd = decodedJWT.getClaim("password").asString();
            headers.put("username", name);
            headers.put("password", pwd);
            return headers;
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }
}
