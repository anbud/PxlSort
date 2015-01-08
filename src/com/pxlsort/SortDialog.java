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