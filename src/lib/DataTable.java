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

import java.util.ArrayList;

/**
 *
 * @author Maciej Kosarzecki
 */
public class DataTable implements Comparable {
    
    /**
     * Int number of a table. 
     */
    private final int number;
    
    /**
     * Number of a batch this table belongs to. 
     */
    private Integer batch;
    
    /**
     * An array of cells keys. 
     */
    private DataCellKey [] cellKeys;
    

    
    /**
     * An ArrayList containing rows of data. 
     */
    private ArrayList<DataRow> rows;
    
    
    /**
     * Default constructor.
     * @param number int number of a table. 
     */
    public DataTable(int number)
    {
        this.number = number;
        cellKeys = new DataCellKey[Lib.COL_NUMBER];
        rows = new ArrayList<>();
        batch = null;
        
    }
    
    /**
     * Adds new data row to already existing. 
     * @param row 
     */
    public void addDataRow(DataRow row) { rows.add(row); }
    
    
    /**
     * Returns data row at the specified index. 
     * @param index int index of data row. 
     * @return data row at the specified index. 
     */
    public DataRow getDataRow(int index) { return rows.get(index); }
    
    /**
     * Returns data rows of this table. 
     * @return ArrayList of data rows. 
     */
    public ArrayList<DataRow> getDataRows() { return rows; }
 
    /**
     * Adds new cell key to a specidied position. 
     * @param cellKey cell key to be added. 
     * @param column column number that added key referes to.
     */
    public void addCellKey(DataCellKey cellKey, int column)
    {
        if(column>=Lib.COL_NUMBER || column<0)
            throw new IllegalArgumentException("Column int value must be in range of 0..columns_number-1");
        cellKeys[column] = cellKey;
    }
    
    /**
     * Returns data cell keys of this table. 
     * @return DataCellKey objects array. 
     */
    public DataCellKey[] getCellKeys() { return cellKeys; }
    
    /**
     * Returns number of this table. 
     * @return int table number.
     */
    public int getNumber() { return number; }
    
    public void specifyBatch() throws AuditException
    {
        if(cellKeys[Lib.COL_HEADER_SN_AND_VC].isOpened()
                && cellKeys[Lib.COL_HEADER_P_CHECK].isOpened()
                && cellKeys[Lib.COL_HEADER_MARK_VOTED].isOpened())
            batch = 1;
        else if (cellKeys[Lib.COL_HEADER_P_CHECK].isOpened()
                && cellKeys[Lib.COL_HEADER_POSS_VOTE].isOpened())
            batch = 2;
        else if (cellKeys[Lib.COL_HEADER_POSS_VOTE].isOpened()
                && cellKeys[Lib.COL_HEADER_MARK_VOTED].isOpened()
                && cellKeys[Lib.COL_HEADER_PRE_SUM_A].isOpened()
                && cellKeys[Lib.COL_HEADER_FINAL_SUM_A].isOpened())
            batch = 3;
        else if (cellKeys[Lib.COL_HEADER_POSS_VOTE].isOpened()
                && cellKeys[Lib.COL_HEADER_MARK_VOTED].isOpened()
                && cellKeys[Lib.COL_HEADER_PRE_SUM_B].isOpened()
                && cellKeys[Lib.COL_HEADER_FINAL_SUM_B].isOpened())
            batch = 4;
        else if (cellKeys[Lib.COL_HEADER_SN_AND_VC].isOpened()
                && cellKeys[Lib.COL_HEADER_FINAL_SUM_A].isOpened()
                && cellKeys[Lib.COL_HEADER_FINAL_SUM_B].isOpened())
            batch = 5;
        else 
            throw new AuditException("Cannot specify batch of a table number "
                    +number+" for specified keys opened!");
    }

    
    @Override
    public int compareTo(Object t) {
        DataTable table = (DataTable)t;
        int secondNumber = table.getNumber();
        if (number < secondNumber) 
        {
            return -1;
        } 
        else if (number == secondNumber)
        {
            return 0;
        }
        else 
        {
            return 1;
        }
    }
    
}
