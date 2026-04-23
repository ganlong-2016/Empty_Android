package com.scania.android.core.car.aidl;

import com.scania.android.core.car.aidl.MediaTrackInfo;

/**
 * 车机媒体会话回调：宿主系统（AIDL 服务端）把最新媒体信息推回客户端。
 */
oneway interface IMediaSessionCallback {
    void onTrackChanged(in MediaTrackInfo track);
    void onPlaybackStateChanged(int state, long positionMs);
}
