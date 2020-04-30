import javax.swing.JFrame;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Color;
import java.io.*;
import javax.swing.*;

/*
Circle class extends JPanel which is being created in the main function
*/
public class Circle extends JPanel {
    public List<PlotPoint> pp;
    public int clicked_X = -1;
    public int clicked_Y = -1;
    public int released_X = -1;
    public int released_Y = -1;
    public boolean drawCircle = false;
    public boolean draggingCircle = false;

    /*
    Circle constructor defined the prefered size of the JPanel and
    it subscribes to mouse click events on the JPanel
    */
    public Circle(){
      setPreferredSize(new Dimension(700, 700));
      pp =  generatePlotPoints(100, 100, 25);

      addMouseMotionListener(new MouseMotionListener(){
        public void mouseMoved(MouseEvent e){}
        public void mouseDragged(MouseEvent e){
          draggingCircle = true;
          released_X = e.getX();
          released_Y = e.getY();
          repaint();
        }
      });
      addMouseListener(new MouseListener(){
                    public void mouseClicked(MouseEvent e){}
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                    public void mouseMoved(MouseEvent event){}
                    public void mousePressed(MouseEvent e) {
                      clicked_X = e.getX();
                      clicked_Y = e.getY();
                    }
                    public void mouseReleased(MouseEvent e) {
                      released_X = e.getX();
                      released_Y = e.getY();
                      if(Math.abs(released_X - clicked_X) < 5
                        && Math.abs(released_Y - clicked_Y) < 5){
                        //do nothing
                      }else{
                          drawCircle = true;
                      }
                      repaint();
                  }
        });
    }

    /*
    generatePlotPoints functions builds the list of PlotPoints which defines
    the 20*20 matrix space
    */
    public List<PlotPoint> generatePlotPoints(int x_start, int y_start, int distance){
      List<PlotPoint> pp = new ArrayList<PlotPoint>();
        for(int i = 0; i < 20 ; i++){
           for(int j = 0; j < 20; j++){
             pp.add(new PlotPoint(x_start + i*distance, y_start + j*distance));
           }
        }
        return pp;
    }

    public void paint(Graphics g) {
      super.paintComponent(g);
        if(drawCircle){
          int x = Math.abs(clicked_X - released_X);
          int y = Math.abs(clicked_Y - released_Y);
          int radius = (int)Math.sqrt(x*x + y*y);
          g.setColor(Color.BLUE);
          //Adding 5 to handle the width of the point itself
          g.drawOval(clicked_X - radius, clicked_Y - radius,
                      2*radius + 5, 2*radius + 5);
          for(PlotPoint p : pp){
            if(p.getSelected()){
              p.flipSelected();
            }
          }
          int inner_radius = Integer.MAX_VALUE;
          int outer_radius = Integer.MIN_VALUE;
          /*
          Intuition: Since the grid is of size 20*20, circle on the perimeter
          of the grid would have radius 10 and circumference ~63 units.
          The circle covers 360 degrees, so assuming that all points will be on
          circumference, the points will be sepereated by an angle of ~ 360/63
          which is equal to ~ 5.7
          Hence we are incrementing the angle by 5 degrees and finding radian
          points on the circumference of the circle.
          Using these points, we are then finding the closest points in the grid
          */
          for(int angle = 0; angle < 360; angle+=5){
            int min = Integer.MAX_VALUE;
            PlotPoint ref = null;
            int x_d = (int)(radius*Math.cos(angle) + clicked_X);
            int y_d = (int)(radius*Math.sin(angle) + clicked_Y);
            for(PlotPoint p: pp){
              int m = Math.abs(p.getX() - x_d);
              int n = Math.abs(p.getY() - y_d);
              int dist = (int)Math.sqrt(m*m + n*n);
              if(dist < min){
                min = dist;
                ref = p;
              }
            }
            for(PlotPoint p: pp){
              if(p == ref){
                int x_c = Math.abs(clicked_X - p.getX());
                int y_c = Math.abs(clicked_Y - p.getY());
                int current_radius = (int)Math.sqrt(x_c*x_c + y_c*y_c);
                if(current_radius > outer_radius){ outer_radius = current_radius;}
                if(current_radius < inner_radius){ inner_radius = current_radius;}
                if(!p.getSelected()){
                  p.flipSelected();
                }
              }
            }
          }
          g.setColor(Color.RED);
          //Adding 5 to handle the width of the point itself
          g.drawOval(clicked_X - inner_radius, clicked_Y - inner_radius,
                      2*inner_radius+5, 2*inner_radius+5);
          g.drawOval(clicked_X - outer_radius, clicked_Y - outer_radius,
                      2*outer_radius+5, 2*outer_radius+5);
          //cleanup
          drawCircle = false;
          draggingCircle = false;
          clicked_X = -1;
          clicked_Y = -1;
          released_X = -1;
          released_Y = -1;
        }else if(draggingCircle){
          int x = Math.abs(clicked_X - released_X);
          int y = Math.abs(clicked_Y - released_Y);
          int radius = (int)Math.sqrt(x*x + y*y);
          g.drawOval(clicked_X - radius, clicked_Y - radius,
                      2*radius+5, 2*radius+5);
        }

        for(PlotPoint p : pp){
          if(p.getSelected()){
            g.setColor(Color.BLUE);
            g.fillRect(p.getX(), p.getY(), 5, 5);
            p.flipSelected();
          }else{
            g.setColor(Color.GRAY);
            g.fillRect(p.getX(), p.getY(), 5, 5);
          }
        }
    }

    /*
    Main function
    */
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JFrame frame = new JFrame(" Circles");
          frame.getContentPane().add(new Circle());
          frame.pack();
          frame.setVisible(true);
          frame.setResizable(false);
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
      });
    }
}

/*
PlotPoint class defines the point on the matrix
*/
class PlotPoint {
  private int x,y;
  private boolean selected;
  public PlotPoint(int x, int y){
      this.x = x;
      this.y = y;
      this.selected = false;
  }
  /*
    Getter function for x value
  */
  public int getX(){
    return this.x;
  }
  /*
    Getter function for y value
  */
  public int getY(){
    return this.y;
  }
  /*
    function to flip the selected value
  */
  public void flipSelected(){
    this.selected = !this.selected;
  }
  /*
    Getter function for selected value
  */
  public boolean getSelected(){
    return this.selected;
  }
}
