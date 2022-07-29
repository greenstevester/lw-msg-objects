package net.greensill.lw.msg.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.time.Instant;

import static net.greensill.lw.common.Common.getFormattedInstant;

@Getter
@Setter
@EqualsAndHashCode
@JsonPropertyOrder({"msgType", "eventName", "eventCreatedAt", "msgHeader", "msgPayload"})
public abstract class AbstractEvent<T> implements Event<T> {

    private final MsgHeader msgHeader;
    private final T msgPayload;

    private final Instant eventCreatedAt;

    @JsonProperty("msgType")
    private final MsgType msgType = MsgType.EVENT;

    private static Clock clock = Clock.systemUTC();

    @JsonCreator
    public AbstractEvent(
            @JsonProperty("msgHeader") MsgHeader msgHeader,
            @JsonProperty("msgPayload") T msgPayload,
            @JsonProperty("eventCreatedAt") Instant eventCreatedAt) {
        this.msgHeader = msgHeader;
        this.msgPayload = msgPayload;
        this.eventCreatedAt = eventCreatedAt;
    }

    public AbstractEvent(
            @JsonProperty("msgHeader") MsgHeader msgHeader,
            @JsonProperty("msgPayload") T msgPayload) {
        this(msgHeader, msgPayload, clock.instant());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(" [");
        if (getEventName() != null) sb.append("eventName=").append(getEventName()).append(", ");
        if (getMsgPayload() != null) sb.append("msgPayload=").append(getMsgPayload()).append(", ");
        if (getEventCreatedAt() != null) sb.append("eventCreatedAt=").append(getFormattedInstant(getEventCreatedAt()));
        return sb.append("]").toString();
    }

    @Override
    public abstract String getEventName();

}
