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
 * Class containing information about data cell key. 
 * @author Maciej Kosarzecki
 */
public class DataCellKey {
    /**
     * Base64 encoded string commitment to a key. 
     */
    private String commitment; 
    
    /**
     * Base64 encoded string decommitment value. 
     */
    private String decommitment; 
    
    /**
     * Base64 encoded string value of a key. 
     */
    private String plaintext;
    
    /**
     * Determines if key is opened. 
     */
    private boolean opened;
    
    /**
     * Default constructor of this class. 
     * @param comm base64 encoded commitment to a key value. 
     */
    public DataCellKey(String comm)
    {
        this.commitment = comm;
        opened = false;
    }
    
    /**
     * Default constructor of this class. 
     * @param comm base64 encoded commitment to a key value. 
     * @param decomm base64 encoded decommitment value.
     * @param plain base64 encoded value of a key.
     */
    public DataCellKey(String comm, String decomm, String plain)
    {
        this(comm);
        this.decommitment = decomm;
        this.plaintext = plain;
        if(!decommitment.isEmpty() && !plaintext.isEmpty())
            opened = true;
    }
    
    /**
     * Tells if a key is opened. 
     * @return boolean value specifying if a key is opened. 
     */
    public boolean isOpened() { return opened; }
    
    /**
     * Retunrs base64 String commitment value. 
     * @return base64 String commitment value.
     */
    public String getCommitment() { return commitment; } 
    
    /**
     * Retunrs base64 String decommitment value. 
     * @return base64 String decommitment value.
     */
    public String getDecommitment() { return decommitment; } 
    
    /**
     * Retunrs base64 String plaintext value. 
     * @return base64 String plaintext value.
     */
    public String getPlaintext() { return plaintext; } 
}
