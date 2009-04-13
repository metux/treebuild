
package org.de.metux.treebuild.base;

import org.de.metux.unitool.base.PackageInfo;

public interface IImportNode extends INode
{
    public String[] getLibraryPathes();
    public String[] getIncludePathes();
    public PackageInfo[] getPackageInfo();
    
    public static final int type_PkgConfig      = 1;
    public static final int type_LibtoolArchive = 2;
    public static final int type_Explicit       = 3;
    
    public int getImportType();
}
