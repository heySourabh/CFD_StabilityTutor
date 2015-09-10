package gui;

import java.awt.FlowLayout;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author Sourabh Bhat
 */
public class ActionButtonsPanel extends JPanel {

    public static ImageIcon finalSolutionIcon;
    public static ImageIcon playIcon;
    public static ImageIcon pauseIcon;
    public static ImageIcon resetIcon;
    // Final solution button
    public JButton finalSolutionButton = new JButton("Plot Final Solution");
    public JButton playOrPauseButton = new JButton("Play");
    // Reset button
    public JButton resetButton = new JButton("Read Inputs & Reset");
    // Speed Selector 
    public JSlider speedSlider = new JSlider(1, 100);

    public ActionButtonsPanel() {
        resetIcon = new ImageIcon(getClass().getResource("reload.png"));
        playIcon = new ImageIcon(getClass().getResource("player_play.png"));
        pauseIcon = new ImageIcon(getClass().getResource("player_pause.png"));
        finalSolutionIcon = new ImageIcon(getClass().getResource("player_end.png"));
        
        resetButton.setIcon(resetIcon);
        playOrPauseButton.setIcon(playIcon);
        finalSolutionButton.setIcon(finalSolutionIcon);

        setLayout(new FlowLayout());
        setBorder(BorderFactory.createTitledBorder("What do you want to do?"));
        add(resetButton);
        add(playOrPauseButton);
        add(finalSolutionButton);

        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(speedSlider.getMinimum(), new JLabel("Slow"));
        labels.put(speedSlider.getMaximum(), new JLabel("Fast"));
        speedSlider.setLabelTable(labels);
        speedSlider.setPaintLabels(true);
        speedSlider.setBorder(BorderFactory.createTitledBorder("Animation Speed"));
        add(speedSlider);
    }
}
