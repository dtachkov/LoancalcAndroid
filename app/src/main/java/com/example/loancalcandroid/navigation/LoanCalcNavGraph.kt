package com.example.loancalcandroid.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.bestdate.BestDateScreen
import com.example.loancalcandroid.ui.common.FeaturePlaceholderScreen
import com.example.loancalcandroid.ui.compare.CompareScreen
import com.example.loancalcandroid.ui.extras.ExtraCategory
import com.example.loancalcandroid.ui.extras.ExtraFormPrefill
import com.example.loancalcandroid.ui.extras.ExtraFormScreen
import com.example.loancalcandroid.ui.extras.ExtrasTabsScreen
import com.example.loancalcandroid.ui.forecast.ForecastScreen
import com.example.loancalcandroid.ui.help.ExtraTypesHelpScreen
import com.example.loancalcandroid.ui.help.ScheduleHelpScreen
import com.example.loancalcandroid.ui.help.WebViewScreen
import com.example.loancalcandroid.ui.settings.SettingsScreen
import com.example.loancalcandroid.ui.home.HomeScreen
import com.example.loancalcandroid.ui.home.HomeViewModel
import com.example.loancalcandroid.ui.loan.LoanEditorScreen
import com.example.loancalcandroid.billing.navigateToPurchase
import com.example.loancalcandroid.billing.navigateWithLicenseCheck
import com.example.loancalcandroid.ui.offers.OfferDetailScreen
import com.example.loancalcandroid.ui.purchase.PurchaseScreen
import com.example.loancalcandroid.ui.offers.OffersScreen
import com.example.loancalcandroid.ui.requisites.RequisitesScreen
import com.example.loancalcandroid.ui.schedule.SchedulePaymentDetailScreen
import com.example.loancalcandroid.ui.schedule.ScheduleScreen
import com.example.loancalcandroid.ui.sumbypayment.SumByPaymentScreen
import com.example.loancalcandroid.ui.tax.TaxScreen
import ru.kredit.calculator.data.model.ExtraType

@Composable
fun LoanCalcNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
    pendingScheduleLoanId: Long? = null,
    onPendingScheduleHandled: () -> Unit = {},
) {
    LaunchedEffect(pendingScheduleLoanId) {
        val loanId = pendingScheduleLoanId ?: return@LaunchedEffect
        homeViewModel.selectLoan(loanId)
        navController.navigate(Route.schedule(loanId)) {
            launchSingleTop = true
        }
        onPendingScheduleHandled()
    }

    NavHost(
        navController = navController,
        startDestination = Route.HOME,
        modifier = modifier,
    ) {
        composable(Route.HOME) {
            HomeScreen(
                viewModel = homeViewModel,
                onSettingsClick = { navController.navigate(Route.SETTINGS) },
                onAddLoanClick = { navController.navigate(Route.ADD_LOAN) },
                onEditLoanClick = { loanId -> navController.navigate(Route.editLoan(loanId)) },
                onEarlyPaymentClick = { loanId ->
                    navController.navigateWithLicenseCheck(
                        featureTitleRes = R.string.feature_extra_payments,
                        destinationRoute = Route.extraForm(loanId),
                    )
                },
                onScheduleClick = { loanId -> navController.navigate(Route.schedule(loanId)) },
                onRequisitesClick = { loanId -> navController.navigate(Route.requisites(loanId)) },
                onExtrasClick = { loanId -> navController.navigate(Route.extrasList(loanId)) },
                onForecastClick = { loanId -> navController.navigate(Route.forecast(loanId)) },
                onBestDateClick = { loanId -> navController.navigate(Route.bestDate(loanId)) },
                onTaxClick = { loanId -> navController.navigate(Route.tax(loanId)) },
                onCompareClick = { loanId -> navController.navigate(Route.compare(loanId)) },
                onSumByPaymentClick = { navController.navigate(Route.SUM_BY_PAYMENT) },
            )
        }

        composable(Route.SUM_BY_PAYMENT) {
            SumByPaymentScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOffersClick = { navController.navigate(Route.OFFERS) },
                onHelpClick = { navController.navigate(Route.helpTopic(Route.HELP_TOPIC_APP)) },
                onVoteClick = { navController.navigate(Route.helpTopic(Route.HELP_TOPIC_VOTE)) },
                onExtraTypesHelpClick = { navController.navigate(Route.helpTopic(Route.HELP_TOPIC_EXTRA_TYPES)) },
                onPremiumClick = {
                    navController.navigateToPurchase(R.string.feature_premium)
                },
            )
        }

        composable(
            route = Route.PURCHASE,
            arguments = listOf(navArgument(Route.ARG_FEATURE_TITLE) { type = NavType.StringType }),
        ) { backStackEntry ->
            val featureTitle = android.net.Uri.decode(
                backStackEntry.arguments?.getString(Route.ARG_FEATURE_TITLE).orEmpty(),
            )
            PurchaseScreen(
                featureTitle = featureTitle,
                onBack = { navController.popBackStack() },
                onPurchased = { navController.popBackStack() },
            )
        }

        composable(Route.ADD_LOAN) {
            LoanEditorScreen(
                loanId = null,
                onBack = { navController.popBackStack() },
                onSaved = { loanId ->
                    homeViewModel.selectLoan(loanId)
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = Route.EDIT_LOAN,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            LoanEditorScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = Route.SCHEDULE,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            var refreshSchedule by rememberSaveable { mutableStateOf(false) }
            val refreshFromExtra = backStackEntry.savedStateHandle
                .getStateFlow("refresh_schedule", false)
                .collectAsStateWithLifecycle()

            LaunchedEffect(refreshFromExtra.value) {
                if (refreshFromExtra.value) {
                    refreshSchedule = true
                    backStackEntry.savedStateHandle["refresh_schedule"] = false
                }
            }

            ScheduleScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onAddExtra = {
                    navController.navigateWithLicenseCheck(
                        featureTitleRes = R.string.feature_extra_payments,
                        destinationRoute = Route.extraForm(loanId),
                    )
                },
                onHelp = { navController.navigate(Route.helpTopic(Route.HELP_TOPIC_SCHEDULE)) },
                onPaymentClick = { listIndex, prevDateMillis ->
                    navController.navigate(Route.schedulePayment(loanId, listIndex, prevDateMillis))
                },
                refreshTrigger = refreshSchedule,
                onRefreshHandled = { refreshSchedule = false },
            )
        }

        composable(
            route = Route.SCHEDULE_PAYMENT,
            arguments = listOf(
                navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType },
                navArgument(Route.ARG_LIST_INDEX) { type = NavType.IntType },
                navArgument(Route.ARG_PREV_DATE_MILLIS) {
                    type = NavType.LongType
                    defaultValue = 0L
                },
            ),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            val listIndex = backStackEntry.arguments?.getInt(Route.ARG_LIST_INDEX) ?: return@composable
            val prevDateMillis = backStackEntry.arguments?.getLong(Route.ARG_PREV_DATE_MILLIS) ?: 0L
            SchedulePaymentDetailScreen(
                loanId = loanId,
                listIndex = listIndex,
                previousPaymentDateMillis = prevDateMillis,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Route.HELP,
            arguments = listOf(navArgument(Route.ARG_HELP_TOPIC) { type = NavType.StringType }),
        ) { backStackEntry ->
            when (backStackEntry.arguments?.getString(Route.ARG_HELP_TOPIC)) {
                Route.HELP_TOPIC_SCHEDULE -> ScheduleHelpScreen(onBack = { navController.popBackStack() })
                Route.HELP_TOPIC_EXTRA_TYPES -> ExtraTypesHelpScreen(onBack = { navController.popBackStack() })
                Route.HELP_TOPIC_APP -> WebViewScreen(
                    title = stringResource(R.string.label_help_app),
                    url = Route.URL_HELP_APP,
                    onBack = { navController.popBackStack() },
                )
                Route.HELP_TOPIC_VOTE -> WebViewScreen(
                    title = stringResource(R.string.label_new_features),
                    url = Route.URL_VOTE,
                    onBack = { navController.popBackStack() },
                )
                else -> FeaturePlaceholderScreen(
                    title = stringResource(R.string.help_title),
                    loanId = null,
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(
            route = Route.REQUISITES,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            RequisitesScreen(loanId = loanId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Route.EXTRAS_LIST,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            ExtrasTabsScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onAddExtra = { category ->
                    navController.navigateWithLicenseCheck(
                        featureTitleRes = R.string.feature_extra_payments,
                        destinationRoute = Route.extraForm(loanId, category),
                    )
                },
                onEditExtra = { extraId -> navController.navigate(Route.editExtra(loanId, extraId)) },
                onBestDateClick = { navController.navigate(Route.bestDate(loanId)) },
            )
        }

        composable(
            route = Route.EXTRA_FORM,
            arguments = listOf(
                navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType },
                navArgument(Route.ARG_EXTRA_CATEGORY) {
                    type = NavType.StringType
                    defaultValue = ExtraCategory.EARLY.name
                },
            ),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            if (loanId == 0L) return@composable
            val categoryName = backStackEntry.arguments?.getString(Route.ARG_EXTRA_CATEGORY)
                ?: ExtraCategory.EARLY.name
            val category = runCatching { ExtraCategory.valueOf(categoryName) }
                .getOrDefault(ExtraCategory.EARLY)
            val prefillHandle = navController.previousBackStackEntry?.savedStateHandle
            val prefillAmount = prefillHandle?.get<String>(Route.ARG_PREFILL_AMOUNT).orEmpty()
            val prefillDateMillis = prefillHandle?.get<Long>(Route.ARG_PREFILL_DATE_MILLIS) ?: 0L
            val prefillExtraTypeName = prefillHandle?.get<String>(Route.ARG_PREFILL_EXTRA_TYPE).orEmpty()
            val prefillExtraType = runCatching { ExtraType.valueOf(prefillExtraTypeName) }.getOrNull()
            prefillHandle?.remove<String>(Route.ARG_PREFILL_AMOUNT)
            prefillHandle?.remove<Long>(Route.ARG_PREFILL_DATE_MILLIS)
            prefillHandle?.remove<String>(Route.ARG_PREFILL_EXTRA_TYPE)
            ExtraFormScreen(
                loanId = loanId,
                extraId = null,
                category = category,
                prefill = ExtraFormPrefill(
                    amount = prefillAmount,
                    dateMillis = prefillDateMillis,
                    extraType = prefillExtraType,
                ),
                viewModelStoreOwner = backStackEntry,
                onBack = { navController.popBackStack() },
                onSaved = {
                    runCatching {
                        navController.getBackStackEntry(Route.schedule(loanId))
                            .savedStateHandle["refresh_schedule"] = true
                    }
                    navController.popBackStack()
                },
                onExtraTypesHelpClick = {
                    navController.navigate(Route.helpTopic(Route.HELP_TOPIC_EXTRA_TYPES))
                },
            )
        }

        composable(
            route = Route.EDIT_EXTRA,
            arguments = listOf(
                navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType },
                navArgument(Route.ARG_EXTRA_ID) { type = NavType.LongType },
            ),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            val extraId = backStackEntry.arguments?.getLong(Route.ARG_EXTRA_ID) ?: return@composable
            ExtraFormScreen(
                loanId = loanId,
                extraId = extraId,
                category = ExtraCategory.EARLY,
                viewModelStoreOwner = backStackEntry,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
                onExtraTypesHelpClick = {
                    navController.navigate(Route.helpTopic(Route.HELP_TOPIC_EXTRA_TYPES))
                },
            )
        }

        composable(
            route = Route.FORECAST,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            ForecastScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onScheduleClick = { navController.navigate(Route.schedule(loanId)) },
            )
        }

        composable(
            route = Route.BEST_DATE,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            BestDateScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onPurchaseRequired = {
                    navController.navigateToPurchase(R.string.feature_best_date)
                },
                onAddExtra = { amount, dateMillis, extraType ->
                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                        set(Route.ARG_PREFILL_AMOUNT, amount)
                        set(Route.ARG_PREFILL_DATE_MILLIS, dateMillis)
                        set(Route.ARG_PREFILL_EXTRA_TYPE, extraType)
                    }
                    navController.navigateWithLicenseCheck(
                        featureTitleRes = R.string.feature_extra_payments,
                        destinationRoute = Route.extraForm(
                            loanId = loanId,
                            category = ExtraCategory.EARLY,
                        ),
                    )
                },
            )
        }

        composable(
            route = Route.TAX,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            TaxScreen(loanId = loanId, onBack = { navController.popBackStack() })
        }

        composable(
            route = Route.COMPARE,
            arguments = listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val loanId = backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID) ?: return@composable
            CompareScreen(
                loanId = loanId,
                onBack = { navController.popBackStack() },
                onPurchaseRequired = {
                    navController.navigateToPurchase(R.string.feature_best_loan)
                },
            )
        }

        composable(Route.OFFERS) {
            OffersScreen(
                onBack = { navController.popBackStack() },
                onOfferClick = { offerId -> navController.navigate(Route.offerDetail(offerId)) },
            )
        }

        composable(
            route = Route.OFFER_DETAIL,
            arguments = listOf(navArgument(Route.ARG_OFFER_ID) { type = NavType.LongType }),
        ) { backStackEntry ->
            val offerId = backStackEntry.arguments?.getLong(Route.ARG_OFFER_ID) ?: return@composable
            OfferDetailScreen(
                offerId = offerId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
