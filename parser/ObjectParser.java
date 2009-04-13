
package org.de.metux.treebuild.parser;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import java.util.Properties;
import org.de.metux.treebuild.base.TreebuildConfig;

abstract class ObjectParser
{
    public ObjectParser current_sub_parser = null;
    public boolean done = false;
    public String namespace;
    public String localname;
    public String qualifiedname;
    public Properties attributes;
    public String type;
    public String content;
    public TreebuildConfig config;

    void handle_characters(char[] ch, int start, int length)
    {
	String t = new String(ch,start,length);
	if (content==null)
	    content = t;
	else
	    content += t;
    }
    
    void handle_Sub(String name, Properties attrs)
    {
	throw new RuntimeException("["+type+"] unhandled sub: "+name);
    }

    /* this element is closed */
    abstract void handle_Close();

    static Properties attrs2properties(Attributes a)
    {
	Properties p = new Properties();
	if (a!=null)
	{
	    int l = a.getLength();
	    for (int x=0; x<l; x++)
	        p.setProperty(a.getQName(x),a.getValue(x));
	}
	return p;
    }
	
    void setSubParser(ObjectParser p)
    {
	if (p==null)
	    throw new RuntimeException("setSubParser(): passed NULL object");
	
	current_sub_parser = p;
    }
	    
    public void characters(char[] ch, int start, int length) 
    {
        if (current_sub_parser!=null)
        {
	    current_sub_parser.characters(ch,start,length);
	    if (current_sub_parser.done)
	        current_sub_parser = null;
	}
	else
	{	
	    handle_characters(ch,start,length);
	}
    }

    public void startElement(String ns, String localName, String qName, Attributes attrs) 
    {
        if (current_sub_parser!=null)
        {
	    current_sub_parser.startElement(ns,localName,qName,attrs);
	    if (current_sub_parser.done)
		current_sub_parser = null;
        }
        else
        {
	    if (!ns.equals(""))
		throw new RuntimeException("uh! where does the namespace come from ?!");
	    if (!localName.equals(""))
		throw new RuntimeException("uh! where does the localName come from ?!");

	    handle_Sub(qName,attrs2properties(attrs));
        }
    }

    public void endElement(String ns, String localName, String qName) 
    {
	if (current_sub_parser!=null)
	{
	    current_sub_parser.endElement(ns,localName,qName);
	    if (current_sub_parser.done)
		current_sub_parser = null;
        }
        else
        {
	    handle_Close();
	    done = true;
        }
    }
    
    public ObjectParser(TreebuildConfig cf, Properties pr, String t)
    {
	attributes = pr;
	type = t;
	config = cf;
    }
}
