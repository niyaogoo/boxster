package la.xiaoxiao.boxster.finder;

/**
 * 可刷新的远程服务发现者
 */
public interface RefreshableRemoteServiceFinder extends RemoteServiceFinder {

    default void onRefresh(RefreshHandler refreshHandler) {
        if (refreshHandler != null) {
            refreshHandler.afterRefresh();
        }
    }

    void setRefreshHandler(RefreshHandler handler);

    interface RefreshHandler {
        void afterRefresh();
    }

}
