package net.greensill.lw.msg.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.greensill.lw.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@Tag("UnitTest")
public class MsgHeaderUnitTest extends AbstractUnitTest {

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

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        super.setUp();
        objectMapper = TestUtils.createJsonMapper();
        log.info("test setup complete");
    }

    @Test
    void testMsgEmptyHeader() {

        MsgHeader msgHeader = MsgHeader.emptyMsgHeader();
        assertEquals(0, msgHeader.size());
        log.info("emptyMsgHeader:{}",msgHeader);
    }

    @Test
    void testMsgHeader() {

        MsgHeader msgHeader = new MsgHeader(HEADER_MAP);
        assertEquals(1, msgHeader.size());
        assertEquals(FIRST_VALUE, msgHeader.get(FIRST_KEY));
        log.info("msgheader:{}",msgHeader);
    }

    @Test
    void testMsgHeaderException() {

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            MsgHeader msgHeader1 = new MsgHeader(HEADER_MAP);
            msgHeader1.put(SECOND_KEY, SECOND_VALUE);
        });

        assertEquals(MsgHeader.MSG_HEADER_IS_IMMUTABLE,exception.getMessage());

    }

    @Test
    void testAddNullValueToMsgHeader() throws JsonProcessingException {

        MsgHeader msgHeader = MsgHeader.plus(KEY_WITH_NULL_VALUE, null).and(OTHER_KEY, VALUE).and(THIRD_KEY, THIRD_VALUE);
        assertEquals(3, msgHeader.size());
        assertNull(msgHeader.get(KEY_WITH_NULL_VALUE));
        assertEquals(VALUE, msgHeader.get(OTHER_KEY));

        String serialized = objectMapper.writer().writeValueAsString(msgHeader);
        log.info("serialized: {}",serialized);
        assert(serialized.contains("nullkey"));
    }


}
