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

package anabil.swingui.model;

import java.util.List;

import anabil.Person;
import anabil.swingui.util.SimpleListModel;


/**
 * Trivial list model for the users with an accessor for the
 * underlying data and a method for informing of non-model based
 * updates.
 * 
 * Doesn't subclass DefaultListModel as it's bunk.
 * 
 * @author mth
 */
public class PersonsListModel extends SimpleListModel {
  
  /**
   * Get the underlying users.
   * @return the users.
   */
  public Person[] getUsers() {
    List data = getList();
    return (Person[]) data.toArray(new Person[data.size()]);
  }

  /**
   * Update the users.
   * @param users the new set of users.
   */
  public void update(Person[] users) {
    super.update(users);
  }
  
}
