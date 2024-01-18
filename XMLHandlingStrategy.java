package com.example.semproj;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class XMLHandlingStrategy implements HandlingStrategy {
    @Override
    public ArrayList<String> getStringArray(File file) throws ParserConfigurationException, IOException, SAXException {
        ArrayList<String> text = new ArrayList<>();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        Document document = builderFactory.newDocumentBuilder().parse(file);
        NodeList stringNodeList = document.getElementsByTagName("string");
        for (int i = 0; i < stringNodeList.getLength(); i++) {
            Node stringNode = stringNodeList.item(i);
            text.add(stringNode.getTextContent());
        }
        return text;
    }
}