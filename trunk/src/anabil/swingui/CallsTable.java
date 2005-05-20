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

package anabil.swingui;

import java.awt.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import anabil.Call;
import anabil.Person;
import anabil.swingui.model.CallsTableModel;
import anabil.swingui.model.PersonsListModel;


/**
 * A simple calls table with a custom editor/renderer for the user
 * column.
 * 
 * TODO:
 * It would be significantly more useful if this had sortable
 * columns and filtering (with the totals respecting the filtering).
 * 
 * @author mth
 */
class CallsTable extends JTable {

  /**
   * Own reference for simplicity.
   */
  private final CallsTableModel _callsTableModel;

  /**
   * The users list model.
   */
  private final PersonsListModel _usersListModel;

  /**
   * A reference to the main application.
   */
  private Application _application;

  /**
   * Constructs.
   *
   * @param application
   *          a reference back to the main application.
   * 
   */
  public CallsTable(final Application application) {
    super(application.getCallsModel());
    _application = application;
    _callsTableModel = _application.getCallsModel();
    _usersListModel = _application.getPersonsModel();
    
    //Editor/renderer for the users column.
    setDefaultEditor(Set.class, new PeopleEditor());
    setDefaultRenderer(Set.class, new PeopleRenderer());
  }

  class PeopleEditor extends DefaultCellEditor {

    /**
     * List separator (I suspect there's a better way...)
     */
    private static final String SEPARATOR = "---------";

    /**
     * The set of possible {@link anabil.Person}s.
     */
    private Set _suggestedPeople;

    /**
     * The set of all {@link anabil.Person}s not in {@link #_suggestedPeople}.
     */
    private Set _otherPeople;

    /**
     * The call corresponding to the row being edited.
     */
    private Call _currentCall;

    /**
     * Constructs.
     */
    public PeopleEditor() {
      super(new JComboBox());
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
     *      java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      _currentCall = _callsTableModel.getCall(row);
      _suggestedPeople = _currentCall.getSuggestedPeople();
      _otherPeople = new HashSet(Arrays.asList((_usersListModel.getUsers())));
      _otherPeople.removeAll(_suggestedPeople);

      JComboBox combo = (JComboBox) super.getTableCellEditorComponent(table,
          value, isSelected, row, column);
      Vector comboModelData = new Vector(_suggestedPeople);
      comboModelData.add(SEPARATOR);
      comboModelData.addAll(_otherPeople);

      combo.setModel(new DefaultComboBoxModel(comboModelData));
      return combo;
    }

    public Object getCellEditorValue() {
      Object o = super.getCellEditorValue();

      if (o instanceof Person) {
        Person person = (Person) o;
        if (_suggestedPeople.contains(person)) {
          return person;
        }
        else {
          final String number = _currentCall.getNumber();
          final String location = _currentCall.getLocation();
          final String[] options = new String[] {
              "Assign to " + person + " just this time.",
              "Add '" + number + "' to " + person,
              "Add '" + location + "' to " + person, "Cancel" };
          final int choice = JOptionPane.showOptionDialog(getComponent(),
              "Please choose", "You selected an unexpected user", 1,
              JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
          switch (choice) {
          case 0:
            return person;
          case 1:
            person.addNumber(number);
            _application.notifyOfUpdatedUser(person);
            _application.identifyCalls();
            _callsTableModel.fireTableDataChanged();
            return person;
          case 2:
            person.addLocation(location);
            _application.notifyOfUpdatedUser(person);
            _application.identifyCalls();
            _callsTableModel.fireTableDataChanged();
            return person;
          default:
            return _suggestedPeople;
          }
        }
      }
      else {
        // This handles the string separator.
        return _suggestedPeople;
      }
    }

  }

  /**
   * Renderer so trivial I can't remember why it exists!
   */
  class PeopleRenderer extends DefaultTableCellRenderer {
  }
}