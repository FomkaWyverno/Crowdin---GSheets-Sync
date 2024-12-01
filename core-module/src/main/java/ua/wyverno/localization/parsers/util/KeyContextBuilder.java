package ua.wyverno.localization.parsers.util;

public class KeyContextBuilder {
    private final String contextActorField;
    private final String contextField;
    private final String contextTimingField;
    private final String contextVoiceField;
    private final String contextDubField;
    private final String contextFormattedTool;
    private final String contextFormattedToolURL;

    private String actor;
    private String context;
    private String timing;
    private String voice;
    private String dub;
    private boolean hasFormattedColumn = false;

    public KeyContextBuilder(KeyContextConfig keyContextConfig) {
        this.contextActorField = keyContextConfig.getContextActorField();
        this.contextField = keyContextConfig.getContextField();
        this.contextTimingField = keyContextConfig.getContextTimingField();
        this.contextVoiceField = keyContextConfig.getContextVoiceField();
        this.contextDubField = keyContextConfig.getContextDubField();
        this.contextFormattedTool = keyContextConfig.getContextFormattedTool();
        this.contextFormattedToolURL = keyContextConfig.getContextFormattedToolURL();
    }

    public KeyContextBuilder actor(String actor) {
        this.actor = actor;
        return this;
    }

    public KeyContextBuilder context(String context) {
        this.context = context;
        return this;
    }

    public KeyContextBuilder timing(String timing) {
        this.timing = timing;
        return this;
    }

    public KeyContextBuilder voice(String voice) {
        this.voice = voice;
        return this;
    }

    public KeyContextBuilder dub(String dub) {
        this.dub = dub;
        return this;
    }

    public KeyContextBuilder hasFormattedColumn(boolean hasFormattedColumn) {
        this.hasFormattedColumn = hasFormattedColumn;
        return this;
    }

    public String build() {
        StringBuilder contextBuilder = new StringBuilder();
        if (this.actor != null) {
            contextBuilder.append(this.contextActorField).append(": ").append(this.actor).append("\n");
        }
        if (this.context != null && !this.context.isEmpty()) {
            contextBuilder.append(this.contextField).append(": ")
                    .append(this.context).append("\n");
        }
        if (this.context != null && !this.context.isEmpty() && this.timing != null && !this.timing.isEmpty()) {
            contextBuilder.append(this.contextTimingField).append(": ")
                    .append(this.timing).append("\n");
        }
        if (this.voice != null && !this.voice.isEmpty()) {
            contextBuilder.append(this.contextVoiceField).append(": ").append(this.voice).append("\n");
        }
        if (this.dub != null && !this.dub.isEmpty()) {
            contextBuilder.append(this.contextDubField).append(": ").append(this.dub).append("\n");
        }
        if (this.hasFormattedColumn) {
            contextBuilder.append(this.contextFormattedTool).append(":\n")
                    .append(this.contextFormattedToolURL);
        }

        return contextBuilder.toString();
    }
}
