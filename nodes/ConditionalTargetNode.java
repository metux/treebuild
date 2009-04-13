
package org.de.metux.treebuild.nodes;

//
// pkg config import node: queries pkg-config for a certain package
// and push its variables into appropriate packages.
//

import org.de.metux.util.Exec;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.INode;
import java.util.Properties;

public class ConditionalTargetNode extends TargetNode
{
    public ConditionalTargetNode(TreebuildConfig cf, Properties pr)
    {
	super(cf,pr);
	System.err.println("loading conditional target node...");
    }
    
    public static boolean typecheck(INode node)
    {
	try{ PackageNode n=(PackageNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    
    public String getNodetype()
    {
	return "POOL";
    }
}
