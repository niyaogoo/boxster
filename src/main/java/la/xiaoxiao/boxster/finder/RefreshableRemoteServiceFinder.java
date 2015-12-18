package la.xiaoxiao.boxster.finder;

import java.util.List;

/**
 * 可刷新的远程服务发现者
 */
public interface RefreshableRemoteServiceFinder extends RemoteServiceFinder {

    default void onRefresh(List<RefreshHandler> refreshHandlers) {
        if (refreshHandlers != null) {
            refreshHandlers.forEach(RefreshableRemoteServiceFinder.RefreshHandler::afterRefresh);
        }
    }

    void addRefreshHandler(RefreshHandler handler);

    interface RefreshHandler {
        void afterRefresh();
    }

}
