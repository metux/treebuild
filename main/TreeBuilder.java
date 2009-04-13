
package org.de.metux.treebuild.main;

import java.io.File;
import org.de.metux.util.StrUtil;
import org.de.metux.util.FileOps;
import org.de.metux.util.Environment;
import org.de.metux.util.StrUtil;
import org.de.metux.propertylist.IPropertylist;
import org.de.metux.propertylist.EIllegalValue;
import org.de.metux.unitool.base.EParameterInvalid;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.EPIException;
import org.de.metux.treebuild.base.TreebuildConfig;
import org.de.metux.treebuild.base.EPIException;
import org.de.metux.treebuild.parser.LoadTree;
import org.de.metux.treebuild.nodes.PackageNode;


public class TreeBuilder
{
    public TreebuildConfig conf;

    public TreeBuilder()
	throws EParameterInvalid, EPIException
    {
	String fn = Environment.getenv("TREEBUILD_CONFIG");
	if (StrUtil.isEmpty(fn))
	    throw new EPIException("$TREEBUILD_CONFIG required");

	System.err.println("using default treebuild config file: "+fn);
	conf = new TreebuildConfig(new File(fn));
    }

    public TreeBuilder(File f)
	throws EPIException
    {
	System.err.println("using treebuild config file: "+f.toString());
	conf = new TreebuildConfig(f);
    }

    public PackageNode loadPackage(File f)
	throws EIllegalValue
    {
	IPropertylist unitool_properties = conf.getUnitoolPropertylist();
	
	PackageNode pkg = new LoadTree().loadPackage(conf,f);

	String target_system = unitool_properties.get_str("target-system");

	// FIXME !!!
	if (StrUtil.isEmpty(target_system))
	    pkg.setPropertyIfMissing("@@system-name",     target_system);
	else
	    pkg.setPropertyIfMissing("@@system-name", "$(@TARGET/name)");
	
	// FIXME !!!
	pkg.setPropertyIfMissing("@@system-root",     "/opt/xcompiler/$(@@system-name)/sys-root");
	pkg.setPropertyIfMissing("@@install-root",    "/tmp/INSTALL_ROOT/$(@@package-name)/ROOT");
	pkg.setPropertyIfMissing("@@current-workdir", 	FileOps.getcwd());
	pkg.setPropertyIfMissing("@@build-workdir",    "$(@@current-workdir)/_build_/");

	// is this really necessary ?
	pkg.loadPropertiesMissing(unitool_properties);

	// now load potential USEFLAGS 
	String env_treebuild_features = Environment.getenv("TREEBUILD_FEATURES");
	// fixme: should we probably just do ==null, so empty vars 
	// can explicitly be passed ?
	if (!StrUtil.isEmpty(env_treebuild_features))
	    pkg.setFeatureFlags(env_treebuild_features);

	return pkg;
    }
}
