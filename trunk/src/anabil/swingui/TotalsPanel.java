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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
   * Constructs.
   * @param application a reference back to the main application.
   */
  public TotalsPanel(final Application application) {
    _application = application;
    // This stops the panel from collapsing to nothing when there
    // are no people.
    setPreferredSize(new Dimension(0, 35));
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
    Person[] users = _application.getPersonsModel().getUsers();
    for (int i = 0; i < users.length; i++) {
      Person user = users[i];
      JPanel userSummaryArea = new JPanel();
      JLabel label = new JLabel(user.getName());
      JFormattedTextField totalField = new JFormattedTextField(
          new NumberFormatter(NUMBER_FORMAT));
      totalField.setEditable(false);
      totalField.setValue(new Double(0));
      totalField.setPreferredSize(new Dimension(65, 18));
      _totalFieldsForUser.put(user, totalField);
      userSummaryArea.add(label);
      userSummaryArea.add(totalField);

      add(userSummaryArea);
    }
    
    // Both these calls seem necessary to get the UI to update.
    validate();
    repaint();
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
    updateTotals(event.getSource());
  }

  /**
   * Update the displayed totals.
   * @param model the totals.
   */
  private void updateTotals(final BillModel model) {
    final Bill bill = model.getBill();
    final Person[] users = _application.getPersonsModel().getUsers();
    for (int i = 0; i < users.length; i++) {
      getTotalFieldForUser(users[i]).setValue(new Double(bill == null ? 0 : bill.getTotalFor(users[i])));
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
}
