
package org.de.metux.treebuild.nodes;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import org.de.metux.util.StrUtil;
import org.de.metux.util.FileOps;
import org.de.metux.util.StrReplace;
import org.de.metux.util.StrSplit;
import org.de.metux.unitool.tools.LinkExecutable;
import org.de.metux.unitool.base.LinkerParam;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.EDependencyMissing;

public class ExecutableTargetNode extends TargetNode
{
    static final String cf_executable_filename = "executable-filename";
    static final String cf_executable_pathname = "executable-pathname";
    static final String cf_workdir             = "@@build-workdir";
    static final String cf_alias               = "alias";
    
    public ExecutableTargetNode(TreebuildConfig cf, Properties pr)
    {
	super(cf,pr);
    }
    
    public static boolean typecheck(INode node)
    {
	try{ ExecutableTargetNode n=(ExecutableTargetNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }
    
    public String getNodetype()
    {
	return "EXECUTABLE-TARGET";
    }

    public void run_Build_Prepare()
    {
	FileOps.mkdir(getPropertyMandatory(cf_workdir));
    }

    public void run_Build()
	throws EDependencyMissing
    {
	run_Build_Prepare();	
	childs_Build();
	run_Build_Source();
	run_Build_Link();
    }

    // build an executable
    public void run_Build_Link()
	throws EDependencyMissing
    {
	String output_filename = getPropertyMandatory(cf_executable_filename);
	String output_pathname = getPropertyMandatory(cf_executable_pathname);
	
	long mtime_output = new File(output_pathname).lastModified();
	long mtime_childs = lastModified_childs();

	if (mtime_output>mtime_childs)
	{
	    System.err.println("Executable \""+output_filename+"\" is up to date");
	    return;
	}

	LinkerParam par = new LinkerParam(toolconf);
	prepareLinkerParam(par);
	par.setModuleName(getProperty("name"));
	par.setOutputFile(output_pathname);
	new LinkExecutable(par).run();
    }
    
    public void run_Install()
    {
	childs_Install();

	String installdir = getPropertyMandatory("@PACKAGE/install-bindir");
	String output_filename = getPropertyMandatory(cf_executable_filename);
	String output_pathname = getPropertyMandatory(cf_executable_pathname);

	// install the executable file
	install_file(
	    output_pathname,
	    installdir,
	    "executable",
	    // FIXME
	    getPropertyMandatory("@PACKAGE/fmode/library/sharedobject")
	);

	// process aliases 
	String aliases[] = StrSplit.split(getProperty(cf_alias));
	if (aliases!=null)
	{
	    for (int x=0; x<aliases.length; x++)
	    {
		// fixme: should we also support aliases in other dirs ?
		System.err.println("alias: "+aliases[x]);
		System.err.println("executable:" +output_filename);
		System.err.println("installdir: "+installdir);
		install_symlink(
		    output_filename,
		    aliases[x],
		    installdir
		);
	    }
	}
//	throw new RuntimeException("foo");
    }
    
    public String getProperty(String name)
    {
	// special handling for several properties

	if (name.equals(cf_executable_pathname))
	{
	    return 
		getPropertyMandatory(cf_workdir)+
		getPropertyMandatory(cf_executable_filename);
	}
	else if (name.equals(cf_executable_filename))
	{
	    // this property may also be given by user
	    // (but better should not
	    String res = super.getProperty(name);
	    if (!StrUtil.isEmpty(res))
		return res;
	    
	    // not given by user - render it
	    String modname = getProperty("name");

	    // FIXME !!! this has to be fetched from the global config !
	    String fn = getPropertyMandatory(
		"@TARGET/os/executable/output-filename-noversion");

	    fn = StrReplace.replace("{MODULE_NAME}",  modname, fn);
	    fn = StrReplace.replace("{VERSION_INFO}", "", fn);

	    return fn;
	}
		
	return super.getProperty(name);
    }
}
