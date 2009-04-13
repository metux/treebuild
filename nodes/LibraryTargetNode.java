
package org.de.metux.treebuild.nodes;

import java.io.File;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import org.de.metux.util.StrReplace;
import org.de.metux.util.Exec;
import org.de.metux.util.Version;
import org.de.metux.util.PathNormalizer;
import org.de.metux.util.StrUtil;
import org.de.metux.util.FileOps;
import org.de.metux.unitool.base.PackageInfo;
import org.de.metux.unitool.base.LinkerParam;
import org.de.metux.unitool.db.StorePkgConfig;
import org.de.metux.unitool.tools.LinkSharedLibrary;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.IImportNode;
import org.de.metux.treebuild.base.EDependencyMissing;

public class LibraryTargetNode extends TargetNode
{
    // FIXME: has to be defined more properly 
    static final String cf_module_name            = "name";
    static final String cf_library_pkgconfig_file = "pkg-config-file";
    static final String cf_library_pkgconfig_name = "pkg-config-name";
    static final String cf_library_dll_name       = "dlname";
    static final String cf_library_dll_version    = "dlversion";
    static final String cf_library_dll_filename   = "@@library-dll-filename";
    static final String cf_library_dll_shortname  = "@@library-dll-shortname";
    static final String cf_library_dll_soname     = "@@library-dll-soname";
    static final String cf_workdir                = "@@build-workdir";

    public LibraryTargetNode(TreebuildConfig cf, Properties pr)
    {
	super(cf,pr);
    }
    
    public static boolean typecheck(INode node)
    {
	try{ LibraryTargetNode n=(LibraryTargetNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }

    public boolean hasBinary()
    {
	String type = getProperty("type");

	if (StrUtil.isEmpty(type))
	    return true;
	
	if (type.equals("nobinary"))
	    return false;
	
	throw new RuntimeException("unhandled type: \""+type+"\"");
    }
    
    public String getNodetype()
    {
	return "LIBRARY-TARGET";
    }

    void run_Build_PkgConfig()
    {
	// pkg-config output entirely comes from config
	// we have no mtimes here yet

	String builddir = getPropertyMandatory(cf_workdir)+"/";
	String pkgconfig_output = builddir+getPropertyMandatory(cf_library_pkgconfig_file);
	String mod_name = getProperty(cf_module_name);
	String dl_name  = getProperty(cf_library_dll_name);
	
	PackageInfo pkg = new PackageInfo();
	pkg.name = mod_name;
	pkg.description = StrUtil.fix_notnull(getProperty("description"));
	pkg.version     = StrUtil.fix_notnull(getProperty("version"));

	/* now render directories */
	/* FIXME: we have to render dependencies too ! */
	String includedir = getProperty("@PACKAGE/install-includedir");
	String libdir     = getProperty("@PACKAGE/install-libdir");
	String prefix     = getProperty("@PACKAGE/install-prefix");
	String eprefix    = getProperty("@PACKAGE/install-exec-prefix");
	String libexecdir = getProperty("@PACKAGE/instlal-libexecdir");
	
	if (StrUtil.isEmpty(eprefix))
	    eprefix = prefix;

	// fixme: we should use LibraryInfo etc for this stuff	
	pkg.include_pathes.add(PathNormalizer.normalize(includedir));

	if (hasBinary())
	{
	    pkg.libraries.add(dl_name);
	    pkg.library_pathes.add(PathNormalizer.normalize(libdir));
	}

	IImportNode[] imports = getImportNodes();
	String pkgconfig = "";
	for (int x=0; x<imports.length; x++)
	{
	    switch (imports[x].getImportType())
	    {
		case ImportNode.type_PkgConfig:
		    pkgconfig += imports[x].getProperty("name");
		    pkg.requires_pkgconfig.add(imports[x].getProperty("name"));
		break;
		default:
		    throw new RuntimeException("unsupported import type: "+imports[x].getImportType());
	    }
	}

	pkg.properties.setProperty("prefix",      prefix);
	pkg.properties.setProperty("exec_prefix", eprefix);
	pkg.properties.setProperty("libdir",      libdir);
	pkg.properties.setProperty("libexecdir",  libexecdir);
	
	try
	{
	    StorePkgConfig.store_file(pkg,pkgconfig_output);	
	} 
	catch (Exception e)
	{
	    throw new RuntimeException(e);
	}
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

	if (hasBinary())
	    run_Build_Link();

	run_Build_PkgConfig();	
    }

    // build an shared library
    public void run_Build_Link()
	throws EDependencyMissing
    {
	String module_name     = getPropertyMandatory(cf_module_name);
	String so_name         = getPropertyMandatory(cf_library_dll_soname);
	String dll_filename    = getPropertyMandatory(cf_library_dll_filename);
	String output_filename = getPropertyMandatory(cf_workdir)
	                       + dll_filename;
	long mtime_output = new File(output_filename).lastModified();
	long mtime_childs = lastModified_childs();

	if (!(mtime_childs>=mtime_output))
	{
	    System.err.println("Shared Library \""+dll_filename+"\" is up to date");
	    return;
	}

	System.err.println("module_name="+module_name);
	System.err.println("so_name="+so_name);

// FIXME!!! soname muss korrekt gesetzt werden !
// libfoo.so.3	
	LinkerParam par = new LinkerParam(toolconf);
	prepareLinkerParam(par);
	par.setModuleName(module_name);
	par.setDLName(so_name);
	par.setLinktypeShared();
//	par.addLinkFlag("strip-debug");
	par.addLinkFlag("no-undefined");
	par.setOutputFile(output_filename);
	new LinkSharedLibrary(par).run();
    }
    
    public void run_Install_pkgconfig()
    {
	install_file(
	    getPropertyMandatory("@@build-workdir")+"/"+
	    	getPropertyMandatory(cf_library_pkgconfig_file),
	    getPropertyMandatory("@PACKAGE/install-pkgconfigdir"),
	    "pkgconfig",
	    "u=rw,go=r");
    }

    public void run_Install_so()
    {
	String shortname = getPropertyMandatory(cf_library_dll_shortname);
	String soname    = getPropertyMandatory(cf_library_dll_soname);
	String filename  = getPropertyMandatory(cf_library_dll_filename);
	String libdir    = getPropertyMandatory("@PACKAGE/install-libdir");
	
	install_file(
	    getPropertyMandatory(cf_workdir)+filename,
	    libdir,
	    "sharedobject",
	    getPropertyMandatory("@PACKAGE/fmode/library/sharedobject")
	);
	install_symlink(filename, soname, libdir);	
	install_symlink(soname, shortname, libdir);
	
	// run ldconfig -- FIXME !!!
	Exec e = new Exec();
	String cmdline = "ldconfig -N -r "+
	    path2instroot("/")+" "+
	    getPropertyMandatory("@PACKAGE/install-sys-libdir")+" "+
	    getPropertyMandatory("@PACKAGE/install-libdir")+" "+
	    getProperty("@PACKAGE/install-x11-libdir");

	System.err.println("CMDLINE="+cmdline);
	System.err.println("now calling ldconfig ... its not necessarily an error if this fails");
    }
    
    public void run_Install()
    {
	childs_Install();
	if (hasBinary())
	    run_Install_so();
	run_Install_pkgconfig();
    }

    public String getProperty(String name)
    {
	// special handling for several properties - brutally 
	// overruling existing ones
	
	// ... none yet ;-o
	
	{
	    String val =  super.getProperty(name);
	    if (!StrUtil.isEmpty(val))
		return val;
	}

	// handle properties which are *NOT* existing / empty

	if (name.equals(cf_library_dll_soname))
	{
	    Version ver = new Version(
		getPropertyMandatory(cf_library_dll_version));
	    
	    return
		StrReplace.replace(
		    "{LIBRARY_NAME}", 
		    getPropertyMandatory(cf_library_dll_name),
		StrReplace.replace("{VERSION.0}", (""+ver.digits[0]),
	        getPropertyMandatory("@TARGET/os/library/dll/soname")
	    ));
	}

	// the name of our library in pkgconfig's namespace
	if (name.equals(cf_library_pkgconfig_name))
	    return this.getProperty(cf_module_name);

	if (name.equals(cf_library_pkgconfig_file))
	    return this.getProperty(cf_library_pkgconfig_name)+".pc";
	
	if (name.equals(cf_library_dll_name))
	    return this.getProperty(cf_module_name);
	    
	if (name.equals(cf_library_dll_filename))
	    return 	
		StrReplace.replace("{LIBRARY_NAME}", 
		    getPropertyMandatory(cf_library_dll_name),
		StrReplace.replace("{VERSION_INFO}",
		    getPropertyMandatory(cf_library_dll_version),
		getPropertyMandatory("@TARGET/os/library/dll/output-filename")));

	if (name.equals(cf_library_dll_shortname))
	    return 
		StrReplace.replace("{LIBRARY_NAME}",
		getPropertyMandatory(cf_library_dll_name),
		getPropertyMandatory("@TARGET/os/library/dll/shortname"));

	return super.getProperty(name);
    }
}
