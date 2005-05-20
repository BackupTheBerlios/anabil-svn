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


/**
 * An event that indicates a bill has been re-calculated. 
 * @author mth
 */
public class BillRecalculationEvent {
  
  /**
   * The bill that was the source of the event.
   */
  private BillModel _source;
  
  /**
   * Contstructs.
   * @param source the bill model that was the source of this event.
   */
  public BillRecalculationEvent(final BillModel source) {
    _source = source;
  }

  /**
   * Gets the bill model that was the source of the event.
   * @return the bill model that was the source of the event.
   */
  public BillModel getSource() {
    return _source;
  }

}
