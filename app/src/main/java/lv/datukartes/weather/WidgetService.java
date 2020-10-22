package lv.datukartes.weather;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class WidgetService extends RemoteViewsService
{

    @Override
    public WidgetRemoteViewsFactory onGetViewFactory(Intent intent)
    {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
