/*
 *   JSampler - a java front-end for LinuxSampler
 *
 *   Copyright (C) 2005, 2006 Grigor Kirilov Iliev
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.BufferedReader;
import java.io.StringReader;

import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import javax.swing.border.EtchedBorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import org.jsampler.CC;
import org.jsampler.DefaultLSConsoleModel;
import org.jsampler.HF;
import org.jsampler.Instrument;
import org.jsampler.LSConsoleModel;
import org.jsampler.LscpUtils;

import org.jsampler.event.LSConsoleEvent;
import org.jsampler.event.LSConsoleListener;

import static javax.swing.KeyStroke.*;
import static org.jsampler.view.classic.ClassicI18n.i18n;


/**
 *
 * @author Grigor Iliev
 */
public class LSConsolePane extends JPanel {
	private enum AutocompleteMode { AUTOCOMPLETE, HISTORY_SEARCH, CMD_LIST_SEARCH }
	private AutocompleteMode autocompleteMode = AutocompleteMode.AUTOCOMPLETE;
	
	private Window owner;
	
	private final JButton btnMenu = new ToolbarButton();
	private JPopupMenu menu = new JPopupMenu();
	
	private final LSConsoleTextPane console = new LSConsoleTextPane();
	
	private final JPanel inputPane = new JPanel();
	private final JLabel lInput = new JLabel();
	private final JTextField tfSearch = new JTextField();
	private final CmdLineTextField tfInput = new CmdLineTextField();
	
	private AutoCompleteWindow autoCompleteWindow;
	
	private final LSConsoleModel model = new DefaultLSConsoleModel();
	
	private final StringBuffer consoleText = new StringBuffer();
	
	private final LSConsoleViewMode lsConsoleViewMode;
	
	private boolean processingSearch = false;
	
	
	/** Creates a new instance of <code>LSConsolePane</code>. */
	public
	LSConsolePane(Window owner) {
		setOwner(owner);
		
		model.setCommandHistorySize(ClassicPrefs.getLSConsoleHistSize());
		String s = ClassicPrefs.getLSConsoleHistory();
		
		BufferedReader br = new BufferedReader(new StringReader(s));
		
		try {
			s = br.readLine();
			while(s != null) {
				model.addToCommandHistory(s);
				s = br.readLine();
			}
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
		
		lsConsoleViewMode = new LSConsoleViewMode();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createGlue());
		
		btnMenu.setIcon(Res.iconDown16);
		btnMenu.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		btnMenu.setFocusPainted(false);
		p.add(btnMenu);
		p.setMaximumSize(new Dimension(Short.MAX_VALUE, p.getPreferredSize().height));
		
		add(p);
		
		setBackgroundColor(ClassicPrefs.getLSConsoleBackgroundColor());
				
		setTextColor(ClassicPrefs.getLSConsoleTextColor());
		
		add(new JScrollPane(console));
		
		inputPane.setLayout(new BoxLayout(inputPane, BoxLayout.X_AXIS));
		inputPane.setBorder(tfInput.getBorder());
		tfInput.setBorder(BorderFactory.createEmptyBorder());
		tfSearch.setBorder(BorderFactory.createEmptyBorder());
		
		lInput.setOpaque(false);
		lInput.setVisible(false);
		inputPane.add(lInput);
		
		Dimension d = new Dimension(Short.MAX_VALUE, tfSearch.getPreferredSize().height);
		tfSearch.setMaximumSize(d);
		
		tfSearch.setVisible(false);
		tfSearch.setFocusTraversalKeysEnabled(false);
		inputPane.add(tfSearch);
		
		d = new Dimension(Short.MAX_VALUE, tfInput.getPreferredSize().height);
		tfInput.setMaximumSize(d);
		
		tfInput.setFocusTraversalKeysEnabled(false);
		inputPane.add(tfInput);
		
		add(inputPane);
		
		tfInput.addActionListener(getHandler());
		tfInput.getDocument().addDocumentListener(getHandler());
		getModel().addLSConsoleListener(getHandler());
		
		tfSearch.addActionListener(new Actions(Actions.APPLY_SEARCH));
		
		installKeyboardListeners();
		initMenu(owner);
	}
	
	private void
	installKeyboardListeners() {
		KeyStroke k = getKeyStroke(KeyEvent.VK_TAB, 0);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.AUTOCOMPLETE);
		tfInput.getActionMap().put(Actions.AUTOCOMPLETE, new Actions(Actions.AUTOCOMPLETE));
		
		k = getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.HISTORY_SEARCH);
		tfInput.getActionMap().put (
			Actions.HISTORY_SEARCH, new Actions(Actions.HISTORY_SEARCH)
		);
		
		k = getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.CMD_LIST_SEARCH);
		tfInput.getActionMap().put (
			Actions.CMD_LIST_SEARCH, new Actions(Actions.CMD_LIST_SEARCH)
		);
		
		k = getKeyStroke(KeyEvent.VK_UP, 0);
		tfInput.getInputMap(JComponent.WHEN_FOCUSED).put(k, Actions.MOVE_UP);
		tfInput.getActionMap().put(Actions.MOVE_UP, new Actions(Actions.MOVE_UP));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, 0);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_DOWN);
		tfInput.getActionMap().put(Actions.MOVE_DOWN, new Actions(Actions.MOVE_DOWN));
		
		k = getKeyStroke(KeyEvent.VK_HOME, 0);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_HOME);
		tfInput.getActionMap().put(Actions.MOVE_HOME, new Actions(Actions.MOVE_HOME));
		
		k = getKeyStroke(KeyEvent.VK_END, 0);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_END);
		tfInput.getActionMap().put(Actions.MOVE_END, new Actions(Actions.MOVE_END));
		
		k = getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_MASK);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.CLEAR_CONSOLE);
		tfInput.getActionMap().put (
			Actions.CLEAR_CONSOLE, new Actions(Actions.CLEAR_CONSOLE)
		);
		
		k = getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		tfInput.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			k, Actions.CANCEL_SELECTION
		);
		tfInput.getActionMap().put (
			Actions.CANCEL_SELECTION, new Actions(Actions.CANCEL_SELECTION)
		);
		
		k = getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_MASK);
		tfInput.getInputMap(WHEN_FOCUSED).put(k, Actions.QUIT_SESSION);
		tfInput.getActionMap().put(Actions.QUIT_SESSION, new Actions(Actions.QUIT_SESSION));
		
		
		k = getKeyStroke(KeyEvent.VK_UP, 0);
		tfSearch.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_UP);
		tfSearch.getActionMap().put(Actions.MOVE_UP, new Actions(Actions.MOVE_UP));
		
		k = getKeyStroke(KeyEvent.VK_DOWN, 0);
		tfSearch.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_DOWN);
		tfSearch.getActionMap().put(Actions.MOVE_DOWN, new Actions(Actions.MOVE_DOWN));
		
		k = getKeyStroke(KeyEvent.VK_HOME, 0);
		tfSearch.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_HOME);
		tfSearch.getActionMap().put(Actions.MOVE_HOME, new Actions(Actions.MOVE_HOME));
		
		k = getKeyStroke(KeyEvent.VK_END, 0);
		tfSearch.getInputMap(WHEN_FOCUSED).put(k, Actions.MOVE_END);
		tfSearch.getActionMap().put(Actions.MOVE_END, new Actions(Actions.MOVE_END));
		
		k = getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		tfSearch.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put (
			k, Actions.CANCEL_SEARCH
		);
		tfSearch.getActionMap().put (
			Actions.CANCEL_SEARCH, new Actions(Actions.CANCEL_SEARCH)
		);
	}
	
	private void
	initMenu(Window owner) {
		JMenuItem mi = new JMenuItem(lsConsoleViewMode);
		menu.add(mi);
		
		menu.addSeparator();
		
		JMenu clearMenu = new JMenu(i18n.getMenuLabel("LSConsolePane.clear"));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.clearConsole"));
		clearMenu.add(mi);
		mi.addActionListener(new Actions(Actions.CLEAR_CONSOLE));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.clearSessionHistory"));
		clearMenu.add(mi);
		mi.addActionListener(new Actions(Actions.CLEAR_SESSION_HISTORY));
		
		menu.add(clearMenu);
		
		JMenu exportMenu = new JMenu(i18n.getMenuLabel("LSConsolePane.export"));
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.exportSession"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				LscpScriptDlg dlg = new LscpScriptDlg();
				dlg.setCommands(getModel().getSessionHistory());
				dlg.setVisible(true);
			}
		});
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.exportCommandHistory"));
		exportMenu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				LscpScriptDlg dlg = new LscpScriptDlg();
				dlg.setCommands(getModel().getCommandHistory());
				dlg.setVisible(true);
			}
		});
		
		menu.add(exportMenu);
		
		mi = new JMenuItem(i18n.getMenuLabel("LSConsolePane.runScript"));
		menu.add(mi);
		mi.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				((MainFrame)CC.getMainFrame()).runScript();
			}
		});
		
		btnMenu.addActionListener(new ActionListener() {
			public void
			actionPerformed(ActionEvent e) {
				int x = (int)btnMenu.getMinimumSize().getWidth();
				x -= (int)menu.getPreferredSize().getWidth();
				int y = (int)btnMenu.getMinimumSize().getHeight() + 1;
				menu.show(btnMenu, x, y);
			}
		});
	}
	
	/**
	 * Gets the LS Console data model.
	 * @return The LS Console data model.
	 */
	public LSConsoleModel
	getModel() { return model; }
	
	/**
	 * Sets the text color of the LS Console.
	 * @param c The text color of the LS Console.
	 */
	public void
	setTextColor(Color c) {
		console.setTextColor(c);
		
		lInput.setForeground(c);
		tfInput.setForeground(c);
		tfSearch.setForeground(c);
		
		autoCompleteWindow.setTextColor(c);
	}
	
	/**
	 * Gets the text color of the LS Console.
	 * @return The text color of the LS Console.
	 */
	public Color
	getTextColor() { return console.getTextColor(); }
	
	/**
	 * Sets the background color of the LS Console.
	 * @param c The background color of the LS Console.
	 */
	public void
	setBackgroundColor(Color c) {
		console.setBackground(c);
		
		lInput.setBackground(c);
		inputPane.setBackground(c);
		tfInput.setBackground(c);
		tfSearch.setBackground(c);
		
		autoCompleteWindow.setBackgroundColor(c);
	}
	
	/**
	 * Gets the background color of the LS Console.
	 * @return The background color of the LS Console.
	 */
	public Color
	getBackgroundColor() { return console.getBackground(); }
	
	/**
	 * Sets the notification messages' color.
	 * @param c The notification messages' color.
	 */
	public void
	setNotifyColor(Color c) { console.setNotifyColor(c); }
	
	/**
	 * Sets the warning messages' color.
	 * @param c The warning messages' color.
	 */
	public void
	setWarningColor(Color c) { console.setWarningColor(c); }
	
	/**
	 * Sets the error messages' color.
	 * @param c The error messages' color.
	 */
	public void
	setErrorColor(Color c) { console.setErrorColor(c); }
	
	public class CmdLineTextField extends JTextField {
		CmdLineTextField() {
			setTransferHandler(new TransferHandler("instrumentLoad"));
		}
		
		public String
		getInstrumentLoad() { return getSelectedText(); }
		
		public void setInstrumentLoad(String instr) {
			if(instr == null) return;
			
			if(!Instrument.isDnDString(instr)) {
				replaceSelection(instr);
				return;
			}
			
			String[] args = instr.split("\n");
			if(args.length < 6) return;
			
			String s = "LOAD INSTRUMENT NON_MODAL '" + args[4] + "' " + args[5] + " ";
			getModel().setCommandLineText(s);
			requestFocus();
		}
	}
	
	private class LSConsoleViewMode extends AbstractAction {
		LSConsoleViewMode() { }
		
		public void
		actionPerformed(ActionEvent e) {
			MainFrame mainFrame = (MainFrame)CC.getMainFrame();
			mainFrame.setLSConsolePopOut(!mainFrame.isLSConsolePopOut());
			
			setName(mainFrame.isLSConsolePopOut());
		}
		
		private void
		setName(boolean b) {
			if(b) {
				putValue(Action.NAME, i18n.getMenuLabel("LSConsolePane.popin"));
			} else {
				putValue(Action.NAME, i18n.getMenuLabel("LSConsolePane.popout"));
			}	
		}
	}
	
	/**
	 * Updates the text of the menu item responsible for changing the pop-out/pop-in mode.
	 */
	public void
	updateLSConsoleViewMode() {
		if(getOwner() instanceof LSConsoleDlg) lsConsoleViewMode.setName(true);
		else if(getOwner() instanceof MainFrame) {
			lsConsoleViewMode.setName(((MainFrame)getOwner()).isLSConsolePopOut());
		}
	}
	
	public void
	setOwner(Window owner) {
		if(autoCompleteWindow != null && autoCompleteWindow.isVisible())
			autoCompleteWindow.setVisible(false);
		
		autoCompleteWindow = new AutoCompleteWindow(owner);
		
		if(getOwner() != null) getOwner().removeWindowListener(getHandler());
		owner.addWindowListener(getHandler());
		
		this.owner = owner;
	}
	
	public Window
	getOwner() { return owner; }
	
	/** Hides the autocomplete window. */
	public void
	hideAutoCompleteWindow() { autoCompleteWindow.setVisible(false); }
	
	
	private final SearchHandler searchHandler = new SearchHandler();
	
	private SearchHandler
	getSearchHandler() { return searchHandler; }
	
	private class SearchHandler implements DocumentListener {
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { processSearch(); }
		
		public void
		removeUpdate(DocumentEvent e) { processSearch(); }
		
		public void
		changedUpdate(DocumentEvent e) { processSearch(); }
	}
	
		
	private final Handler eventHandler = new Handler();
	
	private Handler
	getHandler() { return eventHandler; }
	
	private class Handler extends WindowAdapter
				implements ActionListener, DocumentListener, LSConsoleListener {
		
		// ActionListener
		public void
		actionPerformed(ActionEvent e) {
			if(autoCompleteWindow.isVisible()) autoCompleteWindow.applySelection();
			else getModel().execCommand();
		}
		
		// DocumentListener
		public void
		insertUpdate(DocumentEvent e) { getModel().setCommandLineText(tfInput.getText()); }
		
		public void
		removeUpdate(DocumentEvent e) { getModel().setCommandLineText(tfInput.getText()); }
		
		public void
		changedUpdate(DocumentEvent e) { getModel().setCommandLineText(tfInput.getText()); }
		
		// WindowListener
		public void
		windowActivated(WindowEvent e) {
			if(autocompleteMode == AutocompleteMode.AUTOCOMPLETE) {
				tfInput.requestFocusInWindow();
			}  else tfSearch.requestFocusInWindow();
		}
		
		public void
		windowDeactivated(WindowEvent e) { autoCompleteWindow.setVisible(false); }
			
		public void
		windowIconified(WindowEvent e) { autoCompleteWindow.setVisible(false); }
		
		// LSConsoleListener
		
		/** Invoked when the text in the command line is changed. */
		public void
		commandLineTextChanged(LSConsoleEvent e) {
			commandChanged(e.getPreviousCommandLineText());
		}
		
		/** Invoked when the command in the command line has been executed. */
		public void 
		commandExecuted(LSConsoleEvent e) {
			console.addCommand(getModel().getLastExecutedCommand());
		}
		
		/** Invoked when response is received from LinuxSampler. */
		public void
		responseReceived(LSConsoleEvent e) {
			console.addCommandResponse(e.getResponse());
		}
	}
	
	private void
	commandChanged(String oldCmdLine) {
		if(!tfInput.getText().equals(getModel().getCommandLineText()))
			tfInput.setText(getModel().getCommandLineText());
		
		if(LscpUtils.spellCheck(tfInput.getText())) {
			tfInput.setForeground(console.getTextColor());
		} else {
			tfInput.setForeground(console.getErrorColor());
			if(autoCompleteWindow.isVisible()) autoCompleteWindow.setVisible(false);
		}
		
		if(autoCompleteWindow.isVisible()) processAutoComplete(oldCmdLine);
	}
	
	private boolean
	isProcessingSearch() { return processingSearch; }
	
	private void
	setProcessingSearch(boolean b) { processingSearch = b; }
	
	/** Invoked when the tab key is pressed. */
	private void
	processAutoComplete() {
		processAutoComplete(null);
	}
	
	private void
	processAutoComplete(String oldCmdLine) {
		String cmdLine = getModel().getCommandLineText();
		switch(autocompleteMode) {
		case AUTOCOMPLETE:
			
			break;
		case HISTORY_SEARCH:
		case CMD_LIST_SEARCH:
			if(autoCompleteWindow.isVisible()) return;
		}
		
		autocompleteMode = AutocompleteMode.AUTOCOMPLETE;
		
		final String[] cmdS;
		
		try {
			cmdS = LscpUtils.getCompletionPossibilities(cmdLine);
		} catch(IllegalStateException e) {
			autoCompleteWindow.setVisible(false);
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}
		
		if(cmdS.length == 0) {
			autoCompleteWindow.setVisible(false);
			return;
		}
		if(cmdS.length == 1) {
			if(oldCmdLine != null && oldCmdLine.startsWith(cmdLine)) return;
			
			// To prevent IllegalStateException exception.
			SwingUtilities.invokeLater(new Runnable() {
				public void
				run() { tfInput.setText(cmdS[0]); }
			});
			
			return;
		}
		
		autoCompleteWindow.setCommandList(cmdS);
		autoCompleteWindow.setVisible(true);
	}
	
	private void
	startHistorySearch() {
		autocompleteMode = AutocompleteMode.HISTORY_SEARCH;
		lInput.setText("History Search: ");
		
		startSearch();
	}
	
	private void
	startCmdListSearch() {
		autocompleteMode = AutocompleteMode.CMD_LIST_SEARCH;
		lInput.setText("Command List Search: ");
		
		startSearch();
	}
	
	private void
	startSearch() {
		if(!isProcessingSearch())
			tfSearch.getDocument().addDocumentListener(getSearchHandler());
		
		setProcessingSearch(true);
		
		lInput.setVisible(true);
		tfInput.setVisible(false);
		tfSearch.setText(getModel().getCommandLineText());
		tfSearch.setVisible(true);
		tfSearch.requestFocusInWindow();
		
		revalidate();
		repaint();
		
		processSearch();
	}
	
	private void
	processSearch() {
		String[] cmdS = null;
		
		switch(autocompleteMode) {
		case HISTORY_SEARCH:
			cmdS = getModel().searchCommandHistory(tfSearch.getText());
			break;
		case CMD_LIST_SEARCH:
			cmdS = getModel().searchCommandList(tfSearch.getText());
			break;
		}
		
		autoCompleteWindow.setCommandList(cmdS);
		autoCompleteWindow.setVisible(cmdS.length > 0);
	}
	
	private void
	stopSearch(boolean cancelSearch) {
		setProcessingSearch(false);
		
		tfSearch.getDocument().removeDocumentListener(getSearchHandler());
		
		lInput.setVisible(false);
		lInput.setText("");
		tfSearch.setVisible(false);
		tfInput.setVisible(true);
		tfInput.requestFocusInWindow();
		
		revalidate();
		repaint();
		
		if(cancelSearch) {
			autoCompleteWindow.setVisible(false);
			return;
		}
		
		if(autoCompleteWindow.isVisible()) autoCompleteWindow.applySelection();
		else getModel().setCommandLineText(tfSearch.getText());
	}
	
	
	private class Actions extends AbstractAction {
		private static final String AUTOCOMPLETE = "autocomplete";
		private static final String HISTORY_SEARCH = "historySearch";
		private static final String CMD_LIST_SEARCH = "cmdListSearch";
		private static final String CANCEL_SEARCH = "cancelSearch";
		private static final String APPLY_SEARCH = "applySearch";
		private static final String MOVE_UP = "moveUp";
		private static final String MOVE_DOWN = "moveDown";
		private static final String MOVE_HOME = "moveHome";
		private static final String MOVE_END = "moveEnd";
		private static final String CANCEL_SELECTION = "cancelSelection";
		private static final String CLEAR_CONSOLE = "clearConsole";
		private static final String CLEAR_SESSION_HISTORY = "clearSessionHistory";
		private static final String CLEAR_COMMAND_HISTORY = "clearCommandHistory";
		private static final String QUIT_SESSION = "quitSession";
		
		Actions(String name) { super(name); }
		
		public void
		actionPerformed(ActionEvent e) {
			String key = getValue(Action.NAME).toString();
			
			if(key == AUTOCOMPLETE) {
				processAutoComplete();
			} else if(key == HISTORY_SEARCH) {
				startHistorySearch();
			} else if(key == CMD_LIST_SEARCH) {
				startCmdListSearch();
			} else if(key == CANCEL_SEARCH) {
				stopSearch(true);
			} else if(key == APPLY_SEARCH) {
				stopSearch(false);
			} else if(key == MOVE_UP) {
				if(autoCompleteWindow.isVisible()) {
					autoCompleteWindow.selectPreviousItem();
					return;
				}
				
				getModel().browseCommandHistoryUp();
			} else if(key == MOVE_DOWN) {
				if(autoCompleteWindow.isVisible()) {
					autoCompleteWindow.selectNextItem();
					return;
				}
				
				getModel().browseCommandHistoryDown();
			} else if(key == MOVE_HOME) {
				if(autoCompleteWindow.isVisible()) {
					autoCompleteWindow.selectFirstItem();
					return;
				}
				
				JTextField tf = tfInput.isVisible() ? tfInput : tfSearch;
				tf.setCaretPosition(0);
			} else if(key == MOVE_END) {
				if(autoCompleteWindow.isVisible()) {
					autoCompleteWindow.selectLastItem();
					return;
				}
				
				JTextField tf = tfInput.isVisible() ? tfInput : tfSearch;
				tf.setCaretPosition(tf.getText().length());
			} else if(key == CANCEL_SELECTION) {
				autoCompleteWindow.setVisible(false);
			} else if(key == CLEAR_CONSOLE) {
				console.setText("");
			} else if(key == CLEAR_SESSION_HISTORY) {
				getModel().clearSessionHistory();
			} else if(key == CLEAR_COMMAND_HISTORY) {
				getModel().clearCommandHistory();
			} else if(key == QUIT_SESSION) {
				getModel().clearSessionHistory();
				console.setText("");
				((MainFrame)CC.getMainFrame()).setLSConsoleVisible(false);
			}
		}
	}
	
	private class AutoCompleteWindow extends JWindow {
		private int MAX_HEIGHT = 140;
		
		private JList list = new JList();
		private JScrollPane scrollPane;
		
		AutoCompleteWindow(Window owner) {
			super(owner);
			
			owner.addComponentListener(getHandler());
			
			list.addMouseListener(new MouseAdapter() {
				public void
				mouseClicked(MouseEvent e) {
					if(list.getSelectedIndex() != -1) applySelection();
				}
			});
			
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						
			scrollPane = new JScrollPane(list);
			//sp.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
			scrollPane.setPreferredSize (
				new Dimension(scrollPane.getPreferredSize().width, MAX_HEIGHT)
			);
			add(scrollPane);
			
			int i = JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT;
				getRootPane().getInputMap(i).put (
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				"applySelection"
			);
		
			getRootPane().getActionMap().put ("applySelection", new AbstractAction() {
				public void
				actionPerformed(ActionEvent e) { applySelection(); }
			});
		
		}
		
		public void
		setCommandList(String[] cmdS) {
			list.setListData(cmdS);
			if(cmdS.length > 0) list.setSelectedIndex(0);
			
			int h = list.getPreferredSize().height + 6;
			if(h > MAX_HEIGHT) h = MAX_HEIGHT;
			if(h == scrollPane.getSize().height) return;
			
			scrollPane.setPreferredSize (
				new Dimension(scrollPane.getPreferredSize().width, h)
			);
			
			scrollPane.setMaximumSize (
				new Dimension(scrollPane.getPreferredSize().width, h)
			);
			
			setVisible(false);
			setSize(getSize().width, h);
			setVisible(true);
		}
		
		public void
		setVisible(boolean b) {
			if(b) updateLocation0();
			super.setVisible(b);
		}
		
		public void
		selectNextItem() {
			int size = list.getModel().getSize();
			if(size == 0) return;
			
			int i = list.getSelectedIndex();
			if(i == -1 || i == size - 1) list.setSelectedIndex(0);
			else list.setSelectedIndex(i + 1);
			
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		
		public void
		selectPreviousItem() {
			int size = list.getModel().getSize();
			if(size == 0) return;
			
			int i = list.getSelectedIndex();
			if(i == -1 || i == 0) list.setSelectedIndex(size - 1);
			else list.setSelectedIndex(i - 1);
			
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		
		public void
		selectFirstItem() {
			int size = list.getModel().getSize();
			if(size == 0) return;
			
			list.setSelectedIndex(0);
			
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		
		public void
		selectLastItem() {
			int size = list.getModel().getSize();
			if(size == 0) return;
			
			list.setSelectedIndex(size - 1);
			
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		
		/**
		 * Sets the text color of the autocompletion list.
		 * @param c The text color of autocompletion list.
		 */
		public void
		setTextColor(Color c) {
			//Object o = list.getCellRenderer();
			//if(o instanceof JComponent) ((JComponent)o).setForeground(c);
			list.setForeground(c);
		}
		
		/**
		 * Sets the background color of the autocompletion list.
		 * @param c The background color of the autocompletion list.
		 */
		public void
		setBackgroundColor(Color c) { list.setBackground(c); }
	
		private void
		updateLocation() {
			if(!isVisible()) return;
			updateLocation0();
		}
		
		private void
		updateLocation0() {
			Dimension d;
			d = new Dimension(inputPane.getSize().width - 6, getSize().height);
			setPreferredSize(d);
			
			Point p = inputPane.getLocationOnScreen();
			pack();
			setLocation(p.x + 3, p.y - getSize().height);
		}
		
		private void
		applySelection() {
			Object o = list.getSelectedValue();
			if(o != null) tfInput.setText(o.toString());
			setVisible(false);
		}
		
		private final Handler eventHandler = new Handler();
	
		private Handler
		getHandler() { return eventHandler; }
	
		private class Handler extends ComponentAdapter {
			
			// ComponentListener
			public void
			componentMoved(ComponentEvent e) { updateLocation(); }
				
			public void
			componentResized(ComponentEvent e) { updateLocation(); }
		}
	}
}

class LSConsoleTextPane extends JTextPane {
	private final String STYLE_ROOT = "root";
	private final String STYLE_REGULAR = "regular";
	private final String STYLE_BOLD = "bold";
	private final String STYLE_NOTIFY_0 = "notificationMessage0";
	private final String STYLE_NOTIFY = "notificationMessage";
	private final String STYLE_WARN_0 = "warningMessage0";
	private final String STYLE_WARN = "warningMessage";
	private final String STYLE_ERROR_0 = "errorMessage0";
	private final String STYLE_ERROR = "errorMessage";
	
	private Color cmdColor;
	private Color notifyColor;
	private Color warnColor;
	private Color errorColor;
	
	LSConsoleTextPane() {
		cmdColor = ClassicPrefs.getLSConsoleTextColor();
		notifyColor = ClassicPrefs.getLSConsoleNotifyColor();
		errorColor = ClassicPrefs.getLSConsoleErrorColor();
		warnColor = ClassicPrefs.getLSConsoleWarningColor();
		
		Style def;
		def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyledDocument doc = getStyledDocument();
		Style root = doc.addStyle(STYLE_ROOT, def);
		Style regular = doc.addStyle(STYLE_REGULAR, root);
		
		Style style = doc.addStyle(STYLE_BOLD, regular);
		StyleConstants.setBold(style, true);
		
		style = doc.addStyle(STYLE_NOTIFY_0, regular);
		StyleConstants.setForeground(style, notifyColor);
		doc.addStyle(STYLE_NOTIFY, style);
		
		style = doc.addStyle(STYLE_WARN_0, regular);
		StyleConstants.setForeground(style, warnColor);
		doc.addStyle(STYLE_WARN, style);
			
		style = doc.addStyle(STYLE_ERROR_0, regular);
		StyleConstants.setForeground(style, errorColor);
		doc.addStyle(STYLE_ERROR, style);
			
		setEditable(false);
		
	}
	
	/**
	 * Sets the text color.
	 * @param c The text color.
	 */
	public void
	setTextColor(Color c) {
		cmdColor = c;
		
		StyledDocument doc = getStyledDocument();
		Style root = doc.getStyle(STYLE_ROOT);
		StyleConstants.setForeground(root, cmdColor);
	}
	
	/**
	 * Gets the text color of the LS Console.
	 * @return The text color of the LS Console.
	 */
	public Color
	getTextColor() { return cmdColor; }
	
	/**
	 * Sets the notification messages' color.
	 * @param c The notification messages' color.
	 */
	public void
	setNotifyColor(Color c) {
		notifyColor = c;
		
		Style notify = getStyledDocument().getStyle(STYLE_NOTIFY_0);
		StyleConstants.setForeground(notify, notifyColor);
	}
	
	/**
	 * Gets the notification messages' color.
	 * @return The notification messages' color.
	 */
	public Color
	getNotifyColor() { return notifyColor; }
	
	/**
	 * Gets the warning messages' color.
	 * @return The warning messages' color.
	 */
	public Color
	getWarningColor() { return warnColor; }
	
	/**
	 * Sets the warning messages' color.
	 * @param c The warning messages' color.
	 */
	public void
	setWarningColor(Color c) {
		warnColor = c;
		
		Style warn = getStyledDocument().getStyle(STYLE_WARN_0);
		StyleConstants.setForeground(warn, warnColor);
	}
	
	/**
	 * Gets the error messages' color.
	 * @return The error messages' color.
	 */
	public Color
	getErrorColor() { return errorColor; }
	
	/**
	 * Sets the error messages' color.
	 * @param c The error messages' color.
	 */
	public void
	setErrorColor(Color c) {
		errorColor = c;
		
		Style error = getStyledDocument().getStyle(STYLE_ERROR_0);
		StyleConstants.setForeground(error, errorColor);
	}
	
	/**
	 * Adds the specified command to this text pane.
	 * @param cmd The command to be added.
	 */
	public void
	addCommand(String cmd) {
		StyledDocument doc = getStyledDocument();
		try {
			String s = "lscp> ";
			doc.insertString(doc.getLength(), s, doc.getStyle(STYLE_BOLD));
			s = cmd + "\n";
			doc.insertString(doc.getLength(), s, doc.getStyle(STYLE_REGULAR));
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
	}
	
	/**
	 * Adds the specified command response to this text pane.
	 * @param cmd The command response to be added.
	 */
	public void
	addCommandResponse(String cmdResponse) {
		StyledDocument doc = getStyledDocument();
		try {
			String s = cmdResponse + "\n";
			Style style = doc.getStyle(STYLE_REGULAR);
			if(s.startsWith("ERR:")) style = doc.getStyle(STYLE_ERROR);
			else if(s.startsWith("WRN:")) style = doc.getStyle(STYLE_WARN);
			else if(s.startsWith("NOTIFY:")) style = doc.getStyle(STYLE_NOTIFY);
			doc.insertString(doc.getLength(), s, style);
		} catch(Exception x) {
			CC.getLogger().log(Level.INFO, HF.getErrorMessage(x), x);
		}
	}
}
