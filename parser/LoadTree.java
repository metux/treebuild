
package org.de.metux.treebuild.parser;

import java.io.File;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.de.metux.treebuild.base.*;
import org.de.metux.treebuild.nodes.*;
import org.de.metux.propertylist.IPropertylist;

public class LoadTree
{
    private class sax_cb extends DefaultHandler
    {
	ObjectParser root_parser;
	
	sax_cb(ObjectParser r)
	{
	    root_parser = r;
	}

	public void startElement(
	    String uri, 
	    String localName, 
	    String qName, 
	    Attributes attrs
	) throws SAXException
	{
	    root_parser.startElement(uri,localName,qName,attrs);
	}

	public void endElement(String namespace, String localName, String qName)
	    throws SAXException
	{
	    root_parser.endElement(namespace,localName,qName);
	}

	public void characters(char[] ch, int start, int length) 
	    throws SAXException
	{
	    root_parser.characters(ch,start,length);
	}
    }

    public INode parseDocument(File src, TreebuildConfig config)
    {
	RootObjectParser root_parser = new RootObjectParser(config,src);
	SAXParserFactory spf = SAXParserFactory.newInstance();
	try
	{
	    SAXParser sp = spf.newSAXParser();
	    sp.parse(src,new sax_cb(root_parser));	    
	}
	catch (SAXException se)
	{
	    se.printStackTrace();
	}
	catch (ParserConfigurationException pce)
	{
	    pce.printStackTrace();
	}
	catch (IOException ie)
	{
	    ie.printStackTrace();
	}
	return root_parser.getNode();
    }

    public PackageNode loadPackage(TreebuildConfig config, File src)
    {
	INode root = parseDocument(src,config);
	INode nodes [] = root.getChilds();
	for (int x=0; x<nodes.length; x++)
	{
	    try { return (PackageNode)nodes[x]; }
	    catch (ClassCastException e) { 
		System.err.println("LoadTree.loadPackage() Ugh. other object than PackageNode ???");	    
	    }
	}
	return null;
    }
}
