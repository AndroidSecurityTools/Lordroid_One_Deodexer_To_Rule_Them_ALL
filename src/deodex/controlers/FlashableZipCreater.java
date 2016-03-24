/*
 *  Lordroid One Deodexer To Rule Them All
 * 
 *  Copyright 2016 Rachid Boudjelida <rachidboudjelida@gmail.com>
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
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package deodex.controlers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.alee.laf.progressbar.WebProgressBar;

import deodex.R;
import deodex.S;
import deodex.tools.FilesUtils;
import deodex.tools.Logger;
import deodex.tools.Zip;
import deodex.ui.MyWebButton;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class FlashableZipCreater extends JFrame implements Runnable, MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int posX = 0;
	private int posY = 0;

	JPanel rootPanel = new JPanel();

	MyWebButton okBtn = new MyWebButton("Ok");
	MyWebButton openContainingFolder = new MyWebButton(R.getString("0000000"));

	WebProgressBar bar = new WebProgressBar();

	ArrayList<File> fileToAdd = new ArrayList<File>();
	File systemFolder;
	ZipFile zipFile;

	/**
	 * 
	 * @param systemFolderp
	 *            the system folder from which the flashable zip will be made
	 * @param zipFilep
	 *            the flashable zip FILE
	 * @param c
	 *            the component to which this frame will be positionned
	 *            relatively to it
	 */
	public FlashableZipCreater(File systemFolderp, File zipFilep, Component c) {
		this.setLocationRelativeTo(c);
		this.setSize(405, 115);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setTitle(R.getString("0000001"));

		this.systemFolder = systemFolderp;
		zipFilep.getParentFile().mkdirs();
		FilesUtils.copyFile(S.DUMMY_ZIP, zipFilep);

		try {
			this.zipFile = new ZipFile(zipFilep);
		} catch (ZipException e) {
			Logger.appendLog("[FlashableZipCreater][EX]" + e.getStackTrace());
			e.printStackTrace();
		}

		rootPanel.setBackground(R.PANELS_BACK_COLOR);
		okBtn.setBackground(R.BUTTONS_BACK_COLOR);
		openContainingFolder.setBackground(R.BUTTONS_BACK_COLOR);
		bar.setForeground(Color.BLACK);

		bar.setBackground(Color.WHITE);
		bar.setBgBottom(new Color(236, 240, 241));
		bar.setBgTop(new Color(189, 195, 199));
		bar.setProgressTopColor(new Color(46, 204, 113));
		bar.setProgressBottomColor(new Color(39, 174, 96));

		okBtn.setFont(R.getCouriernormal());
		openContainingFolder.setFont(R.getCouriernormal());
		bar.setFont(R.getCouriernormal());
		bar.setStringPainted(true);

		this.okBtn.setEnabled(false);
		this.openContainingFolder.setEnabled(false);

		// Actions
		this.okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getThisFram().dispose();

			}

		});
		// FIXME : me this out of here
		openContainingFolder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(zipFile.getFile().getParentFile());
					getThisFram().dispose();
				} catch (IOException e1) {
					Logger.appendLog("[FlashableZipCreater][EX]" + e1.getStackTrace());
					e1.printStackTrace();
				}
			}

		});

		// bounds
		rootPanel.setLayout(null);
		rootPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		bar.setBounds(5, 5, 395, 50);
		okBtn.setBounds(5, 60, 90, 40);
		openContainingFolder.setBounds(100, 60, 300, 40);

		rootPanel.add(bar);
		rootPanel.add(okBtn);
		rootPanel.add(openContainingFolder);

		rootPanel.addMouseListener(this);
		rootPanel.addMouseMotionListener(this);
		this.add(rootPanel, BorderLayout.CENTER);
		this.setUndecorated(true);
		this.setResizable(false);
		this.setVisible(true);
		initFilesList();

		new Thread(this).start();
	}

	/**
	 * 
	 * @return this very instance of JFrame to make a reference to this object
	 *         it self
	 */
	private JFrame getThisFram() {
		return this;
	}

	/**
	 * lists all files that will be added to the flashable zip ony .apk and .jar
	 * files will be added here
	 */
	private void initFilesList() {
		this.fileToAdd.addAll(FilesUtils.searchrecursively(systemFolder, ".apk"));
		this.fileToAdd.addAll(FilesUtils.searchrecursively(systemFolder, ".jar"));
		bar.setMinimum(0);
		bar.setMaximum(this.fileToAdd.size());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent ev) {

		int oldX = ev.getXOnScreen();
		int oldY = ev.getYOnScreen();

		this.setLocation(this.getLocation().x + (oldX - this.posX), this.getLocation().y + (oldY - this.posY));
		this.posX = ev.getXOnScreen();
		this.posY = ev.getYOnScreen();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.posX = e.getXOnScreen();
		this.posY = e.getYOnScreen();
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	/**
	 * 
	 * @return the percentage of the current progress
	 */
	private String perCent() {
		// 100 ==> max
		// ? ==> value
		// ? = (value*100)/max
		return bar.getValue() * 100 / bar.getMaximum() + "%";

	}

	@Override
	public void run() {
		for (File f : this.fileToAdd) {

			Zip.AddFileToFolderInZip(systemFolder, f, zipFile);
			bar.setValue(bar.getValue() + 1);
			bar.setString("Creating Zip file " + this.perCent());
		}
		this.okBtn.setEnabled(true);
		this.openContainingFolder.setEnabled(true);
	}

}
