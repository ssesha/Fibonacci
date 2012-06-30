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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Provides utility functions to manipulate dates and date times.
 * 
 * @since 2.2
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class DateUtils {

  /**
   * Check if two dates are on the same day.
   * 
   * @param lhs
   * @param rhs
   * @return True if {@code lhs} and {@code rhs} are on the same day.
   */
  public static boolean isSameDay(Date lhs, Date rhs) {
    return isSameDay(lhs, rhs, TimeZone.getDefault());
  }

  /**
   * Check if two dates are on the same day.
   * 
   * @param lhs
   * @param rhs
   * @return True if {@code lhs} and {@code rhs} are on the same day.
   */
  public static boolean isSameDay(Date lhs, Date rhs, TimeZone timeZone) {
    Calendar clhs = new GregorianCalendar(timeZone);
    Calendar crhs = new GregorianCalendar(timeZone);

    clhs.setTime(lhs);
    crhs.setTime(rhs);
    return clhs.get(Calendar.DAY_OF_YEAR) == crhs.get(Calendar.DAY_OF_YEAR)
        && clhs.get(Calendar.YEAR) == crhs.get(Calendar.YEAR);
  }

  /**
   * Parse a string formatted as "HH.MM" and returns a Calendar object set with
   * the time.
   * 
   * @param time The string to parse
   * @return The newly created Calendar object with the parsed time.
   */
  public static Calendar getCalendar(String time) {
    return getCalendar(time, TimeZone.getDefault());
  }

  /**
   * Parse a string formatted as "HH.MM" and returns a Calendar object set with
   * the time.
   * 
   * @param time The string to parse
   * @return The newly created Calendar object with the parsed time.
   */
  public static Calendar getCalendar(String time, TimeZone timeZone) {
    Calendar calendar = new GregorianCalendar(timeZone);

    String[] timeComponents = time.split(":");
    setTime(calendar, Integer.parseInt(timeComponents[0]), Integer.parseInt(timeComponents[1]), 0,
        0);
    return calendar;
  }

  /**
   * Set the time of the {@code calendar}.
   * 
   * @param calendar The calendar to which to set the time
   * @param hour The hour to set
   * @param minute The minute to set
   * @param second The second to set
   * @param millisecond The millisecond to set
   */
  public static void setTime(Calendar calendar, int hour, int minute, int second, int millisecond) {
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millisecond);
  }

  /**
   * Set the time from {@code source} to {@code target} and not modify the date.
   * 
   * @param target The Calendar object to which set the time.
   * @param source The Calendar object from which to read the time.
   */
  public static void setTime(Calendar target, Calendar source) {
    target.set(Calendar.HOUR_OF_DAY, source.get(Calendar.HOUR_OF_DAY));
    target.set(Calendar.MINUTE, source.get(Calendar.MINUTE));
    target.set(Calendar.SECOND, source.get(Calendar.SECOND));
    target.set(Calendar.MILLISECOND, source.get(Calendar.MILLISECOND));
  }
}
