
package org.de.metux.treebuild.parser;

import java.util.Properties;
import org.de.metux.treebuild.base.*;
import org.de.metux.treebuild.nodes.ResourceNode;

class ResourceObjectParser extends ObjectParser
{
    INode pkgnode;

    void handle_Close()
    {
	pkgnode.addChild(new ResourceNode(config,attributes));
    }
	
    ResourceObjectParser(TreebuildConfig cf, Properties attrs, INode pkg)
    {
	super(cf, attrs,"RESOURCE");
	pkgnode = pkg;
    }
}
