
package org.de.metux.treebuild.base;

import java.util.Properties;

public interface IPackageNode
{
    public void addConfFeature(Properties f);
    public void addConfHeader(Properties h);
    public void addConfSubstitute(Properties s);
}
