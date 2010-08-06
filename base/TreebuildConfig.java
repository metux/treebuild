
package org.de.metux.treebuild.base;

import java.io.File;
import java.util.Properties;
import org.de.metux.util.StrUtil;
import org.de.metux.datasource.Cached_TextDB_Loader;
import org.de.metux.propertylist.IPropertylist;
import org.de.metux.propertylist.Propertylist;
import org.de.metux.propertylist.EIllegalValue;
import org.de.metux.unitool.base.ToolConfig;
import org.de.metux.unitool.db.UnitoolConf;

public class TreebuildConfig
{
    public IPropertylist conf;
    public IPropertylist unitool_conf;
    public ToolConfig	 unitool;
    
    public static final String cf_unitool_config = "@TARGET/unitool-config";
    private Cached_TextDB_Loader textdb_loader = new Cached_TextDB_Loader();

    private boolean __load_properties_top(String filename)
    {
	Properties pr = textdb_loader.load(filename);
	if (pr == null)
	    return false;
	conf.loadProperties_top(pr);
	return true;
    }

    public TreebuildConfig(File f)
	throws EPIException
    {
	String filename = f.getAbsolutePath();
	System.err.println("Using treebuild config file: "+filename);
	conf = new Propertylist();
	if (!__load_properties_top(filename))
	    throw new RuntimeException("coud not load treebuild config: "+filename);

	//  now load the unitool config
	try{
	    String cfname = conf.get_str(cf_unitool_config);
	    if (StrUtil.isEmpty(cfname))
		throw new EPropertyMissing(cf_unitool_config);

	    // now load the unitool config
	    unitool = UnitoolConf.getToolConfig(new File(cfname));
	    unitool_conf = unitool.getPropertylist();
	}
	catch (EIllegalValue e)
	{
	    throw new EPropertyMissing(cf_unitool_config,e);
	}
    }
	
    public IPropertylist getPropertylist()
    {
	return conf;
    }
    
    public IPropertylist getUnitoolPropertylist()
    {
	return unitool_conf;
    }
}
