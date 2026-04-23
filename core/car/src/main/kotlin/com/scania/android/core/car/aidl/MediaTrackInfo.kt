package com.scania.android.core.car.aidl

import android.os.Parcel
import android.os.Parcelable

/**
 * AIDL 跨进程传输的媒体曲目信息。
 *
 * 对应 aidl 文件：`core/car/src/main/aidl/com/scania/android/core/car/aidl/MediaTrackInfo.aidl`。
 */
data class MediaTrackInfo(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverUri: String?,
) : Parcelable {

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(artist)
        dest.writeString(album)
        dest.writeLong(durationMs)
        dest.writeString(coverUri)
    }

    companion object CREATOR : Parcelable.Creator<MediaTrackInfo> {
        override fun createFromParcel(source: Parcel): MediaTrackInfo = MediaTrackInfo(
            id = source.readString().orEmpty(),
            title = source.readString().orEmpty(),
            artist = source.readString().orEmpty(),
            album = source.readString().orEmpty(),
            durationMs = source.readLong(),
            coverUri = source.readString(),
        )

        override fun newArray(size: Int): Array<MediaTrackInfo?> = arrayOfNulls(size)
    }
}
