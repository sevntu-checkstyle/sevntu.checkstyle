package com.github.sevntu.checkstyle.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XmlUtil {
    private XmlUtil() {
    }

    public static Document getRawXml(String fileName, String code, String unserializedSource)
            throws ParserConfigurationException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(true);

            final DocumentBuilder builder = factory.newDocumentBuilder();

            return builder.parse(new InputSource(new StringReader(code)));
        }
        catch (IOException | SAXException ex) {
            Assert.fail(fileName + " has invalid xml (" + ex.getMessage() + "): "
                    + unserializedSource);
        }

        return null;
    }

    public static Set<Node> getChildrenElements(Node node) {
        final Set<Node> result = new LinkedHashSet<>();

        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() != Node.TEXT_NODE) {
                result.add(child);
            }
        }

        return result;
    }

    public static Node findElementByTag(Set<Node> nodes, String tag) {
        Node result = null;

        for (Node child : nodes) {
            if (tag.equals(child.getNodeName())) {
                result = child;
                break;
            }
        }

        return result;
    }

    public static Set<Node> findElementsByTag(Set<Node> nodes, String tag) {
        Set<Node> result = new HashSet<Node>();

        for (Node child : nodes) {
            if (tag.equals(child.getNodeName())) {
                result.add(child);
            }
        }

        return result;
    }
}
