package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import solvers.GuiAndSolverIntegrator;

/**
 *
 * @author Sourabh Bhat
 */
public class MainWindow extends JFrame {
    
    public MainWindow() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
        }
        showAboutDialog();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("*** Time stepping solution and stability demonstration - Programmed by Sourabh Bhat ***");
        setSize(600, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(iconImage());
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpMenuItem = new JMenuItem("About");
        helpMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(helpMenuItem);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        getContentPane().setLayout(new BorderLayout());
        InputPanel inputPanel = new InputPanel();
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        ActionButtonsPanel actionButtonsPanel = new ActionButtonsPanel();
        getContentPane().add(actionButtonsPanel, BorderLayout.SOUTH);
        PlotPanel plotPanel = new PlotPanel();
        getContentPane().add(plotPanel, BorderLayout.CENTER);
        GuiAndSolverIntegrator integrator = new GuiAndSolverIntegrator(inputPanel, actionButtonsPanel, plotPanel);
        integrator.integrate();
        
        CaptionAnimation captionAnimation = new CaptionAnimation(this);
        captionAnimation.start();
    }
    static double gaussianConstA;
    static boolean increasingIcon = true;

    public static Image iconImage() {
        int width = 64, height = 64;
        BufferedImage iconImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        // Gaussian function plot on image
        gaussianConstA += (increasingIcon ? 0.1 : -0.1);
        increasingIcon = (gaussianConstA > 1.0 || gaussianConstA < 0.0 ? !increasingIcon : increasingIcon);
        double a = gaussianConstA * height;
        double k = 0.005;
        Graphics2D g2d = iconImage.createGraphics();
        g2d.setColor(Color.red);
        for (int i = 0; i < width; i++) {
            int x = i;
            double y = (int) (a * Math.exp(-k * Math.pow(x - width / 2, 2.0)));
            
            g2d.drawLine(x, (int) Math.round(height - (0.1 * height + y)), x, height);
        }
        
        return (iconImage);
    }
    
    private String helpMsg() {
        return "This is a tool for CFD learners and CFD teachers "
                + "to demonstrate the effects of "
                + "changing parameters like Courant number, space differencing, "
                + "time differencing, number of divisions and stopping time on "
                + "the numerical solution of Advection or Diffusion PDE.\n"
                + "Enter the parameters and make selections in the top panel, "
                + "and then click the play or final solution button.\n"
                + "My advice is to modify only one parameter at a time and observe the effects "
                + "due to change of the parameter on the solution. This will make "
                + "you see the effect of the parameter more clearly. "
                + "Please click the 'Read Inputs & Reset' button after changing parameters "
                + "to consider the new parameters before clicking play or else "
                + "old values will be used.\n"
                + "The initialization is automatically done by the program as a "
                + "combination of various functions.\n\n"
                + "Programmed by:\n"
                + "Sourabh Bhat\n"
                + "Assistant Professor, Department of Aerospace Engineering\n"
                + "University of Petroleum and Energy Studies\n"
                + "Dehradun, India - 248007\n"
                + "Email: mail.sourabhbhat@gmail.com; "
                + "heySourabh@gmail.com; "
                + "sourabh.bhat@iitbombay.org; "
                + "spbhat@ddn.upes.ac.in";
    }
    
    private void showAboutDialog() {
        JTextArea textArea = new JTextArea(helpMsg(), 21, 80);
        JScrollPane msgScrollPane = new JScrollPane(textArea);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, msgScrollPane, "About", JOptionPane.INFORMATION_MESSAGE);
    }
}

class CaptionAnimation implements Runnable {
    
    Thread thread;
    JFrame frame;
    
    public CaptionAnimation(JFrame frame) {
        this.frame = frame;
        thread = new Thread(this);
    }
    
    public void start() {
        thread.start();
    }
    
    @Override
    public void run() {
        
        while (true) {
            while (!frame.isActive()) {
                delay(1000);
            }
            
            delay(200);
            String oldTitle = frame.getTitle();
            String newTitle = oldTitle.substring(1) + oldTitle.charAt(0);
            frame.setTitle(newTitle);
            frame.setIconImage(MainWindow.iconImage());
        }
    }
    
    private void delay(long timeInMillis) {
        try {
            Thread.sleep(timeInMillis);
        } catch (InterruptedException ex) {
            // Do nothing
        }
    }
}