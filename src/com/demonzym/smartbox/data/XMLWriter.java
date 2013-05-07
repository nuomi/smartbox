package com.demonzym.smartbox.data;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * ·â×°xml
 * @author Administrator
 *
 */
public class XMLWriter {
	
	public static String foobar2xmlDOM(List<Foobar> foobarlist) {
		String xmlStr = null;
		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			document = builder.newDocument();
			document.setXmlVersion("1.0");
			
			Element data = document.createElement("data");
			document.appendChild(data);
			Element uid = document.createElement("uid");
			data.appendChild(uid);
			
			Iterator<Foobar> iterator = foobarlist.iterator();
			while (iterator.hasNext()) {
				Foobar foobar = iterator.next();
				Element foobarelement = document.createElement("foobar");
				
				Element id = document.createElement("id");
				id.appendChild(document.createTextNode(foobar.getId()));

				Element name = document.createElement("name");
				name.appendChild(document.createTextNode(foobar.getName()));

				Element img = document.createElement("img");
				img.appendChild(document.createTextNode(foobar.getImage()));

				Element link = document.createElement("link");
				link.appendChild(document.createTextNode(foobar.getLink()));
				
				foobarelement.appendChild(id);
				foobarelement.appendChild(name);
				foobarelement.appendChild(img);
				foobarelement.appendChild(link);
				
				data.appendChild(foobarelement);
				
			}
			
			TransformerFactory   tf   =   TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			ByteArrayOutputStream   bos   =   new   ByteArrayOutputStream();
			t.transform(new DOMSource(document), new StreamResult(bos));
			xmlStr = bos.toString();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return xmlStr;
	}

	public static String foobar2xml(List<Foobar> foobarlist) {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("utf-8", true);
			serializer.startTag("", "data");
			serializer.startTag("", "uid");
			serializer.text(UserInfo.uid);
			serializer.endTag("", "uid");
			Iterator<Foobar> iterator = foobarlist.iterator();
			while (iterator.hasNext()) {
				Foobar foobar = iterator.next();
				serializer.startTag("", "foobar");
				serializer.startTag("", "id");
				serializer.text("" + foobar.getId());
				serializer.endTag("", "id");
				serializer.startTag("", "name");
				serializer.text("" + foobar.getName());
				serializer.endTag("", "name");
				serializer.startTag("", "img");
				serializer.text("" + foobar.getImage());
				serializer.endTag("", "img");
				serializer.startTag("", "link");
				serializer.text("" + foobar.getLink());
				serializer.endTag("", "link");
				serializer.endTag("", "foobar");
			}
			serializer.endTag("", "data");
			serializer.endDocument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return writer.toString();
	}
}
