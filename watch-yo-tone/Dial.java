//file: Dial.java
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;

public class Dial extends JComponent {
  int minValue, value, maxValue, radius;
  
  public Dial( ) { this(0, 100, 0); }
  
  public Dial(int minValue, int maxValue, int value) {
    this.minValue = minValue; 
    this.maxValue = maxValue;
    this.value = value;
    setForeground(Color.lightGray);

    addMouseListener(new MouseAdapter( ) {
      public void mousePressed(MouseEvent e) { spin(e); }
    });
    addMouseMotionListener(new MouseMotionAdapter( ) {
      public void mouseDragged(MouseEvent e) { spin(e); }
    });
  }
  
  protected void spin(MouseEvent e) {
    int y = e.getY( );
    int x = e.getX( );
    double th = Math.atan((1.0 * y - radius) / (x - radius));
    int value=((int)(th / (2 * Math.PI) * (maxValue - minValue)));
    if (x < radius)
      setValue(value + maxValue / 2);
    else if (y < radius)
      setValue(value + maxValue);
    else
      setValue(value);
  }

  public void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D)g;
    int tick = 10;
    radius = getSize( ).width / 2 - tick;
    g2.setPaint(getForeground().darker( ));
    g2.drawLine(radius * 2 + tick / 2, radius,
        radius * 2 + tick, radius);
    g2.setStroke(new BasicStroke(2));
    draw3DCircle(g2, 0, 0, radius, true);
    int knobRadius = radius / 10;
    double th = value * (2 * Math.PI) / (maxValue - minValue);
    int x = (int)(Math.cos(th) * (radius - knobRadius * 3)),
    y = (int)(Math.sin(th) * (radius - knobRadius * 3));
    g2.setStroke(new BasicStroke(1));
    draw3DCircle(g2, x + radius - knobRadius,
                 y + radius - knobRadius, knobRadius, false );
  }

  private void draw3DCircle( Graphics g, int x, int y,
                             int radius, boolean raised) {
    Color foreground = getForeground( );
    Color light = foreground.brighter( );
    Color dark = foreground.darker( );
    g.setColor(foreground);
    g.fillOval(x, y, radius * 2, radius * 2);
    g.setColor(raised ? light : dark);
    g.drawArc(x, y, radius * 2, radius * 2, 45, 180);
    g.setColor(raised ? dark : light);
    g.drawArc(x, y, radius * 2, radius * 2, 225, 180);
  }

  public Dimension getPreferredSize( ) {
    return new Dimension(100, 100);
  }

  public void setValue(int value) {
    firePropertyChange( "value", this.value, value );
    this.value = value;
    repaint( );
    fireEvent( );
  }
  public int getValue( )  { return value; }
  public void setMinimum(int minValue)  { this.minValue = minValue; }
  public int getMinimum( )  { return minValue; }
  public void setMaximum(int maxValue)  { this.maxValue = maxValue; }
  public int getMaximum( )  { return maxValue; }

  public void addDialListener(DialListener listener) {
    listenerList.add( DialListener.class, listener );
  }
  public void removeDialListener(DialListener listener) {
    listenerList.remove( DialListener.class, listener );
  }

  void fireEvent( ) {
    Object[] listeners = listenerList.getListenerList( );
    for ( int i = 0; i < listeners.length; i += 2 )
      if ( listeners[i] == DialListener.class )
        ((DialListener)listeners[i + 1]).dialAdjusted( 
          new DialEvent(this, value) );
  }
}