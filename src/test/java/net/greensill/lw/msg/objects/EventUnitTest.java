package net.greensill.lw.msg.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.greensill.lw.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static net.greensill.lw.common.Common.getFormattedInstant;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("UnitTest")
public class EventUnitTest extends AbstractUnitTest {

    public static final String FIRST_VALUE = "first-value";
    public final static Map<String, Object> HEADER_MAP = Collections.singletonMap("first-key", "first-value");
    public static final String KEY_WITH_NULL_VALUE = "nullkey";
    public static final String OTHER_KEY = "otherkey";
    public static final String VALUE = "value";
    public static final String THIRD_KEY = "third-key";
    public static final String THIRD_VALUE = "third-value";
    public static final String SECOND_KEY = "second-key";
    public static final String SECOND_VALUE = "second-value";
    public static final String FIRST_KEY = "first-key";
    public static final String DATE_TIME_STAMP = "2022-07-28T18:19:09.456944Z";
    public static final String PAYLOAD = "payload";

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        super.setUp();
        objectMapper = TestUtils.createJsonMapper();
        log.info("test setup complete");
    }

    @Test
    void testEventMsg() throws JsonProcessingException {
        SomethingHappenedEvent event = new SomethingHappenedEvent(MsgHeader.emptyMsgHeader(),"payload");
        String serializedEvent = objectMapper.writer().writeValueAsString(event);
        log.info("event:{}",event);
        log.info("serializedEvent: {}",serializedEvent);
        SomethingHappenedEvent event2 = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(serializedEvent);
        log.info("deserializedEvent:{}",event2);

    }

    @Test
    public void whenDeserializingDateUsingCustomDeserializer_thenCorrect() throws IOException {

        String json = "{\n" +
                "  \"msgHeader\" : { },\n" +
                "  \"msgPayload\" : \"payload\",\n" +
                "  \"eventCreatedAt\" : \"" + DATE_TIME_STAMP + "\",\n" +
                "  \"msgType\" : \"EVENT\"\n" +
                "}";

        SomethingHappenedEvent event = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(json);
        assertEquals(DATE_TIME_STAMP, getFormattedInstant(event.getEventCreatedAt()));
        assertEquals(MsgHeader.emptyMsgHeader(), event.getMsgHeader());
        assertEquals(PAYLOAD, event.getMsgPayload());
        assertEquals(Msg.MsgType.EVENT, event.getMsgType());
    }

    @Test
    void testAddNullValueToMsgHeader() throws JsonProcessingException {

        MsgHeader msgHeader = MsgHeader.plus(KEY_WITH_NULL_VALUE, null).and(OTHER_KEY, VALUE).and(THIRD_KEY, THIRD_VALUE);
        assertEquals(3, msgHeader.size());
        assertNull(msgHeader.get(KEY_WITH_NULL_VALUE));
        assertEquals(VALUE, msgHeader.get(OTHER_KEY));

        SomethingHappenedEvent event = new SomethingHappenedEvent(msgHeader, PAYLOAD);
        String serialized = objectMapper.writer().writeValueAsString(event);
        log.info("event:{}",event);
        log.info("serialized: {}",serialized);
        SomethingHappenedEvent deserialized = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(serialized);
        log.info("deserialized:{}",deserialized);
        assertNotNull(getFormattedInstant(event.getEventCreatedAt()));
        assertEquals(3, event.getMsgHeader().size());
        assertEquals(msgHeader, event.getMsgHeader());
        assertEquals(PAYLOAD, event.getMsgPayload());
        assertEquals(Msg.MsgType.EVENT, event.getMsgType());
        assert(event.getMsgHeader().containsKey("nullkey"));
        assert(event.getMsgHeader().containsValue(null));
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @JsonPropertyOrder({"msgType", "eventName","eventCreatedAt", "msgHeader", "msgPayload" })
    // see https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
    static class SomethingHappenedEvent implements Event<Object>{

        private final MsgHeader msgHeader;
        private final Object msgPayload;
        @JsonProperty("eventName")
        private final String eventName = "SomethingHappenedEvent";
        private final Instant eventCreatedAt;

        @JsonProperty("msgType")
        private final MsgType msgType = MsgType.EVENT;

        private static Clock clock = Clock.systemUTC();

        @JsonCreator
        public SomethingHappenedEvent(
                @JsonProperty("msgHeader") MsgHeader msgHeader,
                @JsonProperty("msgPayload") Object msgPayload,
                @JsonProperty("eventCreatedAt") Instant eventCreatedAt) {
            this.msgHeader = msgHeader;
            this.msgPayload = msgPayload;
            this.eventCreatedAt = eventCreatedAt;
        }

        public SomethingHappenedEvent(
                @JsonProperty("msgHeader") MsgHeader msgHeader,
                @JsonProperty("msgPayload") Object msgPayload) {
                this(msgHeader, msgPayload,clock.instant());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder().append(" [");
            if (getEventName()!=null) sb.append("eventName=").append(getEventName()).append(", ");
            if (getMsgPayload()!=null) sb.append("msgPayload=").append(getMsgPayload()).append(", ");
            if (getEventCreatedAt() !=null) sb.append("eventCreatedAt=").append(getFormattedInstant(getEventCreatedAt()));
            return sb.append("]").toString();
        }

    }


}
