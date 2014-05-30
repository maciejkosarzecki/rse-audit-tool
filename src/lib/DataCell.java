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

import javax.xml.bind.DatatypeConverter;

/**
 * Class representing a single cell of table data.
 * @author Maciej Kosarzecki
 */
public class DataCell {
    /**
     * Base64 encoded initial vector used for cell encryption.
     */
    private final String initialVector;
    
    /**
     * Base64 encoded ciphertext.
     */
    private final String ciphertext;
    
    /**
     * Base64 encoded plaintext.
     */
    private String plaintext;
    
    /**
     * Default class constructor. 
     * @param IV base64 encoded initial vector used fot cell encryption.
     * @param c base64 encoded ciphertext. 
     */
    public DataCell(String IV, String c)
    {
        this.initialVector = IV;
        this.ciphertext = c;
    }
    
    
    public void setPlaintext(String plain)
    {
        plaintext = plain;
    }
    
    public String getCiphertext() { return ciphertext; }
    
    public String getInitialVector() { return initialVector; }
    
    public String getPlaintext() { return plaintext; }
    
    public String getPlaintextASCIIEncoded() 
    {
        return new String(DatatypeConverter.parseBase64Binary(plaintext));
    }
}
