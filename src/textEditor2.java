import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Base64;


public class textEditor2 extends JFrame implements ActionListener {
    // GUI components that will be in the Text Editor
    JTextPane textPane;
    JScrollPane scrollPane;
    JMenuBar menuBar;
    JMenu fileMenu, editMenu, formatMenu;
    JMenuItem newItem, openItem, saveItem, exitItem, searchItem;
    JToolBar toolBar;
    JButton boldButton, italicButton, underlineButton;
    JLabel wordCountLabel;
    Timer autosaveTimer;

    // Constructor to set up the GUI
    public textEditor2() {
        // Set up the frame
        setTitle("Custom Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Creating the text pane for styled text options
        textPane = new JTextPane();
        scrollPane = new JScrollPane(textPane);
        add(scrollPane, BorderLayout.CENTER);

        // Here I am adding a document listener to update the word count in real time         
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateWordCount();
            }
            public void removeUpdate(DocumentEvent e) {
                updateWordCount();
            }
            public void changedUpdate(DocumentEvent e) {
                updateWordCount();
            }
        });

        
        // Create menu bar
        menuBar = new JMenuBar();

        // Create 'File' menu and add menu items for formatting
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

        // Creating an "Edit" menu for future implementation
        editMenu = new JMenu("Edit");
        searchItem = new JMenuItem("Search");
        searchItem.addActionListener(this);
        editMenu.add(searchItem);
        
        menuBar.add(editMenu);

        // Creating format menu ("in Progress")
        formatMenu = new JMenu("Format");
        menuBar.add(formatMenu);

        // Set menu bar to the frame
        setJMenuBar(menuBar);

        // Create toolbar for text formatting 
        toolBar = new JToolBar();
        boldButton = new JButton("B");
        italicButton = new JButton("I");
        underlineButton = new JButton("U");

        // These are the fonts/styles that will occur when clicking the different buttons
        boldButton.setFont(new Font("Serif", Font.BOLD, 16));
        italicButton.setFont(new Font("Serif", Font.ITALIC, 16));
        underlineButton.setFont(new Font("Serif", Font.PLAIN, 16));

        toolBar.add(boldButton);
        toolBar.add(italicButton);
        toolBar.add(underlineButton);

        add(toolBar, BorderLayout.NORTH);

        // Adding the action listeners for formatting buttons
        boldButton.addActionListener(e -> applyTextStyle(StyleConstants.Bold));
        italicButton.addActionListener(e -> applyTextStyle(StyleConstants.Italic));
        underlineButton.addActionListener(e -> applyTextStyle(StyleConstants.Underline));

        // Word count label
        wordCountLabel = new JLabel("Word Count: 0");
        add(wordCountLabel, BorderLayout.SOUTH);

        // Initialize autosave timer to save in certain time frame
        initializeAutosave();

        // Make the GUI visible
        setVisible(true);
    }

    // Apply text style to selected text
    private void applyTextStyle(Object bold) {
            StyledDocument doc = textPane.getStyledDocument();
            int start = textPane.getSelectionStart();
            int end = textPane.getSelectionEnd();
            
            if (start != end) {
                MutableAttributeSet attr = new SimpleAttributeSet();
                boolean isSelected = false;
    
                // Check if the attribute is currently applied
                if (bold == StyleConstants.Bold) {
                    isSelected = StyleConstants.isBold(doc.getCharacterElement(start).getAttributes());
                    StyleConstants.setBold(attr, !isSelected);
                } else if (bold == StyleConstants.Italic) {
                    isSelected = StyleConstants.isItalic(doc.getCharacterElement(start).getAttributes());
                    StyleConstants.setItalic(attr, !isSelected);
                } else if (bold == StyleConstants.Underline) {
                isSelected = StyleConstants.isUnderline(doc.getCharacterElement(start).getAttributes());
                StyleConstants.setUnderline(attr, !isSelected);
            }

            // Apply the attribute to the selected text
            doc.setCharacterAttributes(start, end - start, attr, false);
        }

    }

    // Action events for the menu items 
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newItem) {
            textPane.setText(""); // Clear the text area for new document
        } else if (e.getSource() == openItem) {
            openFile(); // Open an existing file
        } else if (e.getSource() == saveItem) {
            saveFile(); // Save the current document
        } else if (e.getSource() == searchItem) {
            openSearchDialog(); // Open the Search dialog
        } else if (e.getSource() == exitItem) {
            System.exit(0); // Exit the application
        }
    }



    // Method to open a file with decryption
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String encryptedText = reader.readLine();
                String decryptedText = new String(Base64.getDecoder().decode(encryptedText));
                textPane.setText(decryptedText);
                reader.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be opened.");
            }
        }
    }

    // This is to save a file with encryption
    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String plainText = textPane.getText();
                String encryptedText = Base64.getEncoder().encodeToString(plainText.getBytes());
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(encryptedText);
                writer.close();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "File could not be saved.");
            }
        }
    }

    // Method to open the Search dialog
    private void openSearchDialog() {
        String searchText = JOptionPane.showInputDialog(this, "Enter text to search:");
        if (searchText != null && !searchText.isEmpty()) {
            searchInDocument(searchText);
        }
    }

    // Method to search text in the document
    private void searchInDocument(String searchText) {
        String text = textPane.getText();
        int index = text.indexOf(searchText);
        if (index >= 0) {
            textPane.setCaretPosition(index);
            textPane.select(index, index + searchText.length());
        } else {
            JOptionPane.showMessageDialog(this, "Text not found.");
        }
    }

    // Method to initialize autosave
    private void initializeAutosave() {
        autosaveTimer = new Timer(20000, e -> saveFile());
        autosaveTimer.start();
    }


    // This is the method that actually updates the word count 
    private void updateWordCount(){
        String text = textPane.getText().trim();
        String[] words = text.isEmpty() ? new String[0] : text.split("\\s+");
        wordCountLabel.setText("Word Count:" + words.length);
    }

    // Main method to launch the text editor
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new textEditor2());
    }
}
