import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

@SuppressWarnings("serial")
public class WordEdit extends JFrame {

	private boolean edited;
	private String fileName;
	private JTextArea text;
	private JMenuBar options;
	private JMenu file;
	private JMenu edit;
	private JScrollPane scrollable;
	private Action Open, Save, SaveAs, Cut, Copy, Paste, Exit;

	public static void main(String[] args) {
		WordEdit editor = new WordEdit();
		editor.setVisible(true);
	}

	public WordEdit() {
		fileName = "Untitled";
		setupMenus();
		setupWindow();
		addlisteners();
	}

	/**
	 * This method creates multiple actions that are then added to the JMenus
	 * 
	 */
	private void setupMenus() {
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

		// initialize jmenus and add actions
		options = new JMenuBar();

		file = new JMenu("File");
		edit = new JMenu("Edit");
		file.add(Open).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
						java.awt.Event.CTRL_MASK));
		file.add(Save).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
						java.awt.Event.CTRL_MASK));
		file.add(SaveAs);
		file.add(Exit);
		edit.add(Cut).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X,
						java.awt.Event.CTRL_MASK));
		edit.add(Copy).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C,
						java.awt.Event.CTRL_MASK));
		edit.add(Paste).setAccelerator(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V,
						java.awt.Event.CTRL_MASK));
		options.add(file);
		options.add(edit);
	}

	private void setupWindow() {
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		text = new JTextArea();
		text.setEditable(true);
		text.setLineWrap(true);
		scrollable = new JScrollPane(text,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.add(options, BorderLayout.NORTH);
		this.add(scrollable);
		this.setSize(600, 400);
		this.setResizable(true);
		this.setTitle(fileName);
	}

	private void addlisteners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exit();
			}
		});
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent arg0) {
				edited = true;
			}

		});
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
