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

package es.davideg.uned.ii.proyecto.image.filter;

import es.davideg.uned.ii.proyecto.image.GraphicsUtilities;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.security.InvalidParameterException;

/**
 * <p>Esta clase permite la transformación de una imagen en un valor numerico.</p>
 * @author David Escribano García
 */
public class NumericFilter
{
    /**
     * <p>Transforma una imagen en un valor numerico.</p>
     * <p>La transformación se realiza asociando a cada pixel un valor binario
     * (0 o 1) en función del valor del pixel. Con la matriz resultante se forma
     * un número binario empenzando por el valor superior izquierdo y terminando
     * por el inferior derecho. Finalmente ese valor se transforma a entero.</p>
     * @param src La imagen a procesar
     * @return Valor numerico que representa la imagen
     * @throws InvalidParameterException Si la imagen de entrada no es en blanco y negro
     */
    public BigInteger filter(final BufferedImage src)
    {
        if (src.getType() != BufferedImage.TYPE_BYTE_GRAY)
            throw new InvalidParameterException("Image must be of TYPE_BYTE_GRAY type");

        BigInteger dest = BigInteger.ZERO;
        byte[] data = GraphicsUtilities.getPixels(src, 0, 0, src.getWidth(), src.getHeight(), (byte[])null);
        
        
        for (byte pixel : data)
        {
            dest = dest.shiftLeft(1);

            if (pixel == -1)
                dest = dest.add(BigInteger.ONE);
        }
        return dest;
    }
}
