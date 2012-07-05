package com.icreate.projectx.meetingscheduler.util;

import com.icreate.projectx.meetingscheduler.model.Attendee;

import java.util.Comparator;

/**
 * Comparator used to sort a list of attendees. The attendees are sorted
 * alphabetically and from selected to unselected.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class AttendeeComparator implements Comparator<Attendee> {

  /**
   * Comparator instance to avoid allocating a new one each time it is used.
   */
  private static final AttendeeComparator instance = new AttendeeComparator();

  /**
   * Private constructor.
   */
  private AttendeeComparator() {
  }

  /**
   * Retrieve comparator instance.
   * 
   * @return The Comparator instance.
   */
  public static AttendeeComparator getInstance() {
    return instance;
  }

  @Override
  public int compare(Attendee lhs, Attendee rhs) {
    if (lhs.selected == rhs.selected)
      return lhs.name.compareToIgnoreCase(rhs.name);
    else
      // Put selected on top.
      return rhs.selected.compareTo(lhs.selected);
  }
}
