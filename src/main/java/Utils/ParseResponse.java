package Utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.table.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseResponse {

    public static String OutputMessage (String response, String message) throws ParseException {
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        String result = jsonObj.get("ok").toString();
        String post;
        if (result.equals("true")) {
            post = message;
        } else {
            String reason = jsonObj.get("error").toString();
            post = "Action failed with reason " + reason;
        }
        return post;
    }

    public static List MessageHistory (String token, List messages, String channel) throws ParseException, IOException {
        Boolean hasMore = null;
        String cursor = "";
        do {
            String url = "https://slack.com/api/conversations.history?channel="+channel+"&cursor="+cursor;
            String response = HTTPRequest.Request(url, "GET", token);
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObj = (JSONObject) obj;
            String result = jsonObj.get("ok").toString();
            if (result.equals("true")) {
                hasMore = (Boolean) jsonObj.get("has_more");
                JSONArray messagesList = (JSONArray) jsonObj.get("messages");
                for (int i = 0; i < messagesList.size(); i++) {
                    List<Value> rowValues = new ArrayList<>();
                    JSONObject currentMessage = (JSONObject) messagesList.get(i);
                    if ("".equals(currentMessage.get("text").toString())) {
                        if (currentMessage.get("files") != null) {
                            rowValues.add(new StringValue("Message includes file attachment with no text"));
                        } else {
                            rowValues.add(new StringValue("No text found in message"));
                        }
                    } else {
                        rowValues.add(new StringValue(currentMessage.get("text").toString()));
                    }
                    rowValues.add(new StringValue(currentMessage.get("ts").toString()));
                    Row currentRow = new Row(rowValues);
                    messages.add(currentRow);
                }
            } else {
                String reason = jsonObj.get("error").toString();
                List<Value> row = new ArrayList<>();
                row.add(new StringValue("Action failed with error reason " + reason));
                Row currentRow = new Row(row);
                messages.add(currentRow);
            }
            if(hasMore!=null && hasMore){
                JSONObject metadata = (JSONObject) jsonObj.get("response_metadata");
                cursor = (String) metadata.get("next_cursor");
            }
        }while(hasMore!=null && hasMore);

        return messages;
    }


    public static List MessageHistoryStrings (String token, List messages, String channel) throws ParseException, IOException {
        Boolean hasMore;
        String cursor="";

        do {
            String url = "https://slack.com/api/conversations.history?channel="+channel+"&limit=5"+"&cursor="+cursor;
            String response = HTTPRequest.Request(url, "GET", token);
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObj = (JSONObject) obj;
            String result = jsonObj.get("ok").toString();
            hasMore = (Boolean) jsonObj.get("has_more");
            if (result.equals("true")) {
                JSONArray messagesList = (JSONArray) jsonObj.get("messages");
                for (int i = 0; i < messagesList.size(); i++) {
                    JSONObject currentMessage = (JSONObject) messagesList.get(i);
                    if ("".equals(currentMessage.get("text").toString())) {
                        if (currentMessage.get("files") != null) {
                            messages.add("Message includes file attachment with no text");
                        } else {
                            messages.add("No text found in message");
                        }
                    } else {
                        messages.add(currentMessage.get("text").toString());
                    }
                }
            } else {
                String reason = jsonObj.get("error").toString();
                messages.add("Action failed with error reason " + reason);
            }
            if(hasMore!=null&&hasMore){
                JSONObject metadata = (JSONObject) jsonObj.get("response_metadata");
                cursor = metadata.get("next_cursor").toString();
            }
        }while(hasMore!=null && hasMore);

        return messages;
    }
}

