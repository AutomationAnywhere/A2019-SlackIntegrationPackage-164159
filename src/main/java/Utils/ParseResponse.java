package Utils;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.impl.TableValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.table.Row;
import com.automationanywhere.botcommand.data.model.table.Table;
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

    public static TableValue MessageHistory (String token, String channel) throws ParseException, IOException {
        Table msgTable = new Table();
        List<Row> msgTableRows = new ArrayList<>();
        List<Schema> msgTableSchema = new ArrayList<>();
        msgTableSchema.add(new Schema("Message Text"));
        msgTableSchema.add(new Schema("Message Timestamp"));
        msgTableSchema.add(new Schema("Message Type"));

        Boolean hasMore = null;
        String cursor = "";
        do {
            String url = "https://slack.com/api/conversations.history?channel="+channel+"&cursor="+cursor;
            String response = HTTPRequest.Request(url, "GET", token);
            Object obj = new JSONParser().parse(response);
            JSONObject jsonObj = (JSONObject) obj;
            String result = jsonObj.get("ok").toString();
            List<Value> messageRow = new ArrayList<>();
            if(result.equals("false")){
                String reason = jsonObj.get("error").toString();
                messageRow.add(new StringValue("Action failed with error reason " + reason));
                messageRow.add(new StringValue(GetCurrentTime.secondsSince1970().toString()));
                messageRow.add(new StringValue("Error"));

                msgTableRows.add(new Row(messageRow));
            }
            else if(result.equals("true")) {
                hasMore = (Boolean) jsonObj.get("has_more");
                JSONArray messagesList = (JSONArray) jsonObj.get("messages");
                List<Row> currentPageRows = getMessages(messagesList);

                msgTableRows.addAll(currentPageRows);
            }
            if(hasMore!=null && hasMore){
                JSONObject metadata = (JSONObject) jsonObj.get("response_metadata");
                cursor = (String) metadata.get("next_cursor");
            }
        }while(hasMore!=null && hasMore);

        msgTable.setRows(msgTableRows);
        msgTable.setSchema(msgTableSchema);
        return new TableValue(msgTable);
    }

    public static List<Row> getMessages(JSONArray messagesList){
        List<Row> currentPageRows = new ArrayList<>();

        for (Object currentMessageObject : messagesList) {
            List<Value> currentMessageRow = new ArrayList<>();
            JSONObject currentMessage = (JSONObject) currentMessageObject;

            String currentText = currentMessage.get("text").toString();
            String currentTimestamp = currentMessage.get("ts").toString();
            String currentMsgType = "";
            if (currentMessage.get("files") != null &&
                    "".equals(currentText)) {
                //currentMessageRow.add(new StringValue("Message includes file attachment with no text"));
                currentMessageRow.add(new StringValue(currentText));
                currentMsgType="File";
            }
            else if ("".equals(currentText)) {
                //currentMessageRow.add(new StringValue("No text found in message"));
                currentMessageRow.add(new StringValue(currentText));
                currentMsgType="Empty Text";
            }
            else {
                currentMessageRow.add(new StringValue(currentText));
                currentMsgType="Text";
            }
            currentMessageRow.add(new StringValue(currentTimestamp));
            currentMessageRow.add(new StringValue(currentMsgType));
            Row currentRow = new Row(currentMessageRow);
            currentPageRows.add(currentRow);
        }

        return currentPageRows;
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

