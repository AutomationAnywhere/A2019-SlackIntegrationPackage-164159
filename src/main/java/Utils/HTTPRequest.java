package Utils;
import okhttp3.*;
import org.apache.http.HttpEntity;
import java.io.FileOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HTTPRequest {

    public static String Request(String url, String method, String auth) throws IOException {
        URL urlForRequest = new URL(url);
        String readLine;
        String output = "";
        HttpURLConnection connection = (HttpURLConnection) urlForRequest.openConnection();
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Authorization", "Bearer " + auth);
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            output = response.toString();
            //return null;
        } else {
            output = "error";
        }
        return output;
    }

    //private static String LINE_FEED = "\r\n";

    public static String POSTwFile(String url, String filepath, String token) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
          .build();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
          .addFormDataPart("file","/"+filepath,
            RequestBody.create(MediaType.parse("application/octet-stream"),
            new File("/"+filepath)))
          .build();
        Request request = new Request.Builder()
          .url(url)
          .method("POST", body)
          .addHeader("Authorization", "Bearer "+token)
          .addHeader("Content-Type", "multipart/form-data")
          .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }
}



