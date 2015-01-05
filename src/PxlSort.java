/*
 *  PxlSort 0.3.7
 * 
 *  Copyright (C) 2014 - Andrej Budinčević
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

import java.io.File;
import java.io.IOException;

import java.util.Random;
import java.util.EmptyStackException;
import java.util.Stack;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

abstract class info {
	public static String NAME = "PxlSort";
	public static String VERSION = "0.3.7";
}

class PixelSort {
	private BufferedImage image;
	private int dir;
	private int by;
	private File file;

	private Stack<BufferedImage> stackUndo = new Stack<BufferedImage>();
	private Stack<BufferedImage> stackRedo = new Stack<BufferedImage>();

	public PixelSort(File input) throws IOException {
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
	        		value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x00ff0000) >> 16);
	        	else if(by == 2)
	        		value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x0000ff00) >> 8);
	        	else 
	        		value = (byte) (((dir == 1 ? image.getRGB(t, i) : image.getRGB(i, t)) & 0x000000ff));
	        	
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
	        		tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x00ff0000) >> 16);
	        	else if(by == 2)
	        		tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x0000ff00) >> 8);
	        	else 
	        		tmp[j] = (byte) (((dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t)) & 0x000000ff));
	        	
	        	indices[i] = (dir == 1 ? image.getRGB(t, i-j) : image.getRGB(i-j, t));	        
	        } 	
	       
	        
	        for(int i = 0; i < tmp.length; i++) {
	        	if(dir == 1) {
            		image.setRGB(t, i, indices[i]);
	        	} else if(dir == 2)
            		image.setRGB(i, t, indices[i]);
	        }
	    }
	}


	public void sort(int dir, int by) {
		stackUndo.push(deepCopy(image));

		this.dir = dir;
		this.by = by;

		int lim = (this.dir == 2 ? image.getHeight() : image.getWidth());

		for(int i = 0; i < lim; i++) {
			radix(i);		
		}
	}

	public void transpose() {	
		stackUndo.push(deepCopy(image));

		int h = image.getHeight();
		int w = image.getWidth();

		for (int i = 0; i < w; i++) {
			for (int j = i; j < h; j++) {
				int tmp1 = image.getRGB(i, j);
				image.setRGB(i, j, image.getRGB(j, i));
				image.setRGB(j, i, tmp1);
			}
		}
	}

	public void randomize() {
		stackUndo.push(deepCopy(image));

		int h = image.getHeight();
		int w = image.getWidth();

		for (int i = 0; i < w*h/2; i++) {
			Random rnd = new Random();

			int rndX = rnd.nextInt(w);
			int rndY = rnd.nextInt(h);

			int rndX1 = rnd.nextInt(w);
			int rndY1 = rnd.nextInt(h);

			int tmp1 = image.getRGB(rndX, rndY);
			image.setRGB(rndX, rndY, image.getRGB(rndX1, rndY1));
			image.setRGB(rndX1, rndY1, tmp1);
		}
	}

	public void write() throws IOException {          
		String tmp[] = file.getAbsolutePath().split("\\.");

		String format = tmp[tmp.length-1];
		ImageIO.write(image, format, new File(file.getAbsolutePath()));    
	}

	public void write(String out) throws IOException {
		String tmp[] = out.split("\\.");

		String format = tmp[tmp.length-1];
		ImageIO.write(image, format, new File(out));  
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

class scalableImage extends JLabel {
	private static final long serialVersionUID = -7834609287406439489L;
	
	private Image image;

	public scalableImage(ImageIcon image) {
		super(image);
		this.image = image.getImage();
	}

	public void setIcon(ImageIcon image) {
		this.image = image.getImage();
		super.setIcon(image);
	}	

	public double getScaleFactor(int iMasterSize, int iTargetSize) {
		double dScale = 1;
		if (iMasterSize > iTargetSize) 
			dScale = (double) iTargetSize / (double) iMasterSize;
		else 
			dScale = (double) iTargetSize / (double) iMasterSize;

		return dScale;

	}

	public double getScaleFactorToFill(Dimension masterSize, Dimension targetSize) {
		double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
		double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);

		double dScale = Math.max(dScaleHeight, dScaleWidth);

		return dScale;
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

class sortDialog extends JDialog {
	private static final long serialVersionUID = -256962939081259275L;
	private int by = 1, dir = 1;

	public int getBy() {
		return by;
	}

	public int getDir() {
		return dir;
	}

	public sortDialog() {
		setTitle(info.NAME + " - Sort options");

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


class aboutDialog extends JDialog {
	private static final long serialVersionUID = 6268703702230129630L;

	public aboutDialog() {
		JLabel desc = new JLabel("<html><body style='width: 140px;'>" + info.NAME + " is a small application used for sorting pixels in order to create interesting glitch effects (also called glitch art). Currently, it can sort pixels, randomize their order and transpose the image.</body></html>", SwingConstants.CENTER);
		JLabel name = new JLabel(info.NAME + " " + info.VERSION, SwingConstants.CENTER);

		name.setFont(new Font("Serif", Font.BOLD, 26));

		add(name, BorderLayout.NORTH);
		add(desc);

		setSize(220, 180);
		setVisible(true);
	}
}

class PixelSortGUI extends JFrame {
	private static final long serialVersionUID = -9127474621378199941L;
	
	private boolean saved = true;
	private PixelSort pxl;
	
	private JLabel imageLabel;
	
	private class fileChoice implements ActionListener {
		private JFileChooser fc;

		public fileChoice(JFileChooser fc) {
			this.fc = fc;
		}

		public void actionPerformed(ActionEvent e) {
			int rv = fc.showOpenDialog(getContentPane());

			if (rv == JFileChooser.APPROVE_OPTION) {
				try {
					if(pxl == null)
						pxl = new PixelSort(fc.getSelectedFile());
					else
						pxl.resetImage(fc.getSelectedFile());
					
				} catch(IOException ex) {
					JOptionPane.showMessageDialog(getContentPane(), "Unsupported or damaged image!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}

				if(pxl.getImage() != null) {
					if(imageLabel == null) {
						imageLabel = new scalableImage(new ImageIcon(pxl.getImage()));						
					} else {
						remove(imageLabel);
						imageLabel = new scalableImage(new ImageIcon(pxl.getImage()));	
					}
					
					add(imageLabel);
					setTitle(info.NAME + " - " + fc.getSelectedFile().getName());
				} else
					JOptionPane.showMessageDialog(getContentPane(), "Unsupported or damaged image!", "Image error", JOptionPane.ERROR_MESSAGE);    
				
				validate();
				repaint();
			} else {
				pxl = null;
			}
		}
	}

	
	public PixelSortGUI() {		
		final ImageIcon loader = new ImageIcon("loader.gif");

		setLayout(new BorderLayout(5, 5));

		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenu editMenu = new JMenu("Edit");


		JMenuItem hmAbout = new JMenuItem("About " + info.NAME);
		hmAbout.setMnemonic(KeyEvent.VK_A);
		hmAbout.setToolTipText("About " + info.NAME + " application");
		hmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JDialog about = new aboutDialog();

				about.setLocationRelativeTo(getContentPane());
				about.setVisible(true);
			}
		});

		helpMenu.add(hmAbout);

		JMenuItem emUndo = new JMenuItem("Undo");
		emUndo.setMnemonic(KeyEvent.VK_U);
		emUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		emUndo.setToolTipText("Undo the last operation");
		emUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(pxl != null && pxl.getImage() != null) {
					if(!pxl.undo()) 
						JOptionPane.showMessageDialog(getContentPane(), "Nothing to undo!", "Undo error", JOptionPane.ERROR_MESSAGE); 
					else {
						validate();
						repaint();
					}
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		editMenu.add(emUndo);

		JMenuItem emRedo = new JMenuItem("Redo");
		emRedo.setMnemonic(KeyEvent.VK_R);
		emRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		emRedo.setToolTipText("Redo the last operation");
		emRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(pxl != null && pxl.getImage() != null) {
					if(!pxl.redo()) 
						JOptionPane.showMessageDialog(getContentPane(), "Nothing to redo!", "Redo error", JOptionPane.ERROR_MESSAGE); 
					else {						
						validate();
						repaint();
					}
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		editMenu.add(emRedo);

		JMenuItem fmExit = new JMenuItem("Exit");
		fmExit.setMnemonic(KeyEvent.VK_E);
		fmExit.setToolTipText("Exit application");
		fmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
		});

		final JFileChooser fc = new JFileChooser();
		fc.setDragEnabled(true);
		fc.setFileFilter(new FileNameExtensionFilter("Supported images", "bmp", "png", "jpg", "jpeg", "gif"));

		JMenuItem fmOpen = new JMenuItem("Open");
		fmOpen.setMnemonic(KeyEvent.VK_O);
		fmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		fmOpen.setToolTipText("Open a new file");
		fmOpen.addActionListener(new fileChoice(fc));

		JMenuItem fmSave = new JMenuItem("Save");
		fmSave.setMnemonic(KeyEvent.VK_S);
		fmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		fmSave.setToolTipText("Save changes to the current file");
		fmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(pxl != null && pxl.getImage() != null) {
					try {
						pxl.write();
						setTitle(info.NAME + " - " + pxl.getImageName());
						saved = true;
					} catch(IOException e) {
						JOptionPane.showMessageDialog(getContentPane(), "Unknown error ocurred while writing to file!", "Write error", JOptionPane.ERROR_MESSAGE); 
						saved = false;
					}
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		JMenuItem fmSaveAs = new JMenuItem("Save as...");
		fmSaveAs.setMnemonic(KeyEvent.VK_V);
		fmSaveAs.setToolTipText("Save the current file as");
		fmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(pxl != null && pxl.getImage() != null) {
					int rv = fc.showSaveDialog(getContentPane());

					if (rv == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						
						try {
							pxl.write(file.getAbsolutePath());
							saved = true;
						} catch(IOException e) {
							JOptionPane.showMessageDialog(getContentPane(), "Unknown error ocurred while writing to file!", "Write error", JOptionPane.ERROR_MESSAGE); 
							saved = false;
						}
					}
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		fileMenu.add(fmOpen);
		fileMenu.add(fmSave);
		fileMenu.add(fmSaveAs);
		fileMenu.addSeparator();              
		fileMenu.add(fmExit);


		menu.add(fileMenu);
		menu.add(editMenu);
		menu.add(helpMenu);

		setJMenuBar(menu);

		JPanel south = new JPanel();
		south.setLayout(new FlowLayout());

		final JButton cRand = new JButton("Randomize");
		final JButton cTrans = new JButton("Transpose");
		final JButton cSort = new JButton("Sort");

		cRand.setMnemonic(KeyEvent.VK_R);
		cTrans.setMnemonic(KeyEvent.VK_T);
		cSort.setMnemonic(KeyEvent.VK_S);

		cRand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pxl != null && pxl.getImage() != null) {
					imageLabel.setIcon(loader);

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							pxl.randomize();

							validate();
							repaint();
							
							saved = false;

							setTitle(info.NAME + " - " + pxl.getImageName() + "*");
						}
					});
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		cTrans.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pxl != null && pxl.getImage() != null) {
					imageLabel.setIcon(loader);

					EventQueue.invokeLater(new Runnable() {
						public void run() {
							pxl.transpose();

							validate();
							repaint();
							
							saved = false;
							
							setTitle(info.NAME + " - " + pxl.getImageName() + "*");
						}
					});
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		cSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pxl != null && pxl.getImage() != null) {
					final JDialog sorter = new sortDialog();
					
					sorter.setLocationRelativeTo(getContentPane());
					sorter.setVisible(true);

					sorter.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {	
							imageLabel.setIcon(loader);

							final int by = ((sortDialog) sorter).getBy();
							final int dir = ((sortDialog) sorter).getDir();

							EventQueue.invokeLater(new Runnable() {
								public void run() {
									pxl.sort(dir, by);

									validate();
									repaint();
									
									saved = false;

									setTitle(info.NAME + " - " + pxl.getImageName() + "*");
								}
							});
						}
					});

				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});


		south.add(cSort);
		south.add(cRand);
		south.add(cTrans);


		add(south, BorderLayout.SOUTH);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(!saved && pxl != null) {
					int c = JOptionPane.showConfirmDialog(getContentPane(), "You have unsaved changes! Are sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
					
					if(c == JOptionPane.YES_OPTION)
						System.exit(0);
				} else 
					System.exit(0);
			}
		});

		setSize(800, 600);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		setTitle(info.NAME);
		
		setVisible(true);
	}
}

public class PxlSort { 
	public static void main(String[] args) {  
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e) {}

		new PixelSortGUI();		
	}
}