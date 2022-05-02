package com.automationanywhere.commands.tests;

import Utils.ParseResponse;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.table.Table;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.net.URLEncoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.testng.Assert.assertEquals;

public class MiscTests {

    public static void main(String[] args) throws IOException, ParseException, JSONException {


        String token = "";
        String channel = "";
        channel = URLEncoder.encode(channel, StandardCharsets.UTF_8);

        String url = "https://slack.com/api/conversations.history?channel="+channel;
        //String response = HTTPRequest.Request(url, "GET",token);
        //Retrieve APIKey String that is passed as Session Object
//        String title = "This is a test file";
//        String file = "C:\\Users\\felipecl\\Downloads\\UI_SQL_example.zip";
//        String url = "https://slack.com/api/files.upload?channels=" + channel + "&title=" + title;
//        String response = HTTPRequest.POSTwFile(url, file, token);

        //System.out.println(response);

    }

    @Test
    public void testMessageHistoryToTable() throws ParseException, IOException {
        String token = "";
        String channel = "";
        TableValue tablev = ParseResponse.MessageHistory(token, channel);
        Table table = tablev.get();
        System.out.println("Headers: ");
        table.getSchema().forEach((schema -> System.out.print(schema.getName() + "|")));
        System.out.println("\nRows: "+"\n");
        table.getRows().forEach((row -> System.out.println(row.getValues())) );
    }

}
