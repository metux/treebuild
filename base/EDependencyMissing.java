
package org.de.metux.treebuild.base;

import java.io.File;

public class EDependencyMissing extends EPIException
{
    public EDependencyMissing(String name)
    {
	super(name);
    }
    public EDependencyMissing(String name, Exception e)
    {
	super(name,e);
    }
    public EDependencyMissing(File f, Exception e)
    {
	super(f.toString(),e);
    }
}
