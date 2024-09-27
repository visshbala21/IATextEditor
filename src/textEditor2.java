import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class textEditor2 extends JFrame implements ActionListener {

    JTextArea textArea;
    JScrollPane scrollPane;
    JMenuBar menuBar;
    JMenu fileMenu, editMenu, formatMenu, viewMenu, helpMenu;
    JMenuItem newItem, openItem, saveItem, exitItem;


    public textEditor2() {
        // Set up the frame
        setTitle("Enhanced Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        textArea = new JTextArea();
        scrollPane = new JScrollPane(textArea);
        add(scrollPane);


        menuBar = new JMenuBar();


        fileMenu = new JMenu("File");
        newItem = new JMenuItem("New");
        newItem.addActionListener(this);
        fileMenu.add(newItem);

        openItem = new JMenuItem("Open");
        openItem.addActionListener(this);
        fileMenu.add(openItem);

        saveItem = new JMenuItem("Save");
        saveItem.addActionListener(this);
        fileMenu.add(saveItem);

        exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(this);
        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem("Undo"));
        editMenu.add(new JMenuItem("Redo"));
        editMenu.addSeparator();
        editMenu.add(new JMenuItem("Cut"));
        editMenu.add(new JMenuItem("Copy"));
        editMenu.add(new JMenuItem("Paste"));
        menuBar.add(editMenu);


        formatMenu = new JMenu("Format");
        formatMenu.add(new JMenuItem("Word Wrap"));
        formatMenu.add(new JMenuItem("Font"));
        menuBar.add(formatMenu);


        viewMenu = new JMenu("View");
        viewMenu.add(new JMenuItem("Zoom"));
        viewMenu.add(new JMenuItem("Status Bar"));
        menuBar.add(viewMenu);


        helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem("View Help"));
        helpMenu.add(new JMenuItem("About Enhanced Text Editor"));
        menuBar.add(helpMenu);


        setJMenuBar(menuBar);


        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() == newItem) {
            textArea.setText("");
        } else if ( e.getSource() == openItem) {
            openFile();
        } else if ( e.getSource() == saveItem) {
            saveFile();
        } else if ( e.getSource() == exitItem) {
            System.exit(0);
        }
    }


    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if ( option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                textArea.read(reader, null);
                reader.close();
            } catch ( IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be opened.");
            }
        }
    }


    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if ( option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                textArea.write(writer);
                writer.close();
            } catch ( IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be saved.");
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new textEditor2());
    }
}
