
package org.de.metux.treebuild.base;

public class EPropertyMissing extends EPIException
{
    public EPropertyMissing(String name)
    {
	super(name);
    }
    public EPropertyMissing(String name, Exception e)
    {
	super(name,e);
    }
}
