package io.rid.stockscreenerapp.ui.screen.companyOverview

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.rid.stockscreenerapp.api.ApiResponse
import io.rid.stockscreenerapp.api.Repository
import io.rid.stockscreenerapp.data.CompanyOverview
import io.rid.stockscreenerapp.database.Dao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyOverviewViewModel @Inject constructor(
    private val repository: Repository,
    @ApplicationContext private val context: Context,
    private val dao: Dao
) : ViewModel() {

    private val _companyOverview = MutableStateFlow(CompanyOverview())
    val companyOverview: StateFlow<CompanyOverview> = _companyOverview.asStateFlow()

    fun getCompanyOverview(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when(val result = repository.getCompanyOverview(symbol)) {
                is ApiResponse.Success -> {

                }

                is ApiResponse.Err -> {

                }
            }
        }
    }

}