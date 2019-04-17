package com.yunchuan.tingyanwu.ad.util;

public class FileLoadEvent {

    long total;
    long bytesLoaded;

    public long getBytesLoaded() {
        return bytesLoaded;
    }

    public long getTotal() {
        return total;
    }

    public FileLoadEvent(long total, long bytesLoaded) {
        this.total = total;
        this.bytesLoaded = bytesLoaded;
    }
}
