
package org.de.metux.treebuild.parser;

import java.util.Properties;
import java.util.Hashtable;

import org.de.metux.treebuild.base.*;
import org.de.metux.treebuild.nodes.*;

class ImportObjectParser extends ObjectParser
{
    INode parent_node;
    
    void handle_Close()
    {
	parent_node.addChild(new ImportNode(config,attributes));
    }

    ImportObjectParser(TreebuildConfig cf,Properties attrs, INode parent)
    {
        super(cf,attrs,"IMPORT");
	parent_node = parent;
    }
}
