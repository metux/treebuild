
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.nodes.PackageNode;
import org.de.metux.treebuild.base.TreebuildConfig;

class PackageObjectParser extends ObjectParser
{
    Properties meta;
    INode parent;
    PackageNode pkgnode;
    
    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("meta"))
	    setSubParser(new CommonPropertyParser(config,attrs,meta));
	else if (qName.equals("property"))
	    setSubParser(new CommonPropertyParser(config,attrs,pkgnode));
	else if (qName.equals("configure"))
	    setSubParser(new ConfigureObjectParser(config,attrs,pkgnode));
	else if (qName.equals("library"))
	    setSubParser(new LibraryObjectParser(config,attrs,pkgnode));
	else if (qName.equals("executable"))
	    setSubParser(new ExecutableObjectParser(config,attrs,pkgnode));
	else if (qName.equals("manual"))
	    setSubParser(new ManualObjectParser(config,attrs,pkgnode));
	else if (qName.equals("resource"))
	    setSubParser(new ResourceObjectParser(config,attrs,pkgnode));
	else 
	    super.handle_Sub(qName,attrs);
    }
	
    void handle_Close()
    {
	parent.addChild(pkgnode);
    }
	
    PackageObjectParser(
	TreebuildConfig cf, Properties attrs, INode parent_node)
    {
	super(cf,attrs,"PACKAGE");
	meta = new Properties();
	parent = parent_node;
	pkgnode = new PackageNode(cf,attrs);
    }
}
