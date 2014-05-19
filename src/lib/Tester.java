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
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

/**
 *
 * @author Maciej Kosarzecki
 */
public class Tester {
    public static void main(String args[])
    {
//        try {
//            byte [] plain = DatatypeConverter.parseBase64Binary("9ifxW/ApXvr1Xy5kBwoivw==");
//            byte [] decomm = DatatypeConverter.parseBase64Binary("j/eECFn/W/OR7Q9LVVkeAw==");
//             
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            
//            byte[] c = new byte [plain.length + decomm.length];
//            System.arraycopy(plain, 0, c, 0, plain.length);
//            System.arraycopy(decomm, 0, c, plain.length, decomm.length);
//            
//            byte [] out = md.digest(c);
//            String s = DatatypeConverter.printBase64Binary(out);
//            System.out.println(s);
//        } catch (NoSuchAlgorithmException ex) {
//            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        try 
//        {
//            ZipFile zipFile = new ZipFile("/home/maciej/Studia/RSE/audit_data/ABB.zip");
//            zipFile.extractAll("/home/maciej/Studia/RSE/audit_data/ABB_extracted");		
//	} 
//        catch(ZipException e) 
//        {
//            e.printStackTrace();
//        }
//        ExtractionKit ek = new ExtractionKit();
//        try {
//            ek.extractFiles("/home/maciej/Studia/RSE/audit_data/");
//        } catch (ZipException ex) {
//            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
//        }
        Auditor auditor = new Auditor();
        auditor.extractABB("/home/maciej/Studia/RSE/audit_data/");
    }
}
