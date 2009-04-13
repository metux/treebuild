
package org.de.metux.treebuild.nodes;

import java.util.Properties;
import org.de.metux.util.StrUtil;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.IIncludeNode;

public class HeaderNode extends PINode_childs implements IIncludeNode
{
    String 	filename;
    String 	type;
    boolean	is_public;
    
    final static String cf_name                    = "name";
    final static String cf_file_mode               = "mode";
    final static String cf_install_dir             = "install-dir";
    final static String cf_scope                   = "scope";
    final static String cf_default_file_mode       = "include-target-mode";
    final static String cf_default_install_dir     = "include-target-dir";
    final static String cf_default_file_mode_value = "u=rw,go=r";

    public HeaderNode(TreebuildConfig cf, Properties p)
    {
	super(cf, p.getProperty(cf_name),p);    
    }
    
    public HeaderNode(TreebuildConfig cf, String n, String t, boolean pub)
    {
	super(cf,n);
	filename = n;
	type = t;
	is_public = pub;
    }

    public String getNodetype()
    {
	return "HEADER";
    }

    public String getProperty(String name)
    {
	String val;
	
	val = super.getProperty(name);
	if (!StrUtil.isEmpty(val))
    	    return val;
	
	if (name.equals(cf_file_mode))
	{
	    val = getProperty(cf_default_file_mode);
	    if (!StrUtil.isEmpty(val))
		return val;
	    
	    return cf_default_file_mode_value;
	}
	else if (name.equals(cf_install_dir))
	{
	    return getPropertyMandatory(cf_default_install_dir);
	}
	else if (name.equals(cf_scope))
	{
	    System.err.println("WARNING: missing scope for include file \""+getPropertyMandatory(name)+"\" ... assuming public");
	    return "public";
	}
	
	return val;
    }

    public void run_Install()
    {
	String name  = getPropertyMandatory(cf_name);
	String scope = getPropertyMandatory(cf_scope);
	
	if (scope.equals("public") || scope.equals("protected") || scope.equals("private"))
	    install_file(
		name,
		getPropertyMandatory(cf_install_dir), 
		"header",
		getPropertyMandatory(cf_file_mode)
	    );
	else if (scope.equals("hidden"))
	    System.out.println("not installing hidden include file: "+name);
	else
	    throw new RuntimeException("unhandled scope \""+scope+"\" for include-file \""+name+"\"");
    }
}
