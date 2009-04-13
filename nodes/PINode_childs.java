
package org.de.metux.treebuild.nodes;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Date;
import java.util.Properties;

import org.de.metux.util.StrUtil;
import org.de.metux.util.FileOps;
import org.de.metux.util.PathNormalizer;
import org.de.metux.propertylist.IPropertylist;
import org.de.metux.propertylist.FallbackPropertylist;
import org.de.metux.treebuild.base.INode;
import org.de.metux.treebuild.base.EDependencyMissing;
import org.de.metux.treebuild.base.TreebuildConfig;

public abstract class PINode_childs implements INode
{
    Hashtable 			childs = new Hashtable();
    INode[]   		childs_cache = null;
    String 			id = null;
    INode			parent = null;
    FallbackPropertylist	properties;
    TreebuildConfig		config;

    public final static String cf_sysroot = "@@system-root";

    public PINode_childs(TreebuildConfig cf, String name)
    {
	id = name;
	properties = new FallbackPropertylist((IPropertylist)null);
	config     = config;
    }

    public PINode_childs(TreebuildConfig cf, String name, Properties pr)
    {
	config = cf;
	id = name;
	properties = new FallbackPropertylist(pr);
    }

    public PINode_childs(TreebuildConfig cf, String name, IPropertylist pr)
    {
	config = cf;
	id = name;
	properties = new FallbackPropertylist(pr);
    }

    public String getID()
    {
	return id;
    }

    // FIMXE !
    public String path2sysroot(String path)
    {
	return getPropertyMandatory(cf_sysroot)+"/"+path;
    }
    
    public String path2instroot(String path)
    {
	return 
	    PathNormalizer.normalize(
		getPropertyMandatory("@@install-root")+"/"+path);
    }

    public void addProperty(String name, String value)
    {
	properties.add(name,value);
    }

    public void setProperty(String name, String value)
    {
	properties.set(name,value);
    }
    
    /* set an property to given value if its not already set */
    public void setPropertyIfMissing(String name, String value)
    {
	if (StrUtil.isEmpty(getProperty(name)))
	    setProperty(name, value);
    }

    /* load properties from external Propertylist as they are missing */
    public void loadPropertiesMissing(Properties db)
    {
	for (Enumeration e=db.propertyNames(); e.hasMoreElements();)
	{
	    String name = (String)e.nextElement();
	    setPropertyIfMissing(name, db.getProperty(name));
	}
    }
    /* load properties from external Propertylist as they are missing */
    public void loadPropertiesMissing(IPropertylist db)
    {
	for (Enumeration e=db.propertyNames(); e.hasMoreElements();)
	{
	    String name = (String)e.nextElement();
	    try
	    {   
		setPropertyIfMissing(name, db.get_str(name));
	    }
	    catch (Exception ex)
	    {
		throw new RuntimeException(ex);
	    }
	}
    }

    public String getProperty(String name)
    {
	// FIXME: ugly hack
	try {
	return properties.get_str(name);
	} catch (Exception e) { throw new RuntimeException("misconfiguration", e); }
    }

    public String getProperty(String name, String def)
    {
	String v = getProperty(name);
	if (StrUtil.isEmpty(v))
	    return def;
	return v;
    }
    
    public String getPropertyRec(String name)
    {
	String val = getProperty(name);
	if ((val!=null)&&(val.length()==0))
	    return val;
	if (parent==null)
	    return "";
	return parent.getPropertyRec(name);
    }

    public String getPropertyMandatory(String name)
    {
	String str = getProperty(name);
	if ((str==null)||(str.length()==0))
	    throw new RuntimeException("missing value for: "+name);
	return str;
    }
    
    public IPropertylist getPropertylist()
    {
	return properties;
    }

    public void setParent(INode p)
    {
	parent = p;
	properties.setFallback(p.getPropertylist());
    }
    
    public void addChild(INode child)
    {
	childs.put(child.getID(),child);
	child.setParent(this);
	childs_cache = null;
	System.err.println("adding child "+child.getID()+" to "+id);
    }
    
    public INode [] getChilds()
    {
	if (childs_cache==null)
	{
	    childs_cache = new INode[childs.size()];
	    int x=0;
	    for (Enumeration e=childs.elements(); 
		 e.hasMoreElements();
	         childs_cache[x++] = (INode)e.nextElement());
	}
	return childs_cache;
    }
    
    public INode getParent()
    {
	return parent;    
    }
    
    /* -------------------- node/childs expired ------------------- */
    public boolean childs_Expired() throws EDependencyMissing
    {
	boolean okay = false;
	INode my_childs[] = getChilds();
	
	for (int x=0; x<my_childs.length; x++)
	    if (my_childs[x]!=null)
		okay |= my_childs[x].Expired();

	return okay;
    }

    public boolean Expired() throws EDependencyMissing
    {
	childs_Expired();
	return true;
    }

    /* -------------------- autodep stage --------------------- */
    public void run_Autodep()
    {
	childs_Autodep();
    }

    protected void childs_Autodep()
    {
	INode my_childs[] = getChilds();
	for (int x=0; x<my_childs.length; x++)
	    if (my_childs[x]!=null)
		my_childs[x].run_Autodep();
    }

    /* ----- BUILD stage ----- */
    public void childs_Build()
	throws EDependencyMissing
    {
	INode my_childs[] = getChilds();
	for (int x=0; x<my_childs.length; x++)
	    if (my_childs[x]!=null)
		(my_childs[x]).run_Build();
    }

    public void run_Build()
	throws EDependencyMissing
    {
	childs_Build();
    }

    /* ------------------- configure stage -------------------- */    
    protected void childs_Configure()
	throws EDependencyMissing
    {
	INode my_childs[] = getChilds();
	for (int x=0; x<my_childs.length; x++)
	    if (my_childs[x]!=null)
		(my_childs[x]).run_Configure();
    }

    public void run_Configure()
	throws EDependencyMissing
    {
	childs_Configure();
    }

    /* -------------------- install stage  -------------------- */
    protected void childs_Install()
    {
	INode my_childs[] = getChilds();
	for (int x=0; x<my_childs.length; x++)
	    if (my_childs[x]!=null)
		(my_childs[x]).run_Install();
    }

    public void run_Install()
    {
	childs_Install();
    }
    
    protected void install_file(String source, String dest, String type, String mode)
    {
	String installdir = path2instroot(dest);
	
	System.out.println("Installing ["+type+"] \""+source+"\" into \""+installdir+"\"");
	
	if (!FileOps.mkdir(installdir))
	    throw new RuntimeException("could not create dir: "+installdir);
	
	if (!FileOps.cp(source,installdir+"/",mode))
	    throw new RuntimeException("could not copy file: "+source+" -> "+installdir);
    }
    
    protected void install_symlink(String target, String name, String destdir)
    {
	destdir = path2instroot(destdir);
	System.out.println("Installing symlink: "+name+" => "+target+" in "+destdir);
	
	if (!FileOps.mkdir(destdir))
	    throw new RuntimeException("could not create dir: "+destdir);

	FileOps.symlink(target,destdir+"/"+name);
    }
}
