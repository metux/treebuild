
package org.de.metux.treebuild.nodes;

//
// pkg config import node: queries pkg-config for a certain package
// and push its variables into appropriate packages.
//

import org.de.metux.util.Exec;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.propertylist.IPropertylist;

public class RootNode extends PINode_childs
{
    public RootNode(TreebuildConfig cf, String name)
    {
	super(cf,name,cf.getPropertylist());
    }

//    public static boolean typecheck(INode node)
//    {
//	try{ RootNode n=(RootNode)node; }
//	catch (ClassCastException e) { return false; }
//	return true;
//    }
    
    public String getNodetype()
    {
	return "ROOT";
    }
}
