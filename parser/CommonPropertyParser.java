
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.base.*;

class CommonPropertyParser extends ObjectParser
{
    Properties properties;
    INode node;

    void handle_characters(char[] ch, int start, int length)
    {
	String str = new String(ch,start,length);
	if (content == null)
	    content = str;
	else 
	    content += str;
    }

    void handle_Close()
    {
	String name=attributes.getProperty("name");
	String value=attributes.getProperty("value");
	    
	if (name==null)
	    throw new RuntimeException("[PROPERTY] missing name attribute");

	if (value==null)
	    value = content;

	if (value==null)
	    throw new RuntimeException("[PROPERTY] missing value attribute: "+name);

	if (properties!=null)
	    properties.setProperty(name,value);

	if (node!=null)
	    node.setProperty(name,value);
    }

    CommonPropertyParser(TreebuildConfig cf, Properties attrs, Properties props)
    {
	super(cf, attrs,"PROPERTY");
	properties = props;
    }
    
    CommonPropertyParser(TreebuildConfig cf, Properties attrs, INode n)
    {
	super(cf, attrs,"PROPERTY");
	node = n;    
    }
}
