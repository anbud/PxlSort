package com.pxlsort;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ScalableImage extends JLabel {
	private static final long serialVersionUID = -7834609287406439489L;

	private Image image;

	public ScalableImage(ImageIcon image) {
		super(image);
		this.image = image.getImage();
	}

	public double getScaleFactor(int masterSize, int targetSize) {
		return (double) targetSize/(double) masterSize;

	}

	public double getScaleFactorToFill(Dimension masterSize, Dimension targetSize) {
		double scaleWidth = getScaleFactor(masterSize.width, targetSize.width);
		double scaleHeight = getScaleFactor(masterSize.height, targetSize.height);

		return Math.max(scaleHeight, scaleWidth);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		super.paintComponent(g);

		double scaleFactor = Math.min(1d, getScaleFactorToFill(new Dimension(image.getWidth(null), image.getHeight(null)), getSize()));

		int scaleWidth = (int) Math.round(image.getWidth(null) * scaleFactor);
		int scaleHeight = (int) Math.round(image.getHeight(null) * scaleFactor);

		int width = getWidth() - 1;
		int height = getHeight() - 1;

		int x = (width - scaleWidth) / 2;
		int y = (height - scaleHeight) / 2;

		g2d.drawImage(image, x, y, scaleWidth, scaleHeight, this);
	}
}