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

package anabil.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import anabil.Person;

import com.Ostermiller.util.FileHelper;

/**
 * A trivial user implementation.
 */
public class SimplePerson implements Person {

  /**
   * The prefix used to identify a number entry.
   */
  private static final String NUMBER_PREFIX = "n: ";

  /**
   * The prefix used to identify a location entry.
   */
  private static final String LOCATION_PREFIX = "l: ";

  /**
   * The set of locations called by this user.
   */
  private final Set _locations = new HashSet();

  /**
   * The set of numbers called by this user.
   */
  private final Set _numbers = new HashSet();

  /**
   * The name of this user.
   */
  private final String _name;

  /**
   * Constructs.
   * @param name the name of the user.
   */
  public SimplePerson(final String name) {
    _name = name;
  }

  
  public String getName() {
    return _name;
  }

  public void addLocation(final String location) {
    _locations.add(location);
  }

  public void addNumber(final String number) {
    _numbers.add(number);
  }

  public boolean callsNumber(final String number) {
    return _numbers.contains(number);
  }

  public boolean callsLocation(final String location) {
    return _locations.contains(location);
  }
  
  public String[] getCalledLocations() {
    return (String[]) _locations.toArray(new String[_locations.size()]);
  }

  public String[] getCalledNumbers() {
    return (String[]) _numbers.toArray(new String[_locations.size()]);
  }

  
  public void save() throws IOException {
    final File f = new File("./config/users/", _name);
    final PrintWriter out = new PrintWriter(new FileOutputStream(f));
    for (final Iterator iter = _locations.iterator(); iter.hasNext();) {
      final String location = (String) iter.next();
      out.print(LOCATION_PREFIX);
      out.println(location);
    }
    for (final Iterator iter = _numbers.iterator(); iter.hasNext();) {
      final String number = (String) iter.next();
      out.print(NUMBER_PREFIX);
      out.println(number);
    }
    out.flush();
    out.close();
  }

  public void load() throws IOException {
    final File f = new File("./config/users/", _name);
    final BufferedReader in = new BufferedReader(new FileReader(f));

    String line;
    try {
      while ((line = in.readLine()) != null) {
        if (line.startsWith(LOCATION_PREFIX)) {
          _locations.add(line.substring(LOCATION_PREFIX.length()));
        }
        else if (line.startsWith(NUMBER_PREFIX)) {
          _numbers.add(line.substring(NUMBER_PREFIX.length()));
        }
      }
      System.out.println("Loaded user " + _name + ":" + _numbers + _locations);
    }
    finally {
      in.close();
    }
  }

  public String toString() {
    return _name;
  }


  public void removeLocation(String location) {
    _locations.remove(location);
  }


  public void removeNumber(String number) {
    _numbers.remove(number);
  }


  public void delete() throws IOException {
    final File f = new File("./config/users/", _name);
    final File backup = new File("./config/users/", _name + ".deleted");
    FileHelper.move(f, backup, true);
  }

}
