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

import java.util.Set;

import javax.swing.table.AbstractTableModel;

import anabil.Call;
import anabil.Person;

/**
 * Provide a table model interface for an array of calls.
 */
public class CallsTableModel extends AbstractTableModel {

  /**
   * The various column indices.
   */
  private static final int PERSON = 4;
  private static final int AMOUNT = 3;
  private static final int NUMBER = 2;
  private static final int LOCATION = 1;
  private static final int DATE = 0;

  /**
   * The calls that are the basis of the model.
   */
  private Call[] _calls;

  /**
   * Constructs.
   * The model is intially empty.
   * @see #update(Call[])
   */
  public CallsTableModel() {
    _calls = new Call[] {};
  }

  /**
   * Update the calls.
   * 
   * @param calls
   *          the calls.
   */
  public void update(final Call[] calls) {
    _calls = calls;
    fireTableDataChanged();
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() {
    return 5;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() {
    return _calls.length;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int rowIndex, int columnIndex) {
    switch (columnIndex) {
    case DATE:
      return _calls[rowIndex].getDate();
    case LOCATION:
      return _calls[rowIndex].getLocation();
    case NUMBER:
      return _calls[rowIndex].getNumber();
    case AMOUNT:
      return new Double(_calls[rowIndex].getCost());
    case PERSON:
      final Call call = _calls[rowIndex];
      if (call.isIdentified()) {
        return call.getPersonResponsible();
      }
      else {
        return call.getSuggestedPeople();
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnName(int)
   */
  public String getColumnName(int column) {
    switch (column) {
    case DATE:
      return "Date";
    case LOCATION:
      return "Location";
    case NUMBER:
      return "Number";
    case AMOUNT:
      return "Cost";
    case PERSON:
      return "People";
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnClass(int)
   */
  public Class getColumnClass(int columnIndex) {
    switch (columnIndex) {
    case PERSON:
      return Set.class;
    }
    return Object.class;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return columnIndex == PERSON;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    if (aValue instanceof Set) {
      Set users = (Set) aValue;
      _calls[rowIndex].setSuggestedPeople(users);
    }
    else {
      Person user = (Person) aValue;
      _calls[rowIndex].setPersonResponsible(user);
    }
    
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /**
   * Get a call.
   * 
   * @param column
   *          the column to find the user for.
   * @return the call at the given column.
   */
  public Call getCall(final int column) {
    return _calls[column];
  }
}
