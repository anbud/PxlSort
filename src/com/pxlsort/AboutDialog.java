package com.pxlsort;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AboutDialog extends JDialog {
	private static final long serialVersionUID = 6268703702230129630L;

	public AboutDialog() {
		JLabel desc = new JLabel("<html><body style='width: 140px;'>" + Info.NAME + " is a small application used for sorting pixels in order to create interesting glitch effects (also called glitch art). Currently, it can sort pixels, randomize their order and transpose the image.</body></html>", SwingConstants.CENTER);
		JLabel name = new JLabel(Info.NAME + " " + Info.VERSION, SwingConstants.CENTER);

		name.setFont(new Font("Serif", Font.BOLD, 26));

		add(name, BorderLayout.NORTH);
		add(desc);

		setSize(220, 180);
		setVisible(true);
	}
}