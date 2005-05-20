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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import anabil.Person;
import anabil.impl.SimplePerson;
import anabil.swingui.model.PersonsListModel;
import anabil.swingui.util.SimpleListModel;


/**
 * A JPanel showing the current users.
 * 
 * @author mth
 */
class PeoplePanel extends JPanel implements ListSelectionListener, ListDataListener {

  /**
   * An action to remove the selected number from the selected person. 
   */
  private class RemoveNumberAction extends AbstractAction {
    
    public RemoveNumberAction() {
      super("Remove number");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
    }

    public void actionPerformed(ActionEvent e) {
      final String number = (String) numbersList.getSelectedValue();
      final Person person = (Person) _userList.getSelectedValue();
      if (person != null && number != null) {
        person.removeNumber(number);
        _usersListModel.notifyOfUpdate(person);
      }
    }

  }
  /**
   * An action to remove the selected location from the selected person.
   */
  private class RemoveLocationAction extends AbstractAction {
    
    public RemoveLocationAction() {
      super("Remove location");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
    }

    public void actionPerformed(ActionEvent e) {
      final String location = (String) locationsList.getSelectedValue();
      final Person person = (Person) _userList.getSelectedValue();
      if (person != null && location != null) {
        person.removeLocation(location);
        _usersListModel.notifyOfUpdate(person);
      }
    }

  }
  
  /**
   * An action to create a new person.
   */
  private class AddPersonAction extends AbstractAction {
    
    public AddPersonAction() {
      super("Create new");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
    }

    public void actionPerformed(final ActionEvent e) {
      final String name = JOptionPane.showInputDialog("Enter the name of the new user", "");
      if (name != null && !"".equals(name)) {
        if (_application.getUserByName(name) != null) {
          // Can't have duplicate users
          JOptionPane.showMessageDialog(PeoplePanel.this, "Person '" + name + "' already exists.", "Error", JOptionPane.ERROR_MESSAGE);
          actionPerformed(e);
        }
        else {
          _application.addNewUser(new SimplePerson(name));
        }
      }
    }
  }
  
  /**
   * An action to remove a person.
   */
  private class RemovePersonAction extends AbstractAction {
    
    public RemovePersonAction() {
      super("Delete selected");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
    }

    public void actionPerformed(final ActionEvent e) {
      Person user = (Person) _userList.getSelectedValue();
      if (user != null) {
        _application.removeUser(user);
      }
    }
  }


  /**
   * The height of the JLists showing numbers and locations.
   */
  private static final int LIST_HEIGHT = 200;

  /**
   * Users list model.
   */
  private final PersonsListModel _usersListModel;
  
  /**
   * A list gui component showing the users.
   */
  private JList _userList;

  /**
   * A model of the list of locations called by the currently
   * selected user.  Never null, possibly empty.
   */
  private final SimpleListModel _locationsModel = new SimpleListModel();

  /**
   * A model of the list of numbers called by the currently
   * selected user.  Never null, possibly empty.
   */
  private final SimpleListModel _numbersModel = new SimpleListModel();

  /**
   * The list gui component displaying locations called by the selected user.
   */
  private JList locationsList;

  /**
   * The list gui component displaying numbers called by the selected user.
   */
  private JList numbersList;

  /**
   * A reference back to the main application.
   */
  private final Application _application;

  /**
   * Constructs.
   * @param model the model to use.
   */
  public PeoplePanel(final Application application) {
    _application = application;
    _usersListModel = application.getPersonsModel();

    initModel();
    initGui();
    setupPopupMenu();
  }
  
  /**
   * Creates a popup menu that is shown when the user right clicks on
   * the list of people.
   */
  private void setupPopupMenu() {
    final JPopupMenu menu = new JPopupMenu();
    menu.add(new JMenuItem(new AddPersonAction()));
    menu.add(new JMenuItem(new RemovePersonAction()));
    // Listen for popup triggers.  Checks in mousePressed and
    // released as per javadoc.
    _userList.addMouseListener(new MouseAdapter() {
      public void mousePressed(final MouseEvent me) {
        if (me.isPopupTrigger()) {
          maybeShowPopup(me.getPoint());
        }
      }
      public void mouseReleased(final MouseEvent me) {
        if (me.isPopupTrigger()) {
          maybeShowPopup(me.getPoint());
        }
      }
      
      /**
       * Show a popup menu for the JList if the click was over a selected
       * item.
       * @param clickPosition the x,y location of the mouse click.
       */
      private void maybeShowPopup(final Point clickPosition) {
        if (!_userList.isSelectionEmpty()
            && _userList.locationToIndex(clickPosition) == _userList.getSelectedIndex()) {
          menu.show(_userList, (int) clickPosition.getX(), (int) clickPosition.getY());
        }
      }
    });
  }
  
  /**
   * Initialise our connections to the data model.
   */
  private void initModel() {
    _usersListModel.addListDataListener(this);
  }

  /**
   * Initialise the GUI.
   */
  private void initGui() {
    setLayout(new BorderLayout());
    _userList = new JList();
    // The order of these two calls is important.
    // The JList must be informed of changes to the person list model
    // before we are, so that our call to getSelectedValue doesn't
    // ArrayIndexOutOfBounds on a delete.
    // EventListenerLists are processed in reverse order.
    // This probably means something isn't quite right...
    _userList.getSelectionModel().addListSelectionListener(this);
    _userList.setModel(_usersListModel);
    
    final JPanel userDetails = new JPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    final GridBagLayout gbl = new GridBagLayout();
    userDetails.setLayout(gbl);
    
    gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0; gbc.weighty = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    JLabel locationsLabel = new JLabel("Locations called regularly");
    gbl.setConstraints(locationsLabel, gbc);
    userDetails.add(locationsLabel);
    
    locationsList = new JList(_locationsModel);
    locationsList.setPreferredSize(new Dimension(0, LIST_HEIGHT));
    locationsList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1; gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.SOUTHEAST;
    gbl.setConstraints(locationsList, gbc);
    userDetails.add(locationsList);
    
    final JButton removeLocation = new JButton(new RemoveLocationAction());
    gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    gbc.weightx = 0; gbc.weighty = 0;
    gbl.setConstraints(removeLocation, gbc);
    userDetails.add(removeLocation);
    
    final JLabel numbersLabel = new JLabel("Numbers called regularly");
    gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0; gbc.weighty = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbl.setConstraints(numbersLabel, gbc);
    userDetails.add(numbersLabel);
    
    numbersList = new JList(_numbersModel);
    numbersList.setPreferredSize(new Dimension(0, LIST_HEIGHT));
    numbersList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1; gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.SOUTHEAST;
    gbl.setConstraints(numbersList, gbc);
    userDetails.add(numbersList);
    
    final JButton removeNumber = new JButton(new RemoveNumberAction());
    gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.NORTHEAST;
    gbc.weightx = 0; gbc.weighty = 0;
    gbl.setConstraints(removeNumber, gbc);
    userDetails.add(removeNumber);

    final JPanel userListAndControls = new JPanel(new BorderLayout());
    userListAndControls.add(_userList);
    
    final JPanel controls = new JPanel();
    controls.setLayout(new GridLayout(2, 1));
    controls.add(new JButton(new AddPersonAction()));
    controls.add(new JButton(new RemovePersonAction()));
    userListAndControls.add(controls, BorderLayout.SOUTH);
    
    final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.add(userListAndControls);
    splitPane.add(userDetails);
    add(splitPane);
    splitPane.setDividerLocation(125);
  }
  
  /**
   * Update the tables showing details of the current user.
   */
  private void updateTables() {
    Person user = (Person) _userList.getSelectedValue();
    if (user == null) {
      _numbersModel.update(new String[]{});
      _locationsModel.update(new String[]{});
    }
    else {
      _numbersModel.update(user.getCalledNumbers());
      _locationsModel.update(user.getCalledLocations());
    }
  }
  
  
  // USER LIST SELECTION
    
  public void valueChanged(final ListSelectionEvent e) {
    // We're only interested in one event per selection
    // activity.
    if (e.getValueIsAdjusting()) {
      return;
    }
    updateTables();
  }

  // LIST LISTENER FOR USERS

  public void intervalAdded(ListDataEvent e) {
    //Not our concern, list is directly connected.
  }

  public void intervalRemoved(ListDataEvent e) {
    //The selected user could have been deleted.
    updateTables();
  }

  public void contentsChanged(ListDataEvent e) {
    //The selected user could have been edited.
    updateTables();
  }

}
