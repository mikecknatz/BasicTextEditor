import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.DefaultEditorKit;
/**
 * 
 * @author Michael K
 * Start date: 7/10/2015
 * Goals:
 * 		create a text editor approximately equivalent to Windows built-in notepad
 * 
 * Deficiencies:
 * 		Find (and replace) functionality not yet implemented
 * 		Printing not yet implemented.
 * 
 */
@SuppressWarnings("serial")
public class WordEdit extends JFrame {

	private boolean edited, wordwrap;
	private String fileName,currFont;
	private int currSize;
	private JTextArea text;
	private String sizes[] = {"2","4","6","8","10","12","14","16","18","20","32","64"};
	private JMenuBar options;
	private JMenu file, edit, format;
	private JScrollPane scrollable;
	private Action Open, Save, SaveAs, Cut, Copy, Paste, Exit, LineWrap;
	private JList<String> ft, fs;

	public static void main(String[] args) {
		WordEdit editor = new WordEdit();
		editor.setVisible(true);
	}

	public WordEdit() {
		fileName = "Untitled";
		wordwrap = true;
		setupMenus();
		setupWindow();
		addlisteners();
	}

	/**
	 * This method initializes all actions that are then added to the JMenus
	 * including key shortcuts for said actions.
	 * 
	 */
	private void setupMenus() {
		LineWrap = new AbstractAction("Line Wrap",null){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wordwrap = !wordwrap;
				text.setLineWrap(wordwrap);
			}
		};
		Save = new AbstractAction("Save", null) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
			}
		};
		Open = new AbstractAction("Open", null) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
		};
		Exit = new AbstractAction("Exit", null) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exit();
			}
		};
		SaveAs = new AbstractAction("Save As", null) {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAs();
			}
		};
		Cut = new DefaultEditorKit.CutAction();
		Cut.putValue(Action.NAME, "Cut");
		Copy = new DefaultEditorKit.CopyAction();
		Copy.putValue(Action.NAME, "Copy");
		Paste = new DefaultEditorKit.PasteAction();
		Paste.putValue(Action.NAME, "Paste");

		// initialize jmenus and add actions with shortcut keys where necessary
		options = new JMenuBar();
		//options under file
		file = new JMenu("File");
		file.add(Open).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
						java.awt.Event.CTRL_MASK));
		file.add(Save).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
						java.awt.Event.CTRL_MASK));
		file.add(SaveAs);
		file.add(Exit);
		//editing options
		edit = new JMenu("Edit");
		edit.add(Cut).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
						java.awt.Event.CTRL_MASK));
		edit.add(Copy).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
						java.awt.Event.CTRL_MASK));
		edit.add(Paste).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
						java.awt.Event.CTRL_MASK));
		//formating options
		format = new JMenu("Format");
		ft = new JList<String>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
		fs = new JList<String>(sizes);
		ft.setLayoutOrientation(JList.VERTICAL);
		fs.setLayoutOrientation(JList.VERTICAL);
		JScrollPane fts = new JScrollPane(ft);
		JScrollPane fss = new JScrollPane(fs);
		JMenu temp1 = new JMenu("Font");
		JMenuItem type = new JMenu("Type");
		type.add(fts);
		JMenuItem siz = new JMenu("Size");
		siz.add(fss);
		temp1.add(type);
		temp1.add(siz);
		format.add(temp1);
		format.add(LineWrap);
		options.add(file);
		options.add(edit);
		options.add(format);
	}

	/**
	 * Add components to the frames and set the frame with desired attributes.
	 */
	private void setupWindow() {
		//initialize text
		text = new JTextArea();
		//get default fonts
		currFont = text.getFont().getFontName();
		currSize = text.getFont().getSize();
		text.setEditable(true);
		text.setLineWrap(wordwrap);
		//initialize scrollable
		scrollable = new JScrollPane(text,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//add components to frame and set attributes.
		this.add(options, BorderLayout.NORTH);
		this.add(scrollable);
		this.setSize(600, 400);
		this.setResizable(true);
		this.setTitle(fileName);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	/**
	 * Add the listeners to frame and components.
	 */
	private void addlisteners() {
		//overrides the default closing action to make sure that there is no accidental data loss.
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		//lets the program know if there was a change in the text.
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				edited = true;
			}
		});
		//Create menu on right click.
		text.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0)) {
					JPopupMenu opts = new JPopupMenu();
					opts.add(Cut);
					opts.add(Copy);
					opts.add(Paste);
					opts.show(arg0.getComponent(), arg0.getX(), arg0.getY());
				}
			}
		});
		ft.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				currFont = (String)ft.getSelectedValue();
				text.setFont(new Font(currFont, Font.PLAIN, currSize));
			}
		});
		fs.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				// TODO Auto-generated method stub
				currSize = Integer.parseInt((String)fs.getSelectedValue());
				text.setFont(new Font(currFont, Font.PLAIN, currSize));
			}
		});
	}

	private void save() {
		File check = new File(fileName);
		if (check.exists() && !check.isDirectory()
				&& !fileName.equals("untitled")) {
			try {
				String toSave = text.getText();
				PrintWriter printer = new PrintWriter(check);
				printer.print(toSave);
				printer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			saveAs();
		}
	}

	private void saveAs() {
		FileDialog load = new FileDialog(this, "Choose a file", FileDialog.SAVE);
		load.setDirectory("C:\\");
		load.setFile("*.txt");
		load.setVisible(true);
		fileName = load.getDirectory() + load.getFile();
		this.setTitle(fileName);
		try {
			String toSave = text.getText();
			PrintWriter printer = new PrintWriter(new File(fileName));
			printer.print(toSave);
			printer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void exit() {
		if (edited) {
			int choice = JOptionPane.showConfirmDialog(null,
					"This text has been modified, would you like to save?",
					"WARNING", JOptionPane.YES_NO_CANCEL_OPTION);
			switch (choice) {
			case JOptionPane.YES_OPTION:
				save();
				break;
			case JOptionPane.NO_OPTION:
				break;
			case JOptionPane.CANCEL_OPTION:
			default:
				return;
			}
			;
		}
		System.exit(0);
	}

	private void open() {
		Scanner fileScan;
		FileDialog load = new FileDialog(this, "Choose a file", FileDialog.LOAD);
		load.setDirectory("C:\\");
		load.setFile("*.txt");
		load.setVisible(true);
		fileName = load.getDirectory() + load.getFile();
		// use getfile because the directory will always have something so
		// filename cannot be null
		if (load.getFile() == null) {
			fileName = "untitled";
			return;
		}
		try {
			fileScan = new Scanner(new File(fileName));
			text.setText(fileScan.useDelimiter("\\Z").next());
			fileScan.close();
		} catch (FileNotFoundException e) {
			JOptionPane
					.showConfirmDialog(
							null,
							"File is missing, corrupt, or open in another application.",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
