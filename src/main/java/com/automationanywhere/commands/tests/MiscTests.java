package com.automationanywhere.commands.tests;

import Utils.ParseResponse;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import Utils.HTTPRequest;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class MiscTests {

    public static void main(String[] args) throws IOException, ParseException, JSONException {


        String token = "";
        String channel = "";
        List<String> messages = new ArrayList<>();
        channel = URLEncoder.encode(channel, StandardCharsets.UTF_8);

       /* String url = "https://slack.com/api/conversations.history?channel="+channel;
        String response = HTTPRequest.Request(url, "GET",token);

        messages = ParseResponse.MessageHistoryStrings(token, messages, channel);*/

        //Retrieve APIKey String that is passed as Session Object
        String title = "This is a test file";
        String file = "C:\\Users\\felipecl\\Downloads\\UI_SQL_example.zip";
        String url = "https://slack.com/api/files.upload?channels=" + channel + "&title=" + title;
        String response = HTTPRequest.POSTwFile(url, file, token);

        System.out.println(response);

    }

}
