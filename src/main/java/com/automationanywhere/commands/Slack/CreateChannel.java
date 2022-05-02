package com.automationanywhere.commands.Slack;

import Utils.HTTPRequest;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Create Channel in Slack", label = "Create Channel",
        node_label = "Create channel {{channel}} in session {{sessionName}}", description = "Creates a channel in Slack", icon = "SLACK.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign result to a Dictionary Variable", return_type = DataType.DICTIONARY, return_description = "If successful, channel ID is contained at key 'ID'")

public class CreateChannel {
    @Sessions
    private Map<String, Object> sessions;

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public DictionaryValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Channel Name", description = "e.g. mynewchannel - do not include '#', lower case only") @NotEmpty String channel
    ) throws IOException, ParseException {

        if ("".equals(channel.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "channel"));
        }

        if (!this.sessions.containsKey(sessionName)){
            throw new BotCommandException(MESSAGES.getString("incorrectSession",sessionName));
        }
        //Retrieve APIKey String that is passed as Session Object
        String token = (String) this.sessions.get(sessionName);
        Map<String, Value> ResMap = new LinkedHashMap(); //For output
        channel=URLEncoder.encode(channel, StandardCharsets.UTF_8);
        String url = "https://slack.com/api/conversations.create?&name="+channel;
        String response = HTTPRequest.Request(url, "POST", token);
        //Parse JSON response to get result of request
        Object obj = new JSONParser().parse(response);
        JSONObject jsonObj = (JSONObject) obj;
        String result = jsonObj.get("ok").toString();
        //String post;
        if (result.equals("true")){
            JSONObject channelInfo = (JSONObject) jsonObj.get("channel");
            ResMap.put("Success", new StringValue(result));
            ResMap.put("ID", new StringValue(channelInfo.get("id").toString()));
        } else {
            String reason = jsonObj.get("error").toString();
            ResMap.put("Success", new StringValue(result));
            ResMap.put("Error", new StringValue(reason));
        }

        return new DictionaryValue(ResMap);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}

