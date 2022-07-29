package net.greensill.lw.msg.objects;

public interface Event<T> extends Msg<T> {

    String getEventName();

}
