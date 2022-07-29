package net.greensill.lw.msg.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.greensill.lw.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static net.greensill.lw.common.Common.getFormattedInstant;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("UnitTest")
public class EventUnitTest extends AbstractUnitTest {

    public static final String KEY_WITH_NULL_VALUE = "nullkey";
    public static final String OTHER_KEY = "otherkey";
    public static final String VALUE = "value";
    public static final String THIRD_KEY = "third-key";
    public static final String THIRD_VALUE = "third-value";
    public static final String DATE_TIME_STAMP = "2022-07-28T18:19:09.456944Z";
    public static final String PAYLOAD = "payload";

    UUID TEST_UUID = UUID.fromString("48fa9fb7-1177-48fa-8198-0f80f06b100a");

    public final static Map<String, String> MAP = Collections.singletonMap("first-key", "first-value");

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        super.setUp();
        objectMapper = TestUtils.createJsonMapper();
        log.info("test setup complete");
    }

    @Test
    void testEventMsg() throws JsonProcessingException {
        SomePayload somePayload = getPayload();
        SomethingHappenedEvent event = new SomethingHappenedEvent(MsgHeader.emptyMsgHeader(),somePayload);
        String serialized = objectMapper.writer().writeValueAsString(event);
        log.info("event:{}",event);
        log.info("serialized: {}",serialized);
        SomethingHappenedEvent deserialized = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(serialized);
        log.info("deserialized:{}",deserialized);


    }

    @Test
    public void whenDeserializingDateUsingCustomDeserializer_thenCorrect() throws IOException {

        String json = "{\n" +
                "  \"msgType\" : \"EVENT\",\n" +
                "  \"eventName\" : \"SomethingHappenedEvent\",\n" +
                "  \"eventCreatedAt\" : \"2022-07-29T12:45:40.005592Z\",\n" +
                "  \"msgHeader\" : { },\n" +
                "  \"msgPayload\" : {\n" +
                "    \"uuid\" : \"48fa9fb7-1177-48fa-8198-0f80f06b100a\",\n" +
                "    \"intValue\" : 1,\n" +
                "    \"stringValue\" : \"payload\",\n" +
                "    \"stringMap\" : {\n" +
                "      \"first-key\" : \"first-value\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        SomethingHappenedEvent event = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(json);
        assertEquals("2022-07-29T12:45:40.005592Z", getFormattedInstant(event.getEventCreatedAt()));
        assertEquals(MsgHeader.emptyMsgHeader(), event.getMsgHeader());
        assertEquals(getPayload(), event.getMsgPayload());
        assertEquals(Msg.MsgType.EVENT, event.getMsgType());
    }

    @Test
    void testAddNullValueToMsgHeader() throws JsonProcessingException {

        MsgHeader msgHeader = MsgHeader.plus(KEY_WITH_NULL_VALUE, null).and(OTHER_KEY, VALUE).and(THIRD_KEY, THIRD_VALUE);
        assertEquals(3, msgHeader.size());
        assertNull(msgHeader.get(KEY_WITH_NULL_VALUE));
        assertEquals(VALUE, msgHeader.get(OTHER_KEY));

        SomethingHappenedEvent event = new SomethingHappenedEvent(msgHeader, getPayload());
        String serialized = objectMapper.writer().writeValueAsString(event);
        log.info("event:{}",event);
        log.info("serialized: {}",serialized);
        SomethingHappenedEvent deserialized = objectMapper.readerFor(SomethingHappenedEvent.class).readValue(serialized);
        log.info("deserialized:{}",deserialized);
        assertNotNull(getFormattedInstant(event.getEventCreatedAt()));
        assertEquals(3, event.getMsgHeader().size());
        assertEquals(msgHeader, event.getMsgHeader());
        assertEquals(getPayload(), event.getMsgPayload());
        assertEquals(Msg.MsgType.EVENT, event.getMsgType());
        assert(event.getMsgHeader().containsKey("nullkey"));
        assert(event.getMsgHeader().containsValue(null));
    }

    private SomePayload getPayload() {
        return new SomePayload(TEST_UUID, 1, PAYLOAD, MAP);
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    // see https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
    static class SomethingHappenedEvent extends AbstractEvent<SomePayload> {

        @JsonProperty("eventName")
        private final String eventName = "SomethingHappenedEvent";

        @JsonProperty("msgType")
        private final MsgType msgType = MsgType.EVENT;

        private static Clock clock = Clock.systemUTC();

        @JsonCreator
        public SomethingHappenedEvent(
                @JsonProperty("msgHeader") MsgHeader msgHeader,
                @JsonProperty("msgPayload") SomePayload msgPayload,
                @JsonProperty("eventCreatedAt") Instant eventCreatedAt) {
            super(msgHeader, msgPayload,eventCreatedAt);
        }

        public SomethingHappenedEvent(
                @JsonProperty("msgHeader") MsgHeader msgHeader,
                @JsonProperty("msgPayload") SomePayload msgPayload) {
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

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode()
    static class SomePayload implements Serializable {

        private final UUID uuid;
        private final Integer intValue;
        private final String stringValue;
        private final Map<String,String> stringMap;

        @JsonCreator
        public SomePayload(
                @JsonProperty("uuid") UUID uuid,
                @JsonProperty("intValue") Integer intValue,
                @JsonProperty("stringValue") String stringValue,
                @JsonProperty("stringMap") Map<String,String> stringMap) {

            this.uuid = uuid;
            this.intValue = intValue;
            this.stringValue = stringValue;
            this.stringMap = stringMap;
        }

    }

}
