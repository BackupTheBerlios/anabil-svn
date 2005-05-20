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

import java.io.IOException;

/**
 * A user.
 */
public interface Person {

  /**
   * The (unique) name of the user.
   * @return the name.
   */
  String getName();

  /**
   * Add a location that this user calls.
   * @param location the location to add.
   */
  void addLocation(String location);

  /**
   * Add a number that this user calls.
   * @param number the number to add.
   */
  void addNumber(String number);

  /**
   * Query whether this user calls the given number.
   * @param number the number to check.
   * @return true iff the user calls the given number.
   */
  boolean callsNumber(final String number);

  /**
   * Query whether this user calls the given location.
   * @param location the location to check.
   * @return true iff the user calls the given location.
   */
  boolean callsLocation(final String location);
  
  /**
   * Get the locations called by this user.
   * @return the locations.
   */
  String[] getCalledLocations();

  /**
   * Get the numbers called by this user.
   * @return the numbers.
   */
  String[] getCalledNumbers();

  /**
   * Save the user to disk.
   * @throws IOException if the save fails.
   */
  void save() throws IOException;

  /**
   * Load the user from disk.
   * @throws IOException if the load fails.
   */
  void load() throws IOException;

  /**
   * Remove the given location.
   * @param location the location to remove.
   */
  void removeLocation(String location);

  /**
   * Remove the given number.
   * @param number the number to remove.
   */
  void removeNumber(String number);

  /**
   * Delete a user.
   * @throws IOException if the delete fails due to an IO exception.
   */
  void delete() throws IOException;
}
