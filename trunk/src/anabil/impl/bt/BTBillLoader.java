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

package anabil.impl.bt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import anabil.Bill;
import anabil.BillLoader;
import anabil.Call;
import anabil.impl.SimpleBill;
import anabil.impl.SimpleCall;

import com.Ostermiller.util.ExcelCSVParser;

/**
 * A bill loader for BT bills.
 * If other companies make available CVS format bills then we should
 * have a CSV bill parser that takes a mapping from column to call
 * field or similar. 
 */
public class BTBillLoader implements BillLoader {

  public Bill loadBill(final String name, final File location)
      throws IOException {
    final List calls = new ArrayList();

    ExcelCSVParser parser = new ExcelCSVParser(new FileInputStream(location));

    int datePosition = -1;
    int locationPosition = -1;
    int numberPosition = -1;
    int costPosition = -1;
    final String[] headers = parser.getLine();
    for (int headerIndex = 0; headerIndex < headers.length; headerIndex++) {
      if ("Date".equals(headers[headerIndex])) {
        datePosition = headerIndex;
      }
      else if ("Destination".equals(headers[headerIndex])) {
        locationPosition = headerIndex;
      }
      else if ("CalledNo".equals(headers[headerIndex])) {
        numberPosition = headerIndex;
      }
      else if ("Cost".equals(headers[headerIndex])) {
        costPosition = headerIndex;
      }
    }

    if (datePosition == -1 || locationPosition == -1 || numberPosition == -1
        || costPosition == -1) {
      throw new IOException("Input is not in BT CSV format");
    }

    String[] line;
    while ((line = parser.getLine()) != null) {
      if (line.length == headers.length) {
        calls.add(new SimpleCall(line[datePosition].trim(),
            line[locationPosition].trim(), line[numberPosition].trim(), Double
                .parseDouble(line[costPosition])));
      }
    }

    return new SimpleBill((Call[]) calls.toArray(new SimpleCall[] {}));
  }

}