
package org.de.metux.treebuild.base;

public class Conversion
{
    public static boolean is_ISourceNode(INode node)
    {
	try { ISourceNode src = (ISourceNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    public static boolean is_ITargetNode(INode node)
    {
	try { ITargetNode src = (ITargetNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    public static boolean is_IImportNode(INode node)
    {
	try { IImportNode src = (IImportNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    public static boolean is_IPackageNode(INode node)
    {
	try { IPackageNode src = (IPackageNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    public static boolean is_IDependNode(INode node)
    {
	try { IDependNode d = (IDependNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    public static boolean is_IIncludeNode(INode node)
    {
	try { IIncludeNode i = (IIncludeNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    
    public static IDependNode to_IDependNode(INode node)
    {
	try { return (IDependNode)node; }
	catch (ClassCastException e) { return null; }
    }
}
