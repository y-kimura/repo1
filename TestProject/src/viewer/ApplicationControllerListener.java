package viewer;

import viewer.model.Item;

/**
 * ApplicationController経由で発生したイベントを取得する.
 * @author jgb.dev
 */
public interface ApplicationControllerListener {
    public void selectedIndexChanged(Item item);
}
