package ar.com.oxen.nibiru.mobile.sample.app.ui;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import ar.com.oxen.nibiru.mobile.core.api.business.security.Profile;
import ar.com.oxen.nibiru.mobile.core.api.event.Event;
import ar.com.oxen.nibiru.mobile.core.api.event.EventBus;
import ar.com.oxen.nibiru.mobile.core.api.event.EventHandler;
import ar.com.oxen.nibiru.mobile.core.api.geolocation.GeolocationManager;
import ar.com.oxen.nibiru.mobile.core.api.geolocation.Position;
import ar.com.oxen.nibiru.mobile.core.api.handler.HandlerRegistration;
import ar.com.oxen.nibiru.mobile.core.api.ui.AlertManager;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.ClickHandler;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.HasClickHandler;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.TakesValue;
import ar.com.oxen.nibiru.mobile.core.api.ui.mvp.View;
import ar.com.oxen.nibiru.mobile.core.api.ui.place.Place;
import ar.com.oxen.nibiru.mobile.core.api.ui.place.PlaceManager;
import ar.com.oxen.nibiru.mobile.core.impl.mvp.BasePresenter;
import ar.com.oxen.nibiru.mobile.sample.app.ui.SamplePresenter.Display;

public class SamplePresenter extends BasePresenter<Display> {
	public interface Display extends View {
		HasClickHandler getAlertTrigger();

		HasClickHandler getGreetingTrigger();

		TakesValue<String> getNameLoad();

		TakesValue<String> getGreetingDisplay();

		HasClickHandler getBackTrigger();

		HasClickHandler getShowLocation();
	}

	private final GeolocationManager geolocationManager;
	private final EventBus eventBus;
	private final Profile profile;
	private final PlaceManager placeManager;
	private final SampleMessages messages;
	private HandlerRegistration handlerRegistration;

	@Inject
	public SamplePresenter(Display view, AlertManager alertManager,
			GeolocationManager geolocationManager, EventBus eventBus,
			Profile profile, PlaceManager placeManager, SampleMessages messages) {
		super(view, alertManager);
		this.geolocationManager = checkNotNull(geolocationManager);
		this.eventBus = checkNotNull(eventBus);
		this.profile = checkNotNull(profile);
		this.placeManager = checkNotNull(placeManager);
		this.messages = checkNotNull(messages);
	}

	@Override
	public void go(Place place) {
		getView().getGreetingDisplay().setValue(
				messages.hi(profile.getUsername()));

		getView().getAlertTrigger().setClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				String message = messages.testMessage(profile.getFirstName(),
						profile.getLastName());
				eventBus.createEvent("showAlert")
						.addParameter("message", message).fire();
			}
		});

		handlerRegistration = eventBus.addHandler("showAlert",
				new EventHandler() {

					@Override
					public void onEvent(Event event) {
						String message = event.getParameter("message");
						getAlertManager().showMessage(message);
					}
				});

		getView().getGreetingTrigger().setClickHandler(new ClickHandler() {

			@Override
			public void onClick() {
				String message = messages
						.hi(getView().getNameLoad().getValue());
				getView().getGreetingDisplay().setValue(message);
			}
		});

		getView().getBackTrigger().setClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				placeManager.back();
			}
		});

		getView().getShowLocation().setClickHandler(new ClickHandler() {
			@Override
			public void onClick() {
				geolocationManager.watchPosition(new Cbk<Position>() {

					@Override
					public void onSuccess(Position result) {
						getView().getGreetingDisplay()
								.setValue(
										result.getCoordinates().getLongitude()
												+ ","
												+ result.getCoordinates()
														.getLatitude());
					}
				});
			}
		});
	}

	@Override
	public void onStop() {
		handlerRegistration.removeHandler();
	}
}
