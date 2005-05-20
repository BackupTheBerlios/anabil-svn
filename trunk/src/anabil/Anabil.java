/*
 * Anabil - Bill analyzer.
 * Copyright (C) 2005  Matt Hillsdon.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package anabil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import anabil.impl.SimplePerson;
import anabil.impl.bt.BTBillLoader;


/**
 * Class responsible for retaining user state and analysing calls.
 * 
 * @copyright
 * @author mth
 * @rcsid $Id$
 */
public class Anabil {

  /**
   * Map from name to user.
   */
  private Map _usersByName = new HashMap();

  /**
   * Find a user by name.
   * 
   * @return the user;
   */
  public Person getUser(final String name) {
    return (Person) _usersByName.get(name);
  }

  /**
   * Get the users.
   * 
   * @return the users.
   */
  public Person[] getUsers() {
    return (Person[]) _usersByName.values().toArray(
        new Person[_usersByName.values().size()]);
  }
  
  /**
   * Add a user.
   */
  public void addUser(final Person user) {
    _usersByName.put(user.getName(), user);
  }

  /**
   * Best effort call identification.
   * 
   * @param calls
   *          the calls to identify.
   */
  public void identifyCalls(Call[] calls) {
    for (int callIndex = 0; callIndex < calls.length; ++callIndex) {
      final Call call = calls[callIndex];
      
      // Don't override explicitly set users
      if (call.isIdentified()) {
        continue;
      }

      
      // Record which users have which type of 'claim' on paying for the call.
      final Set locationClaims = new HashSet();
      final Set numberClaims = new HashSet();

      for (final Iterator iter = _usersByName.values().iterator(); iter
          .hasNext();) {
        Person user = (Person) iter.next();
        if (user.callsNumber(call.getNumber())) {
          numberClaims.add(user);
        }
        // Location claims are uninteresting if there are number claims.
        else if (user.callsLocation(call.getLocation())) {
          locationClaims.add(user);
        }
      }

      // Number claims take priority
      if (!numberClaims.isEmpty()) {
        call.setSuggestedPeople(numberClaims);
      }
      else if (!locationClaims.isEmpty()) {
        call.setSuggestedPeople(locationClaims);
      }
      else {
        call.setSuggestedPeople(Collections.EMPTY_SET);
      }
    }
  }

  /**
   * Load the users.
   * 
   * @throws IOException
   *           if loading fails due to an IO error.
   */
  public void loadUsers() throws IOException {
    File usersLocation = new File("./config/users/");
    File[] users = usersLocation.listFiles(new FileFilter() {

      public boolean accept(File pathname) {
        return !(pathname.isDirectory() || (pathname.getName().indexOf('.') >= 0));
      }
    });
    for (int i = 0; i < users.length; i++) {
      String name = users[i].getName();
      SimplePerson user = new SimplePerson(name);
      _usersByName.put(name, user);
      user.load();
    }
  }

  /**
   * Load the users.
   * 
   * @throws IOException
   *           if loading fails due to an IO error.
   */
  public void saveUsers() throws IOException {
    for (Iterator iter = _usersByName.values().iterator(); iter.hasNext();) {
      Person user = (Person) iter.next();
      user.save();
    }
  }

  /**
   * Dump command-line application for testing.
   * 
   * @param args
   *          the command-line arguments.
   * @throws IOException
   *           if anything goes wrong.
   */
  public static void main(final String[] args) throws IOException {
    Anabil annabil = new Anabil();
    annabil.loadUsers();

    BillLoader loader = new BTBillLoader();
    Bill bill = loader.loadBill(new Date().toString(), new File(args[0]));
    Call[] calls = bill.getCalls();
    annabil.identifyCalls(calls);

    for (int i = 0; i < calls.length; i++) {
      System.out.println(calls[i]);
    }
  }

  /**
   * Remove the given user.
   * @param user the user to remove.
   */
  public void removeUser(Person user) {
    _usersByName.remove(user.getName());
  }
}
