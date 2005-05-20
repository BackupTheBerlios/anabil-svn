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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.NumberFormatter;

import anabil.Bill;
import anabil.Person;
import anabil.swingui.model.BillListener;
import anabil.swingui.model.BillModel;
import anabil.swingui.model.BillRecalculationEvent;


/**
 * Widget that shows bill total information for each user.
 *
 * @author mth
 */
class TotalsPanel extends JPanel implements BillListener, ListDataListener {
  
  /**
   * Number format to 2dp.
   */
  private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
  static {
    if (NUMBER_FORMAT instanceof DecimalFormat) {
      DecimalFormat format = (DecimalFormat) NUMBER_FORMAT;
      format.setMaximumFractionDigits(2);
      format.setMinimumFractionDigits(2);
    }
  }

  /**
   * The main application.
   */
  private final Application _application;

  /**
   * A map from user {@link Person} to total fields {@link JFormattedTextField}.
   */
  private Map _totalFieldsForUser = new HashMap();

  /**
   * The total to share between the user.
   * TODO: move this state out of GUI code.
   */
  private double _shared = 0;
  
  /**
   * The number of users {@link #_shared} is shared between.
   * TODO: move this state out of GUI code.
   */
  private double _between = 1;

  /**
   * The shared fixed amount.
   */
  private double _sharedFixedAmount = 0;
  
  /**
   * The tax rate.
   */
  private double _taxRate = 1.175;
  
  /**
   * The field holding the fixed amount.
   */
  private JFormattedTextField _fixedField;
  

  /**
   * Constructs.
   * @param application a reference back to the main application.
   */
  public TotalsPanel(final Application application) {
    _application = application;
    // This stops the panel from collapsing to nothing when there
    // are no people.
    setMinimumSize(new Dimension(0, 35));
    initModel();
    refreshGui();
  }
  
  /**
   * Initialise our links to the model.
   * We listen for bill recalculations, and display the new totals.
   * We also update the display if the users have changed.
   */
  private void initModel() {
    _application.getBillModel().addBillListener(this);
    _application.getPersonsModel().addListDataListener(this);
  }

  /**
   * Intialise the GUI.
   */
  private void refreshGui() {
    removeAll();
    _totalFieldsForUser.clear();
    Person[] people = _application.getPersonsModel().getUsers();
    for (int i = 0; i < people.length; i++) {
      Person person = people[i];
      if (!person.getName().equals("Shared")) {
        add(createTotalField(person));
      }
    }
    // Put shared last.
    Person shared = _application.getUserByName("Shared");
    if (shared != null) {
      add(createTotalField(shared));
    }
    
    JPanel fixedAmountPanel = new JPanel();
    JLabel fixedAmountLabel = new JLabel("Fixed amount");
    _fixedField = new JFormattedTextField(
        new NumberFormatter(NUMBER_FORMAT));
    _fixedField.setValue(new Double(_sharedFixedAmount));
    _fixedField.setFocusLostBehavior(JFormattedTextField.COMMIT);
    _fixedField.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        updateSharedCost();
      }      
    });
    _fixedField.addFocusListener(new FocusAdapter() {
      public void focusLost(final FocusEvent e) {
        updateSharedCost();
      }
    });
    _fixedField.setPreferredSize(new Dimension(50, 18));
    fixedAmountPanel.add(fixedAmountLabel);
    fixedAmountPanel.add(_fixedField);
    add(fixedAmountPanel);    
    
    // Both these calls seem necessary to get the UI to update.
    validate();
    repaint();
  }

  /**
   * @param person
   * @return
   */
  private JPanel createTotalField(Person person) {
    JPanel userSummaryArea = new JPanel();
    JLabel label = new JLabel(person.getName());
    JFormattedTextField totalField = new JFormattedTextField(
        new NumberFormatter(NUMBER_FORMAT));
    totalField.setEditable(false);
    totalField.setValue(new Double(0));
    totalField.setPreferredSize(new Dimension(50, 18));
    _totalFieldsForUser.put(person, totalField);
    userSummaryArea.add(label);
    userSummaryArea.add(totalField);
    return userSummaryArea;
  }
  

  /**
   * Get the text field in which to display the total for the given user.
   * 
   * @param user
   *          the user.
   * @return the text field in which to display the total.
   */
  private JFormattedTextField getTotalFieldForUser(Person user) {
    return (JFormattedTextField) _totalFieldsForUser.get(user);
  }

  // BILL LISTENER
  
  /**
   * Update the totals.
   */
  public void billRecalculated(final BillRecalculationEvent event) {
    updateSharedCost();
    updateTotals(event.getSource());
  }

  /**
   * Update the displayed totals.
   * @param model the totals.
   */
  private void updateTotals(final BillModel model) {
    final Bill bill = model.getBill();
    final Person[] users = _application.getPersonsModel().getUsers();
    final double sharedPerPerson = getSharedPerPerson();
    for (int i = 0; i < users.length; i++) {
      if (users[i].getName().equals("Shared")) {
        double cost = bill == null ? 0 : bill.getTotalFor(users[i]);
        getTotalFieldForUser(users[i]).setValue(new Double(cost * _taxRate));
      }
      else {
        double cost = bill == null ? sharedPerPerson : sharedPerPerson + bill.getTotalFor(users[i]);
        getTotalFieldForUser(users[i]).setValue(new Double(cost * _taxRate));
      }
    }
  }
  
  // USERS LIST

  public void intervalAdded(ListDataEvent e) {
    refreshGui();
  }

  public void intervalRemoved(ListDataEvent e) {
    refreshGui();
  }

  public void contentsChanged(ListDataEvent e) {
    // Content changes could include the name of the user, which we display.
    refreshGui();
  }

  /**
   * Update the cost that is to be shared between people.
   */
  private void updateSharedCost() {
    try {
      _fixedField.commitEdit();
    }
    catch (ParseException e) {
    }
    Number number = (Number) _fixedField.getValue();
    if (number != null) {
      _sharedFixedAmount = number.doubleValue(); 
      double sharedCalls = 0;
      final Person shared = _application.getUserByName("Shared");
      int size = _application.getPersonsModel().getSize();
      if (shared != null) {
         sharedCalls = _application.getBillModel().getBill().getTotalFor(shared);
         size--;
      }
      
      _shared = _sharedFixedAmount + sharedCalls;
      _between = size;
      updateTotals(_application.getBillModel());
    }
  }
  
  /**
   * Get the amount of shared cost each person must pay.
   * @return the amount of shared cost each person must pay.
   */
  public double getSharedPerPerson() {
    return _shared / _between;
  }
    
}
