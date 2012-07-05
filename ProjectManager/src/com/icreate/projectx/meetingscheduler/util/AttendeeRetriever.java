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

import com.icreate.projectx.meetingscheduler.model.Attendee;
import com.icreate.projectx.meetingscheduler.model.Constants;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Get the contacts from the phone for the selected account.
 * 
 * @author Alain Vongsouvanh (alainv@google.com)
 */
public class AttendeeRetriever {

  private Activity activity;
  private Account account;
  private Attendee currentUser;

  /**
   * Construct a new AttendeeRetriever.
   * 
   * @param activity Activity to run contact's retrieval from.
   * @param account Account to use.
   */
  public AttendeeRetriever(Activity activity, Account account) {
    this.activity = activity;
    this.account = account;

    this.currentUser = new Attendee("Me (" + account.name + ")", account.name, null);
  }

  /**
   * Get the list of user's contacts.
   * 
   * @return The list of contacts.
   */
  public List<Attendee> getAttendees() {
    List<Attendee> result = new ArrayList<Attendee>();
    ContentResolver cr = activity.getContentResolver();
    Cursor cursor =
        cr.query(ContactsContract.Contacts.CONTENT_URI, new String[] {BaseColumns._ID,
            Contacts.DISPLAY_NAME, Contacts.IN_VISIBLE_GROUP}, Contacts.IN_VISIBLE_GROUP + " = 1",
            null, null);

    try {
      if (cursor.getCount() > 0) {
        while (cursor.moveToNext()) {
          long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
          String email = getEmail(cr, id);

          if (email != null) {
            String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
            String imageUri = getPhotoUri(cr, id);

            result.add(new Attendee(name + " (" + email + ")", email, imageUri));
          }
        }
      } else {
        Log.e(Constants.TAG, "No contacts found.");
      }
    } finally {
      cursor.close();
    }
    Attendee current = getCurrentUser();
    current.selected = true;
    result.add(current);

    return result;
  }

  /**
   * Get the current user as an attendee.
   * 
   * @return The current user as an attendee.
   */
  public Attendee getCurrentUser() {
    return currentUser;
  }

  /**
   * Get the correct email address to use for the current contact. The first
   * choice is the e-mail having the same domain as the user's, if none is
   * available, the first GMail address is chosen.
   * 
   * @param cr ContentResolver to use to get the list of e-mail addresses.
   * @param id Contact's ID
   * @return The "best" email address.
   */
  private String getEmail(ContentResolver cr, long id) {
    Cursor cursor =
        cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, new String[] {Email.DATA,
            Email.IS_PRIMARY}, Email.CONTACT_ID + " = '" + id + "'", null, Email.IS_PRIMARY
            + " DESC");
    String result = null;

    if (cursor != null) {
      try {
        if (cursor.getCount() > 0) {
          while (cursor.moveToNext()) {
            String email = cursor.getString(cursor.getColumnIndex(Email.DATA));

            if (email.contains("@")) {
              // Get the first e-mail on the same domain as the uer's.
              if (isSameDomain(account.name, email)) {
                result = email;
                break;
              }
              // Else, get the first gmail address.
              else if (isSameDomain("@gmail.com", email) && result == null) {
                result = email;
              }
            }
          }
          // If none of the above has been found, use the first email address.
          if (result == null) {
            if (cursor.moveToFirst()) {
              result = cursor.getString(cursor.getColumnIndex(Email.DATA));
            }
          }
        }
      } finally {
        cursor.close();
      }
    }

    return result;
  }

  /**
   * Check if two email addresses are of the same domain.
   * 
   * @param lhs Left-hand side e-mail address.
   * @param rhs Right-hand side e-mail address.
   * @return Whether or not 2 email addresses are on the same domain.
   */
  private boolean isSameDomain(String lhs, String rhs) {
    return lhs.substring(lhs.indexOf('@')).equalsIgnoreCase(rhs.substring(rhs.indexOf('@')));
  }

  /**
   * Get the contact's Photo URI if it exists.
   * 
   * @param cr The ContentResolver to use to get the contact's data.
   * @param id The contact's ID.
   * 
   * @return The contact's Photo URI or null if none is available.
   */
  private String getPhotoUri(ContentResolver cr, long id) {
    Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
    Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
    AssetFileDescriptor descriptor = null;

    try {
      descriptor = cr.openAssetFileDescriptor(photoUri, "r");

      descriptor.close();
      return photoUri.toString();
    } catch (Exception e) {
      if (descriptor != null) {
        try {
          descriptor.close();
        } catch (IOException ex) {
        }
      }
      // The contact has no pictures.
      return null;
    }
  }
}
