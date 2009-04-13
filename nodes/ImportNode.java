
package org.de.metux.treebuild.nodes;

import java.util.Properties;
import org.de.metux.unitool.db.QueryPkgConfig;
import org.de.metux.unitool.base.PackageInfo;
import org.de.metux.treebuild.base.IImportNode;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.TreebuildConfig;

import org.de.metux.treebuild.base.EDependencyMissing;

public class ImportNode extends PINode_childs implements IImportNode
{
    static final String cf_type = "type";
    static final String cf_name = "name";
    static final String cf_type_pkgconfig = "pkg-config";

    PackageInfo[] pkginfo;
    
    public ImportNode(TreebuildConfig cf, Properties pr)
    {
	super(cf, pr.getProperty("name"),pr);
    }
    
    public static boolean typecheck(INode node)
    {
	try{ ImportNode n=(ImportNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }

    public String[] getLibraryPathes()
    {
	getPackageInfo();
	return pkginfo[0].library_pathes.getNames();
    }
    
    public String[] getIncludePathes()
    {
	getPackageInfo();
	return pkginfo[0].include_pathes.getNames();
    }

    public String[] getLibraryNames()
    {
	getPackageInfo();
	return pkginfo[0].libraries.getNames();
    }
    
    public int getImportType()
    {
	String type = getProperty(cf_type);
	if (type.equals(cf_type_pkgconfig))
	    return type_PkgConfig;
	if (type.equals("libtool-archive"))
	    return type_LibtoolArchive;
	if (type.equals("explicit"))
	    return type_Explicit;

	throw new RuntimeException("unsupported import type: "+type);
    }

    public PackageInfo[] getPackageInfo()
    {
	if (pkginfo==null)
	{
	    pkginfo = new PackageInfo[1];

	    QueryPkgConfig query = new QueryPkgConfig();
	    query.setSysroot(getPropertyMandatory(cf_sysroot));
	    query.setPath(getPropertyMandatory("@TARGET/pkg-config-path"));
	    query.setCommand(getPropertyMandatory("@TARGET/pkg-config-cmdline"));
	
	    pkginfo[0] = query.queryPackage(
		getPropertyMandatory("name"),
	        getProperty("min-version")
	    );
	}
	return pkginfo;
    }
    
    public void run_Configure_pkgconfig()
    {
	getPackageInfo();
    }

    public void run_Configure()
	throws EDependencyMissing
    {
	childs_Configure();
	switch (getImportType())
	{
	    case type_PkgConfig:
		run_Configure_pkgconfig();
	    break;

	    case type_LibtoolArchive:
		throw new RuntimeException("libtool archives not supported yet");

	    case type_Explicit:
		throw new RuntimeException("explicit imports are not supported yet");

	    default:
		throw new RuntimeException("unhandled type: "+getImportType());
	}
    }
    
    public String getNodetype()
    {
	return "PKGCONFIG-IMPORT";
    }
}
