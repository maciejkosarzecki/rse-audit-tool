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

import java.io.UnsupportedEncodingException;
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
            
        } catch (ZipException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (AuditException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** 
     * Performs the audit of the elections.
     */
    public void audit()
    {
        checkOpenedKeysCorrectness();
        checkKeyCommitmentsConsistency();
        decryptCells();
//        int n = 0;
    }
    
    /**
     * Checks consistency of opened keys. 
     * @return true if consistency kept, false otherwise. 
     */
    private boolean checkOpenedKeysCorrectness()
    {
        boolean correct = true;
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
                    {
                        correct = false;
                        System.out.println("Warning! Table: "+table.getNumber()+" column: "+(i+1)+" key inconsistency found!");
                    }
            }
        }
        if(correct)
            System.out.println("Opened keys consistency kept!");
        else
            System.out.println("Warning! Inconsistency found in opened keys!");
        return correct;
    }
    
    /**
     * Checks consistency of decommitment and plaintext values of an opened key.
     * @param openedComm String commitment value.
     * @param openedDecomm String decommitment value.
     * @param openedPlain String plaintext value. 
     * @return true if consistency is kept, false otherwise. 
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
    
    /**
     * Checks consistency of commitments to keys and commitmenets to opened keys.
     * @return true if consistency is kept, false otherwise.
     */
    private boolean checkKeyCommitmentsConsistency()
    {
        boolean correct = true;
        for(int i=0; i<dataTablesKeys.length; i++)
        {
            for(int j=0; j<Lib.COL_NUMBER; j++)
            {
                if(dataTablesKeys[i].getCellKeys()[j].isOpened())
                {
                    if(!dataTablesKeys[i].getCellKeys()[j].getCommitment()
                            .equals(dataTables[i].getCellKeys()[j].getCommitment()))
                    {
                        correct = false;
                        System.out.println("Warning! Table: "+dataTables[i].getNumber()+" column: "+(j+1)+" key commitments to a key"
                                + " is different from key commitment to an opened key! Commitment to a key: "
                            +dataTables[i].getCellKeys()[j].getCommitment()+" commitment to an opened key: "
                            +dataTablesKeys[i].getCellKeys()[j].getCommitment());
                    } 
                    else
                    {
                        System.out.println("Table: "+dataTables[i].getNumber()+" column: "+(j+1)+" commitment to a key: "
                            +dataTables[i].getCellKeys()[j].getCommitment()+" commitment to an opened key: "
                            +dataTablesKeys[i].getCellKeys()[j].getCommitment());
                    }
                }
            }
        }
        if(correct)
            System.out.println("Consistency of commitments to keys and "
                    + "commitments to opened keys kept!");
        else
            System.out.println("Warning! Inconsistency found in commitments "
                    + "to keys and commitments to opened keys!");
        return correct;
    }
    
    private void decryptCells()
    {
        DataCellKey[] keys;
        
        // for each table
        for(int i=0; i<dataTables.length; i++)
        {
            keys = dataTablesKeys[i].getCellKeys();
            // for each column of a table
            for(int column=0; column<Lib.COL_NUMBER; column++)
            {
                if(keys[column].isOpened())
                {
                    DecryptionKit cipher = new DecryptionKit();
                    cipher.setKey(DatatypeConverter.parseBase64Binary(keys[column].getPlaintext()));
                    for(DataRow row : dataTables[i].getDataRows())
                    {
                        String c = row.getDataCell(column).getCiphertext();
                        String iv = row.getDataCell(column).getInitialVector();
                        byte[] byteC = DatatypeConverter.parseBase64Binary(c);
                        byte[] byteIV = DatatypeConverter.parseBase64Binary(iv);
                        byte[] out = cipher.decrypt(byteIV,byteC);
                        String message = DatatypeConverter.printBase64Binary(out);
                        String messageString = new String(out);
                        
                        System.out.println(messageString);
                        message +="!!!!!";
                        row.getDataCell(column).setPlaintext(messageString);
                    }
                }
            }
        }
    }
    
    private void verifyTally()
    {
        int yesB3 = 0;
        int noB3 = 0;
        int yesB4 = 0;
        int noB4 = 0; 
        
        // verify tally for batch #3
//        for(DataTable table : )
    }
}
