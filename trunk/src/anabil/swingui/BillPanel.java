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

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A panel to display the current bill.
 * @author mth
 */
class BillPanel extends JPanel {

  /**
   * A reference back to the main application, where most interesting things
   * happen.
   */
  private Application _application;
  
  /**
   * Contstructs.
   * @param model       the model to use.
   */
  public BillPanel(final Application application) {
    _application = application;
    
    initModel();
    initGui();
  }

  /**
   * Initialise the link to the application data models.
   */
  private void initModel() {

  }

  /**
   * Initialise the GUI.
   */
  private void initGui() {
    setLayout(new BorderLayout());
    add(new JScrollPane(new CallsTable(_application)),
        BorderLayout.CENTER);
  }

}
