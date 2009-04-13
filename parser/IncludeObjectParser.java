
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.nodes.HeaderNode;
import org.de.metux.treebuild.base.*;

class IncludeObjectParser extends ObjectParser
{
    ITargetNode target;
    
    void handle_Close()
    {
	target.addInclude(new HeaderNode(config,attributes));
    }
	
    IncludeObjectParser(TreebuildConfig cf, Properties attrs, ITargetNode t)
    {
	super(cf,attrs,"INCLUDE");
	target = t;
    }
}
