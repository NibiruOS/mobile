package org.nibiru.mobile.ios.preferences;

import org.nibiru.mobile.core.api.preferences.Preferences;
import org.nibiru.mobile.core.impl.preferences.AbstractPreferences;

import javax.annotation.Nullable;
import javax.inject.Inject;

import apple.foundation.NSUserDefaults;

public class NSUserDefaultsPreferences extends AbstractPreferences {
    @Inject
    public NSUserDefaultsPreferences() {
    }


    @Override
    public <T> T getParameter(String key) {
        return stringToObject(NSUserDefaults.standardUserDefaults()
                .stringForKey(key));
    }

    @Override
    public Preferences addParameter(String key, @Nullable Object value) {
        NSUserDefaults.standardUserDefaults()
                .setObjectForKey(objectToString(value), key);
        return this;
    }
}
