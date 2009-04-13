/*

    this interface represents target nodes

*/ 

package org.de.metux.treebuild.base;

public interface ITargetNode extends INode
{
    public void addSource(ISourceNode node);
    public void addInclude(IIncludeNode node);
    public void addSubTarget(ITargetNode node);
    public IImportNode[] getImportNodes();
}
