
package org.de.metux.treebuild.main;

import org.de.metux.util.Environment;
import org.de.metux.util.StrUtil;
import org.de.metux.unitool.base.*;
import org.de.metux.treebuild.base.*;
import org.de.metux.treebuild.parser.*;
import org.de.metux.treebuild.nodes.*;
import org.de.metux.propertylist.IPropertylist;
import org.de.metux.propertylist.EIllegalValue;
import org.de.metux.unitool.db.UnitoolConf;
import java.util.Properties;
import java.util.Enumeration;
import java.io.File;

public class PIBuild extends Command
{
    public TreeBuilder builder;
    
    public PIBuild(String[] args)
	throws EParameterInvalid, EPIException
    {
	super(args);
	String cf = get_str("config");
	if (StrUtil.isEmpty(cf))
	    builder = new TreeBuilder();
	else
	    builder = new TreeBuilder(new File(cf));
    }

    public boolean run() 
	throws EParameterMissing, EParameterInvalid, EInstallFailed, 
	    EIllegalValue,
	    EDependencyMissing
    {
	PackageNode pkg = builder.loadPackage(
	    new File(get_str_mandatory("recipe")));

	pkg.run_Configure();
	pkg.run_Autodep();
	pkg.run_Build();
	pkg.run_Install();

	return true;
    }
}
