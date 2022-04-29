package com.automationanywhere.commands.Slack;


import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
@CommandPkg(label = "End Session", name = "End Session", description = "Session End", icon = "SLACK.svg", node_label = "End Session {{sessionName}}",
        comment = true ,  text_color = "#7B848B" , background_color =  "#99b3c7")

public class EndSession {

    @Sessions
    private Map<String, Object> sessions;

    @Execute
    public void end(@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName){
        String token = (String) this.sessions.get(sessionName);
        sessions.remove(sessionName);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }

}
