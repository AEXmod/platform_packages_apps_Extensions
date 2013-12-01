/*
 * Copyright (C) 2017 AospExtended ROM Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aospextended.extensions;

import android.app.ActivityManagerNative;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManagerGlobal;
import android.view.IWindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Locale;
import android.text.TextUtils;
import android.view.View;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.Utils;

public class Notifications extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_LESS_NOTIFICATION_SOUNDS = "less_notification_sounds";

    private ListPreference mAnnoyingNotifications;
    private ListPreference mTickerMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.notifications);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
	
	mTickerMode = (ListPreference) findPreference("ticker_mode");
        mTickerMode.setOnPreferenceChangeListener(this);
        int tickerMode = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_TICKER,
                1, UserHandle.USER_CURRENT);
        mTickerMode.setValue(String.valueOf(tickerMode));
        mTickerMode.setSummary(mTickerMode.getEntry());

	mAnnoyingNotifications = (ListPreference) findPreference(PREF_LESS_NOTIFICATION_SOUNDS);
        int notificationThreshold = Settings.System.getInt(getContentResolver(),
                Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, 0);
        mAnnoyingNotifications.setValue(Integer.toString(notificationThreshold));
        int valueIndex = mAnnoyingNotifications.findIndexOfValue(String.valueOf(notificationThreshold));
        if (valueIndex > 0) {
            mAnnoyingNotifications.setSummary(mAnnoyingNotifications.getEntries()[valueIndex]);
        }
        mAnnoyingNotifications.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
	 ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mAnnoyingNotifications) {
            String notificationThreshold = (String) newValue;
            int notificationThresholdValue = Integer.parseInt(notificationThreshold);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.MUTE_ANNOYING_NOTIFICATIONS_THRESHOLD, notificationThresholdValue);
            int notificationThresholdIndex = mAnnoyingNotifications
                    .findIndexOfValue(notificationThreshold);
            mAnnoyingNotifications
                    .setSummary(mAnnoyingNotifications.getEntries()[notificationThresholdIndex]);
            return true;
        } else if (preference.equals(mTickerMode)) {
            int tickerMode = Integer.parseInt(((String) newValue).toString());
            Settings.System.putIntForUser(getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_TICKER, tickerMode, UserHandle.USER_CURRENT);
            int index = mTickerMode.findIndexOfValue((String) newValue);
            mTickerMode.setSummary(
                    mTickerMode.getEntries()[index]);
            return true;
        }
        return false;
    }
}
