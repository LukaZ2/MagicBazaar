package me.magicbazaar.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.HttpResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.CharBuffer;

public class HttpHandler {

    public static JsonObject get(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int status = connection.getResponseCode();
        InputStreamReader streamReader = new InputStreamReader(status == 200 ? connection.getInputStream() : connection.getErrorStream());

        StringBuilder sb = new StringBuilder();
        int buf;
        while ((buf = streamReader.read()) != -1)
            sb.append((char) buf);
        String output = sb.toString();

        streamReader.close();
        connection.disconnect();

        if (status != 200) throw new HttpResponseException(status, output);

        return JsonParser.parseString(output).getAsJsonObject();
    }

}
