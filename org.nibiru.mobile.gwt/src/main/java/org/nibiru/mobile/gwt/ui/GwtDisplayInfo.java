package org.nibiru.mobile.gwt.ui;

import org.nibiru.mobile.core.api.ui.DisplayInfo;

import javax.inject.Inject;

public class GwtDisplayInfo implements DisplayInfo {
	@Inject
	public GwtDisplayInfo() {
	}

	@Override
	public native boolean isSmall() /*-{
		return /Android|webOS|iPhone|iPod|BlackBerry|Windows Phone|bada|Bada/i.test(navigator.userAgent)
	}-*/; 
}
