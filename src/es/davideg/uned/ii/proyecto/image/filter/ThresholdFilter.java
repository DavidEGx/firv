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

import java.awt.image.BufferedImage;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;

/**<p>Esta clase proporciona una función umbral sobre una imagen.<br />
 * A cada pixel con un valor por encima del umbral se le asignará un valor
 * máximo, mientras los que esten por debajo tendrán el valor minimo.</p>
 *
 * <p>El valor por defecto del umbral es 128</p>
 * 
 * <p>La imagen destino puede ser la misma que la imagen origen</p>
 * 
 * @see LookupOp
 * @see LookupTable
 * @author David Escribano García
 */
public class ThresholdFilter extends AbstractFilter
{
    private short thresold = 128;
    
    /**
     * Establece el valor del umbral.
     * @param thresold Nuevo valor del umbral
     */
    public void setThresold(final short thresold)
    {
        this.thresold = thresold;
    }

    /**
     * <p>
     * Aplica un filtro para obtener una imagen umbralizada a partir de la
     * imagen de entrada.
     * </p>
     * @param src La imagen a procesar
     * @param dest La imagen en la que se almacenaran los resultados del proceso, puede ser nula
     * @return Imagen con el filtro aplicado
     */
    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest)
    {
        if (dest == null)
            dest = createCompatibleDestImage(src, null);
        
        short[] data = new short[256];
        for (short i = 0; i < thresold; i++)
        {
            data[i] = 0;
        }
        for (short i = thresold; i < 256; i++)
        {
            data[i] = -1;
        }
        LookupTable lookupTable = new ShortLookupTable(0, data);
        LookupOp op = new LookupOp(lookupTable, null);
        return op.filter(src, dest);
    }
}
