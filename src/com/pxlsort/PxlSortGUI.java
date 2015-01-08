package com.pxlsort;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PxlSortGUI extends JFrame {
	private static final long serialVersionUID = -9127474621378199941L;

	private boolean saved = true;

	private PxlSort pxl;

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
						pxl = new PxlSort(fc.getSelectedFile());
					else
						pxl.resetImage(fc.getSelectedFile());

				} catch(IOException ex) {
					JOptionPane.showMessageDialog(getContentPane(), "Unsupported or damaged image!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}

				if(pxl.getImage() != null) {
					if(imageLabel == null) {
						imageLabel = new ScalableImage(new ImageIcon(pxl.getImage()));						
					} else {
						remove(imageLabel);
						imageLabel = new ScalableImage(new ImageIcon(pxl.getImage()));	
					}

					add(imageLabel);
					setTitle(Info.NAME + " - " + fc.getSelectedFile().getName());
				} else
					JOptionPane.showMessageDialog(getContentPane(), "Unsupported or damaged image!", "Image error", JOptionPane.ERROR_MESSAGE);    

				validate();
				repaint();
			}
		}
	}


	public PxlSortGUI() {		
		setLayout(new BorderLayout(5, 5));

		final Timer time = new Timer(Info.ANIM_SPEED, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});

		JMenuBar menu = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		JMenu helpMenu = new JMenu("Help");
		JMenu editMenu = new JMenu("Edit");


		JMenuItem hmAbout = new JMenuItem("About " + Info.NAME);
		hmAbout.setMnemonic(KeyEvent.VK_A);
		hmAbout.setToolTipText("About " + Info.NAME + " application");
		hmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JDialog about = new AboutDialog();

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
					new Thread(new Runnable() {
						public void run() {
							time.start();

							if(!pxl.undo()) 
								JOptionPane.showMessageDialog(getContentPane(), "Nothing to undo!", "Undo error", JOptionPane.ERROR_MESSAGE); 
							else {						
								validate();
								repaint();
							}

							time.stop();
						}
					}).start();
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
					new Thread(new Runnable() {
						public void run() {
							time.start();

							if(!pxl.redo()) 
								JOptionPane.showMessageDialog(getContentPane(), "Nothing to redo!", "Redo error", JOptionPane.ERROR_MESSAGE); 
							else {						
								validate();
								repaint();
							}

							time.stop();
						}
					}).start();
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
				System.exit(0);
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
						setTitle(Info.NAME + " - " + pxl.getImageName());
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
					final JDialog rand = new RandDialog(pxl.getImage().getWidth()*pxl.getImage().getHeight());

					rand.setLocationRelativeTo(getContentPane());
					rand.setVisible(true);

					rand.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {	
							final int value = ((RandDialog) rand).getValue();

							new Thread(new Runnable() {
								public void run() {
									time.start();

									pxl.randomize(value);

									time.stop();

									validate();
									repaint();

									saved = false;

									setTitle(Info.NAME + " - " + pxl.getImageName() + "*");
								}
							}).start();
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
					new Thread(new Runnable() {
						public void run() {
							time.start();

							pxl.transpose();

							time.stop();

							validate();
							repaint();

							saved = false;

							setTitle(Info.NAME + " - " + pxl.getImageName() + "*");
						}
					}).start();
				} else {
					JOptionPane.showMessageDialog(getContentPane(), "No image loaded!", "Image error", JOptionPane.ERROR_MESSAGE);    
				}
			}
		});

		cSort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(pxl != null && pxl.getImage() != null) {
					final JDialog sorter = new SortDialog();

					sorter.setLocationRelativeTo(getContentPane());
					sorter.setVisible(true);

					sorter.addWindowListener(new WindowAdapter() {
						public void windowClosed(WindowEvent e) {	
							final int by = ((SortDialog) sorter).getBy();
							final int dir = ((SortDialog) sorter).getDir();

							new Thread(new Runnable() {
								public void run() {
									time.start();

									pxl.sort(dir, by);

									time.stop();

									validate();
									repaint();

									saved = false;

									setTitle(Info.NAME + " - " + pxl.getImageName() + "*");
								}
							}).start();
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

		setTitle(Info.NAME);

		setVisible(true);
	}
}