package edu.xmu.hwb.filter;

/**
 * Created by Herrfe on 14-8-22.
 */
public class AuthorizeResult {
    /**
     * 身份验证是否成功
     */
    private boolean isAuthorized;
    /**
     * 身份ID
     */
    private Object id;

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public Object getID() {
        return id;
    }

    public void setAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
