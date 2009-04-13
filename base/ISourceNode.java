
package org.de.metux.treebuild.base;

public interface ISourceNode extends INode
{
    // fixme: add flags for several branches (ie. pic vs. nonpic)
    public String getOutputFilename();
    public String getBuildToolName();
    public String getInputFilename();
    
    public long lastModified()
	throws EDependencyMissing;
}
