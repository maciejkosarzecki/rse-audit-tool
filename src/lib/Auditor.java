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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;
import net.lingala.zip4j.exception.ZipException;

/**
 * Main class of the application. 
 * @author Maciej Kosarzecki
 */
public class Auditor {
    
    private DataTable [] dataTables;
    private DataTable [] dataTablesKeys;
    private ExtractionKit ek;
    private MessageDigest md;
    
    /**
     * Default constructor.
     */
    public Auditor()
    {
        try {
            ek = new ExtractionKit();
            md = MessageDigest.getInstance(Lib.HASH_FUNCTION);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Extracts .csv files from a specified .zip erchive and creates 
     * DataTable[] objects. 
     * @param abbPath String path to ABB .zip file. 
     */
    public void extractABB(String abbPath)
    {
        try {
            ek.extractFiles(abbPath);
            dataTables = ek.extractData(abbPath);
            dataTablesKeys = ek.extractDataKeys(abbPath);
            
            int n = 0;
        } catch (ZipException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void audit()
    {
        checkOpenedKeysCorrectness();
    }
    
    private void checkOpenedKeysCorrectness()
    {
        for(DataTable table : dataTablesKeys)
        {
            for(int i=0; i<Lib.COL_NUMBER; i++)
            {
                if(table.getCellKeys()[i].isOpened())
                    if(checkOpenedKeyCorrectness(
                            table.getCellKeys()[i].getCommitment(),
                            table.getCellKeys()[i].getDecommitment(),
                            table.getCellKeys()[i].getPlaintext()))
                        System.out.println("Table: "+table.getNumber()+" column: "+(i+1)+" key consistency kept!");
                else
                        System.out.println("Table: "+table.getNumber()+" column: "+(i+1)+" key inconsistency found!");
            }
        }
    }
    
    /**
     * Checks consistency of decommitment and plaintext values of an opened key.
     * @param openedComm String commitment value.
     * @param openedDecomm String decommitment value.
     * @param openedPlain String plaintext value. 
     * @return if consistency is kept. 
     */
    private boolean checkOpenedKeyCorrectness(String openedComm,
            String openedDecomm, String openedPlain)
    {
        
        byte [] plain = DatatypeConverter.parseBase64Binary(openedPlain);
        byte [] decomm = DatatypeConverter.parseBase64Binary(openedDecomm);
        byte [] digestEntry = new byte [plain.length + decomm.length];
        System.arraycopy(plain, 0, digestEntry, 0, plain.length);
        System.arraycopy(decomm, 0, digestEntry, plain.length, decomm.length);
        byte comm [] = md.digest(digestEntry);
        String computedComm = DatatypeConverter.printBase64Binary(comm);
        return openedComm.equals(computedComm);
    }
    
    
}
