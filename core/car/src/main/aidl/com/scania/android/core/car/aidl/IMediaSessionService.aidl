package com.scania.android.core.car.aidl;

import com.scania.android.core.car.aidl.IMediaSessionCallback;
import com.scania.android.core.car.aidl.MediaTrackInfo;

/**
 * 车机媒体会话 AIDL：Launcher 通过本接口控制/订阅音乐、FM、播客等。
 *
 * 这里只声明接口，具体实现方由车机宿主提供。默认情况下 Launcher
 * 端会走 FakeMediaSessionDataSource 提供本地 demo 数据。
 */
interface IMediaSessionService {
    void registerCallback(IMediaSessionCallback callback);
    void unregisterCallback(IMediaSessionCallback callback);

    MediaTrackInfo getCurrentTrack();
    int getPlaybackState();

    void play();
    void pause();
    void next();
    void previous();
    void seekTo(long positionMs);
}
