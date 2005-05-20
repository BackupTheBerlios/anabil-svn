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

package anabil;

/**
 * A telephone bill.
 * 
 * @author mth
 */
public interface Bill {

  /**
   * Get all the calls in this bill.
   * @return the calls in this bill.
   */
  Call[] getCalls();

  /**
   * Update the totals for the users who have been allocated calls in this bill.
   */
  void updateTotals();
  
  /**
   * Get the total payable in this bill by the given user since
   * the last call to updateTotals.
   * @param user the user to check the total for.
   * @return the current total for the given user.
   */
  public double getTotalFor(final Person user);
  
}