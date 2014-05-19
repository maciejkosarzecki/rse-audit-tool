/*
 * Copyright (C) 2014 Maciej Kosarzecki
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package lib;

/**
 * Class representing a single data row in a data table.
 * @author Maciej Kosarzecki
 */
public class DataRow {
    /**
     * Array of cells in a single row. 
     */
    private DataCell[] cells; 
    
    /**
     * Default constructor of this class. 
     */
    public DataRow()
    {
        cells = new DataCell[Lib.COL_NUMBER];
    }
    
    /**
     * Adds a data cell to a specified position in this data row. 
     * @param cell data cell to be added.
     * @param column column number. 
     */
    public void addCell(DataCell cell, int column)
    {
        if(column>=Lib.COL_NUMBER || column<0)
            throw new IllegalArgumentException("Column int value must be in range of 0..columns_number-1");
        cells[column] = cell;
    }
    
    /**
     * Returns a data cell from a specified position.
     * @param column column number.
     * @return data cell from a specified position. 
     */
    public DataCell getDataCell(int column)
    {
        if(column>=Lib.COL_NUMBER || column<0)
            throw new IllegalArgumentException("Column int value must be in range of 0..columns_number-1");
        return cells[column];
    }
            
}
