
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.base.ITargetNode;
import org.de.metux.treebuild.nodes.SourceNode;
import org.de.metux.treebuild.base.TreebuildConfig;

class SourceObjectParser extends ObjectParser
{
    ITargetNode target;
    SourceNode source;
    
    void handle_Close()
    {
    	target.addSource(source);
    }

    void handle_Sub(String qName, Properties attrs)
    {
	if (qName.equals("property"))
	    setSubParser(new CommonPropertyParser(config,attrs,source));
	else if (qName.equals("depend"))
	    setSubParser(new DependObjectParser(config,attrs,source));
	else 
	    super.handle_Sub(qName,attrs);
    }
	
    SourceObjectParser(TreebuildConfig cf, Properties attrs, ITargetNode t)
    {
	super(cf,attrs,"SOURCE");
	target = t;
	source = new SourceNode(
	    cf,
	    attributes.getProperty("name"),
	    attributes.getProperty("type"),
	    attributes
	);
    }
}
