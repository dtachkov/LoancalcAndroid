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
import com.example.loancalcandroid.ui.common.AllLoansScreen
import com.example.loancalcandroid.ui.common.FeaturePlaceholderScreen
import com.example.loancalcandroid.ui.common.SettingsScreen
import com.example.loancalcandroid.ui.home.HomeScreen
import com.example.loancalcandroid.ui.home.HomeViewModel

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
                onAllLoansClick = { navController.navigate(Route.ALL_LOANS) },
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

        composable(Route.ALL_LOANS) {
            AllLoansScreen(
                homeViewModel = homeViewModel,
                onBack = { navController.popBackStack() },
                onLoanClick = { loanId ->
                    homeViewModel.selectLoan(loanId)
                    navController.popBackStack()
                },
                onAddLoanClick = { navController.navigate(Route.ADD_LOAN) },
            )
        }

        loanFeatureRoute(Route.ADD_LOAN, R.string.add_loan, navController)
        loanFeatureRoute(Route.EDIT_LOAN, R.string.action_edit_loan, navController, withLoanId = true)
        loanFeatureRoute(Route.SCHEDULE, R.string.quick_schedule, navController, withLoanId = true)
        loanFeatureRoute(Route.REQUISITES, R.string.quick_requisites, navController, withLoanId = true)
        loanFeatureRoute(Route.EXTRAS_LIST, R.string.menu_extras_list, navController, withLoanId = true)
        loanFeatureRoute(Route.EXTRA_FORM, R.string.quick_early_payment, navController, withLoanId = true)
        loanFeatureRoute(Route.FORECAST, R.string.menu_forecast, navController, withLoanId = true)
        loanFeatureRoute(Route.BEST_DATE, R.string.menu_best_date, navController, withLoanId = true)
        loanFeatureRoute(Route.TAX, R.string.menu_tax, navController, withLoanId = true)
        loanFeatureRoute(Route.COMPARE, R.string.menu_compare, navController, withLoanId = true)
        loanFeatureRoute(Route.OFFERS, R.string.all_loans_screen, navController)
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
