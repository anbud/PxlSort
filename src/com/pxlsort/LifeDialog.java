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
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;

public class LifeDialog extends JDialog {
	private static final long serialVersionUID = -1629330603619813780L;
	private int iter;
	private int by;

	public int getIterations() {
		return iter;
	}
	
	public int getBy() {
		return by;
	}

	public LifeDialog() {
		final JSlider slide = new JSlider(JSlider.HORIZONTAL, 0, 400, 200);

		slide.setMajorTickSpacing(50);
		slide.setMinorTickSpacing(1);
		slide.setPaintTicks(true);
		slide.setPaintLabels(true);
		
		JPanel choice = new JPanel(new GridLayout(2, 1));
		JPanel colChoice = new JPanel(new FlowLayout());

		final JRadioButton red = new JRadioButton("Red", true);
		final JRadioButton green = new JRadioButton("Green");
		final JRadioButton blue = new JRadioButton("Blue");

		JButton ok = new JButton("OK");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(red.isSelected()) 
					by = 1;
				else if(green.isSelected()) 
					by = 2;
				else 
					by = 3;
				
				iter = slide.getValue();

				dispose();
			}
		});

		TitledBorder colTitle = BorderFactory.createTitledBorder("Alive cells");
		colChoice.setBorder(colTitle);

		ButtonGroup colChB = new ButtonGroup();

		colChB.add(red);
		colChB.add(green);
		colChB.add(blue);

		colChoice.add(red);
		colChoice.add(green);
		colChoice.add(blue);

		add(colChoice);
		add(ok, BorderLayout.SOUTH);	

		TitledBorder sliderTitle = BorderFactory.createTitledBorder("Generations");
		slide.setBorder(sliderTitle);

		choice.add(slide);
		choice.add(colChoice);
		
		add(choice);

		add(ok, BorderLayout.SOUTH);

		setTitle(Info.NAME + " - Conway's Game of Life");

		setSize(300, 170);
		setVisible(true);
	}
}