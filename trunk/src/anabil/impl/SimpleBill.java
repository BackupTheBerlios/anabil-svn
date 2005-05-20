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

import java.util.HashMap;

import anabil.Bill;
import anabil.Call;
import anabil.Person;

/**
 * A representation of a phone bill.
 */
public class SimpleBill implements Bill {

  /**
   * The calls.
   */
  private final Call[] _calls;

  /**
   * A map of {@link Person} to {@link Double}. 
   */
  private final HashMap _userToTotal = new HashMap();

  /**
   * Constructs.
   * @param calls the calls relating to this bill.
   */
  public SimpleBill(final Call[] calls) {
    _calls = calls;
  }

  public Call[] getCalls() {
    return _calls;
  }
  
  /**
   * Update the calculated totals. TODO: make this efficient and move to
   * {@link Bill}.
   */
  public void updateTotals() {
    //This is somewhat naive, but seems to fair ok.
    _userToTotal.clear();
    final Call[] calls = getCalls();
    for (int i = 0; i < calls.length; i++) {
      Call call = calls[i];
      Person person = null;
      if (call.isIdentified()) {
        person = call.getPersonResponsible();
      }
      else {
        person = call.getSuggestedPerson();
      }
      double total = 0;
      if (person != null) {
        Double currentTotalDouble = (Double) _userToTotal.get(person);
        if (currentTotalDouble != null) {
          total += currentTotalDouble.doubleValue();
        }
        total += call.getCost();
      }
      _userToTotal.put(person, new Double(total));
    }
  }
  
  /**
   * Get the total payable in this bill by the given user since
   * the last call to updateTotals.
   * @param user the user to check the total for.
   * @return the current total for the given user.
   */
  public double getTotalFor(final Person user) {
    double total = 0;
    Double totalDouble = (Double) _userToTotal.get(user);
    if (totalDouble != null) {
      total = totalDouble.doubleValue();
    }
    return total;
  }

}
