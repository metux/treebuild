
package org.de.metux.treebuild.nodes;

import java.util.Properties;
import org.de.metux.treebuild.base.TreebuildConfig;

public class ResourceNode extends PINode_childs
{
    public ResourceNode(TreebuildConfig cf, Properties p)
    {
	super(cf, p.getProperty("name"),p);
    }

    public String getNodetype()
    {
	return "RESOURCE";
    }

    public void run_Install()
    {
	install_file(
	    getPropertyMandatory("name"),
	    getPropertyMandatory("install-dir"),
	    "resource",
	    getPropertyMandatory("mode")
	);
    }
}
