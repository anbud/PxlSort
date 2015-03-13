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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

import javax.imageio.ImageIO;

public class PxlSort {
	private BufferedImage image;
	private File file;

	private int dir;
	private int by;

	private Stack<BufferedImage> stackUndo = new Stack<BufferedImage>();
	private Stack<BufferedImage> stackRedo = new Stack<BufferedImage>();

	public PxlSort(File input) throws IOException {
		file = input;

		image = ImageIO.read(file);
	}

	public File getFile() {
		return file;
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getImageName() {
		return file.getName();
	}

	public void finalize() throws Throwable {
		super.finalize();
		image.flush();
	}

	public BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void radix(int t) {
		int h = (dir == 2 ? image.getWidth() : image.getHeight());

		for (int s = Byte.SIZE - 1; s > -1; s--) {
			byte tmp[] = new byte[h];
			int indices[] = new int[h];

			int j = 0;	

			for (int i = 0; i < h; i++) {
				byte value;

				if(by == 1)
					value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x00ff0000) >> 16 - 128);
				else if(by == 2)
					value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x0000ff00) >> 8 - 128);
				else 
					value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x000000ff) - 128);

				boolean move = value << s >= 0;

				if (s == 0 ? !move : move) {
					indices[j] = (dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t));
					tmp[j++] = value;
				} else {
					if(dir == 1)
						image.setRGB(t, i-j, image.getRGB(t, i));
					else if(dir == 2)
						image.setRGB(i-j, t, image.getRGB(i, t));
				}
			}

			for (int i = j; i < tmp.length; i++) {
				if(by == 1)
					tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x00ff0000) >> 16 - 128);
				else if(by == 2)
					tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x0000ff00) >> 8 - 128);
				else 
					tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x000000ff) - 128);

				indices[i] = (dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t));	        
			} 	


			for(int i = 0; i < tmp.length; i++) {
				if(dir == 1) 
					image.setRGB(t, i, indices[i]);
				else if(dir == 2)
					image.setRGB(i, t, indices[i]);
			}
		}
	}


	public void sort(int dir, int by) {
		stackUndo.push(deepCopy(image));

		this.dir = dir;
		this.by = by;

		int lim = (this.dir == 2 ? image.getHeight() : image.getWidth());

		for(int i = 0; i < lim; i++) 
			radix(i);		
	}

	public void transpose() {	
		stackUndo.push(deepCopy(image));

		int h = image.getHeight();
		int w = image.getWidth();

		for (int i = 0; i < w; i++) {
			for (int j = i; j < h; j++) {
				int tmp = image.getRGB(i, j);
				image.setRGB(i, j, image.getRGB(j, i));
				image.setRGB(j, i, tmp);
			}
		}
	}

	public void randomize(int lim) {
		stackUndo.push(deepCopy(image));

		int h = image.getHeight();
		int w = image.getWidth();

		for (int i = 0; i < lim; i++) {
			Random rnd = new Random();

			int rndX = rnd.nextInt(w);
			int rndY = rnd.nextInt(h);

			int rndX1 = rnd.nextInt(w);
			int rndY1 = rnd.nextInt(h);

			int tmp = image.getRGB(rndX, rndY);
			image.setRGB(rndX, rndY, image.getRGB(rndX1, rndY1));
			image.setRGB(rndX1, rndY1, tmp);
		}
	}

	public void manCell(int i, int j, int state) {
		int value;
		
		for(int x = 0; x < 3; x++) {	
			try {
				if(by == 1)
					value = (image.getRGB(i-1, j+x-1) & 0x00ff0000) >> 16;
				else if(by == 2)
					value = (image.getRGB(i-1, j+x-1) & 0x0000ff00) >> 8;
				else 
					value = (image.getRGB(i-1, j+x-1) & 0x000000ff);
				
				if((value > Info.ALIVE_CONSTANT && state == 1) || (value <= Info.ALIVE_CONSTANT && state == 0)) {
					image.setRGB(i, j, image.getRGB(i-1, j+x-1));
					return;
				}
			} catch(ArrayIndexOutOfBoundsException e) {}
		}

		for(int x = 0; x < 3; x++) {
			try {
				if(by == 1)
					value = (image.getRGB(i+1, j+x-1) & 0x00ff0000) >> 16;
				else if(by == 2)
					value = (image.getRGB(i+1, j+x-1) & 0x0000ff00) >> 8;
				else 
					value = (image.getRGB(i+1, j+x-1) & 0x000000ff);

				if((value > Info.ALIVE_CONSTANT && state == 1) || (value <= Info.ALIVE_CONSTANT && state == 0)) {
					image.setRGB(i, j, image.getRGB(i+1, j+x-1));
					return;
				}
			} catch(ArrayIndexOutOfBoundsException e) {}
		}

		for(int x = 0; x < 2; x++) {
			try {
				if(by == 1)
					value = (image.getRGB(i, j+2*x-1) & 0x00ff0000) >> 16;
				else if(by == 2)
					value = (image.getRGB(i, j+2*x-1) & 0x0000ff00) >> 8;
				else 
					value = (image.getRGB(i, j+2*x-1) & 0x000000ff);

				if((value > Info.ALIVE_CONSTANT && state == 1) || (value <= Info.ALIVE_CONSTANT && state == 0)) {
					image.setRGB(i, j, image.getRGB(i, j+2*x-1));
					return;
				}
			} catch(ArrayIndexOutOfBoundsException e) {}
		}
	}
	
	public void gof(int iter, int by) { 
		this.by = by;
		
		for(int i = 0; i < iter; i++) 
			gameOfLife();
	}

	public void gameOfLife() {
		int h = image.getHeight();
		int w = image.getWidth();

		for(int i = 0; i < w; i++) {
			for(int j = 0; j < h; j++) {				
				int val;

				if(by == 1)
					val = (image.getRGB(i, j) & 0x00ff0000) >> 16;
				else if(by == 2)
					val = (image.getRGB(i, j) & 0x0000ff00) >> 8;
				else 
					val = (image.getRGB(i, j) & 0x000000ff);	

				boolean alive = val > Info.ALIVE_CONSTANT;
		
				int value, aliveNeighbours = 0;
		
		
				for(int x = 0; x < 3; x++) {	
					try {
						if(by == 1)
							value = (image.getRGB(i-1, j+x-1) & 0x00ff0000) >> 16;
						else if(by == 2)
							value = (image.getRGB(i-1, j+x-1) & 0x0000ff00) >> 8;
						else 
							value = (image.getRGB(i-1, j+x-1) & 0x000000ff);
		
						aliveNeighbours = aliveNeighbours + (value > Info.ALIVE_CONSTANT ? 1 : 0);
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
		
				for(int x = 0; x < 3; x++) {
					try {
						if(by == 1)
							value = (image.getRGB(i+1, j+x-1) & 0x00ff0000) >> 16;
						else if(by == 2)
							value = (image.getRGB(i+1, j+x-1) & 0x0000ff00) >> 8;
						else 
							value = (image.getRGB(i+1, j+x-1) & 0x000000ff);
		
						aliveNeighbours = aliveNeighbours + (value > Info.ALIVE_CONSTANT ? 1 : 0);
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
		
				for(int x = 0; x < 2; x++) {
					try {
						if(by == 1)
							value = (image.getRGB(i, j+2*x-1) & 0x00ff0000) >> 16;
						else if(by == 2)
							value = (image.getRGB(i, j+2*x-1) & 0x0000ff00) >> 8;
						else 
							value = (image.getRGB(i, j+2*x-1) & 0x000000ff);
		
						aliveNeighbours = aliveNeighbours + (value > Info.ALIVE_CONSTANT ? 1 : 0);
					} catch(ArrayIndexOutOfBoundsException e) {}
				}
		
				if(alive && (aliveNeighbours < 2 || aliveNeighbours > 3)) 
					manCell(i, j, 0);
				else if(!alive && aliveNeighbours == 3) 
					manCell(i, j, 1);		
			}
		}

	}

	public void write(String out) throws IOException {
		String tmp[] = out.split("\\.");

		String format = tmp[tmp.length-1];
		ImageIO.write(image, format, new File(out));  
	}	

	public void write() throws IOException {          
		write(file.getAbsolutePath());
	}

	public boolean undo() {
		try {
			BufferedImage tmp = stackUndo.pop();
			stackRedo.push(deepCopy(image));	

			int h = tmp.getHeight();
			int w = tmp.getWidth();
			for(int i = 0; i < h; i++)
				for(int j = 0; j < w; j++) 
					image.setRGB(j, i, tmp.getRGB(j, i));

			return true;
		} catch(EmptyStackException e) {
			return false;
		}
	}

	public boolean redo() {
		try {
			BufferedImage tmp = stackRedo.pop();
			stackUndo.push(deepCopy(image));

			int h = tmp.getHeight();
			int w = tmp.getWidth();
			for(int i = 0; i < h; i++)
				for(int j = 0; j < w; j++) 
					image.setRGB(j, i, tmp.getRGB(j, i));

			return true;
		} catch(EmptyStackException e) {
			return false;
		}
	}

	public void resetImage(File input) throws IOException {
		file = input;

		image = ImageIO.read(file);	

		while(!stackUndo.empty())
			stackUndo.pop();

		while(!stackRedo.empty())
			stackRedo.pop();
	}
}