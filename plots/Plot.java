import javax.swing.JFrame;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.Color;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.BorderLayout;

/*
Plot class extends JPanel which is being created in the main function
*/
public class Plot extends JPanel {

    public List<PlotPoint> pp;
    public HashSet<PlotPoint> selectedPp = new HashSet<PlotPoint>();
    public int clicked_X = -1;
    public int clicked_Y = -1;
    public static boolean buttonPressed = false;

    /*
    Plot constructor defined the prefered size of the JPanel and
    it subscribes to mouse click events on the JPanel
    */
    public Plot(){
      setPreferredSize(new Dimension(700, 700));
      pp =  generatePlotPoints(100, 100, 25);
      addMouseListener(new MouseListener(){
                    public void mouseClicked(MouseEvent e){
                      clicked_X = e.getX();
                      clicked_Y = e.getY();
                      for(PlotPoint p : pp){
                        if(clicked_X >= p.getX() && clicked_X <= p.getX() + 5
                          && clicked_Y >= p.getY() && clicked_Y <= p.getY() + 5){
                              p.flipSelected();
                              if(p.getSelected()){
                                selectedPp.add(p);
                              }else{
                                selectedPp.remove(p);
                              }
                          }
                      }
                      repaint();
                    }
                    public void mouseEntered(MouseEvent e) {}
                    public void mouseExited(MouseEvent e) {}
                    public void mouseMoved(MouseEvent event){}
                    public void mousePressed(MouseEvent e) {}
                    public void mouseReleased(MouseEvent e) {}
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
        for(PlotPoint p : pp){
          if(p.getSelected()){
            g.setColor(Color.BLUE);
            g.fillRect(p.getX(), p.getY(), 5, 5);
          }else{
            g.setColor(Color.GRAY);
            g.fillRect(p.getX(), p.getY(), 5, 5);
          }
        }
        if(buttonPressed){
            /*
            Intuition : find the two points which are most
            distant from each other, consider the center of the
            circle as the equidistant point from these two points
            */
            int rad = Integer.MIN_VALUE;
            PlotPoint p1 = new PlotPoint(100,100);
            for(PlotPoint start: selectedPp){
              for(PlotPoint end: selectedPp){
                int i = Math.abs(start.getX() - end.getX());
                int j = Math.abs(start.getY() - end.getY());
                int n = (int)Math.sqrt(i*i + j*j)/2;
                if(n > rad){
                  rad = n;
                  p1 = new PlotPoint((int)(start.getX() + end.getX())/2 ,
                                    (int)(start.getY() + end.getY())/2);
                }
              }
            }
            g.setColor(Color.BLUE);
            //Adding 5 to handle the width of the point itself
            g.drawOval(p1.getX() - rad, p1.getY() - rad,
                        2*rad+5, 2*rad+5);
            buttonPressed = false;
        }
    }

    /*
    main function
    Defines the JFrame and adds components
    It also subscribes to JButton's actionPerformed
    */
    public static void main(String[] args) {
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          JFrame frame = new JFrame(" Circles");
          frame.setSize(700, 800);
          JPanel jp = new Plot();
          frame.add(jp, BorderLayout.NORTH);
          JButton jb = new JButton("GENERATE");
          jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              buttonPressed = true;
              frame.repaint();
            }
          });
          frame.add(jb, BorderLayout.SOUTH);
          frame.pack();
          frame.setVisible(true);
          frame.setResizable(true);
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
