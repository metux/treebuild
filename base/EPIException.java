
package org.de.metux.treebuild.base;

public class EPIException extends Exception
{
    public EPIException(String name)
    {
	super(name);
    }
    public EPIException(String name,Exception e)
    {
	super(name,e);
    }
}
