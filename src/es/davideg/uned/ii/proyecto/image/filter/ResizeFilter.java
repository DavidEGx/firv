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

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.security.InvalidParameterException;

/**
 * <p>Esta clase proporciona metodos para el escalado de una imagen de entrada.
 * </p>
 * 
 * <p>La imagen origen no puede ser la misma que la imagen destino
 * </p>
 * @author David Escribano García
 */
public class ResizeFilter extends AbstractFilter
{
    /** Establece como se va redimensionar la imagen */
    private Method method;

    private double scaleX, scaleY;
    private int width, height;
    private int maxWidth, maxHeight;

    /** Diferentes metodos de redimensionado de imagen */
    private static enum Method { dimensions, scale, maxdimensions }

    /**
     * <p>Establece que se va redimensionar la imagen usando los valores de escala
     * proporcionados.</p>
     * <p>Las dimensiones de la imagen final serán:<br />
     * <code>ancho_destino = ancho_origen * sx;<br /></code>
     * <code>alto_destino = alto_origen * sy;</code>
     * </p>
     * 
     * @param sx Factor de escala del ancho de la imagen
     * @param sy Factor de escala del alto de la imagen
     */
    public void setScale(final double sx, final double sy)
    {
        this.method = Method.scale;
        this.scaleX = sx;
        this.scaleY = sy;
    }

    /**
     * Establece las dimensiones de la imagen en valor absoluto.
     * @param width Ancho de la imagen destino.
     * @param height Alto de la imagen destino.
     * @param proporcional Indica si se deben mantener las proporciones de la
     * imagen. <br/>
     * Si es falso la imagen destino tendrá exactamente las dimensiones
     * <code>width x height</code>, mientras que si es falso las dimensiones
     * podrán ser menores para mantener la proporcionalidad.
     */
    public void setDimensions(final int width, final int height, final boolean proporcional)
    {
        if (proporcional)
        {
            this.method = Method.maxdimensions;
            this.maxWidth = width;
            this.maxHeight = height;
        }
        else
        {
            this.method = Method.dimensions;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Redimensiona la imagen al tamaño indicando previamente mediante la
     * llamada a los metodos <code>setScale</code> o <code>setDimensions</code>.
     * @param src La imagen a procesar
     * @param dest La imagen en la que se almacenaran los resultados del proceso, puede ser nula
     * @return Imagen con el filtro aplicado
     * @throws InvalidParameterException Si no se ha establecido el tamaño de la
     * imagen destino.
     */
    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dest)
    {
        if (method == null)
            throw new InvalidParameterException("You must call first setScale or seDimensions");

        fillData(src);

        if (dest == null)
            dest = createCompatibleDestImage(src, null);

        AffineTransform transform = AffineTransform.getScaleInstance(scaleX, scaleY);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        op.filter(src, dest);
        return dest;
    }

    /**
     * Redimensiona la imagen al tamaño indicando previamente mediante la
     * llamada a los metodos <code>setScale</code> o <code>setDimensions</code>.
     * @param src La imagen a procesar
     * @param dest La imagen en la que se almacenaran los resultados del proceso, puede ser nula
     * @return Imagen con el filtro aplicado
     * @throws InvalidParameterException Si no se ha establecido el tamaño de la
     * imagen destino.
     */
    public Image filter(final Image src, Image dest)
    {
        if (method == null)
            throw new InvalidParameterException("You must call first setScale or seDimensions");
       
        fillData(src);
        dest = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return dest;
    }
    
    /**
     * Crea una imagen vacia compatible con el resultado del filtro.<br />
     * La imagen tendrá el mismo formato que la imagen origen pero su tamaño
     * será diferente.
     * 
     * @param src Imagen de entrada
     * @param destCM El modelo de color de la imagen de destino, puede ser nulo
     * @return Imagen de destino compatible
     */
    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM)
    {
        if (destCM == null)
        {
            destCM = src.getColorModel();
        }

        return new BufferedImage(destCM, destCM.createCompatibleWritableRaster(this.width, this.height), destCM.isAlphaPremultiplied(), null);
    }

    /**
     * <p>Este metodo se encarga de rellenar las dimensiones o escalas
     * correspondientes a la imagen.</p>
     * <p>Se puede solicitar el redimensionado usando valores absolutos de
     * ancho y alto o bien especificar valores de escalado. <br />
     * Aqui se rellenan los datos que no ha introducido el usuario a partir de
     * los que si ha introducido. Por ejemplo, si el usuario introduce valores
     * absolutos de ancho y alto el proceso calculará los valores de escala
     * correspondientes.
     * </p>
     * @param src Imagen de entrada
     */
    private void fillData(Image src)
    {
        if (method == Method.scale)
        {
            width = (int)(scaleX  * src.getWidth(null));
            height = (int)(scaleY * src.getHeight(null));
        }
        else if (method == Method.dimensions)
        {
            scaleX = (double)width  / (double)src.getWidth(null);
            scaleY = (double)height / (double)src.getHeight(null);
        }
        else if (method == Method.maxdimensions)
        {
            final int imageWidth = src.getWidth(null);
            final int imageHeight = src.getHeight(null);

            final double widthScale = maxWidth / (double)imageWidth;
            final double heightScale = maxHeight / (double)imageHeight;

            scaleX = (heightScale < widthScale) ? heightScale : widthScale;
            scaleY = scaleX;

            width = (int) Math.round(imageWidth * scaleX);
            height = (int) Math.round(imageHeight * scaleX);
        }
    }
}
