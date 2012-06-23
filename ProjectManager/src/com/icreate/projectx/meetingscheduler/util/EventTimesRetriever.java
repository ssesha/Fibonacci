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

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.TimePeriod;
import com.icreate.projectx.datamodel.AvailableMeetingTime;
import com.icreate.projectx.datamodel.Constants;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Compute the common free times from the busy times fetched from the
 * BusyTimesRetriever.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class EventTimesRetriever {

  /**
   * The BusyTimesRetriever from which to retrieve the busy time.
   */
  private FreeBusyTimesRetriever busyTimeRetriever;

  /**
   * @return the busyTimeRetriever
   */
  public FreeBusyTimesRetriever getBusyTimeRetriever() {
    return busyTimeRetriever;
  }

  /**
   * @param busyTimeRetriever the busyTimeRetriever to set
   */
  public void setBusyTimeRetriever(FreeBusyTimesRetriever busyTimeRetriever) {
    this.busyTimeRetriever = busyTimeRetriever;
  }

  /**
   * @return the timeSpan
   */
  public int getTimeSpan() {
    return timeSpan;
  }

  /**
   * @param timeSpan the timeSpan to set
   */
  public void setTimeSpan(int timeSpan) {
    this.timeSpan = timeSpan;
  }

  /**
   * @return the useWorkingHours
   */
  public boolean isUseWorkingHours() {
    return useWorkingHours;
  }

  /**
   * @param useWorkingHours the useWorkingHours to set
   */
  public void setUseWorkingHours(boolean useWorkingHours) {
    this.useWorkingHours = useWorkingHours;
  }

  /**
   * @return the skipWeekEnds
   */
  public boolean isSkipWeekEnds() {
    return skipWeekEnds;
  }

  /**
   * @param skipWeekEnds the skipWeekEnds to set
   */
  public void setSkipWeekEnds(boolean skipWeekEnds) {
    this.skipWeekEnds = skipWeekEnds;
  }

  /**
   * @return the workingHoursStart
   */
  public Calendar getWorkingHoursStart() {
    return workingHoursStart;
  }

  /**
   * @param workingHoursStart the workingHoursStart to set
   */
  public void setWorkingHoursStart(Calendar workingHoursStart) {
    this.workingHoursStart = workingHoursStart;
  }

  /**
   * @return the workingHoursEnd
   */
  public Calendar getWorkingHoursEnd() {
    return workingHoursEnd;
  }

  /**
   * @param workingHoursEnd the workingHoursEnd to set
   */
  public void setWorkingHoursEnd(Calendar workingHoursEnd) {
    this.workingHoursEnd = workingHoursEnd;
  }

  private int timeSpan;
  private boolean useWorkingHours;
  private boolean skipWeekEnds;
  private Calendar workingHoursStart;
  private Calendar workingHoursEnd;

  /**
   * Constructor.
   * 
   * @param busyTimeRetriever The BusyTimesRetriever to use for fetching busy
   *        times.
   */
  public EventTimesRetriever(FreeBusyTimesRetriever busyTimeRetriever) {
    this.busyTimeRetriever = busyTimeRetriever;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.android.apps.meetingscheduler.EventTimeRetriever#
   * getAvailableMeetingTime(java.util.List,
   * com.google.android.apps.meetingscheduler.Settings)
   */
  public List<AvailableMeetingTime> getAvailableMeetingTime(List<String> attendees, Date startDate,
      int timeSpan, int meetingLength) throws IOException {
    Log.d(Constants.TAG, "Retrieving busy times from " + startDate.toString() + " for " + timeSpan
        + " days.");
    Map<String, List<TimePeriod>> busyTimes =
        busyTimeRetriever.getBusyTimes(attendees, startDate, timeSpan);
    Log.d(Constants.TAG, "Cleaning busy times from " + startDate.toString() + " for " + timeSpan
        + " days.");
    List<TimePeriod> listBusyTimes = cleanBusyTimes(busyTimes, startDate, timeSpan);
    Log.d(Constants.TAG, "Cleaned busy times: " + listBusyTimes.size());
    List<AvailableMeetingTime> result = findAvailableMeetings(listBusyTimes);
    Log.d(Constants.TAG, "result: " + result.size());


    filterMeetingLength(result, meetingLength);
    splitAvailableMeetings(result);

    return result;
  }

  /**
   * Add weekends and non-working hours as busy times if requested and merge all
   * the busy times.
   * 
   * @param busyTimes The busy times to clean
   * @param startDate The date from which to start cleaning
   * @param timeSpan Number of days from startDate to search for available times
   * 
   * @return A list of cleaned busy times.
   */
  private List<TimePeriod> cleanBusyTimes(Map<String, List<TimePeriod>> busyTimes, Date startDate,
      int timeSpan) {
    List<TimePeriod> listBusyTimes = new ArrayList<TimePeriod>();

    for (List<TimePeriod> busy : busyTimes.values()) {
      listBusyTimes.addAll(busy);
    }

    addStartAndEndDates(listBusyTimes, startDate, timeSpan);

    if (this.skipWeekEnds) {
      addWeekends(listBusyTimes, startDate, timeSpan);
    }
    if (this.useWorkingHours) {
      addWorkingHours(listBusyTimes, startDate, timeSpan);
    }

    mergeBusyTimes(listBusyTimes);
    return listBusyTimes;
  }

  /**
   * Add start and end dates as busy times for boundary.
   * 
   * @param listBusyTimes List of busy times to add start and end to.
   * @param startDate Start date.
   * @param timeSpan Time span.
   */
  private void addStartAndEndDates(List<TimePeriod> listBusyTimes, Date startDate, int timeSpan) {
    Calendar endDate = new GregorianCalendar();

    endDate.setTime(startDate);
    endDate.add(Calendar.DAY_OF_YEAR, timeSpan);

    listBusyTimes.add(new TimePeriod().setStart(new DateTime(startDate.getTime(), 0)).setEnd(
        new DateTime(startDate.getTime(), 0)));
    listBusyTimes.add(new TimePeriod().setStart(new DateTime(endDate.getTime().getTime(), 0))
        .setEnd(new DateTime(endDate.getTime().getTime(), 0)));
  }

  /**
   * Add weekends as busy times to the list of busy times.
   * 
   * @param busyTimes The list of busy times to which to add the weekends
   * @param startDate The start date from which
   * @param timeSpan
   */
  private void addWeekends(List<TimePeriod> busyTimes, Date startDate, int timeSpan) {
    Calendar date = new GregorianCalendar(TimeZone.getDefault());

    date.setTime(startDate);

    for (int i = 0; i < timeSpan; ++i) {
      int day = date.get(Calendar.DAY_OF_WEEK);

      if (day == Calendar.SATURDAY || day == Calendar.SUNDAY) {
        TimePeriod toAdd = new TimePeriod();

        DateUtils.setTime(date, 0, 0, 0, 0);
        toAdd.setStart(new DateTime(date.getTime()));
        DateUtils.setTime(date, 23, 59, 59, 999);
        toAdd.setEnd(new DateTime(date.getTime()));
        busyTimes.add(toAdd);
      }
      date.add(Calendar.DAY_OF_YEAR, 1);
    }
  }

  /**
   * Add non-working hours as busy times to the list of busy times.
   * 
   * @param busyTimes The busy times to which to add the non-working hours
   * @param startDate The start date from which to start adding busy times
   * @param timeSpan The number of day for which to add busy times
   */
  private void addWorkingHours(List<TimePeriod> busyTimes, Date startDate, int timeSpan) {
    Calendar current = new GregorianCalendar(TimeZone.getDefault());

    current.setTime(startDate);

    DateUtils.setTime(current, this.workingHoursStart);

    if (current.getTime().after(startDate)) {
      TimePeriod toAdd = new TimePeriod();

      toAdd.setStart(new DateTime(startDate.getTime()));
      toAdd.setEnd(new DateTime(current.getTime()));
      busyTimes.add(toAdd);
    }

    for (int i = 0; i < timeSpan; ++i) {
      DateUtils.setTime(current, this.workingHoursEnd);
      TimePeriod toAdd = new TimePeriod();

      toAdd.setStart(new DateTime(current.getTime()));
      current.add(Calendar.DAY_OF_YEAR, 1);
      DateUtils.setTime(current, this.workingHoursStart);
      toAdd.setEnd(new DateTime(current.getTime()));
      busyTimes.add(toAdd);
    }
  }

  /**
   * Merge the overlapping busy times, e.g 9:00-10:00 and 10:00-12:00 will
   * become one 9:00-12:00 busy time.
   * 
   * @param busyTimes The busy times to merge.
   */
  private void mergeBusyTimes(List<TimePeriod> busyTimes) {
    sortBusyTime(busyTimes);

    // Merge every busy slots.
    for (int i = 0; i < busyTimes.size(); ++i) {
      TimePeriod current = busyTimes.get(i);

      for (int j = i + 1; j < busyTimes.size();) {
        TimePeriod next = busyTimes.get(j);

        if (current.getEnd().getValue() - next.getStart().getValue() >= 0) {
          if (current.getEnd().getValue() - next.getEnd().getValue() < 0) {
            current.setEnd(next.getEnd());
          }
          busyTimes.remove(j);
        } else {
          break;
        }
      }
    }
  }

  /**
   * Find the available meetings from the list of busy times. The busy times are
   * considered to be on the same day, sorted and merged.
   * 
   * @param busyTimes The busy times from which to compute the available meeting
   * @return The available meetings time from 00:00 to 23:59 of the same day.
   */
  private List<AvailableMeetingTime> findAvailableMeetings(List<TimePeriod> busyTimes) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();

    for (int i = 0; i < busyTimes.size() - 1;) {
      AvailableMeetingTime tmp = new AvailableMeetingTime();

      tmp.start = new Date(busyTimes.get(i).getEnd().getValue());
      tmp.end = new Date(busyTimes.get(++i).getStart().getValue());
      result.add(tmp);
    }

    return result;
  }

  /**
   * Sort a list of busy times by start time.
   * 
   * @param busyTime The list of busy times to sort.
   */
  private void sortBusyTime(List<TimePeriod> busyTime) {
    Collections.sort(busyTime, new Comparator<TimePeriod>() {
      @Override
      public int compare(TimePeriod lhs, TimePeriod rhs) {
        long compare = lhs.getStart().getValue() - rhs.getStart().getValue();
        if (compare == 0) {
          compare = lhs.getEnd().getValue() - rhs.getEnd().getValue();
        }
        return (int) compare;
      }
    });
  }

  /**
   * Split multiple day-meeting times into multiple one-day meeting times.
   * 
   * @param meetings The busy times to clean.
   */
  private void splitAvailableMeetings(List<AvailableMeetingTime> meetings) {
    for (int i = 0; i < meetings.size();) {
      AvailableMeetingTime current = meetings.get(i);

      if (!DateUtils.isSameDay(current.start, current.end)) {
        List<AvailableMeetingTime> splitted = splitMeetingTimes(current.start, current.end);

        meetings.remove(i);
        meetings.addAll(i, splitted);
        i += splitted.size();
      } else
        ++i;
    }
  }

  /**
   * Split a busy time into a set of busy time, each for one day.
   * 
   * @param startDate Start date to start splitting busy times from.
   * @param endDate End date to split busy times until.
   * @return Splitted busy times.
   */
  private List<AvailableMeetingTime> splitMeetingTimes(Date startDate, Date endDate) {
    List<AvailableMeetingTime> result = new ArrayList<AvailableMeetingTime>();
    Calendar currentDay = new GregorianCalendar();

    currentDay.setTime(startDate);
    DateUtils.setTime(currentDay, 23, 59, 59, 999);

    result.add(new AvailableMeetingTime(startDate, currentDay.getTime()));

    while (true) {
      DateUtils.setTime(currentDay, 0, 0, 0, 0);
      currentDay.add(Calendar.DAY_OF_YEAR, 1);
      Date currentStart = currentDay.getTime();

      if (DateUtils.isSameDay(currentStart, endDate)) {
        break;
      }

      DateUtils.setTime(currentDay, 23, 59, 59, 999);
      result.add(new AvailableMeetingTime(currentStart, currentDay.getTime()));
    }

    result.add(new AvailableMeetingTime(currentDay.getTime(), endDate));

    return result;
  }

  /**
   * Filter the meetings which length are less than {@code length}.
   * 
   * @param meetings The meetings to filter.
   * @param length The minimum length of the meetings.
   */
  private void filterMeetingLength(List<AvailableMeetingTime> meetings, int length) {
    for (int i = 0; i < meetings.size();) {
      int meetingLength = getMeetingLength(meetings.get(i));

      if (meetingLength >= length)
        ++i;
      else
        meetings.remove(i);
    }
  }

  /**
   * Compute the length of the {@code meeting} in minutes.
   * 
   * @param meeting The meeting from which to compute the length.
   * @return The length of the meeting in minutes.
   */
  private int getMeetingLength(AvailableMeetingTime meeting) {
    long difference = meeting.end.getTime() - meeting.start.getTime();

    return (int) difference / 60000;
  }

}
