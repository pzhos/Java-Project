package com.example.semproj;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Window extends JFrame {

    private ArrayList<String> strings;
    private HandlingStrategy method;
    private final JTextArea inputArea;
    private final JTextArea outputArea;

    Window(String name) {
        super(name);
        strings = new ArrayList<>();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set the background color to pink
        getContentPane().setBackground(new Color(255, 182, 193)); // Pink color

        setLayout(new FlowLayout());

        inputArea = new JTextArea();
        // Set the background color for inputArea
        inputArea.setBackground(new Color(255, 182, 193)); // Pink color
        inputArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font
        add(inputArea);
        inputArea.setEditable(false);

        JButton read = new JButton("Read");
        // Set the background color for the read button
        read.setBackground(new Color(255, 182, 193)); // Pink color
        read.setFont(new Font("Arial", Font.BOLD, 14)); // Change font
        add(read);

        JButton write = new JButton("Write");
        // Set the background color for the write button
        write.setBackground(new Color(255, 182, 193)); // Pink color
        write.setFont(new Font("Arial", Font.BOLD, 14)); // Change font
        add(write);

        JRadioButton txtWrite = new JRadioButton(".txt");
        add(txtWrite);
        txtWrite.setSelected(true);
        JRadioButton xmlWrite = new JRadioButton(".xml");
        add(xmlWrite);
        ButtonGroup writeGroup = new ButtonGroup();
        writeGroup.add(txtWrite);
        writeGroup.add(xmlWrite);

        outputArea = new JTextArea();
        // Set the background color for outputArea
        outputArea.setBackground(new Color(255, 182, 193)); // Pink color
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14)); // Change font
        add(outputArea);
        outputArea.setEditable(false);

        read.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser("D:\\LABS2SEM\\JavaSemProject\\sem-proj\\src\\main\\java\\com\\example\\semproj\\inputs");
            try {
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    if (fileChooser.getSelectedFile().getName().endsWith("txt")) {
                        setStrategy(new TextHandlingStrategy());
                    } else if (fileChooser.getSelectedFile().getName().endsWith("xml")) {
                        setStrategy(new XMLHandlingStrategy());
                    } else throw new IllegalArgumentException();
                    strings = method.getStringArray(fileChooser.getSelectedFile());
                }
            } catch (IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(null, "Choose correct file.", "Illegal file format!", JOptionPane.ERROR_MESSAGE);
            } catch (IOException | ParserConfigurationException | SAXException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            }
            showInput();
            strings = solveProblems(strings);
            showOutput();
        });

        write.addActionListener(e -> {
            try {
                String dir = "D:\\LABS2SEM\\JavaSemProject\\sem-proj\\src\\main\\java\\com\\example\\semproj\\outputs\\";
                String fileName = JOptionPane.showInputDialog("Enter file name you want to save result to");
                if (fileName.isEmpty()) throw new IllegalArgumentException("File name is empty.");
                if (txtWrite.isSelected()){
                    writeToTXT(dir, fileName + ".txt", strings);
                } else if (xmlWrite.isSelected()){
                    writeToXML(dir, fileName + ".xml", strings);
                }
                JOptionPane.showMessageDialog(null, "File was written successfully!");
            } catch (IOException | ParserConfigurationException | TransformerException |
                     IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(null, exception.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void writeToTXT(String directory, String fileName, ArrayList<String> strings_) throws IOException {
        String f_name = directory + fileName;
        FileWriter fileWriter = new FileWriter(f_name);
        for (String string : strings_) {
            fileWriter.write(string);
            fileWriter.write("\n");
        }
        fileWriter.close();
    }

    public static void writeToXML(String directory, String fileName, ArrayList<String> strings_) throws ParserConfigurationException, TransformerException {
        String f_name = directory + fileName;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        Document document = factory.newDocumentBuilder().newDocument();
        Element root = document.createElement("text");
        document.appendChild(root);
        for (String string : strings_) {
            Element element = document.createElement("string");
            element.setTextContent(string);
            root.appendChild(element);
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(f_name)));
    }

    public static ArrayList<String> solveProblems(ArrayList<String> strings_) {
        Pattern p = Pattern.compile("(\\s*-?\\s*(\\(*\\s*(-?\\s*\\d+(\\.\\d+)?)\\s*\\)*\\s*)*[-+*/^](\\s*\\(*\\s*(-?\\s*\\d+(\\.\\d+)?)\\s*\\)*\\s*)+)+");
        String oldSubstring;
        String newSubstring;
        for (int i = 0; i < strings_.size(); i++) {
            Matcher matcher = p.matcher(strings_.get(i));
            while (matcher.find()) {
                oldSubstring = strings_.get(i).substring(matcher.start(), matcher.end());
                newSubstring = SmartCalculator.calculate(oldSubstring);
                strings_.set(i, strings_.get(i).replace(oldSubstring, newSubstring));
            }
        }
        return strings_;
    }

    private void setStrategy(HandlingStrategy handlingStrategy) {
        method = handlingStrategy;
    }

    private void showInput() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string).append("\n");
        }
        inputArea.setText(stringBuilder.toString());
    }

    private void showOutput() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : strings) {
            stringBuilder.append(string).append("\n");
        }
        outputArea.setText(stringBuilder.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window("Pink GUI");
            window.setSize(400, 300);
            window.setVisible(true);
        });
    }
}
