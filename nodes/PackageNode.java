
package org.de.metux.treebuild.nodes;

//
// pkg config import node: queries pkg-config for a certain package
// and push its variables into appropriate packages.
//

import java.util.Properties;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.de.metux.util.Exec;
import org.de.metux.util.StoreFile;
import org.de.metux.util.StrReplace;
import org.de.metux.util.StrUtil;
import org.de.metux.util.StrSplit;
import org.de.metux.treebuild.base.EDependencyMissing;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.IPackageNode;
import org.de.metux.treebuild.conf.HeaderFile;
import org.de.metux.treebuild.conf.Feature;
import org.de.metux.treebuild.conf.Configure;

public class PackageNode extends PINode_childs implements IPackageNode
{
    Configure  pkg_conf = new Configure();
    HeaderFile pkg_conf_headers[];
    Feature    pkg_conf_features[];
    
    public PackageNode(TreebuildConfig cf, Properties pr)
    {
	super(cf,pr.getProperty("name"),pr);
	setProperty("@@package-name", pr.getProperty("name"));
    }
    
    public static boolean typecheck(INode node)
    {
	try{ PackageNode n=(PackageNode)node; }
	catch (ClassCastException e) { return false; }
	return true;
    }

    boolean _check_feature_deps(Feature f)
    {
	// check if the dependencies are fulfilled.
	boolean okay = true;
	for (int y=0; y<f.require_sys_config.length; y++)
	{
	    String cf = getProperty("@TARGET/"+f.require_sys_config[y]);
	    okay &= StrUtil.toBool(cf,false);
	}
	return okay;
    }

    void _process_header()
	throws EDependencyMissing
    {
	if (pkg_conf_headers!=null)
	    for (int x=0; x<pkg_conf_headers.length; x++)
		_process_header(pkg_conf_headers[x]);
    }

    void _process_header(HeaderFile header)
	throws EDependencyMissing
    {
	if (header==null)
	    return;

	System.err.println("Processing header file: "+header.name);
	System.err.println(" input_file="+header.input_file.getAbsolutePath());
	
	try
	{
	    BufferedReader in = new BufferedReader(
		new FileReader(header.input_file.getAbsolutePath()));

	    String content = "";
	    String line;
	    while ((line=in.readLine())!=null)
	    {
		for (int x=0; x<pkg_conf_features.length; x++)
		{
		    {
			String on_mask   = "#undef "+pkg_conf_features[x].symbol_enabled;
			String on_enable = "#define "+pkg_conf_features[x].symbol_enabled;
		        if (line.startsWith(on_mask))
			{
			    if (pkg_conf_features[x].enabled)
			    {
				System.err.println(" --> [ON] enabled feature: "+pkg_conf_features[x].name+" ("+on_mask+")");
			        line = StrReplace.replace(on_mask,on_enable,line);
			    }
			    else
				System.err.println(" --> [ON] leaving disabled: "+pkg_conf_features[x].name+" ("+on_mask+")");
			}
		    }
		    {
			String off_mask   = "#undef "+pkg_conf_features[x].symbol_disabled;
			String off_enable = "#define "+pkg_conf_features[x].symbol_disabled;
			if (line.startsWith(off_mask))
			{
			    if (!pkg_conf_features[x].enabled)
			    {
				System.err.println(" --> [OFF] enabled feature: "+pkg_conf_features[x].name+" ("+off_mask+")");
				line = StrReplace.replace(off_mask,off_enable,line);
			    }
			    else
				System.err.println(" --> [OFF] leaving disabled: "+pkg_conf_features[x].name+" ("+off_mask+")");
			}
		    }
		}
		content += line+"\n";
	    }
	    StoreFile.store(header.output_file,content);
	}
	catch (FileNotFoundException e)
	{
	    throw new EDependencyMissing(header.input_file,e);
	}
	catch (IOException e)
	{
	    throw new EDependencyMissing(header.input_file,e);
	}
    }

    private void __init_features()
    {
	if (pkg_conf_features!=null)
	    return;
	    
	System.out.println("Initializing features ...");
	pkg_conf_features = pkg_conf.getFeatures();
    }
    
    private void _init()
    {
	if (pkg_conf==null)
	    throw new RuntimeException("ugh! could not load pkg_conf !");

	pkg_conf_headers = pkg_conf.getHeaderFiles();
	__init_features();
    }
    
    void _check_features()
    {
	__init_features();

	if (pkg_conf_features == null)
	    return;
	
	for (int x=0; x<pkg_conf_features.length; x++)
	{
	    Feature f = pkg_conf_features[x];
	    f.dependencies_okay = _check_feature_deps(f);
	    if (f.mode.equals("auto"))
	    {
		if (f.dependencies_okay)
		{
		    System.err.println("auto-enabling: "+f.name);
		    f.enabled = true;
		}
		else
		{
		    System.err.println("auto-disabling: "+f.name);
		    f.enabled = false;
		}
	    }
	    else if (f.mode.equals("off"))
	    {
		System.err.println("disabled: "+f.name);
		f.enabled = false;
	    }
	    else if (f.mode.equals("on"))
	    {
		if (_check_feature_deps(f))
		{
		    System.err.println("enabled: "+f.name);
		    f.enabled = true;
		}
		else
		    throw new RuntimeException("missing dependencies for feature: "+f.name);
	    }
	}
    }

    public void run_Configure()
	throws EDependencyMissing
    {
	_init();
	_check_features();
	_process_header();
    }
    
    public String getNodetype()
    {
	return "PACKAGE";
    }
    
    private void __init_conf()
    {
	if (pkg_conf==null)
	    pkg_conf = new Configure();
    }
     
    public void addConfFeature(Properties p)
    {
	__init_conf();
	pkg_conf.addFeature(p);
    }
    
    public void addConfSubstitute(Properties p)
    {
	__init_conf();
	pkg_conf.addSubstitute(p);
    }
    
    public void addConfHeader(Properties p)
    {
	__init_conf();
	pkg_conf.addHeader(p);
    }
    
    public void setFeatureFlags(String s)
    {
	__init_features();
	
	if (pkg_conf_features == null)
	    throw new RuntimeException("no feature records");
	if (s==null)
	    throw new NullPointerException("missing feature flags to set");
	
	String[] list = StrSplit.split(s,",");
	for (int x=0; x<list.length; x++)
	{
	    String fname;
	    boolean fval;
	
	    System.err.println("processing feature flag: "+list[x]);
	    if (list[x].startsWith("+"))
	    {
		fval = true;
		fname = list[x].substring(1);
	    }
	    else if (list[x].startsWith("-"))
	    {
		fval = false;
		fname = list[x].substring(1);
	    }
	    else
		throw new RuntimeException("dont know how to handle unspecific flag: \""+list[x]+"\"");
	    
	    Feature f = __find_feature(fname);
	    if (f==null)
		throw new RuntimeException("feature \""+fname+"\" not defined");

	    if (fval)
		f.mode = "on";
	    else 
		f.mode = "off";
		
	    System.err.println("Setting feature \""+fname+"\" to "+f.mode);
	}
    }
    
    private Feature __find_feature(String name)
    {
	for (int x=0; x<pkg_conf_features.length; x++)
	{
	    if (pkg_conf_features[x].name.equals(name))
		return pkg_conf_features[x];
	}
	return null;
    }
}
