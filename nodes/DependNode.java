
package org.de.metux.treebuild.nodes;

import java.util.Properties;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.EDependencyMissing;
import org.de.metux.treebuild.base.IDependNode;
import org.de.metux.treebuild.base.TreebuildConfig;
import java.io.File;

public class DependNode extends PINode_childs implements IDependNode
{
    public DependNode(TreebuildConfig cf, Properties p)
    {
	super(cf, p.getProperty("name"),p);    
    }
    
    public String getNodetype()
    {
	return "DEPEND";
    }
    
    public File getFileHandle()
    {
	return new File(id).getAbsoluteFile();
    }

    public long lastModified()
	throws EDependencyMissing
    {
	long mtime = getFileHandle().lastModified();

	if (mtime==0)
	    throw new EDependencyMissing(id);

	return mtime;
    }
    
    public boolean Expired()
    {
	return false;
    }
}
