package com.wang.gvideo.migu.model


/**
 * Date:2018/6/11
 * Description:
 *
 * @author wangguang.
 */
class NewSearchListItem(val name: String,
                        val pID: String,
                        val pics: NewSearchListItem_Pics,
                        val extraData: NewSearchListItem_extraData,
                        val contentType: String,
                        val actor: String,
                        val way: Int) {
    companion object {
        fun changeToOld(item: NewSearchListItem): AppSearchListItem {
            val itemList = mutableListOf<AppSeasonItem>()
            if (item.extraData != null) {
                item.extraData.episodes.forEachIndexed { index, it ->
                    val season = AppSeasonItem("cont=$it;node=${item.pID};", "${item.name}$index", item.pics.lowResolutionH)
                    itemList.add(season)
                }
            }
            var reActor = item.actor
            if (reActor == null) {
                reActor = ""
            }

            var img = item.pics.highResolutionV
            if (img == null) {
                img = item.pics.lowResolutionV
                if (img == null) {
                    img = ""
                }
            }

            var imgH = item.pics.highResolutionH
            if (imgH == null) {
                imgH = item.pics.lowResolutionH
                if (imgH == null) {
                    imgH = ""
                }
            }

            var prop = item.contentType
            if (prop == null) {
                prop = ""
            }
            var type = 0
            if (item.way == 1) {
                if (item.extraData == null) {
                    type = 4
                } else {
                    type = 1
                }
            }
            return AppSearchListItem(item.name, reActor, img, imgH,
                    0, itemList, "$type", "con=${item.pID};0;0", prop, type)
        }
    }
}

class NewSearchListItem_Pics(val lowResolutionH: String,
                             val lowResolutionV: String,
                             val highResolutionH: String,
                             val highResolutionV: String)

class NewSearchListItem_extraData(val episodesTips: List<String>,
                                  val episodes: List<String>)