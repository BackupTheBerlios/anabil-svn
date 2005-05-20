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
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * The main application GUI frame.
 * 
 * @author mth
 */
class ApplicationFrame extends JFrame {

  /**
   * The bills panel.
   */
  private BillPanel _billsPanel;

  /**
   * The users panel.
   */
  private JPanel _usersPanel;

  /**
   * Close the open bill.
   */
  public class CloseAction extends AbstractAction {

    public CloseAction() {
      super("Close");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e) {
      _application.closeBill();
    }

  }

  /**
   * Open a new bill.
   */
  public class OpenAction extends AbstractAction {

    public OpenAction() {
      super("Open");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(final ActionEvent e) {
      final JFileChooser chooser = new JFileChooser();
      chooser.showOpenDialog(ApplicationFrame.this);
      final File file = chooser.getSelectedFile();
      if (file != null) {
        try {
          _application.loadBill(file);
        }
        catch (IOException exception) {
          JOptionPane.showMessageDialog(ApplicationFrame.this,
              "Could not load bill:" + exception.getMessage(), "Error",
              JOptionPane.ERROR);
        }
      }
    }

  }

  /**
   * Exit the app.
   */
  public class ExitAction extends AbstractAction {

    public ExitAction() {
      super("Exit");
      putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_X));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
      cleanlyExit();
    }
  }

  /**
   * A reference back to the main application, where most interesting things
   * happen.
   */
  private Application _application;

  /**
   * Constructs.
   * 
   * @param application
   *          a reference back to the main the application.
   */
  public ApplicationFrame(final Application application) {
    super("Anabil");

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        cleanlyExit();
      }
    });

    _application = application;

    initGui();
    // Ming!
    IOException ex = _application.getUserLoadingException();
    if (ex != null) {
      JOptionPane
          .showMessageDialog(ApplicationFrame.this, "Could not load users:"
              + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }


  /**
   * Cleanly exit.
   */
  private void cleanlyExit() {
    int exitCode = 0;
    try {
      _application.saveState();
    }
    catch (IOException ex) {
      exitCode = 1;
      JOptionPane
          .showMessageDialog(ApplicationFrame.this, "Could not save users:"
              + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    finally {
      _application.exit(exitCode);
    }
  }

  /**
   * Initialise the GUI.
   */
  private void initGui() {
    final Container pane = getContentPane();
    pane.setLayout(new BorderLayout());
    final JTabbedPane tabs = new JTabbedPane();
    _billsPanel = new BillPanel(_application);
    _usersPanel = new PeoplePanel(_application);
    tabs.add("Bill", _billsPanel);
    tabs.add("People", _usersPanel);
    pane.add(tabs);
    // If there aren't any people set the focus to the people panel.
    if (_application.getPersonsModel().getSize() == 0) {
        tabs.setSelectedIndex(1);
    }

    JPanel summaryArea = new TotalsPanel(_application);
    pane.add(summaryArea, BorderLayout.SOUTH);

    initMenu();
  }

  /**
   * Build the menu.
   */
  private void initMenu() {
    JMenuBar menu = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic(KeyEvent.VK_F);
    fileMenu.add(new JMenuItem(new OpenAction()));
    fileMenu.add(new JMenuItem(new CloseAction()));
    fileMenu.add(new JMenuItem(new ExitAction()));
    menu.add(fileMenu);
    setJMenuBar(menu);
  }

}