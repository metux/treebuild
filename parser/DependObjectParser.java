
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.nodes.DependNode;
import org.de.metux.treebuild.base.*;

class DependObjectParser extends ObjectParser
{
    INode target;
    
    void handle_Close()
    {
	target.addChild(new DependNode(config,attributes));
    }
    	
    DependObjectParser(TreebuildConfig cf, Properties attrs, INode t)
    {
	super(cf, attrs,"INCLUDE");
	target = t;
    }
}
