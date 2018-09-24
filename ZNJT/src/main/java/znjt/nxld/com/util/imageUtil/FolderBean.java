package znjt.nxld.com.util.imageUtil;

/**
 * Created by ThinkPad on 2018/9/13.
 */

public class FolderBean {
    private String dir;//路径
    private String firstImaPath;
    private String name;
    private int Count;

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int LastIndexOf = this.dir.lastIndexOf("/");
        this.name = this.dir.substring(LastIndexOf+1);
    }

    public String getFirstImaPath() {
        return firstImaPath;
    }

    public void setFirstImaPath(String firstImaPath) {
        this.firstImaPath = firstImaPath;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }
}
