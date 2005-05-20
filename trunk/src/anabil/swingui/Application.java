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

import java.io.File;
import java.io.IOException;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import anabil.Anabil;
import anabil.Bill;
import anabil.BillLoader;
import anabil.Call;
import anabil.Person;
import anabil.impl.SimpleBill;
import anabil.impl.bt.BTBillLoader;
import anabil.swingui.model.BillModel;
import anabil.swingui.model.CallsTableModel;
import anabil.swingui.model.PersonsListModel;


/**
 * The main GUI application.
 * 
 * This provides access to the models that the swing app is based on.
 * 
 * @author mth
 */
public class Application {

  /**
   * The bill analyser.
   */
  private final Anabil _anabil = new Anabil();

  /**
   * The main application frame.
   */
  private ApplicationFrame _applicationFrame;

  /**
   * The current (open) bill. Null if none has been loaded.
   */
  private BillModel _billModel;

  /**
   * Non-null if we failed to load the users.
   */
  private IOException _userLoadingException;

  /**
   * The calls table model for the currently loaded bill.
   */
  private CallsTableModel _callsTableModel;

  /**
   * The user list model.
   * This probably won't stay as a DLM for long.
   */
  private PersonsListModel _userListModel;

  /**
   * Application entry point.
   * 
   * @param args
   *          command line arguments (not processed).
   */
  public static void main(final String[] args) {
    new Application();
  }

  /**
   * Application entry point. Just new me.
   */
  public Application() {
    try {
      _anabil.loadUsers();
    }
    catch (IOException ex) {
      _userLoadingException = ex;
    }
    // Set the look and feel to the system look and feel.  If a laf has been
    // explicitly set then assume the user knows what they want.  We should
    // also not set the laf if it is specified in swing.properties, but
    // there doesn't seem to be any standard way of checking this (without copying
    // loads of code from {@link javax.swing.UIManager}).
    try {
      if (System.getProperty("swing.defaultlaf") == null) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
    }
    // The default laf will do.
    catch (UnsupportedLookAndFeelException e) {
    }
    catch (InstantiationException e) {
    }
    catch (IllegalAccessException e) {
    }
    catch (ClassNotFoundException e) {
    }

    initModel();
    initGui();
  }
  
  /**
   * Initialise the swing models.
   */
  private void initModel() {
    _callsTableModel = new CallsTableModel();
    _userListModel = new PersonsListModel();
    _userListModel.update(_anabil.getUsers());
    // Re-calculate when the user's change.
    _userListModel.addListDataListener(new ListDataListener() {
      public void intervalAdded(ListDataEvent e) {
        identifyCalls();
      }
      public void intervalRemoved(ListDataEvent e) {
        identifyCalls();
      }
      public void contentsChanged(ListDataEvent e) {
        identifyCalls();
      }
      
    });
    // Or the user selects something in the table.
    _callsTableModel.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        identifyCalls();
      }
    });
    _billModel = new BillModel(_anabil);    
  }
  
  /**
   * Intialise the application GUI.  Note this method does
   * not return.
   */
  private void initGui() {
    _applicationFrame = new ApplicationFrame(this);
    _applicationFrame.setSize(800, 600);
    _applicationFrame.setVisible(true);
  }

  /**
   * Load a bill from a file.
   * 
   * @param file  the file to read the bill from.
   * @throws IOException
   *           if we fail to load the bill.
   */
  public void loadBill(final File file) throws IOException {
    final BillLoader loader = new BTBillLoader();
    _billModel.update(loader.loadBill("bill", file));
    final Bill bill = _billModel.getBill();
    if (bill != null) {
      _callsTableModel.update(bill.getCalls());
    }
    identifyCalls();
  }
  
  /**
   * Add a new user to the list model.
   * TODO: this is a hack, we should listen for events and use them
   * to keep anabil in sync.
   * @param user the new user.
   */
  public void addNewUser(final Person user) {
    _anabil.addUser(user);
    int index = _userListModel.getSize() - 1;
    if (index < 0) {
      index = 0;
    }
    _userListModel.insertElementAt(user, index);
  }
  
  /**
   * Remove a person from the application.
   * @param person the person to remove. 
   */
  public void removeUser(final Person person) {
    _anabil.removeUser(person);
    _userListModel.removeElement(person);
    try {
      person.delete();
    }
    catch (IOException e) {
      // What to do?
    }
  }
  
  /**
   * Get the user list model.
   * Note: this method always returns the same model instance, so it
   * is safe to retain references.
   * @return the users list model.
   */
  public PersonsListModel getPersonsModel() {
    return _userListModel;
  }
  
  /**
   * Get the calls table model.
   * Note: this method always returns the same model instance, so it
   * is safe to retain references.
   * @return the calls table model.
   */
  public CallsTableModel getCallsModel() {
    return _callsTableModel;
  }

  /**
   * Exit with a given exit code.
   * 
   * @param exitCode
   *          the exit code to use.
   */
  public void exit(int exitCode) {
    System.exit(exitCode);
  }

  /**
   * Causes the application to save persistent state to disk.
   * 
   * @throws IOException
   *           if it fails to do so due to an IO error.
   */
  public void saveState() throws IOException {
    _anabil.saveUsers();
  }

  /**
   * Close the currently loaded bill.
   */
  public void closeBill() {
    _billModel.update(new SimpleBill(new Call[] {}));
    _callsTableModel.update(new Call[] {});
  }

  /**
   * Identify the calls in the current bill using the available information.
   */
  public void identifyCalls() {
    _billModel.identifyCalls();
  }

  /**
   * @return the user loading exception, or null if it all went ok.
   */
  public final IOException getUserLoadingException() {
    return _userLoadingException;
  }
  
  /**
   * Notify the application of an update to a user that is not
   * done via a model.
   * @param user the user that has been updated.
   */
  public void notifyOfUpdatedUser(final Person user) {
    _userListModel.notifyOfUpdate(user);    
  }

  /**
   * Get the current bill model.
   * @return the bill model.
   */
  public BillModel getBillModel() {
    return _billModel;
  }
  
  /**
   * Look up a user by name.
   * @param name the name of the user.
   * @return the user if they exist.
   */
  public Person getUserByName(final String name) {
    return _anabil.getUser(name);
  }

}
