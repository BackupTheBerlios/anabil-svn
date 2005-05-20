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

import java.util.Set;

/**
 * A telephone call.
 * 
 * @author mth
 */
public interface Call {
  /**
   * Get the user that is allocated to pay for the call, 
   * or null if no such individual user exists.
   * @return the user.
   */
  Person getSuggestedPerson();

  /**
   * Get the set of users who may be responsible for paying
   * for the call.
   * @return the set of users.
   */
  Set getSuggestedPeople();

  /**
   * Set the users who may be possible for paying for the call.
   * @param users the users.
   */
  void setSuggestedPeople(Set people);

  /**
   * Get the location this call was made to.
   * @return the location.
   */
  String getLocation();

  /**
   * Get the date this call was made on.
   * @return the date.
   */
  String getDate();

  /**
   * Get the number this call was made to.
   * @return the number.
   */
  String getNumber();

  /**
   * Get the cost of this call.
   * @return the cost.
   */
  double getCost();

  /**
   * Check if the call has been identified.
   * @return true iff the call has been explicitly identified via
   * a call to setResponsiblePerson().
   */
  boolean isIdentified();
  
  /**
   * Set the person responsible for the call.
   * @param person the person.
   */
  void setPersonResponsible(Person person);

  /**
   * Get the person responsible for the call.
   */
  Person getPersonResponsible();
}