package com.pxlsort;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

public class SortDialog extends JDialog {
	private static final long serialVersionUID = -256962939081259275L;
	private int by = 1, dir = 1;

	public int getBy() {
		return by;
	}

	public int getDir() {
		return dir;
	}

	public SortDialog() {
		setTitle(Info.NAME + " - Sort options");

		JPanel choice = new JPanel(new GridLayout(2, 1));
		JPanel colChoice = new JPanel(new FlowLayout());
		JPanel dirChoice = new JPanel(new FlowLayout());

		final JRadioButton red = new JRadioButton("Red", true);
		final JRadioButton green = new JRadioButton("Green");
		final JRadioButton blue = new JRadioButton("Blue");

		final JRadioButton hor = new JRadioButton("Horizontal", true);
		final JRadioButton ver = new JRadioButton("Vertical");
		final JRadioButton diag = new JRadioButton("Diagonal");

		/* Diagonal sorting is not functional yet */
		diag.setEnabled(false);

		JButton ok = new JButton("OK");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(red.isSelected()) 
					by = 1;
				else if(green.isSelected()) 
					by = 2;
				else 
					by = 3;

				if(hor.isSelected())
					dir = 1;
				else
					dir = 2;

				dispose();
			}
		});

		TitledBorder colTitle = BorderFactory.createTitledBorder("Color");
		colChoice.setBorder(colTitle);

		TitledBorder dirTitle = BorderFactory.createTitledBorder("Direction");
		dirChoice.setBorder(dirTitle);

		ButtonGroup colChB = new ButtonGroup();

		colChB.add(red);
		colChB.add(green);
		colChB.add(blue);

		colChoice.add(red);
		colChoice.add(green);
		colChoice.add(blue);

		ButtonGroup dirChB = new ButtonGroup();

		dirChB.add(hor);
		dirChB.add(ver);
		dirChB.add(diag);

		dirChoice.add(hor);
		dirChoice.add(ver);
		dirChoice.add(diag);

		choice.add(colChoice);
		choice.add(dirChoice);

		add(choice);
		add(ok, BorderLayout.SOUTH);

		setSize(300, 170);
		setVisible(true);
	}
}