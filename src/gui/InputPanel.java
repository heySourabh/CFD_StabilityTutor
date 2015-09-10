package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import main.DifferencingType;
import solvers.GuiAndSolverIntegrator;

/**
 *
 * @author Sourabh Bhat
 */
public class InputPanel extends JPanel {

    // PDE Type
    JLabel pdeLabel = new JLabel("Select PDE: ");
    public JComboBox<String> pdeComboBox = new JComboBox<>(new String[]{"Advection Equation", "Diffusion Equation"});
    JLabel pdePictureLabel = new JLabel();
    // PDE constant
    JLabel pdeConstantLabel = new JLabel("<html>PDE Constant (<font face=courier>a or " + '\u03b1' + "</font>): </html>");
    public JTextField pdeConstantTextField = new JTextField(5);
    // CFL input
    JLabel cflNumberLabel = new JLabel("Courant Number: ");
    public JTextField cflNumberTextField = new JTextField(5);
    // No. of Divisions
    JLabel numGridPointsLabel = new JLabel("No. of Grid Points: ");
    public JTextField numGridPointsTextField = new JTextField(5);
    // Min x
    JLabel minXLabel = new JLabel("Minumum x: ");
    public JTextField minXTextField = new JTextField(5);
    // Max x
    JLabel maxXLabel = new JLabel("Maximum x: ");
    public JTextField maxXTextField = new JTextField(5);
    // Stopping time
    JLabel stopTimeLabel = new JLabel("Stopping Time: ");
    public JTextField stopTimeTextField = new JTextField(5);
    // DifferencingType
    JLabel spaceDiffTypeLabel = new JLabel("Spatial Differencing scheme: ");
    public JComboBox<String> spaceDiffTypeComboBox;
    // Explicit/Implicit
    JLabel explicitImplicitLabel = new JLabel("Time Differencing scheme: ");
    public JComboBox<String> explicitImplicitComboBox = new JComboBox<>(new String[]{
        "Explicit Method", "Implicit Method"});

    public InputPanel() {
        setBorder(BorderFactory.createTitledBorder("Input for Solving PDE"));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        pdeComboBox.setEditable(false);
        DifferencingType[] dt = DifferencingType.values();
        String[] diffTypes = new String[dt.length];
        for (int i = 0; i < dt.length; i++) {
            diffTypes[i] = dt[i].getName();
        }
        spaceDiffTypeComboBox = new JComboBox<>(diffTypes);
        spaceDiffTypeComboBox.setEditable(false);

        setAllParameters();
        setPdeLabelPicture();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1.0;
        JPanel pdePanel = new JPanel();
        pdePanel.add(pdeLabel);
        pdePanel.add(pdeComboBox);
        pdeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setPdeLabelPicture();
            }
        });
        pdePanel.add(pdePictureLabel);
        add(pdePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JPanel pdeConstantPanel = new JPanel();
        pdeConstantPanel.add(pdeConstantLabel);
        pdeConstantPanel.add(pdeConstantTextField);
        add(pdeConstantPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JPanel cflNumberPanel = new JPanel();
        cflNumberPanel.add(cflNumberLabel);
        cflNumberPanel.add(cflNumberTextField);
        add(cflNumberPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JPanel numGridPointsPanel = new JPanel();
        numGridPointsPanel.add(numGridPointsLabel);
        numGridPointsPanel.add(numGridPointsTextField);
        add(numGridPointsPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JPanel minMaxXPanel = new JPanel();
        minMaxXPanel.add(minXLabel);
        minXTextField.setEnabled(false);
        minMaxXPanel.add(minXTextField);
        minMaxXPanel.add(maxXLabel);
        maxXTextField.setEnabled(false);
        minMaxXPanel.add(maxXTextField);
        add(minMaxXPanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        JPanel stopTimePanel = new JPanel();
        stopTimePanel.add(stopTimeLabel);
        stopTimePanel.add(stopTimeTextField);
        add(stopTimePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel diffTypePanel = new JPanel();
        diffTypePanel.add(spaceDiffTypeLabel);
        diffTypePanel.add(spaceDiffTypeComboBox);
        add(diffTypePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        JPanel explicitImplicitPanel = new JPanel();
        explicitImplicitPanel.add(explicitImplicitLabel);
        explicitImplicitPanel.add(explicitImplicitComboBox);
        add(explicitImplicitPanel, gbc);
    }

    private void setAllParameters() {
        GuiAndSolverIntegrator.resetAllParameters(this);
    }

    private void setPdeLabelPicture() {
        String item = (String) pdeComboBox.getSelectedItem();
        Image img;
        switch (item) {
            case "Advection Equation":
                img = new ImageIcon(getClass().getResource("AdvectionEqnPicture.PNG")).getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH);
                pdePictureLabel.setIcon(new ImageIcon(img));
                break;
            case "Diffusion Equation":
                img = new ImageIcon(getClass().getResource("DiffusionEqnPicture.PNG")).getImage().getScaledInstance(175, 50, Image.SCALE_SMOOTH);
                pdePictureLabel.setIcon(new ImageIcon(img));
                break;
            default:
                pdePictureLabel.setIcon(null);
                break;
        }
    }
}
