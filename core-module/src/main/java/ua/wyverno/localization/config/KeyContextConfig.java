package ua.wyverno.localization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyContextConfig {
    @Autowired
    @Value("${key.context.actor}")
    private String contextActorField;
    @Autowired
    @Value("${key.context.context}")
    private String contextField;
    @Autowired
    @Value("${key.context.timing}")
    private String contextTimingField;
    @Autowired
    @Value("${key.context.voice}")
    private String contextVoiceField;
    @Autowired
    @Value("${key.context.dub}")
    private String contextDubField;
    @Autowired
    @Value("${key.context.formatted.tool}")
    private String contextFormattedTool;
    @Autowired
    @Value("${key.context.formatted.tool.url}")
    private String contextFormattedToolURL;

    public String getContextActorField() {
        return contextActorField;
    }

    public String getContextField() {
        return contextField;
    }

    public String getContextTimingField() {
        return contextTimingField;
    }

    public String getContextVoiceField() {
        return contextVoiceField;
    }

    public String getContextDubField() {
        return contextDubField;
    }

    public String getContextFormattedTool() {
        return contextFormattedTool;
    }

    public String getContextFormattedToolURL() {
        return contextFormattedToolURL;
    }
}
