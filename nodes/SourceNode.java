
package org.de.metux.treebuild.nodes;

import java.util.Properties;
import org.de.metux.util.PathNormalizer;
import org.de.metux.util.StrUtil;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.EDependencyMissing;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.IDependNode;
import org.de.metux.treebuild.base.Conversion;
import org.de.metux.treebuild.base.ISourceNode;

import org.de.metux.unitool.base.ToolType;
import java.io.File;
import org.de.metux.util.PathNormalizer;

public class SourceNode extends PINode_childs implements ISourceNode
{
    String filename;
    String type;
    
    public SourceNode(TreebuildConfig cf, String n, String t)
    {
	super(cf,n);
	filename = n;
	type = t;
    }

    public SourceNode(TreebuildConfig cf, String n, String t, Properties p)
    {
	super(cf,n);
	filename = n;
	type = t;
    }

    public long lastModified()
	throws EDependencyMissing
    {
	long mtime_in = getInputFileHandle().lastModified();
	long mtime_out = new File(getOutputFilename()).lastModified();

	if ((mtime_in<0)||(mtime_out<0))
	    throw new RuntimeException("JAVA-BUG: java.io.File.lastModified() returns negative timestamp");
	    
	if (mtime_in==0)
	    throw new EDependencyMissing(id);

	return ((mtime_out>mtime_in)?mtime_out:mtime_in);
    }

    public String getOutputFilename()
    {
	String[] f = PathNormalizer.split_filename(filename);
	return getProperty("@@build-workdir")+"/"+f[0]+".o";
    }

    public boolean Expired()
	throws EDependencyMissing
    {
	if (childs_Expired())
	    return true;

	File in  = getInputFileHandle();
	File out = new File(getOutputFilename());
	
	long mtime_in = in.lastModified();
	long mtime_out = out.lastModified();
	
	if ((mtime_in<0)||(mtime_out<0))
	    throw new RuntimeException("JAVA-BUG: java.io.File.lastModified() returned negative mtime");

	if (mtime_in==0)
	    throw new RuntimeException("missing input file: "+getInputFilename());

	if (mtime_in > mtime_out)
	    return true;

	INode c[] = getChilds();
	for (int x=0; x<c.length; x++)
	{
	    IDependNode dep = Conversion.to_IDependNode(c[x]);
	    if (dep==null)
		throw new RuntimeException("ugh! why do we have something else than an depend as sub ?!");
	    if (dep.lastModified() > mtime_out)
		return true;
	}	    

	return false;
    }

    public File getInputFileHandle()
    {
	return new File(getInputFilename()).getAbsoluteFile();
    }
    
    public String getInputFilename()
    {
	String dir = getProperty("source-directory");
	if (StrUtil.isEmpty(dir))
	    return filename;
	else 
	    return dir+"/"+filename;
    }
    
    public String getNodetype()
    {
	return "SOURCE";
    }
    
    public static boolean typecheck(INode n)
    {
	try { SourceNode src = (SourceNode)n; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    
    public int getBuildToolID()
    {
	// try to determine by type
	if ((type!=null)&&(type.length()!=0))
	{
	    if (type.equals("text/c-source") || 
	        type.equals("c-source")      ||
		type.equals("c"))
		return ToolType.id_COMPILE_BINOBJ_C;
	
	    if (type.equals("text/c++-source") ||
	        type.equals("c++-source") ||
		type.equals("c++"))
		return ToolType.id_COMPILE_BINOBJ_CPLUSPLUS;
	    
	    throw new RuntimeException("unhandled source type: "+type);
	}
	
	// determine by file extension
	if (filename.endsWith(".c"))
	    return ToolType.id_COMPILE_BINOBJ_C;
	
	if (filename.endsWith(".cpp") || filename.endsWith(".c++"))
	    return ToolType.id_COMPILE_BINOBJ_CPLUSPLUS;
	
	throw new RuntimeException("unhandled extension: "+filename);
    }

    public String getBuildToolName()
    {
	return ToolType.id2tag(getBuildToolID());
    }
}
