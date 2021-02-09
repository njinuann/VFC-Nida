/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 *
 * @author Pecherk
 */
public class EIStream extends ByteArrayOutputStream implements Serializable
{
    private Color color = Color.BLACK;

    public EIStream(Color color)
    {
        setColor(color);
    }

    @Override
    public void flush()
    {
        String message = toString();
//        if (message.length() > 0)
//        {
//            CFEMain.displayArea.append(message, getColor());
//            reset();
//        }
    }

    /**
     * @return the color
     */
    public Color getColor()
    {
        return color;
    }

    /**
     * @param color the color to set
     */
    public final void setColor(Color color)
    {
        this.color = color;
    }
}
