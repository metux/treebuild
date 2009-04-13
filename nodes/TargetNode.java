
package org.de.metux.treebuild.nodes;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;

import org.de.metux.util.StrSplit;

import org.de.metux.treebuild.base.IImportNode;
import org.de.metux.treebuild.base.EPIException;
import org.de.metux.treebuild.base.ITargetNode;
import org.de.metux.treebuild.base.EDependencyMissing;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.IIncludeNode;
import org.de.metux.treebuild.base.ISourceNode;
import org.de.metux.treebuild.base.Conversion;

import org.de.metux.unitool.base.ToolType;
import org.de.metux.unitool.base.ToolConfig;
import org.de.metux.unitool.base.CCompilerParam;
import org.de.metux.unitool.base.LinkerParam;
import org.de.metux.unitool.tools.CCompiler;

import org.de.metux.propertylist.*;

public class TargetNode extends PINode_childs implements ITargetNode
{
    public String cf_include_path = "include-path";

    protected ISourceNode source_node_cache[];
    protected IImportNode import_node_cache[];

    // FIXME!!!
    protected ToolConfig  toolconf;

    public TargetNode(TreebuildConfig cf, Properties pr)
    {
	super(cf, pr.getProperty("name"),pr);
	toolconf = new ToolConfig(properties);
    }
    
    public static boolean typecheck(INode node)
    {
	try { TargetNode n=(TargetNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }

    // check what childs we let in
    public void addChild(INode n)
    {
	if (Conversion.is_ISourceNode(n) ||
	    Conversion.is_IImportNode(n) ||
	    Conversion.is_IIncludeNode(n)	||
	    Conversion.is_ITargetNode(n))
	    super.addChild(n);
	else
	    throw new RuntimeException("child refused: "+n.getID()+" ["+n.getNodetype()+"]");
    }

    public void addSubTarget(ITargetNode n)
    {
	addChild(n);
    }

    public void addAlias(String name)
    {
	addProperty("alias",name);
    }
	    
    public void addSource(ISourceNode n)
    {
	addChild(n);
    }

    public void addInclude(IIncludeNode n)
    {
	addChild(n);
    }

    public String getNodetype()
    {
	return "GENERIC-TARGET";
    }

    // only visible to sub-classes
    // mtime==0 -> n/a
    protected long lastModified_childs()
	throws EDependencyMissing
    {
	long mtime = 0;

	// FIXME: we only handle sources - what's with imports ?
	ISourceNode[] srcs = getSourceNodes();
	for (int x=0; x<srcs.length; x++)
	{
	    long mtime_src = srcs[x].lastModified();
	    if (mtime_src>mtime)
		mtime = mtime_src;
	}
	return mtime;
    }

    /* this function builds the source nodes to their objects */
    public ISourceNode[] getSourceNodes()
    {
	if (source_node_cache!=null)
	    return source_node_cache;

	INode[] child_list = getChilds();
	int num_sources = 0;
	
	for (int x=0; x<child_list.length; x++)
	    if (Conversion.is_ISourceNode(child_list[x]))
		num_sources++;

	ISourceNode[] srcs = new ISourceNode[num_sources];
	int ptr = 0;
	for (int x=0; x<child_list.length; x++)
	    if (Conversion.is_ISourceNode(child_list[x]))
		srcs[ptr++]=(ISourceNode)child_list[x];

	source_node_cache = srcs;
	
	// sanity check
	// FIXME: remove someday
	for (int x=0; x<srcs.length; x++)
	    if (srcs[x]==null)
		throw new RuntimeException("uggh. sanity check failed");

	return srcs;
    }

    /* this function fetches the import nodes */
    public IImportNode[] getImportNodes()
    {
	if (import_node_cache!=null)
	    return import_node_cache;

	INode[] child_list = getChilds();

	/* we first use an really large buffer, and compress it later -- FIXME */
	IImportNode[] imports = new IImportNode[1000];
	int ptr = 0;
	for (int x=0; x<child_list.length; x++)
	    if (Conversion.is_IImportNode(child_list[x]))
		imports[ptr++]=(IImportNode)child_list[x];

	for (int x=0; x<child_list.length; x++)
	{
//	    System.err.println("getImportNodes("+getID()+") child #"+x+" "+child_list[x].getID());

	    if (Conversion.is_ITargetNode(child_list[x]))
	    {
		/* hmm, should we limit it to conditional targets ?? fixme */
		ITargetNode n = (ITargetNode)child_list[x];
		IImportNode[] i = n.getImportNodes();
		if (i!=null)
		    for (int y=0; y<i.length; y++)
			if (i[y]!=null)
			{
			    System.err.println("SUB-IMPORT: "+i[y].getID());
			    imports[ptr++]=i[y];
			}
	    }
	}

	// compress the imports node list
	{
	    IImportNode[] new_imports = new IImportNode[ptr];
	    for (int x=0; x<ptr; x++)
		new_imports[x] = imports[x];
	    imports = new_imports;
	}
	    
	import_node_cache = imports;

	return imports;
    }

    public void run_Build_Source()
    {
	buildSource(getSourceNodes());
    }

    private void buildSource_CCompile(ISourceNode src)
    {
	CCompilerParam par = new CCompilerParam(toolconf);
	
	par.setSysroot(getPropertyMandatory(cf_sysroot));
	par.addSourceFile(src.getInputFilename());
	par.setOutputFile(src.getOutputFilename());
	par.setSysroot(getPropertyMandatory(cf_sysroot));
	par.setPIC(true);	// FIMXE !

	IImportNode imports[] = getImportNodes();
	for (int x=0; x<imports.length; x++)
	    if (imports[x] != null)
		par.addPackageImport(imports[x].getPackageInfo());

	String[] include_pathes = 
	    StrSplit.split(getProperty(cf_include_path));
	par.addIncludePath(include_pathes);
	
	// FIXME: define symbols, etc
	try
	{
	    new CCompiler().run(par);
	}
	catch (Exception e)
	{
	    throw new RuntimeException(e);
	}
    }

    public void buildSource(ISourceNode src)
    {
	if (src==null)
	    throw new RuntimeException("buildSource(ISourceNode) NULL node passed");

	// FIXME
	try {
	    if (!src.Expired())
	    {
//		System.err.println("Source Node \""+src.getInputFilename()+"\" is up to date.");
//		return;
	    }
	}
	catch (EPIException e)
	{
	    throw new RuntimeException(e);
	}
	
	int tool_id = ToolType.tag2id(src.getBuildToolName());
	switch (tool_id)
	{
	    case ToolType.id_COMPILE_BINOBJ_C:
		buildSource_CCompile(src);
	    break;
	    default:
		throw new RuntimeException("buildSource() unsupported tool_id=\""+tool_id+"\"");
	}
    }

    public void buildSource(ISourceNode[] srcs)
    {
	if (srcs!=null)
	    for (int x=0; x<srcs.length; x++)
		buildSource(srcs[x]);
    }
    
    public String getObjectFilesStr(ISourceNode[] srcs)
    {
	String out = null;
	for (int x=0; x<srcs.length; x++)
	    out = ((out==null) ? "" : out + " ")+srcs[x].getOutputFilename();
	return out;
    }
    
    public String getObjectFilesStr()
    {
	return getObjectFilesStr(getSourceNodes());
    }

    public void prepareLinkerParam(LinkerParam par)
    {
	par.setSysroot(getPropertyMandatory(cf_sysroot));
	ISourceNode srcs[] = getSourceNodes();
	for (int x=0; x<srcs.length; x++)
	    par.addObjectLink(srcs[x].getOutputFilename());

	// handle imports
	// FIXME: we also should handle libtool imports ... someday ...
	System.err.println("prepareLinkerParam()");
	IImportNode imports[] = getImportNodes();
	for (int x=0; x<imports.length; x++)
	{
	    System.err.println(" -> "+imports[x].getProperty("name"));
	    par.addLibraryImport(imports[x].getPackageInfo());
	}
    }
}
