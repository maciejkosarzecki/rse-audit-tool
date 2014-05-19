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
 * Class containing useful data used in this auditing tool. 
 * @author Maciej Kosarzecki
 */
public class Lib {
    /**
     * Column header - serial number & vote code - column number 1. 
     */
    public static int COL_HEADER_SN_AND_VC = 0;
    
    /**
     * Column header - print check - column number 2.
     */
    public static int COL_HEADER_P_CHECK = 1;
    
    /**
     * Column header - possible vote - column number 3. 
     */
    public static int COL_HEADER_POSS_VOTE = 2;
    
    /**
     * Column header - marked voted - column number 4.
     */
    public static int COL_HEADER_MARK_VOTED = 3;
    
    /**
     * Column header - pre summand A - column number 5. 
     */
    public static int COL_HEADER_PRE_SUM_A = 4;
    
    /**
     * Column header - pre summand B - column number 6.
     */
    public static int COL_HEADER_PRE_SUM_B = 5;
    
    /**
     * Column header - final summand A - column number 7.
     */
    public static int COL_HEADER_FINAL_SUM_A = 6;
    
    /**
     * Column header - final summand B - column number 8.
     */
    public static int COL_HEADER_FINAL_SUM_B = 7;
    
    /**
     * Int number of columns in a table of data.
     */
    public static int COL_NUMBER = 8;
    
    /**
     * Int number of actual cells in for each column of data (at the momemnt 
     * commitment, plaintext, decommitment). 
     */
    public static int COL_CELLS_NUMBER = 3;
    
    /**
     * String characters delimiting initial vector from ciphertext 
     * data rows of data tables. 
     */
    public static String IV_CIPHERTEXT_DELIMITING_STRING = "  ";
    
    /**
     * String name of audit buletin board package. 
     */
    public static String ABB_FILE_NAME = "ABB.zip";
    
    /**
     * String name of ABB extraction destination folder. 
     */
    public static String ABB_EXTRACTION_DESTINATION_FOLDER = "Archives/";
    
    /**
     * String that is included in ABB prepare type A .csv files name.
     */
    public static String ABB_PREPARE_A_FILES_NAME = "prepare_a";
    
    /**
     * String that is included in ABB prepare type B .csv files name.
     */
    public static String ABB_PREPARE_B_FILES_NAME = "prepare_b";
    
    /**
     * String that is included in ABB final .csv files name.
     */
    public static String ABB_FINALIZE_FILES_NAME = "finalize";
    
    /**
     * String that is included in ABB audit .csv files name.
     */
    public static String ABB_AUDIT_FILES_NAME = "audit";
    
    /**
     * String type of hash function used for key commitments. 
     */
    public static String HASH_FUNCTION = "SHA-1";
}
