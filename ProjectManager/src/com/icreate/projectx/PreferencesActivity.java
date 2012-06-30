// Copyright 2011 Google Inc. All Rights Reserved.

package com.icreate.projectx;

import android.accounts.Account;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.icreate.projectx.R;
import com.icreate.projectx.datamodel.Constants;
import com.icreate.projectx.meetingscheduler.util.OAuthManager;
import com.icreate.projectx.meetingscheduler.util.OAuthManager.AuthHandler;

/**
 * Activity showing the user's preferences.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class PreferencesActivity extends PreferenceActivity {
	/** Sub-dialog IDs. */
	private final int WORKING_HOURS_START_ID = 0;
	private final int WORKING_HOURS_END_ID = 1;

	private SharedPreferences preferences;

	/**
	 * Returns an Intent that will display this Activity.
	 * 
	 * @param context
	 *            The application Context
	 * @return An intent that will display this Activity
	 */
	public static Intent createViewIntent(Context context) {
		Intent intent = new Intent(context, PreferencesActivity.class);
		intent.setClass(context, PreferencesActivity.class);
		return intent;
	}

	/**
	 * Initialize this activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		preferences = PreferenceManager.getDefaultSharedPreferences(this);

		final Preference selectedAccountPref = getPreferenceScreen().findPreference(Constants.SELECTED_ACCOUNT_PREFERENCE);
		String selectedAccount = preferences.getString(Constants.SELECTED_ACCOUNT_PREFERENCE, null);

		if (selectedAccount != null && selectedAccount.length() > 0) {
			selectedAccountPref.setSummary(selectedAccount);
		}
		selectedAccountPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				OAuthManager.getInstance().doLogin("", true, PreferencesActivity.this, new AuthHandler() {
					@Override
					public void handleAuth(Account account, String authToken) {
						if (account != null) {
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this).edit();
							editor.putString(Constants.SELECTED_ACCOUNT_PREFERENCE, account.name);
							editor.commit();
							selectedAccountPref.setSummary(account.name);
						}
					}
				});
				return true;
			}
		});

		setWorkingHoursDialog(Constants.WORKING_HOURS_START_PREFERENCE, WORKING_HOURS_START_ID);
		setWorkingHoursDialog(Constants.WORKING_HOURS_END_PREFERENCE, WORKING_HOURS_END_ID);
	}

	/**
	 * Called when showDialog is called to create and show TimePickerDialogs.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case WORKING_HOURS_START_ID:
			return createTimePickerDialog(Constants.WORKING_HOURS_START_PREFERENCE, getString(R.string.working_hours_start_default_value));
		case WORKING_HOURS_END_ID:
			return createTimePickerDialog(Constants.WORKING_HOURS_END_PREFERENCE, getString(R.string.working_hours_end_default_value));
		}
		return null;
	}

	/**
	 * Set working hour start & end preferences click action.
	 * 
	 * @param key
	 *            Preference's key.
	 * @param dialogID
	 *            Preference dialog's ID.
	 */
	private void setWorkingHoursDialog(String key, final int dialogID) {
		getPreferenceScreen().findPreference(key).setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(dialogID);
				return true;
			}
		});
	}

	/**
	 * Create a time picker dialog to select working hours preference.
	 * 
	 * @param key
	 *            Preference key.
	 * @param defaultValue
	 *            Default value to use.
	 * @return TimePickerDialog.
	 */
	private Dialog createTimePickerDialog(final String key, String defaultValue) {
		String workingHoursString = preferences.getString(key, defaultValue);
		String[] workingHours = workingHoursString.split(":");

		return new TimePickerDialog(this, new OnTimeSetListener() {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(PreferencesActivity.this).edit();
				editor.putString(key, hourOfDay + ":" + minute);
				editor.commit();
			}
		}, Integer.parseInt(workingHours[0]), Integer.parseInt(workingHours[1]), DateFormat.is24HourFormat(this));
	}
}
