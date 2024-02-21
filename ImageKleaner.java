import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Extend JFrame to create a GUI window
public class ImageKleaner extends JFrame {
    // Private fields to store the image files and GUI components
    private List<File> imageFiles;
    private JList<File> listbox;
    private DefaultListModel<File> listModel;
    private JButton selectFilesButton;
    private JButton cleanFilesButton;

    // Constructor that initializes the fields and sets up the GUI
    public ImageKleaner() {
        super("ImageKleaner");                                         // Call the superclass constructor with the window title
        this.imageFiles = new ArrayList<>();                                 // Initialize the image files list as an empty array list
        this.listModel = new DefaultListModel<>();                           // Initialize the list model as a default list model
        this.listbox = new JList<>(listModel);                               // Initialize the list box as a JList with the list model
        this.listbox.setLayoutOrientation(JList.VERTICAL);                   // Set the layout orientation of the list box to vertical
        JScrollPane listScroller = new JScrollPane(listbox);                 // Create a scroll pane to wrap the list box
        listScroller.setPreferredSize(new Dimension(265, 235)); // Set the preferred size of the scroll pane

        // Initialize the select files button as a JButton with the label
        selectFilesButton = new JButton("Select image files");
        // Add an action listener to the button that calls a method when clicked
        selectFilesButton.addActionListener(this::selectFilesButtonFunction);
        // Initialize the clean files button as a JButton with the label
        cleanFilesButton = new JButton("Run image kleaner");
        // Add an action listener to the button that calls a method when clicked
        cleanFilesButton.addActionListener(this::cleanFilesButtonFunction);

        this.setLayout(new FlowLayout());    // Set the layout of the window to a flow layout
        // Add the buttons and the scroll pane to the window
        this.add(selectFilesButton);
        this.add(cleanFilesButton);
        this.add(listScroller);
        this.setSize(305, 320); // Set the size of the window
        // Set the default close operation of the window to exit
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);  // Set the window to be non-resizable
    }

    // Method that handles the action of selecting files button
    private void selectFilesButtonFunction(ActionEvent e) {
        // Create a file chooser object to let the user select multiple image files
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);      // Enable the multi-selection mode of the file chooser
        // Set the file filter to only accept image files with jpg, jpeg, or png extensions
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image files", "jpg", "jpeg", "png"));
        int returnVal = fileChooser.showOpenDialog(this);  // Show the file chooser dialog and get the return value
        if (returnVal == JFileChooser.APPROVE_OPTION) {    // If the user approved the selection, proceed to the next step
            File[] files = fileChooser.getSelectedFiles(); // Get the selected files as an array of File objects
            listModel.clear();                             // Clear the list model to remove any previous files
            // Loop through the selected files and add them to the image files list and the list model
            for (File file : files) {
                imageFiles.add(file);
                listModel.addElement(file);
            }
        }
    }

    // Method that handles the action of cleaning files button
    private void cleanFilesButtonFunction(ActionEvent e) {
        // Check if the image files list is empty, if so, show a message to the user and return
        if (imageFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select the files that you want to clean.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String randomString = generateRandomString(5); // Generate a random string of 5 characters to use as a folder name suffix to prevent overwriting
        String folderName = "KleanFiles_" + randomString;     // Concatenate the prefix "KleanFiles_" with the random string to form the folder name
        File folderPath = new File(folderName);               // Create a file object with the folder name as the path
        if (!folderPath.exists()) {                           // Check if the folder path does not exist, if so, create a new folder
            folderPath.mkdir();
        }
        // Loop through the image files and clean them
        for (File file : imageFiles) {
            try {
                // Read the image data from the file as a buffered image object
                BufferedImage image = ImageIO.read(file);

                // Get the file extension from the file name by finding the last dot and getting the substring after it
                String fileName = file.getName();
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

                // Create a new file object with the folder path and the file name as the path
                File outputFile = new File(folderPath, fileName);
                // Write the image data to the new file without copying the EXIF data
                // Use the file extension as the output format
                ImageIO.write(image, fileExtension, outputFile);
            } catch (IOException ex) {
                // Print the stack trace of the exception if any error occurs
                ex.printStackTrace();
            }
        }
        // Show a message to the user that the task is completed and the cleaned images are in the folder
        JOptionPane.showMessageDialog(this, "Task completed, cleaned images are in \"" + folderPath.getAbsolutePath() + "\" folder.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method that generates a random string of a given length
    private String generateRandomString(int length) {
        // Define a string of characters to choose from
        String characters = "0123456789abcdefghijklmnopqrstuvwxyz";
        // Create a string builder object to append the characters
        StringBuilder result = new StringBuilder();
        // Create a random object to generate random numbers
        Random random = new Random();
        while (length-- > 0) {    // Loop until the length is zero
            // Append a random character from the characters string to the result
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString(); // Return the result as a string
    }

    // Define the main method that runs the program
    public static void main(String[] args) {
        // Invoke the event queue to run the GUI in a separate thread
        EventQueue.invokeLater(() -> {
            // Create an instance of the ImageKleaner class
            ImageKleaner frame = new ImageKleaner();
            // Set the frame to be visible
            frame.setVisible(true);
        });
    }
}

