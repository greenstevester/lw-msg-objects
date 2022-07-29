package net.greensill.lw.msg.objects;

import java.io.Serializable;

public interface Msg<T> extends Serializable {

    enum MsgType {CMD, EVENT, QUERY}

    MsgType getMsgType();

    MsgHeader getMsgHeader();

    T getMsgPayload();

}
