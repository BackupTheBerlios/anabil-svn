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

import javax.swing.event.EventListenerList;

import anabil.Anabil;
import anabil.Bill;

/**
 * Adapts a Bill to provide useful events for swing components.
 * 
 * @author mth
 */
public class BillModel {
  
  /**
   * The bill.
   */
  private Bill _bill;

  /**
   * The event listeners. (Seems rather excessive, maybe change to simpler
   * implentation).
   */
  private final EventListenerList _listenerList = new EventListenerList();

  /**
   * The bill event (see coment for {@link #_listenerList}!).
   */
  private BillRecalculationEvent _billEvent = null;

  /**
   * The bill analyser to use.
   */
  private final Anabil _anabil;

  
  /**
   * Constructs.
   * @param anabil the bill analyser to use.
   */
  public BillModel(final Anabil anabil) {
    _anabil = anabil;
  }

  /**
   * Add a listener to this bill model.
   * @param listener the listener to add.
   */
  public void addBillListener(final BillListener listener) {
    _listenerList.add(BillListener.class, listener);
  }

  /**
   * Remove a listener from this bill model.
   * @param listener the listener to remove.
   */
  public void removeBillListener(BillListener listener) {
    _listenerList.remove(BillListener.class, listener);
  }

  /**
   * Fire a bill changed event to all listeners.
   */
  protected void fireBillRecalculated() {
    Object[] listeners = _listenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == BillListener.class) {
        if (_billEvent == null) {
         _billEvent = new BillRecalculationEvent(this);
        }
        ((BillListener) listeners[i + 1]).billRecalculated(_billEvent);
      }
    }
  }

  /**
   * Delegates to {@link Bill#updateTotals()} then fires an event.
   */
  public void identifyCalls() {
    if (_bill != null) {
      _anabil.identifyCalls(_bill.getCalls());
      _bill.updateTotals();
      fireBillRecalculated();     
    }
  }
  
  /**
   * Get the bill this model is based on.
   * Could be null if {@link #update} has not been called.
   * @return the bill this model is based on. 
   */
  public Bill getBill() {
    return _bill;
  }
  
  /**
   * Update the bill this model is based on.
   * @param bill the new bill.
   */
  public void update(final Bill bill) {
    _bill = bill;
  }
  
}
