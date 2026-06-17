package com.elow.app.ui.text

import com.elow.app.core.model.AppLanguage
import com.elow.app.core.model.ItemType
import java.util.Locale

class ElowStrings(val language: AppLanguage) {
    private val zh: Boolean = language == AppLanguage.CHINESE

    val navHome: String = if (zh) "首页" else "Home"
    val navMe: String = if (zh) "我的" else "Me"

    val onboardingLineOne: String = if (zh) "记录今天" else "Record today."
    val onboardingLineTwo: String = if (zh) "看见减少" else "See reduction."
    val onboardingLineThree: String = if (zh) "收集证明" else "Collect proof."
    val onboardingButton: String = if (zh) "开始使用" else "Let's get started"
    val introRecordTitle: String = if (zh) "记录" else "Record"
    val introRecordDetail: String = if (zh) "喝了什么" else "what you have"
    val introReductionTitle: String = if (zh) "看见" else "See your"
    val introReductionDetail: String = if (zh) "减少" else "reduction"
    val introCollectTitle: String = if (zh) "收集" else "Collect"
    val introCollectDetail: String = if (zh) "证明" else "your proof"

    val todayTitle: String = if (zh) "今天" else "Today"
    val sugarTitle: String = if (zh) "糖" else "Sugar"
    val alcoholTitle: String = if (zh) "酒精" else "Alcohol"
    val goodChoicesTitle: String = if (zh) "今天选择不错" else "Nice choices today!"
    val goodChoicesDetail: String = if (zh) "小步记录，慢慢改变。" else "Small steps, big shifts."
    val recentRecords: String = if (zh) "最近记录" else "Recent Records"
    val seeAll: String = if (zh) "查看全部" else "See all"
    val emptyRecords: String = if (zh) "还没有记录。点 + 添加第一条。" else "No records yet. Tap + to make the first one."

    val addRecord: String = if (zh) "添加记录" else "Add Record"
    val saveRecord: String = if (zh) "保存记录" else "Save Record"
    val addNote: String = if (zh) "添加备注（可选）" else "Add note (optional)"
    val amount: String = if (zh) "容量" else "Amount"
    val estimatedSugar: String = if (zh) "估算糖" else "Est. sugar"
    val about: String = if (zh) "约" else "About"

    val honorWallTitle: String = if (zh) "我的荣誉墙" else "My Honor Wall"
    val collectibles: String = if (zh) "收藏物" else "Collectibles"
    val badges: String = if (zh) "徽章" else "Badges"
    val firstRecord: String = if (zh) "首次记录" else "First Record"
    val sevenDays: String = if (zh) "7 天" else "7 Days"
    val thirtyDays: String = if (zh) "30 天" else "30 Days"
    val consistent: String = if (zh) "持续达成" else "Consistent"
    val sugarCubesSaved: String = if (zh) "少摄入糖块" else "Sugar Cubes Saved"
    val moneySaved: String = if (zh) "已节省金额" else "Money Saved"
    val cubes: String = if (zh) "块" else "cubes"
    val saved: String = if (zh) "已省" else "saved"
    val stageMemories: String = if (zh) "阶段回忆" else "Stage Memories"

    val settingsTitle: String = if (zh) "设置" else "Settings"
    val languageTitle: String = if (zh) "语言" else "Language"
    val languageDetail: String = if (zh) "切换应用显示语言" else "Change app display language"
    val english: String = "English"
    val chinese: String = "中文"

    fun sugarGoalDetail(goal: Int): String =
        if (zh) "目标 $goal g" else "of $goal g goal"

    fun alcoholGoalDetail(limit: Int): String =
        if (zh) "本周目标 $limit 次" else "of $limit occasions"

    fun sugarRecordDetail(grams: Int): String =
        if (zh) "糖 $grams g" else "Sugar $grams g"

    fun alcoholRecordDetail(drinks: Double): String =
        if (zh) "约 ${formatOneDecimal(drinks)} 杯" else "About ${formatOneDecimal(drinks)} drinks"

    fun drinkCountValue(drinks: Double): String =
        if (zh) "${formatOneDecimal(drinks)} 杯" else "${formatOneDecimal(drinks)} drinks"

    fun itemName(itemType: ItemType, fallback: String = itemType.name): String =
        if (zh) {
            when (itemType) {
                ItemType.COLA -> "可乐"
                ItemType.MILK_TEA -> "奶茶"
                ItemType.BEER -> "啤酒"
                ItemType.WINE -> "红酒"
            }
        } else {
            fallback
        }

    private fun formatOneDecimal(value: Double): String =
        String.format(Locale.US, "%.1f", value)
}

fun stringsFor(language: AppLanguage): ElowStrings = ElowStrings(language)
