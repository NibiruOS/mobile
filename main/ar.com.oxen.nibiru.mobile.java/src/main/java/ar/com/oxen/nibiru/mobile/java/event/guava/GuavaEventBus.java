package ar.com.oxen.nibiru.mobile.java.event.guava;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import ar.com.oxen.nibiru.mobile.core.api.event.Event;
import ar.com.oxen.nibiru.mobile.core.api.event.EventHandler;
import ar.com.oxen.nibiru.mobile.core.api.handler.HandlerRegistration;

import com.google.common.eventbus.EventBus;

public class GuavaEventBus implements
		ar.com.oxen.nibiru.mobile.core.api.event.EventBus {
	private final EventBus eventBus;

	@Inject
	public GuavaEventBus(EventBus eventBus) {
		this.eventBus = checkNotNull(eventBus);
	}

	@Override
	public Event createEvent(String id) {
		checkNotNull(id);
		return new GuavaEvent(id, eventBus);
	}

	@Override
	public Event createEvent(Enum<?> id) {
		checkNotNull(id);
		return createEvent(id.toString());
	}

	@Override
	public HandlerRegistration addHandler(String eventId, EventHandler handler) {
		//eventBus.register(object);
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HandlerRegistration addHandler(Enum<?> eventId, EventHandler handler) {
		checkNotNull(eventId);
		checkNotNull(handler);
		return addHandler(eventId.toString(), handler);
	}

}
