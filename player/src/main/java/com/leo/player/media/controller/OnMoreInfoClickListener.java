package com.leo.player.media.controller;

/**
 * Date:2018/4/13
 * Description:
 *
 * @author wangguang.
 */

public interface OnMoreInfoClickListener {

    int TYPE_DOWNLOAD = 0;
    int TYPE_SEASON = 1;
    int TYPE_DEFINITION = 2;

    void onInfoClick(int type);
}
