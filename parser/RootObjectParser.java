
package org.de.metux.treebuild.parser;

import java.util.Properties;
import java.util.Hashtable;
import org.de.metux.treebuild.nodes.RootNode;
import org.de.metux.treebuild.base.*;
import java.io.File;

class RootObjectParser extends ObjectParser
{
    public INode  node;

    RootObjectParser(TreebuildConfig cf, File f)
    {
	super(cf,null,"ROOT");
	node = new RootNode(cf,f.toString());
    }

    public INode getNode()
    {
	return node;
    }
    
    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("package"))
	    setSubParser(new PackageObjectParser(config,attrs,node));
	else
	    super.handle_Sub(qName,attrs);
    }
	
    void handle_Close()
    {
    }
}
