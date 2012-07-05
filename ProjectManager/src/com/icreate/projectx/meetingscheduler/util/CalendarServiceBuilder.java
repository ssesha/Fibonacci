// Copyright 2011 Google Inc. All Rights Reserved.

package com.icreate.projectx.meetingscheduler.util;

import com.google.api.client.extensions.android2.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarRequest;

/**
 * @author alainv
 * 
 */
public class CalendarServiceBuilder {

  /**
   * Builds a Calendar service object.
   * 
   * @param accessToken Access token to use to authorize requests.
   * @return Calendar service object.
   */
  public static Calendar build(String accessToken) {
    HttpTransport transport = AndroidHttp.newCompatibleTransport();
    JacksonFactory jsonFactory = new JacksonFactory();

    GoogleAccessProtectedResource accessProtectedResource =
        new GoogleAccessProtectedResource(accessToken);

    Calendar service =
        Calendar.builder(transport, jsonFactory).setApplicationName("Google-Meeting-Scheduler/1.0")
            .setJsonHttpRequestInitializer(new JsonHttpRequestInitializer() {
              @Override
              public void initialize(JsonHttpRequest request) {
                CalendarRequest calendarRequest = (CalendarRequest) request;

                // TODO: Get an API key from Google's APIs Console:
                // https://code.google.com/apis/console.
                calendarRequest.setKey("AIzaSyCGIXAbwuE0I5yLtVEgwp0mP7YaBiC9HsA");
              }
            }).setHttpRequestInitializer(accessProtectedResource).build();
    return service;
  }
}
