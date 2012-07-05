/*
 * Copyright (c) 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.icreate.projectx.meetingscheduler.util;

import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Freebusy;
import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.icreate.projectx.meetingscheduler.model.Constants;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retrieves the busy times from the Google Calendar API.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class FreeBusyTimesRetriever {

  private Calendar service;

  public FreeBusyTimesRetriever(String accessToken) {
    this.service = CalendarServiceBuilder.build(accessToken);
  }

  /**
   * Constructor.
   */
  public FreeBusyTimesRetriever(Calendar service) {
    this.service = service;
  }

  /**
   * Get busy times from the Calendar API.
   * 
   * @param attendees Attendees to retrieve busy times for.
   * @param startDate Start date to retrieve busy times from.
   * @param timeSpan Number of days to retrieve busy times for.
   * @return Busy times for the selected attendees.
   * @throws IOException
   */
  public Map<String, List<TimePeriod>> getBusyTimes(List<String> attendees, Date startDate,
      int timeSpan) throws IOException {
    Map<String, List<TimePeriod>> result = new HashMap<String, List<TimePeriod>>();
    List<FreeBusyRequestItem> requestItems = new ArrayList<FreeBusyRequestItem>();
    FreeBusyRequest request = new FreeBusyRequest();

    request.setTimeMin(getDateTime(startDate, 0));
    request.setTimeMax(getDateTime(startDate, timeSpan));

    for (String attendee : attendees) {
    	Log.d("attendee", attendee);
      requestItems.add(new FreeBusyRequestItem().setId(attendee));
    }
    request.setItems(requestItems);

    FreeBusyResponse busyTimes;
    try {
      Freebusy.Query query = service.freebusy().query(request);
      // Use partial GET to only retrieve needed fields.
      query.setFields("calendars");
      Log.d("query", query.toString());
      busyTimes = query.execute();
      Log.d("FreeBusy Response", busyTimes.toString());
      for (Map.Entry<String, FreeBusyCalendar> busyCalendar : busyTimes.getCalendars().entrySet()) {
        result.put(busyCalendar.getKey(), busyCalendar.getValue().getBusy());
      }
      Log.d("result", result.toString());
    } catch (IOException e) {
      Log.e(Constants.TAG, "Exception occured while retrieving busy times: " + e.toString());
      if (e instanceof HttpResponseException) {
    	  HttpResponseException exceptionResponse = (HttpResponseException) e;
			String response = exceptionResponse.getMessage();
			int statusCode = exceptionResponse.getStatusCode();
        if (statusCode == 401) {
          // The token might have expired, throw the exception to let calling
          // Activity know.
          throw e;
        }
      }
    }

    return result;
  }

  /**
   * Create a new DateTime object initialized at the current day +
   * {@code daysToAdd}.
   * 
   * @param startDate The date from which to compute the DateTime.
   * @param daysToAdd The number of days to add to the result.
   * 
   * @return The new DateTime object initialized at the current day +
   *         {@code daysToAdd}.
   */
  private DateTime getDateTime(Date startDate, int daysToAdd) {
    java.util.Calendar date = new GregorianCalendar();
    date.setTime(startDate);
    date.add(java.util.Calendar.DAY_OF_YEAR, daysToAdd);
    return new DateTime(date.getTime().getTime(), 0);
  }

}
