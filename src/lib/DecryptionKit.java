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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Toolkit used for columns decryption. 
 * @author Maciej Kosarzecki
 */
public class DecryptionKit {
    
    /**
     * Secret key. 
     */
    private SecretKeySpec keySpec;
    
    /**
     * Cipher used.
     */
    private Cipher cipher;
    
    /**
     * Default constructor. 
     */
    public DecryptionKit()
    {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    /**
     * Sets secret key used for decryption. 
     * @param key byte representation of a key. 
     */
    public void setKey(byte[] key)
    {
        try {
            keySpec = new SecretKeySpec(key, Lib.ENCRYPTION_ALGORITHM);
            cipher = Cipher.getInstance("AES/CBC/ZeroBytePadding", "BC");
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException ex) {
            Logger.getLogger(DecryptionKit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Decrypts ciphrtext for the specified initial vector. 
     * @param initialVector byte representation of initial vector. 
     * @param ciphertext byte representation of ciphertext.
     * @return byte representation of plaintext. 
     */
    public byte[] decrypt(byte[] initialVector, byte[] ciphertext)
    {
        byte [] plaintext = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(initialVector));
            plaintext = cipher.doFinal(ciphertext);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(DecryptionKit.class.getName()).log(Level.SEVERE, null, ex);
        }
        return plaintext;
    }
}
