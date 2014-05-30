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
import java.util.ArrayList;
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
    private ArrayList<ArrayList<Integer>> batches;
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
            batches = new ArrayList<>(Lib.BATCHES_NUMBER);
            for(int i=0; i<Lib.BATCHES_NUMBER; i++)
                batches.add(new ArrayList<Integer>());
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
            // copy keys to dataTables
            setDataTableKeys();
            
            // create ArrayLists with information about tables batches
            for(DataTable t : dataTables)
            {
                batches.get(t.getBatch()-1).add(t.getNumber());
            }
            
            
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
        checkBatchesConsistency();
        try {
            verifyTally();
        } catch (AuditException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                        
//                        row.getDataCell(column).setPlaintext(messageString);
                        row.getDataCell(column).setPlaintext(message);
                    }
                }
            }
        }
    }
    
    private void setDataTableKeys()
    {
        for(int i=0; i<dataTables.length; i++)
        {
            try {
                dataTables[i].setCellKeys(dataTablesKeys[i].getCellKeys());
            } catch (AuditException ex) {
                Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean checkBatchesConsistency()
    {
        int sum = 0;
        for(int i=0; i<batches.size(); i++)
        {
            System.out.println("Batch "+(i+1)+" - " + batches.get(i).size()+" table(s)!");
            sum+=batches.get(i).size();
        }
        System.out.println(sum+" table(s) in total!");
        return checkBatch1Consistency() && checkBatch3Consistency() && checkBatch4Consistency();
    }
    
    private boolean checkBatch1Consistency()
    {
        DataTable table;
        System.out.println("Batch 1 tables consistency check!");
        for(int i=0; i<batches.get(0).size(); i++)
        {
            table = dataTables[batches.get(0).get(i)];
            for(DataRow row : table.getDataRows())
            {
                String col1 = row.getDataCell(Lib.COL_HEADER_SN_AND_VC).getPlaintextASCIIEncoded();
                String col2 = row.getDataCell(Lib.COL_HEADER_P_CHECK).getPlaintextASCIIEncoded();
                String [] col1Split;
                col1Split = col1.split(Lib.COL_SN_AND_VC_DELIMITING_CHAR);
                
                String voted = row.getDataCell(Lib.COL_HEADER_MARK_VOTED).getPlaintextASCIIEncoded();
                if(!(col1Split[1].equals(col2) || col2.equals(Lib.COL_VALUE_NOT_CHECKED)))
                {
                    System.out.println("Inconsistency found! Batch 1 table "+table.getNumber());
                    return false;
                } 
                    
            }
        }
        System.out.println("Batch 1 tables consistency kept!");
        return true;
    }
    
    private boolean checkBatch34Consistency(int column1, int column2, int batch)
    {
        DataTable table;
        System.out.println("Batch "+batch+" tables consistency check!");
        for(int i=0; i<batches.get(batch-1).size(); i++)
        {
            table = dataTables[batches.get(batch-1).get(i)];
            for(DataRow row : table.getDataRows())
            {
                String col1 = row.getDataCell(column1).getPlaintextASCIIEncoded();
                String col2 = row.getDataCell(column2).getPlaintextASCIIEncoded();
                
                if(!(col1.equals(col2) || col1.equals(Lib.COL_VALUE_FAKE_VOTE)))
                {
                    System.out.println("Inconsistency found! Batch "+batch+" table "+table.getNumber());
                    return false;
                }
            }
        }
        System.out.println("Batch "+batch+" tables consistency kept!");
        return true;
    }
    
    private boolean checkBatch3Consistency()
    {
        return checkBatch34Consistency(Lib.COL_HEADER_PRE_SUM_A, Lib.COL_HEADER_FINAL_SUM_A, 3);
    }
    
    private boolean checkBatch4Consistency()
    {
        return checkBatch34Consistency(Lib.COL_HEADER_PRE_SUM_B, Lib.COL_HEADER_FINAL_SUM_B, 4);
    }
    
    private void verifyTally() throws AuditException
    {
        DataTable table;
        int [] yes = new int [batches.get(2).size()+batches.get(3).size()];
        int [] no = new int [batches.get(2).size()+batches.get(3).size()];
        
        int counter = 0;
        // batch 3
        for(int i=0; i<batches.get(2).size(); i++)
        {
            yes[counter] = 0;
            no[counter] = 0;
            
            table = dataTables[batches.get(2).get(i)];
            for(DataRow row : table.getDataRows())
            {
                String preA = row.getDataCell(Lib.COL_HEADER_PRE_SUM_A).getPlaintextASCIIEncoded();
                String finA = row.getDataCell(Lib.COL_HEADER_FINAL_SUM_A).getPlaintextASCIIEncoded();
                
                if(preA.equals(finA))
                {
                    String voted = row.getDataCell(Lib.COL_HEADER_MARK_VOTED).getPlaintextASCIIEncoded();
                    if(voted.equals(Lib.COL_VALUE_VOTED))
                    {
                        String vote = row.getDataCell(Lib.COL_HEADER_POSS_VOTE).getPlaintextASCIIEncoded();
                        if(vote.equals(Lib.COL_VALUE_YES_VOTE))
                            yes[counter]++;
                        else if (vote.equals(Lib.COL_VALUE_NO_VOTE))
                            no[counter]++;
                        else throw new AuditException(("Wrong value in column 3 - possible votes found!"));
                    }
                }
            }
            counter++;
        }
        
        // batch 4
        for(int i=0; i<batches.get(3).size(); i++)
        {
            yes[counter] = 0;
            no[counter] = 0;
            
            table = dataTables[batches.get(3).get(i)];
            for(DataRow row : table.getDataRows())
            {
                String preB = row.getDataCell(Lib.COL_HEADER_PRE_SUM_B).getPlaintextASCIIEncoded();
                String finB = row.getDataCell(Lib.COL_HEADER_FINAL_SUM_B).getPlaintextASCIIEncoded();
                
                if(preB.equals(finB))
                {
                    String voted = row.getDataCell(Lib.COL_HEADER_MARK_VOTED).getPlaintextASCIIEncoded();
                    if(voted.equals(Lib.COL_VALUE_VOTED))
                    {
                        String vote = row.getDataCell(Lib.COL_HEADER_POSS_VOTE).getPlaintextASCIIEncoded();
                        if(vote.equals(Lib.COL_VALUE_YES_VOTE))
                            yes[counter]++;
                        else if (vote.equals(Lib.COL_VALUE_NO_VOTE))
                            no[counter]++;
                        else throw new AuditException(("Wrong value in column 4 - possible votes found!"));
                    }
                }
            }
            counter++;
        }
        int yeses = -1;
        int nos = -1;
        boolean error = false;
        
        if(yes.length>0)
            yeses = yes[0];
        if(no.length>0)
            nos = no[0];
        // verify tally from different tables
        for(int i=1; i<yes.length; i++)
        {
            if(yes[i]!=yeses || no[i]!=nos)
            {
                error = true;
                break;
            }
        }
        if(error)
        {
            System.out.println("Critical error found when verifying a tally! Different number of YES/NO votes in different tables!");
        } else
        {
            System.out.println("Tally verified correctly!");
            System.out.println("Number of YES votes: "+yeses);
            System.out.println("Number of NO votes: "+nos);
        }
        
    }
}
