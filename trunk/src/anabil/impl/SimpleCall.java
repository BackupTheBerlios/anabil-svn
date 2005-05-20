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

import java.util.Collections;
import java.util.Set;

import anabil.Call;
import anabil.Person;

/**
 * A phone call.
 */
public class SimpleCall implements Call {

  /**
   * The date this call was made.
   */
  private final String _date;

  /**
   * The location called.
   */
  private final String _location;

  /**
   * The phone number called.
   */
  private final String _number;

  /**
   * The set of people who might be responsible for paying for this
   * call.
   */
  private Set _suggestedPeople;

  /**
   * The cost of this call.
   */
  private final double _cost;

  /**
   * Set to true to confirm the identification of a call with a given user.
   */
  private Person _personResponsible;

  /**
   * Constructs.
   * @param date     the date this call was made.
   * @param location the location this call was made to.
   * @param number   the number called.
   * @param cost     the cost of this call.
   */
  public SimpleCall(String date, String location, String number, double cost) {
    _date = date;
    _location = location;
    _number = number;
    _cost = cost;
    _suggestedPeople = Collections.EMPTY_SET;
  }

  public Person getSuggestedPerson() {
    if (_suggestedPeople.size() == 1) {
      return (Person) _suggestedPeople.iterator().next();
    }
    return null;
  }

  public Set getSuggestedPeople() {
    return _suggestedPeople;
  }

  public void setSuggestedPeople(final Set people) {
    _suggestedPeople = people;
  }

  public String getLocation() {
    return _location;
  }

  public String getDate() {
    return _date;
  }

  public String getNumber() {
    return _number;
  }

  public String toString() {
    return _date + "\t" + _location + "\t" + _number + "\t" + _cost + "\t"
        + _suggestedPeople;
  }

  public double getCost() {
    return _cost;
  }

  public boolean isIdentified() {
    return _personResponsible != null;
  }

  public void setPersonResponsible(Person person) {
    _personResponsible = person;
  }
  
  public Person getPersonResponsible() {
    return _personResponsible;
  }

}
