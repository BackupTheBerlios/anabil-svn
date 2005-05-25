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

import javax.swing.ListModel;

/**
 * A mutable list model, similar to the mutable combo box
 * model.
 */
public interface MutableListModel extends ListModel {

  /**
   * Remove the element at the given index.
   * @param index the index.
   */
  void removeElementAt(int index);

  /**
   * Remove the given element, if present.
   * @param element the element.
   */
  void removeElement(Object element);

  /**
   * Insert the given element at the given position.
   * @param element the element.
   * @param index the position to insert at.
   */
  void insertElementAt(Object element, int index);

}

