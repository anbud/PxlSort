/*
 *  PxlSort 0.4.1
 * 
 *  Copyright (C) 2014-2015 - Andrej Budinčević
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.

 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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