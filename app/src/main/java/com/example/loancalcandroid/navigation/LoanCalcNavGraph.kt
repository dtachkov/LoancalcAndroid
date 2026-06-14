package com.example.loancalcandroid.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loancalcandroid.R
import com.example.loancalcandroid.ui.common.FeaturePlaceholderScreen
import com.example.loancalcandroid.ui.common.SettingsScreen
import com.example.loancalcandroid.ui.extras.ExtraCategory
import com.example.loancalcandroid.ui.extras.ExtraFormScreen
import com.example.loancalcandroid.ui.extras.ExtrasTabsScreen
import com.example.loancalcandroid.ui.forecast.ForecastScreen
import com.example.loancalcandroid.ui.home.HomeScreen
import com.example.loancalcandroid.ui.home.HomeViewModel
import com.example.loancalcandroid.ui.loan.LoanEditorScreen
import com.example.loancalcandroid.ui.requisites.RequisitesScreen
import com.example.loancalcandroid.ui.schedule.ScheduleScreen

@Composable
fun LoanCalcNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    homeViewModel: HomeViewModel = viewModel(),
) {
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
                onEarlyPaymentClick = { loanId -> navController.navigate(Route.extraForm(loanId)) },
                onScheduleClick = { loanId -> navController.navigate(Route.schedule(loanId)) },
                onRequisitesClick = { loanId -> navController.navigate(Route.requisites(loanId)) },
                onExtrasClick = { loanId -> navController.navigate(Route.extrasList(loanId)) },
                onForecastClick = { loanId -> navController.navigate(Route.forecast(loanId)) },
                onBestDateClick = { loanId -> navController.navigate(Route.bestDate(loanId)) },
                onTaxClick = { loanId -> navController.navigate(Route.tax(loanId)) },
                onCompareClick = { loanId -> navController.navigate(Route.compare(loanId)) },
            )
        }

        composable(Route.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
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
            ScheduleScreen(loanId = loanId, onBack = { navController.popBackStack() })
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
                onAddExtra = { category -> navController.navigate(Route.extraForm(loanId, category)) },
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
            val categoryName = backStackEntry.arguments?.getString(Route.ARG_EXTRA_CATEGORY)
                ?: ExtraCategory.EARLY.name
            val category = runCatching { ExtraCategory.valueOf(categoryName) }
                .getOrDefault(ExtraCategory.EARLY)
            ExtraFormScreen(
                loanId = loanId,
                extraId = null,
                category = category,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
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
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() },
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

        loanFeatureRoute(Route.BEST_DATE, R.string.menu_best_date, navController, withLoanId = true)
        loanFeatureRoute(Route.TAX, R.string.menu_tax, navController, withLoanId = true)
        loanFeatureRoute(Route.COMPARE, R.string.menu_compare, navController, withLoanId = true)
        loanFeatureRoute(Route.OFFERS, R.string.offers_screen, navController)
    }
}

private fun NavGraphBuilder.loanFeatureRoute(
    route: String,
    @StringRes titleRes: Int,
    navController: NavHostController,
    withLoanId: Boolean = false,
) {
    val arguments = if (withLoanId) {
        listOf(navArgument(Route.ARG_LOAN_ID) { type = NavType.LongType })
    } else {
        emptyList()
    }

    composable(route = route, arguments = arguments) { backStackEntry ->
        val loanId = if (withLoanId) {
            backStackEntry.arguments?.getLong(Route.ARG_LOAN_ID)
        } else {
            null
        }
        FeaturePlaceholderScreen(
            title = stringResource(titleRes),
            loanId = loanId,
            onBack = { navController.popBackStack() },
        )
    }
}
