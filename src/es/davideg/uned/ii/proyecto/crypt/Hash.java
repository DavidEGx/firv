/** Copyright 2010 David Escribano García
*
* Author: David Escribano García
* Director: Jesús Antonio Vega Sánchez (CIEMAT)
* Supervisor: Sebastián Dormido Canto (UNED)
*
* Licensed under the EUPL, Version 1.1 or - as soon they will be
* approved by the European Commission - subsequent versions of the
* EUPL (the "Licence"); you may not use this work except in
* compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://www.osor.eu/eupl/european-union-public-licence-eupl-v.1.1
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the Licence is distributed on an "AS
* IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied.
* See the Licence for the specific language governing permissions
* and limitations under the Licence.
*/

package es.davideg.uned.ii.proyecto.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Permite calcular el hash de un fichero.
 * @author David Escribano García
 */
public class Hash
{

    /**
     * Obtiene el hash SHA de un fichero
     * @return Hash del video
     * @throws NoSuchAlgorithmException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getHash(File f) throws NoSuchAlgorithmException, FileNotFoundException, IOException
    {

        MessageDigest md = MessageDigest.getInstance("SHA");
        InputStream fis = new FileInputStream(f);
        byte[] buffer = new byte[1024];
        int numRead;
        do
        {
            numRead = fis.read(buffer);
            if (numRead > 0)
            {
                md.update(buffer, 0, numRead);
            }
        } while (numRead != -1);
        fis.close();
        byte[] digestBytes = md.digest();

        String result = "";
        for (int i=0; i < digestBytes.length; i++)
        {
            result += Integer.toString( ( digestBytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return result;
    }
}
