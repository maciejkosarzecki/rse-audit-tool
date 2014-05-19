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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 * Class used for data extraction from ABB.zip file. 
 * @author Maciej Kosarzecki
 */
public class ExtractionKit {
    
    /**
     * @param abbPath String path of the ABB.zip file. 
     * @return array of DataTable objects.  
     * @throws net.lingala.zip4j.exception.ZipException thrown when cannot 
     * extract specified .zip file. 
     */
    public DataTable[] extractData(String abbPath) throws ZipException
    {
        // extracts ABB.zip
        extractZipFile(abbPath+Lib.ABB_FILE_NAME, abbPath);
        
        // parses .csv table files
        return parseTableFiles(abbPath+Lib.ABB_EXTRACTION_DESTINATION_FOLDER);
        
        
    }
    
    /**
     * Extracts a .zip file. 
     * @param filePath file path.
     * @param destPath destination directory. 
     */
    private void extractZipFile(String filePath, String destPath) throws ZipException
    {
        ZipFile zipFile = new ZipFile(filePath);
        zipFile.extractAll(destPath);
    }
    
    /**
     * Parses .csv table files in which are in directory specified. 
     * @param path 
     */
    private DataTable[] parseTableFiles(String path)
    {
        File[] tableFiles;
        
        DataTable[] prepareATables;
        DataTable[] prepareBTables;
        DataTable[] finalizeTables;
        DataTable[] auditTables;
        DataTable[] tables;
        
        File directory = new File(path);
        
        TableFilesFilter prepareATablesFilter
                = new TableFilesFilter(Lib.ABB_PREPARE_A_FILES_NAME);
        TableFilesFilter prepareBTablesFilter
                = new TableFilesFilter(Lib.ABB_PREPARE_B_FILES_NAME);
        TableFilesFilter finalizeTablesFilter
                = new TableFilesFilter(Lib.ABB_FINALIZE_FILES_NAME);
        TableFilesFilter auditTablesFilter
                = new TableFilesFilter(Lib.ABB_AUDIT_FILES_NAME);
        

        ArrayList<DataRow> auditDataRows;
        
        DataCellKey[] cellKeys;
        
        // parse prepare A type .csv files
        tableFiles = directory.listFiles(prepareATablesFilter);
        prepareATables = new DataTable[tableFiles.length];
        
        for(int i=0; i<prepareATables.length; i++)
        {
            prepareATables[i] = parseTableFile(tableFiles[i]);
        }
        
        // parse prepare B type .csv files
        tableFiles = directory.listFiles(prepareBTablesFilter);
        prepareBTables = new DataTable[tableFiles.length];
        
        for(int i=0; i<prepareBTables.length; i++)
        {
            prepareBTables[i] = parseTableFile(tableFiles[i]);
        }
        
        // parse finalize .csv files
        tableFiles = directory.listFiles(finalizeTablesFilter);
        finalizeTables = new DataTable[tableFiles.length];
        
        for(int i=0; i<finalizeTables.length; i++)
        {
            finalizeTables[i] = parseTableFile(tableFiles[i]);
        }
        
        // parse audit .csv files
        tableFiles = directory.listFiles(auditTablesFilter);
        auditTables = new DataTable[tableFiles.length];
        
        for(int i=0; i<auditTables.length; i++)
        {
            auditTables[i] = parseTableFile(tableFiles[i]);
        }
        
        Arrays.sort(prepareATables);
        Arrays.sort(prepareBTables);
        Arrays.sort(finalizeTables);
        Arrays.sort(auditTables);
  
        // merge tables 
        tables = mergeTables(prepareATables, prepareBTables, finalizeTables);
        return tables;
    }
    
    /**
     * Parses a single .csv table file. 
     * @param file .csv file.
     * @return DataTable object created. 
     */
    private DataTable parseTableFile(File file)
    {
        DataTable table = null;
        int tableNumber;
        
        
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            String linePattern;
            int row = 1;
            String[] fields;
            String comm, plain, decomm;
                        
            
            linePattern = "([ \\w\\+\\=/]*,){23}[ \\w\\+\\=/]*";
            Pattern dataRowPattern = Pattern.compile(linePattern);
            
            while((line = br.readLine())!=null)
            {
                Matcher matcher = dataRowPattern.matcher(line);
                
                if(matcher.matches())
                {
                    fields = line.split(",",-1);
                    
                    if(row == 1)
                    {
                        // handling table number row
                        tableNumber = Integer.parseInt(fields[0]);
                        table = new DataTable(tableNumber);
                    } else if(row == 2)  
                    {
                        // keys row handling
                        comm = "";
                        plain = "";
                        decomm = "";
                        
                        for(int j=0; j<fields.length; j++)
                        {
                            switch(j%Lib.COL_CELLS_NUMBER)
                            {
                                    case 0: 
                                        comm = "";
                                        plain = "";
                                        decomm = "";
                                        comm = fields[j];
                                        break;
                                    case 1: 
                                        plain = fields[j];
                                        break;
                                    case 2: 
                                        decomm = fields[j];
                                        table.addCellKey(new DataCellKey(comm, decomm, plain), j/Lib.COL_CELLS_NUMBER);
                                        break;
                            }
                            
                        }
                    } else
                    {
                        // data rows handling
                        DataRow dataRow = new DataRow();
                        String initialVector, cipherText;
                        String [] data;
                        
                        for(int j=0; j<fields.length; j++)
                        {
                            switch(j%Lib.COL_CELLS_NUMBER)
                            {
                                    case 0: 
                                        if(!fields[j].isEmpty())
                                        {
                                            data = fields[j].split(
                                            Lib.IV_CIPHERTEXT_DELIMITING_STRING);
                                            initialVector = data[0];
                                            cipherText = data[1];
                                            dataRow.addCell(new DataCell(initialVector,cipherText), j/Lib.COL_CELLS_NUMBER);
                                        }
                                        
                                        break;
                                    case 1: 
                                    case 2: 
                                        break;
                            }
                            
                        }
                        table.addDataRow(dataRow);
                    }
                    row++;
                }
            }
            

//            System.out.println(tableNumber + " " + file.getName());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractionKit.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtractionKit.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return table;
    }
    
    /**
     * Function used for merging data tables of different type published 
     * separately by EA. 
     * @param prepareATables ABB prepare type A data tables
     * @param prepareBTables ABB prepare type B data tables
     * @param finalizeTables ABB finalize data tables
     * @return merged data tables
     */
    private DataTable[] mergeTables(DataTable[] prepareATables,
            DataTable[] prepareBTables, DataTable[] finalizeTables)
    {
        ArrayList<DataRow> prepareADataRows;
        ArrayList<DataRow> prepareBDataRows;
        ArrayList<DataRow> finalizeDataRows;
        
        DataTable [] tables = new DataTable[prepareATables.length];
        
        for(int i=0; i<tables.length; i++)
        {
            DataCellKey[] cellKeys = new DataCellKey[Lib.COL_NUMBER];
            
            // get data rows if possible
            // get prepare A data rows
            if(i < prepareATables.length)
            {
                prepareADataRows = prepareATables[i].getDataRows();
                cellKeys[Lib.COL_HEADER_SN_AND_VC] = prepareATables[i].getCellKeys()[Lib.COL_HEADER_SN_AND_VC];
                cellKeys[Lib.COL_HEADER_POSS_VOTE] = prepareATables[i].getCellKeys()[Lib.COL_HEADER_POSS_VOTE];
                cellKeys[Lib.COL_HEADER_PRE_SUM_A] = prepareATables[i].getCellKeys()[Lib.COL_HEADER_PRE_SUM_A];
                cellKeys[Lib.COL_HEADER_PRE_SUM_B] = prepareATables[i].getCellKeys()[Lib.COL_HEADER_PRE_SUM_B];
                
                if(i < prepareBTables.length)
                {
                    prepareBDataRows = prepareBTables[i].getDataRows();
                    cellKeys[Lib.COL_HEADER_FINAL_SUM_A] = prepareBTables[i].getCellKeys()[Lib.COL_HEADER_FINAL_SUM_A];
                    cellKeys[Lib.COL_HEADER_FINAL_SUM_B] = prepareBTables[i].getCellKeys()[Lib.COL_HEADER_FINAL_SUM_B];
                    for(int j=0; j<prepareBDataRows.size(); j++)
                    {
                        prepareADataRows.get(j).addCell(
                                prepareBDataRows.get(j).getDataCell(Lib.COL_HEADER_FINAL_SUM_A),
                                Lib.COL_HEADER_FINAL_SUM_A);
                        prepareADataRows.get(j).addCell(
                                prepareBDataRows.get(j).getDataCell(Lib.COL_HEADER_FINAL_SUM_B),
                                Lib.COL_HEADER_FINAL_SUM_B);
                    }
                    if(i < finalizeTables.length)
                    {
                        finalizeDataRows = finalizeTables[i].getDataRows();
                        cellKeys[Lib.COL_HEADER_P_CHECK] = finalizeTables[i].getCellKeys()[Lib.COL_HEADER_P_CHECK];
                        cellKeys[Lib.COL_HEADER_MARK_VOTED] = finalizeTables[i].getCellKeys()[Lib.COL_HEADER_MARK_VOTED];
                        for(int j=0; j<prepareBDataRows.size(); j++)
                        {
                            prepareADataRows.get(j).addCell(
                                    finalizeDataRows.get(j).getDataCell(Lib.COL_HEADER_P_CHECK),
                                    Lib.COL_HEADER_P_CHECK);
                            prepareADataRows.get(j).addCell(
                                    finalizeDataRows.get(j).getDataCell(Lib.COL_HEADER_MARK_VOTED),
                                    Lib.COL_HEADER_MARK_VOTED);
                        }
                    }
                    
                }
                
                // create new table from prepare A data rows 
                DataTable table = new DataTable(i);
                // add filled data rows 
                for(DataRow row : prepareADataRows)
                    table.addDataRow(row);
                // add cell keys
                for(int j=0; j<Lib.COL_NUMBER; j++)
                {
                    table.addCellKey(cellKeys[j], j);
                }
                tables[i] = table;
            }
        }
        return tables;
    }
    

    
    
    
    
}

/**
 * Class implementing FileFilter interface used for finding .csv data table 
 * files in a specified directory. 
 * @author Maciej Kosarzecki
 */
class TableFilesFilter implements FileFilter
{
    private final String type;
    
    /**
     * Default constructor.
     * @param type String type of a data table. Allowed values:
     * prepare_a
     * prepare_b
     * finalize
     * audit
     */
    public TableFilesFilter(String type)
    {
        this.type = type;
    }
    
    @Override
    public boolean accept(File file) 
    {
        if(file.isDirectory())
            return false;
        else
        {
            String path = file.getAbsolutePath().toLowerCase();
            String name = file.getName();
            return path.endsWith(".csv") && name.contains("xyz_table") && 
                    name.contains(type);
        }
    }
    
}

/**
 * Class implementing FileFilter interface used for finding .csv vote codes 
 * table file in a specified directory. 
 * @author Maciej Kosarzecki
 */
class VoteCodesFileFilter implements FileFilter
{

    @Override
    public boolean accept(File file) 
    {
        if(file.isDirectory())
            return false;
        else
        {
            String path = file.getAbsolutePath().toLowerCase();
            String name = file.getName();
            return path.endsWith(".csv") && name.contains("xyz_table") && name.contains("votecodes_sorted");
        }
    }
    
}
