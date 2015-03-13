/*
 *  PxlSort 0.7.2
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