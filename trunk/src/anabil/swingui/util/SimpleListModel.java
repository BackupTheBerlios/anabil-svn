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

package anabil.swingui.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractListModel;

import anabil.Person;

/**
 * A simple list model.

 * @author mth
 */
public class SimpleListModel extends AbstractListModel implements MutableListModel {

  /**
   * The data store for this model.
   */
  private List _data = new ArrayList();
  
  /**
   * Notify of an update to a user.
   * This fires the appropriate events if the user is in this model,
   * otherwise does nothing.
   *  
   * @param user the user.
   */
  public void notifyOfUpdate(Person user) {
    final int index = _data.indexOf(user);
    if (index != -1) {
      fireContentsChanged(this, index, index);
    }
  }

  public int getSize() {
    return _data.size();
  }

  public Object getElementAt(int index) {
    return _data.get(index);
  }

  /**
   * Update the users.
   * @param users the new set of users.
   */
  public void update(Object[] data) {
    final int lastRemovedIndex = _data.size() - 1;
    _data.clear();
    if (lastRemovedIndex > 0) {
      fireIntervalRemoved(this, 0, lastRemovedIndex);
    }
    _data.addAll(Arrays.asList(data));
    int newLastIndex = _data.size() - 1;
    if (newLastIndex < 0) {
      newLastIndex = 0;
    }
    fireIntervalAdded(this, 0, newLastIndex);
  }
  
  /**
   * Get the data backing this list model.
   * @return 
   */
  protected List getList() {
    return _data;
  }
  
  // Mutators, following the pattern of MutableComboBoxModel.
  // I'm sure there's a good reason there's no common interface...
  
  /* (non-Javadoc)
   * @see anabil.swingui.util.MutableListModel#removeElementAt(int)
   */
  public void removeElementAt(final int index) {
    _data.remove(index);
    fireIntervalRemoved(this, index, index);
  }
  
  /* (non-Javadoc)
   * @see anabil.swingui.util.MutableListModel#removeElement(java.lang.Object)
   */
  public void removeElement(final Object element) {
    final int index = _data.indexOf(element);
    if (index != -1) {
      removeElementAt(index);
    }
  }
  
  /* (non-Javadoc)
   * @see anabil.swingui.util.MutableListModel#insertElementAt(java.lang.Object, int)
   */
  public void insertElementAt(final Object element, final int index) {
    _data.add(index, element);
    fireIntervalAdded(this, index, index);
  }

}
