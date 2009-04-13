/*

    this interface represents target nodes

*/ 
package org.de.metux.treebuild.base;

public interface IDependNode extends INode
{
    public long lastModified()
	throws EDependencyMissing;
}
