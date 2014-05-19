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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.lingala.zip4j.exception.ZipException;

/**
 * Main class of the application. 
 * @author Maciej Kosarzecki
 */
public class Auditor {
    
    DataTable [] dataTables;
    DataTable [] dataTablesKeys;
    ExtractionKit ek;
    
    /**
     * Default constructor.
     */
    public Auditor()
    {
        ek = new ExtractionKit();
    }
    
    public void extractABB(String abbPath)
    {
        try {
            ek.extractFiles("/home/maciej/Studia/RSE/audit_data/");
            dataTables = ek.extractData("/home/maciej/Studia/RSE/audit_data/");
            dataTablesKeys = ek.extractDataKeys("/home/maciej/Studia/RSE/audit_data/");
            int n=0; 
        } catch (ZipException ex) {
            Logger.getLogger(Auditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
