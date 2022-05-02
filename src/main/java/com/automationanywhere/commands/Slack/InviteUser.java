package com.automationanywhere.commands.Slack;

import Utils.HTTPRequest;
import Utils.ParseResponse;
import com.automationanywhere.botcommand.data.Value;
import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import com.automationanywhere.commandsdk.model.AttributeType;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be displayable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "Invite User to Channel", label = "Invite User",
        node_label = "Invite user {{user}} to channel {{channel}} in session {{sessionName}}", description = "Invite User to Channel in Slack", icon = "SLACK.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7",
        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "Assign result (success/fail) to a String variable", return_type = STRING)

public class InviteUser {
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
    public StringValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING,  default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = AttributeType.TEXT) @Pkg(label = "Channel ID", description = "e.g. C018TCFEBQ8") @NotEmpty String channel,
            @Idx(index = "3", type = AttributeType.TEXT) @Pkg(label = "User ID", description = "For multiple users, enter comma-separated list of user IDs e.g. U018301GXBL,U018SQT3E5N") @NotEmpty String user
    ) throws IOException, ParseException {

        if ("".equals(channel.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "channel"));
        }
        if ("".equals(user.trim())) {
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "user name"));
        }
        if (!this.sessions.containsKey(sessionName)){
            throw new BotCommandException(MESSAGES.getString("incorrectSession",sessionName));
        }
        //Retrieve APIKey String that is passed as Session Object
        String token = (String) this.sessions.get(sessionName);
        channel=URLEncoder.encode(channel, StandardCharsets.UTF_8);
        user=URLEncoder.encode(user, StandardCharsets.UTF_8);
        String url = "https://slack.com/api/conversations.invite?channel="+channel+"&users="+user;
        String response = HTTPRequest.Request(url, "POST", token);

        String post = ParseResponse.OutputMessage(response, "User successfully invited to channel");

        return new StringValue(post);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }
}
