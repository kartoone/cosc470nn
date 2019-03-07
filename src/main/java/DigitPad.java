/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JWindow;

/**
 *
 * @author kartoone
 */
public class DigitPad extends JWindow implements MouseListener, MouseMotionListener {

    class Image {
        public byte imgbytes[][] = new byte[28][28];
        Image (byte b[][]) {
            this.imgbytes = b;
        }
    }

    public Image image;            // the image for our window
    public double strokewidth;     // training dataset is 28x28 so no matter the height of the window 
    
    public DigitPad(int width, int height) {
        super();
        setSize(width, height);
        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(new Color(255,255,255));
        setVisible(true);
        strokewidth = width/28.0;
        byte b[][] = new byte[28][28];
        image = new Image(b);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        for(int y=0; y<image.imgbytes.length; y++) {
            for (int x=0; x<image.imgbytes[y].length; x++) {
                int inverted = 255 - image.imgbytes[x][y]&0xFF;
                g2d.setColor(new Color(inverted,inverted,inverted));
                g2d.setStroke(new BasicStroke((float)strokewidth));
                g2d.drawLine((int)Math.round(x*strokewidth), (int)Math.round(y*strokewidth), (int)Math.round(x*strokewidth), (int)Math.round(y*strokewidth));
            }
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = (int)(e.getX()/strokewidth);
        int y = (int)(e.getY()/strokewidth);
        if (x>27) x = 27;
        if (y>27) y = 27;
        image.imgbytes[x][y]=(byte)255;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = (int)(e.getX()/strokewidth);
        int y = (int)(e.getY()/strokewidth);
        if (x>27) x = 27;
        if (y>27) y = 27;
        image.imgbytes[x][y]=(byte)255;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
    
    
}
