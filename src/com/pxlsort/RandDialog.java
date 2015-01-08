package com.pxlsort;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

public class RandDialog extends JDialog {
	private static final long serialVersionUID = -1629330603619813780L;
	private int value;

	public int getValue() {
		return value;
	}

	public RandDialog(int max) {
		final JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, max, max/2);

		slide.setMajorTickSpacing(10);
		slide.setMinorTickSpacing(1);

		final JButton ok = new JButton("OK");		

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				value = slide.getValue();

				dispose();
			}
		});

		TitledBorder sliderTitle = BorderFactory.createTitledBorder("Amount of randomization");
		slide.setBorder(sliderTitle);

		add(slide);

		add(ok, BorderLayout.SOUTH);

		setTitle(Info.NAME + " - Randomization");

		setSize(250, 120);
		setVisible(true);
	}
}