
package org.de.metux.treebuild.nodes;

import java.util.Enumeration;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.TreebuildConfig;

public class AutodepNode extends PINode_childs implements INode 
{
    public AutodepNode(TreebuildConfig cf, String name)
    {
	super(cf,name);
    }

    public static boolean typecheck(INode n)
    {
	try { AutodepNode a = (AutodepNode)n; }
	catch (ClassCastException e) { return false; }
	return true;
    }

    public String getNodetype()
    {
	return "AUTODEP";
    }
}
