/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005-2006 Grigor Iliev <grigor@grigoriliev.com>
 *
 *   This file is part of JSampler.
 *
 *   JSampler is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2
 *   as published by the Free Software Foundation.
 *
 *   JSampler is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with JSampler; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *   MA  02111-1307  USA
 */

package org.jsampler.view.classic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;

import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.juife.EnhancedDialog;
import net.sf.juife.JuifeUtils;
import net.sf.juife.LinkButton;
import net.sf.juife.OkCancelDialog;

import org.jsampler.CC;
import org.jsampler.HF;
import org.jsampler.JSI18n;
import org.jsampler.JSampler;
import org.jsampler.LSConsoleModel;
import org.jsampler.Prefs;

import org.jsampler.task.SetServerAddress;

import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class PrefsDlg extends EnhancedDialog {
	private final GeneralPane genPane = new GeneralPane();
	private final ViewPane viewPane = new ViewPane();
	private final ConsolePane consolePane = new ConsolePane();
	private final ConnectionPane conPane = new ConnectionPane();
	
	private final JButton btnApply = new JButton(i18n.getButtonLabel("apply"));
	private final JButton btnClose = new JButton(i18n.getButtonLabel("close"));
	
	
	public
	PrefsDlg(Frame frm) {
		super(frm, i18n.getLabel("PrefsDlg"), true);
		
		initPrefsDlg();
		handleEvents();
		initPrefs();
		
		setLocation(JuifeUtils.centerLocation(this, frm));
	}
	
	private void
	initPrefsDlg() {
		JTabbedPane tp = new JTabbedPane();
		tp.addTab(i18n.getLabel("PrefsDlg.tabGeneral"), genPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabView"), viewPane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConsole"), consolePane);
		tp.addTab(i18n.getLabel("PrefsDlg.tabConnection"), conPane);
		tp.setAlignmentX(RIGHT_ALIGNMENT);
		
		// Set preferred size for Apply & Exit buttons
		Dimension d = JuifeUtils.getUnionSize(btnApply, btnClose);
		btnApply.setPreferredSize(d);
		btnClose.setPreferredSize(d);

		JPanel btnPane = new JPanel();
		btnPane.setLayout(new BoxLayout(btnPane, BoxLayout.X_AXIS));
		btnPane.add(btnApply);
		btnPane.add(Box.createRigidArea(new Dimension(5, 0)));
		btnPane.add(btnClose);
		btnPane.setAlignmentX(RIGHT_ALIGNMENT);
		
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
		mainPane.add(tp);
		mainPane.add(Box.createRigidArea(new Dimension(0, 12)));
		mainPane.add(btnPane);
		mainPane.setBorder(BorderFactory.createEmptyBorder(11, 12, 12, 12));
		
		getContentPane().add(mainPane);
		
		pack();
		setResizable(false);
	}
	
	private void
	handleEvents() {
		btnApply.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onApply(); }
		});
		
		btnClose.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onExit(); }
		});
	}
	
	protected void
	onOk() {  onApply(); }
	
	protected void
	onCancel() { onExit(); }
		
	private void
	initPrefs() {
		setLSAddress(Prefs.getLSAddress());
		setLSPort(Prefs.getLSPort());
	}
	
	private void
	onApply() {
		genPane.apply();
		viewPane.apply();
		consolePane.apply();
		
		// CONNECTION
		Prefs.setLSAddress(getLSAddress());
		
		boolean b = true;
		String s = getLSPort();
		try {
			if(s.length() > 0) {
				int port = Integer.parseInt(s);
				if(port > 0 && port < 0xffff)
					Prefs.setLSPort(port);
				else b = false;
			} else Prefs.setLSPort(-1);	// -1 resets to default value
		} catch(NumberFormatException x) {
			b = false;
		}
		
		if(!b) {
			JOptionPane.showMessageDialog (
				this, 
				i18n.getError("PrefsDlg.invalidPort", s),
				i18n.getError("error"),
				JOptionPane.ERROR_MESSAGE
			);
			
			return;
		}
		
		CC.getTaskQueue().add (
			new SetServerAddress(Prefs.getLSAddress(), Prefs.getLSPort())
		);
		
		setVisible(false);
	}
	
	private void
	onExit() { setVisible(false); }
	
	private String
	getLSAddress() { return conPane.getLSAddress().trim(); }
	
	private void
	setLSAddress(String s) { conPane.setLSAddress(s); }
	
	private String
	getLSPort() { return conPane.getLSPort().trim(); }
	
	private void
	setLSPort(int port) { conPane.setLSPort(String.valueOf(port)); }
	
	protected static class ColorButton extends JPanel {
		private Color color;
		private final Vector<ActionListener> listeners = new Vector<ActionListener>();
		
		ColorButton() { this(Color.WHITE); }
		
		ColorButton(Color c) {
			color = c;
			
			//setBorderPainted(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			setPreferredSize(new Dimension(42, 16));
			setMaximumSize(new Dimension(42, 16));
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(!isEnabled()) return;
					if(e.getButton() == e.BUTTON1) showColorChooser();
				}
			});
		}
		
		/**
		 * Registers the specified listener to be
		 * notified when the current color is changed.
		 * @param l The <code>ActionListener</code> to register.
		 */
		public void
		addActionListener(ActionListener l) { listeners.add(l); }
	
		/**
		 * Removes the specified listener.
		 * @param l The <code>ActionListener</code> to remove.
		 */
		public void
		removeActionListener(ActionListener l) { listeners.remove(l); }
		
		/** Notifies listeners that the current color is changed. */
		private void
		fireActionPerformed() {
			ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
			for(ActionListener l : listeners) l.actionPerformed(e);
		}
	
		public void
		setEnabled(boolean b) {
			setOpaque(b);
			if(b) setBorder(BorderFactory.createLineBorder(Color.BLACK));
			else setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			//setBorderPainted(!b);
			super.setEnabled(b);
		}
		
		private void
		showColorChooser() {
			ColorDlg dlg = new ColorDlg (
				(Dialog)JuifeUtils.getWindow(this), getColor()
			);
			
			dlg.setVisible(true);
			if(!dlg.isCancelled()) {
				setColor(dlg.getColor());
				fireActionPerformed();
			}
		}
		
		public Color
		getColor() { return color; }
		
		public void
		setColor(Color c) {
			color = c;
			setBackground(color);
		}
	}
	
	protected static class ColorDlg extends OkCancelDialog {
		private final JColorChooser colorChooser = new JColorChooser();
		
		ColorDlg(Dialog owner) { this(owner, Color.WHITE); }
		
		ColorDlg(Dialog owner, Color c) {
			super(owner);
			
			colorChooser.setPreviewPanel(new JPanel());
			colorChooser.setColor(c);
			
			JPanel mainPane = new JPanel();
			mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
			mainPane.add(colorChooser);
			
			mainPane.add(Box.createRigidArea(new Dimension(0, 6)));
			
			final JPanel p = new JPanel();
			p.setBackground(c);
			p.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			mainPane.add(p);
			
			p.setPreferredSize(new Dimension(48, 8));
			p.setMaximumSize(new Dimension(Short.MAX_VALUE, 8));
			
			setMainPane(mainPane);
			
			colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {
				public void
				stateChanged(ChangeEvent e) { p.setBackground(getColor()); }
			});
		}
		
		protected void
		onOk() { setVisible(false); }
		
		protected void
		onCancel() { setVisible(false); }
		
		public Color
		getColor() { return colorChooser.getColor(); }
	}
}

class GeneralPane extends JPanel {
	private final JCheckBox checkWindowSizeAndLocation =
		new JCheckBox(i18n.getLabel("GeneralPane.checkWindowSizeAndLocation"));
	
	private final JCheckBox checkLeftPaneState =
		new JCheckBox(i18n.getLabel("GeneralPane.checkLeftPaneState"));
	
	private final JCheckBox checkShowLSConsoleWhenRunScript =
		new JCheckBox(i18n.getLabel("GeneralPane.checkShowLSConsoleWhenRunScript"));
	
	private final JTextField tfJSamplerHome = new JTextField();
	private final JButton btnChange = new JButton(i18n.getLabel("GeneralPane.btnChange"));
	
	private final JLabel lRecentScriptsSize =
		new JLabel(i18n.getLabel("GeneralPane.lRecentScriptsSize"));
	private final JSpinner spRecentScriptsSize;
	private final JButton btnClearRecentScriptList =
		new JButton(i18n.getButtonLabel("GeneralPane.btnClearRecentScriptList"));
	
	
	public
	GeneralPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		checkWindowSizeAndLocation.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		checkWindowSizeAndLocation.setSelected(ClassicPrefs.getSaveWindowProperties());
		checkWindowSizeAndLocation.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				//checkWindowSizeAndLocation.setEnabled(b);
			}
		});
		
		add(checkWindowSizeAndLocation);
		
		checkLeftPaneState.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		checkLeftPaneState.setSelected(ClassicPrefs.getSaveLeftPaneState());
		add(checkLeftPaneState);
		
		checkShowLSConsoleWhenRunScript.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		
		boolean b = ClassicPrefs.getShowLSConsoleWhenRunScript();
		checkShowLSConsoleWhenRunScript.setSelected(b);
		
		add(checkShowLSConsoleWhenRunScript);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel jhp = new JPanel();
		jhp.setLayout(new BoxLayout(jhp, BoxLayout.Y_AXIS));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		if(CC.getJSamplerHome() != null) tfJSamplerHome.setText(CC.getJSamplerHome());
		Dimension d;
		d = new Dimension(Short.MAX_VALUE, tfJSamplerHome.getPreferredSize().height);
		tfJSamplerHome.setMaximumSize(d);
		p.add(tfJSamplerHome);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(btnChange);
		p.setBorder(BorderFactory.createEmptyBorder(2, 6, 6, 6));
		p.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		jhp.add(p);
		
		String s = i18n.getLabel("GeneralPane.jSamplerHome");
		jhp.setBorder(BorderFactory.createTitledBorder(s));
		jhp.setMaximumSize(new Dimension(Short.MAX_VALUE, jhp.getPreferredSize().height));
		jhp.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(jhp);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel rsp = new JPanel();
		rsp.setLayout(new BoxLayout(rsp, BoxLayout.Y_AXIS));
		
		int i = ClassicPrefs.getRecentScriptsSize();
		spRecentScriptsSize = new JSpinner(new SpinnerNumberModel(i, 0, 100, 1));
		spRecentScriptsSize.setMaximumSize(spRecentScriptsSize.getPreferredSize());
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(lRecentScriptsSize);
		p.add(Box.createRigidArea(new Dimension(5, 0)));
		p.add(spRecentScriptsSize);
		
		p.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		rsp.add(p);
		
		rsp.add(Box.createRigidArea(new Dimension(0, 6)));
		
		btnClearRecentScriptList.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				ClassicPrefs.setRecentScripts(null);
				((MainFrame)CC.getMainFrame()).clearRecentScripts();
			}
		});
		
		btnClearRecentScriptList.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		rsp.add(btnClearRecentScriptList);
		rsp.add(Box.createRigidArea(new Dimension(0, 6)));
		
		rsp.setMaximumSize(new Dimension(Short.MAX_VALUE, rsp.getPreferredSize().height));
		rsp.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		s = i18n.getLabel("GeneralPane.recentScripts");
		rsp.setBorder(BorderFactory.createTitledBorder(s));
		
		add(rsp);
		add(Box.createGlue());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		
		btnChange.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) { onChange(); }
		});
	}
	
	private void
	onChange() {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fc.showOpenDialog(this);
		if(result != JFileChooser.APPROVE_OPTION) return;
		
		String s = CC.getJSamplerHome();
		String suf = File.separator + ".jsampler";
		if(s != null) suf = File.separator + new File(s).getName();
		
		tfJSamplerHome.setText(fc.getSelectedFile().getPath() + suf);
	}
	
	protected void
	apply() {
		ClassicPrefs.setSaveWindowProperties(checkWindowSizeAndLocation.isSelected());
		ClassicPrefs.setSaveLeftPaneState(checkLeftPaneState.isSelected());
		
		boolean b = checkShowLSConsoleWhenRunScript.isSelected();
		ClassicPrefs.setShowLSConsoleWhenRunScript(b);
		
		int size = Integer.parseInt(spRecentScriptsSize.getValue().toString());
		ClassicPrefs.setRecentScriptstSize(size);
		((MainFrame)CC.getMainFrame()).updateRecentScriptsMenu();
		
		String s = tfJSamplerHome.getText();
		if(s.length() > 0 && !s.equals(CC.getJSamplerHome())) {
			CC.changeJSamplerHome(s);
		}
	}
}

class ViewPane extends JPanel {
	private final JLabel lIfaceLanguage =
		new JLabel(i18n.getLabel("ViewPane.lIfaceLanguage"));
	private final JComboBox cbIfaceLanguage = new JComboBox();
	
	private final JLabel lIfaceFont =
		new JLabel(i18n.getLabel("ViewPane.lIfaceFont"));
	private final JComboBox cbIfaceFont = new JComboBox();
	
	private final JCheckBox checkBorderColor =
		new JCheckBox(i18n.getLabel("ViewPane.channelBorderColor"));
	private final PrefsDlg.ColorButton btnBorderColor =
		new PrefsDlg.ColorButton(Color.WHITE);
	
	private final JCheckBox checkHlChnBorderColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkHlChnBorderColor"));
	private final PrefsDlg.ColorButton btnHlChnBorderColor =
		new PrefsDlg.ColorButton(Color.WHITE);
	
	private final JCheckBox checkSelChnBgColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkSelChnBgColor"));
	private final PrefsDlg.ColorButton btnSelChnBgColor =
		new PrefsDlg.ColorButton(Color.WHITE);
	
	private final JCheckBox checkHlChnBgColor =
		new JCheckBox(i18n.getLabel("ViewPane.checkHlChnBgColor"));
	private final PrefsDlg.ColorButton btnHlChnBgColor =
		new PrefsDlg.ColorButton(Color.WHITE);
	
	public
	ViewPane() { initViewPane(); }
	
	private void
	initViewPane() {
		cbIfaceLanguage.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceLanguage.getPreferredSize().height)
		);
		
		for(Locale l : JSI18n.getAvailableLocales()) {
			LocaleBox box = new LocaleBox(l);
			cbIfaceLanguage.addItem(box);
			if (	l.getLanguage().equals(Prefs.getInterfaceLanguage()) &&
				l.getCountry().equals(Prefs.getInterfaceCountry())
			) cbIfaceLanguage.setSelectedItem(box);
		}
		
		cbIfaceFont.setMaximumSize (
			new Dimension(Short.MAX_VALUE, cbIfaceFont.getPreferredSize().height)
		);
		
		cbIfaceFont.addItem("[Default]");
		
		String[] fontS =
		GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		for(String f : fontS) cbIfaceFont.addItem(f);
		
		if(Prefs.getInterfaceFont() == null) cbIfaceFont.setSelectedItem("[Default]");
		else cbIfaceFont.setSelectedItem(Prefs.getInterfaceFont());
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel ifacePane = new JPanel();
		ifacePane.setLayout(new BoxLayout(ifacePane, BoxLayout.X_AXIS));
		ifacePane.add(lIfaceLanguage);
		ifacePane.add(Box.createRigidArea(new Dimension(5, 0)));
		ifacePane.add(cbIfaceLanguage);
		
		add(ifacePane);
		
		add(Box.createRigidArea(new Dimension(0, 6)));
		
		JPanel fontPane = new JPanel();
		fontPane.setLayout(new BoxLayout(fontPane, BoxLayout.X_AXIS));
		fontPane.add(lIfaceFont);
		fontPane.add(Box.createRigidArea(new Dimension(5, 0)));
		fontPane.add(cbIfaceFont);
		
		add(fontPane);
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(createCustomColorsPane());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	private JPanel
	createCustomColorsPane() {
		btnBorderColor.setColor(ClassicPrefs.getChannelBorderColor());
		btnBorderColor.setEnabled(ClassicPrefs.getCustomChannelBorderColor());
		
		checkBorderColor.setSelected(ClassicPrefs.getCustomChannelBorderColor());
		
		checkBorderColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnBorderColor.setEnabled(b);
			}
		});
		
		btnHlChnBorderColor.setColor(ClassicPrefs.getChannelBorderHlColor());
		btnHlChnBorderColor.setEnabled(ClassicPrefs.getCustomChannelBorderHlColor());
		
		checkHlChnBorderColor.setSelected(ClassicPrefs.getCustomChannelBorderHlColor());
		
		checkHlChnBorderColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnHlChnBorderColor.setEnabled(b);
			}
		});
		
		Color color = ClassicPrefs.getSelectedChannelBgColor();
		if(color == null) color = new Color(getBackground().getRGB());
		btnSelChnBgColor.setColor(color);
		btnSelChnBgColor.setEnabled(ClassicPrefs.getCustomSelectedChannelBgColor());
		
		checkSelChnBgColor.setSelected(ClassicPrefs.getCustomSelectedChannelBgColor());
		
		checkSelChnBgColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnSelChnBgColor.setEnabled(b);
			}
		});
		
		color = ClassicPrefs.getHighlightedChannelBgColor();
		if(color == null) color = new Color(getBackground().getRGB());
		btnHlChnBgColor.setColor(color);
		btnHlChnBgColor.setEnabled(ClassicPrefs.getCustomHighlightedChannelBgColor());
		
		checkHlChnBgColor.setSelected(ClassicPrefs.getCustomHighlightedChannelBgColor());
		
		checkHlChnBgColor.addItemListener(new ItemListener() {
			public void
			itemStateChanged(ItemEvent e) {
				boolean b = e.getStateChange() == e.SELECTED;
				btnHlChnBgColor.setEnabled(b);
			}
		});
		
		JButton btnDefaults = new JButton("Reset to defaults");
		btnDefaults.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				ClassicPrefs.setChannelBorderColor(null);
				btnBorderColor.setColor(ClassicPrefs.getChannelBorderColor());
				
				ClassicPrefs.setChannelBorderHlColor(null);
				btnHlChnBorderColor.setColor(ClassicPrefs.getChannelBorderHlColor());
				
				ClassicPrefs.setSelectedChannelBgColor(null);
				btnSelChnBgColor.setColor(ClassicPrefs.getSelectedChannelBgColor());
				
				ClassicPrefs.setHighlightedChannelBgColor(null);
				btnHlChnBgColor.setColor(ClassicPrefs.getHighlightedChannelBgColor());
			}
		});
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel ccp = new JPanel();
		ccp.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 3, 3, 3);
		gridbag.setConstraints(checkBorderColor, c);
		ccp.add(checkBorderColor); 
		
		
		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(btnBorderColor, c);
		ccp.add(btnBorderColor);
		
		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(checkHlChnBorderColor, c);
		ccp.add(checkHlChnBorderColor);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btnHlChnBorderColor, c);
		ccp.add(btnHlChnBorderColor);
		
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(checkSelChnBgColor, c);
		ccp.add(checkSelChnBgColor);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btnSelChnBgColor, c);
		ccp.add(btnSelChnBgColor);
		
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(checkHlChnBgColor, c);
		ccp.add(checkHlChnBgColor);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(btnHlChnBgColor, c);
		ccp.add(btnHlChnBgColor);
		
		JPanel p = new JPanel();
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 6));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		p.add(Box.createGlue());
		p.add(btnDefaults);
		p.add(Box.createGlue());
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(p, c);
		ccp.add(p);
			
		ccp.setBorder (
			BorderFactory.createTitledBorder(i18n.getLabel("ViewPane.CustomColorsPane"))
		);
		
		ccp.setMaximumSize(new Dimension(Short.MAX_VALUE, ccp.getPreferredSize().height));
		
		return ccp;
	}
	
	private String
	getInterfaceLanguage() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getLanguage();
	}
	
	private String
	getInterfaceCountry() {
		LocaleBox box = (LocaleBox)cbIfaceLanguage.getSelectedItem();
		if(box == null) return null;
		return box.getLocale().getCountry();
	}
	
	private String
	getInterfaceFontName() { return cbIfaceFont.getSelectedItem().toString(); }
	
	protected void
	apply() {
		boolean b = Prefs.setInterfaceLanguage(getInterfaceLanguage());
		boolean b2 = Prefs.setInterfaceCountry(getInterfaceCountry());
		if (b || b2) JOptionPane.showMessageDialog (
			this,
			i18n.getMessage("PrefsDlg.ifaceChangeInfo", "JS Classic"),
			null,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		b = false;
		String fontName = getInterfaceFontName();
		if(fontName.equals("[Default]")) {
			b = Prefs.setInterfaceFont(null);
		} else if(Prefs.setInterfaceFont(fontName)) {
			HF.setUIDefaultFont(fontName);
			b = true;
		}
		
		if(b) JOptionPane.showMessageDialog (
			this, 
			i18n.getMessage("PrefsDlg.ifaceFontChangeInfo", "JS Classic"),
			null,
			JOptionPane.INFORMATION_MESSAGE
		);
		
		///***///
		
		b = checkBorderColor.isSelected();
		ClassicPrefs.setCustomChannelBorderColor(b);
		if(b) ClassicPrefs.setChannelBorderColor(btnBorderColor.getColor());
		
		Color c;
		if(b) c = ClassicPrefs.getChannelBorderColor();
		else c = ClassicPrefs.getDefaultChannelBorderColor();
		Channel.setBorderColor(c);
		
		b = checkHlChnBorderColor.isSelected();
		ClassicPrefs.setCustomChannelBorderHlColor(b);
		if(b) ClassicPrefs.setChannelBorderHlColor(btnHlChnBorderColor.getColor());
		
		if(b) c = ClassicPrefs.getChannelBorderHlColor();
		else c = ClassicPrefs.getDefaultChannelBorderHlColor();
		Channel.setBorderHighlightedColor(c);
		
		b = checkSelChnBgColor.isSelected();
		ClassicPrefs.setCustomSelectedChannelBgColor(b);
		if(b) ClassicPrefs.setSelectedChannelBgColor(btnSelChnBgColor.getColor());
		
		if(b) c = ClassicPrefs.getSelectedChannelBgColor();
		else c = new Color(getBackground().getRGB());
		if(c == null) c = new Color(getBackground().getRGB());
		Channel.setSelectedChannelBgColor(c);
		
		b = checkHlChnBgColor.isSelected();
		ClassicPrefs.setCustomHighlightedChannelBgColor(b);
		if(b) ClassicPrefs.setHighlightedChannelBgColor(btnHlChnBgColor.getColor());
		
		if(b) c = ClassicPrefs.getHighlightedChannelBgColor();
		else c = new Color(getBackground().getRGB());
		if(c == null) c = new Color(getBackground().getRGB());
		Channel.setHighlightedChannelBgColor(c);
	}
	
	class LocaleBox {
		private Locale locale;
		
		LocaleBox(Locale locale) { this.locale = locale; }
		
		public Locale
		getLocale() { return locale; }
		
		public String
		toString() { return locale.getDisplayLanguage(JSI18n.i18n.getCurrentLocale()); }
	}
}

class ConsolePane extends JPanel {
	private final JCheckBox checkSaveCmdHist =
		new JCheckBox(i18n.getLabel("ConsolePane.checkSaveCmdHist"));
	
	private final JLabel lCmdHistorySize =
		new JLabel(i18n.getLabel("ConsolePane.lCmdHistorySize"));
	private JSpinner spCmdHistorySize;
	private final JLabel lLines = new JLabel(i18n.getLabel("ConsolePane.lLines"));
	private final JButton btnClearCmdHistory =
		new JButton(i18n.getButtonLabel("ConsolePane.btnClearCmdHistory"));
	
	private final JLabel lTextColor = new JLabel(i18n.getLabel("ConsolePane.lTextColor"));
	private final PrefsDlg.ColorButton btnTextColor = new PrefsDlg.ColorButton();
	
	private final JLabel lBGColor = new JLabel(i18n.getLabel("ConsolePane.lBGColor"));
	private final PrefsDlg.ColorButton btnBGColor = new PrefsDlg.ColorButton();
	
	private final JLabel lNotifyColor = new JLabel(i18n.getLabel("ConsolePane.lNotifyColor"));
	private final PrefsDlg.ColorButton btnNotifyColor = new PrefsDlg.ColorButton();
	
	private final JLabel lWarningColor = new JLabel(i18n.getLabel("ConsolePane.lWarningColor"));
	private final PrefsDlg.ColorButton btnWarningColor = new PrefsDlg.ColorButton();
	
	private final JLabel lErrorColor = new JLabel(i18n.getLabel("ConsolePane.lErrorColor"));
	private final PrefsDlg.ColorButton btnErrorColor = new PrefsDlg.ColorButton();
	
	
	public
	ConsolePane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(checkSaveCmdHist);
		
		add(createCommandHistoryPane());
		add(Box.createRigidArea(new Dimension(0, 6)));
		add(createConsoleColorsPane());
		
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	private JPanel
	createCommandHistoryPane() {
		JPanel chp = new JPanel();
		chp.setAlignmentX(CENTER_ALIGNMENT);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		chp.setLayout(gridbag);
		
		int i = ClassicPrefs.getLSConsoleHistSize();
		spCmdHistorySize = new JSpinner(new SpinnerNumberModel(i, 0, 20000, 1));
		spCmdHistorySize.setMaximumSize(spCmdHistorySize.getPreferredSize());
		
		btnClearCmdHistory.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				LSConsolePane.clearConsoleHistory();
				MainFrame mainFrame = (MainFrame)CC.getMainFrame();
				mainFrame.getLSConsoleModel().clearCommandHistory();
			}
		});
			
		btnClearCmdHistory.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		add(btnClearCmdHistory);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(0, 6, 3, 0);
		gridbag.setConstraints(lCmdHistorySize, c);
		chp.add(lCmdHistorySize); 

		c.gridx = 1;
		c.gridy = 0;
		gridbag.setConstraints(spCmdHistorySize, c);
		chp.add(spCmdHistorySize); 

		c.gridx = 2;
		c.gridy = 0;
		gridbag.setConstraints(lLines, c);
		chp.add(lLines); 

		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(0, 12, 3, 6);
		gridbag.setConstraints(btnClearCmdHistory, c);
		chp.add(btnClearCmdHistory); 

		checkSaveCmdHist.setSelected(ClassicPrefs.getSaveConsoleHistory());
		checkSaveCmdHist.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 4;
		c.insets = new Insets(3, 6, 3, 6);
		c.anchor = GridBagConstraints.CENTER;
		gridbag.setConstraints(checkSaveCmdHist, c);
		chp.add(checkSaveCmdHist); 

		String s = i18n.getLabel("ConsolePane.commandHistory");
		chp.setBorder(BorderFactory.createTitledBorder(s));
		
		chp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		return chp;
	}
	
	private JPanel
	createConsoleColorsPane() {
		JPanel ccp = new JPanel();
		ccp.setAlignmentX(CENTER_ALIGNMENT);
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		ccp.setLayout(gridbag);
		
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lTextColor, c);
		ccp.add(lTextColor); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lBGColor, c);
		ccp.add(lBGColor);
	
		c.gridx = 0;
		c.gridy = 2;
		gridbag.setConstraints(lNotifyColor, c);
		ccp.add(lNotifyColor);
	
		c.gridx = 0;
		c.gridy = 3;
		gridbag.setConstraints(lWarningColor, c);
		ccp.add(lWarningColor);
	
		c.gridx = 0;
		c.gridy = 4;
		gridbag.setConstraints(lErrorColor, c);
		ccp.add(lErrorColor);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		//c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(btnTextColor, c);
		ccp.add(btnTextColor);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(btnBGColor, c);
		ccp.add(btnBGColor);
		
		c.gridx = 1;
		c.gridy = 2;
		gridbag.setConstraints(btnNotifyColor, c);
		ccp.add(btnNotifyColor);
		
		c.gridx = 1;
		c.gridy = 3;
		gridbag.setConstraints(btnWarningColor, c);
		ccp.add(btnWarningColor);
		
		c.gridx = 1;
		c.gridy = 4;
		gridbag.setConstraints(btnErrorColor, c);
		ccp.add(btnErrorColor);
		
		btnTextColor.setColor(ClassicPrefs.getLSConsoleTextColor());
		btnBGColor.setColor(ClassicPrefs.getLSConsoleBackgroundColor());
		btnNotifyColor.setColor(ClassicPrefs.getLSConsoleNotifyColor());
		btnWarningColor.setColor(ClassicPrefs.getLSConsoleWarningColor());
		btnErrorColor.setColor(ClassicPrefs.getLSConsoleErrorColor());
		
		JButton btnDefaults = new JButton("Reset to defaults");
		
		btnDefaults.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				ClassicPrefs.setLSConsoleTextColor(null);
				btnTextColor.setColor(ClassicPrefs.getLSConsoleTextColor());
				
				ClassicPrefs.setLSConsoleBackgroundColor(null);
				btnBGColor.setColor(ClassicPrefs.getLSConsoleBackgroundColor());
				
				ClassicPrefs.setLSConsoleNotifyColor(null);
				btnNotifyColor.setColor(ClassicPrefs.getLSConsoleNotifyColor());
				
				ClassicPrefs.setLSConsoleWarningColor(null);
				btnWarningColor.setColor(ClassicPrefs.getLSConsoleWarningColor());
				
				ClassicPrefs.setLSConsoleErrorColor(null);
				btnErrorColor.setColor(ClassicPrefs.getLSConsoleErrorColor());
			}
		});
		
		JPanel p = new JPanel();
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 6));
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		p.add(Box.createGlue());
		p.add(btnDefaults);
		p.add(Box.createGlue());
		
		c.gridx = 0;
		c.gridy = 5;
		c.gridwidth = 2;
		gridbag.setConstraints(p, c);
		ccp.add(p);
		
		String s = i18n.getLabel("ConsolePane.consoleColors");
		ccp.setBorder(BorderFactory.createTitledBorder(s));
		
		ccp.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
		return ccp;
	}
	
	protected void
	apply() {
		ClassicPrefs.setSaveConsoleHistory(checkSaveCmdHist.isSelected());
		
		int size = Integer.parseInt(spCmdHistorySize.getValue().toString());
		ClassicPrefs.setLSConsoleHistSize(size);
		LSConsoleModel model = ((MainFrame)CC.getMainFrame()).getLSConsoleModel();
		model.setCommandHistorySize(size);
		
		///***///
		
		MainFrame mainFrame = (MainFrame)CC.getMainFrame();
		
		ClassicPrefs.setLSConsoleTextColor(btnTextColor.getColor());
		mainFrame.setLSConsoleTextColor(btnTextColor.getColor());
		
		ClassicPrefs.setLSConsoleBackgroundColor(btnBGColor.getColor());
		mainFrame.setLSConsoleBackgroundColor(btnBGColor.getColor());
		
		ClassicPrefs.setLSConsoleNotifyColor(btnNotifyColor.getColor());
		mainFrame.setLSConsoleNotifyColor(btnNotifyColor.getColor());
		
		ClassicPrefs.setLSConsoleWarningColor(btnWarningColor.getColor());
		mainFrame.setLSConsoleWarningColor(btnWarningColor.getColor());
		
		ClassicPrefs.setLSConsoleErrorColor(btnErrorColor.getColor());
		mainFrame.setLSConsoleErrorColor(btnErrorColor.getColor());
	}
	
	
}

class ConnectionPane extends JPanel {
	final LSPrefsPane lsPrefsPane = new LSPrefsPane(); 
	
	public
	ConnectionPane() { initConnectionPane(); }
	
	private void
	initConnectionPane() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(lsPrefsPane);
		add(Box.createGlue());
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
	}
	
	public String
	getLSAddress() { return lsPrefsPane.getLSAddress(); }
	
	public void
	setLSAddress(String address) { lsPrefsPane.setLSAddress(address); }
	
	public String
	getLSPort() { return lsPrefsPane.getLSPort(); }
	
	public void
	setLSPort(String port) { lsPrefsPane.setLSPort(port); }
}

class LSPrefsPane extends JPanel {
	private final JLabel lAddress = new JLabel(i18n.getLabel("LSPrefsPane.Address"));
	private final JLabel lPort = new JLabel(i18n.getLabel("LSPrefsPane.Port"));
	private final JTextField tfAddress = new JTextField();
	private final JTextField tfPort = new JTextField();


	public
	LSPrefsPane() { initLSPrefsPane(); }

	private void
	initLSPrefsPane() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
	
		setLayout(gridbag);
		
		// Set preferred size for username & password fields
		int w1 = (int) tfAddress.getMinimumSize().getWidth();
		int h1 = (int) tfAddress.getMinimumSize().getHeight();
		Dimension d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfAddress.setMinimumSize(d);
		tfAddress.setPreferredSize(d);
	
		w1 = (int) tfPort.getMinimumSize().getWidth();
		h1 = (int) tfPort.getMinimumSize().getHeight();
		d = new Dimension(w1 > 150 ? w1 : 150, h1);
		tfPort.setMinimumSize(d);
		tfPort.setPreferredSize(d);
	
		c.fill = GridBagConstraints.NONE;
	
		c.gridx = 0;
		c.gridy = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3, 3, 3, 3);
		gridbag.setConstraints(lAddress, c);
		add(lAddress); 

		c.gridx = 0;
		c.gridy = 1;
		gridbag.setConstraints(lPort, c);
		add(lPort);
	
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(tfAddress, c);
		add(tfAddress);
		
		c.gridx = 1;
		c.gridy = 1;
		gridbag.setConstraints(tfPort, c);
		add(tfPort);
		
		setBorder(BorderFactory.createTitledBorder(i18n.getLabel("LSPrefsPane")));
		setMaximumSize(new Dimension(Short.MAX_VALUE, getPreferredSize().height));
	}
	
	public String
	getLSAddress() { return tfAddress.getText(); }
	
	public void
	setLSAddress(String address) { tfAddress.setText(address); }
	
	public String
	getLSPort() { return tfPort.getText(); }
	
	public void
	setLSPort(String port) { tfPort.setText(port); }
}
