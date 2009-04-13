
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.base.*;

class CommonAliasParser extends ObjectParser
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
	    
	if (name==null)
	    throw new RuntimeException("[ALIAS] missing name attribute");

	if (properties!=null)
	    properties.setProperty("alias",name);

	if (node!=null)
	    node.setProperty("alias",name);
    }

    CommonAliasParser(TreebuildConfig cf,Properties attrs, Properties props)
    {
	super(cf,attrs,"ALIAS");
	properties = props;
    }
    
    CommonAliasParser(TreebuildConfig cf, Properties attrs, INode n)
    {
	super(cf,attrs,"ALIAS");
	node = n;    
    }
}
