package com.janprach.multiplatformswt.example;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlReaderExample {
	private final static Logger LOGGER = Logger.getLogger(XmlReaderExample.class.getName());

	private static final String DEFAULT_TEST_XML_FILE_NAME = "/test.xml";

	public int readTestXml() {
		try {
			final InputStream testXml = this.getClass().getResourceAsStream(DEFAULT_TEST_XML_FILE_NAME);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document document = dBuilder.parse(testXml);
			return document.getChildNodes().getLength();
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to read " + DEFAULT_TEST_XML_FILE_NAME, e);
			return 0;
		}
	}

	public void printTestXml() {
		try {
			final InputStream testXml = this.getClass().getResourceAsStream(DEFAULT_TEST_XML_FILE_NAME);
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			final Document document = dBuilder.parse(testXml);

			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			document.getDocumentElement().normalize();

			System.out.println("Root element :" + document.getDocumentElement().getNodeName());
			final NodeList items = document.getElementsByTagName("items");
			for (int i = 0; i < items.getLength(); i++) {
				final Node nNode = items.item(i);
				System.out.println(nNode.getNodeName() + ":");
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					final Element eElement = (Element) nNode;
					System.out.println("   id : " + eElement.getAttribute("id"));
					System.out.println("  foo : " + eElement.getElementsByTagName("foo").item(0).getTextContent());
					System.out.println("  bar : " + eElement.getElementsByTagName("bar").item(0).getTextContent());
				}
			}
		} catch (final Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to print " + DEFAULT_TEST_XML_FILE_NAME, e);
		}
	}
}
