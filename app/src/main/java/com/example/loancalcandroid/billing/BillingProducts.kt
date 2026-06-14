package com.example.loancalcandroid.billing

object BillingProducts {
    const val CONSOLE_APPLICATION_ID = "2666943"
    const val DEEPLINK_SCHEME = "loancalcscheme"

    const val SKU_LICENSE_FOREVER = "kk_forever_3000"
    const val SKU_LICENSE_MONTH = "kk_monthly_100"
    const val SKU_LICENSE_YEAR = "kk_yearly_1000"

    val productIds = listOf(
        SKU_LICENSE_FOREVER,
        SKU_LICENSE_MONTH,
        SKU_LICENSE_YEAR,
    )
}
