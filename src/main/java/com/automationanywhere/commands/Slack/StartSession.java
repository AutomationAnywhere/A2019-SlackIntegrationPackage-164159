package com.automationanywhere.commands.Slack;
import static com.automationanywhere.commandsdk.model.AttributeType.CREDENTIAL;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import Utils.HTTPRequest;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;

import java.io.IOException;
import java.util.Map;

import com.automationanywhere.core.security.SecureString;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

@BotCommand

@CommandPkg(
        name = "Start Session", label = "Start Session",
        node_label = "Start Session {{sessionName}}", description = "Enter OAuth token to authenticate with Slack", icon = "SLACK.svg",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7"
        )

public class StartSession {

    @Sessions
    private Map<String, Object> sessions;
    @com.automationanywhere.commandsdk.annotations.GlobalSessionContext
    private com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext;

    public void setGlobalSessionContext(com.automationanywhere.bot.service.GlobalSessionContext globalSessionContext) {
        this.globalSessionContext = globalSessionContext;
    }


    private static final Messages MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.demo.messages");

    @Execute
    public void action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name",  default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName,
            @Idx(index = "2", type = CREDENTIAL) @Pkg(label = "Token") @NotEmpty SecureString token

    ) throws IOException, ParseException {


        if (this.sessions.containsKey(sessionName)){
            throw new BotCommandException(MESSAGES.getString("sessionInUse",sessionName));
        }

        String tokenCheck = token.getInsecureString();

        //Call Auth test API to validate token...
        String url = "https://slack.com/api/auth.test";
        String output = HTTPRequest.Request(url, "POST", tokenCheck);
        Object obj = new JSONParser().parse(output);
        JSONObject jsonObj = (JSONObject) obj;
        String status = jsonObj.get("ok").toString();
        if(status.equals("false")){
            throw new BotCommandException(MESSAGES.getString("authError", jsonObj.get("error").toString()));
        }
        this.sessions.put(sessionName, tokenCheck);
    }
    public void setSessions(Map<String, Object> sessions) {this.sessions = sessions;}
}
