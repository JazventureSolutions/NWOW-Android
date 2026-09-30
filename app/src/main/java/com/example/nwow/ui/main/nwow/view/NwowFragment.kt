package com.example.nwow.ui.main.nwow.view

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nwow.R
import com.example.nwow.databinding.FragmentNwowBinding
import com.example.nwow.ui.main.MainActivity.Companion.changeActivityName
import com.example.nwow.ui.main.nwow.model.NwowData
import com.example.nwow.ui.main.nwow.model.SuggestedPriceRequest
import com.example.nwow.ui.main.nwow.view_model.NwowViewModel
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.ResponseHandling
import com.example.nwow.utils.Utility

class NwowFragment : Fragment() {

    private lateinit var nwowViewModel: NwowViewModel
    private lateinit var progressDialog: ProgressDialog
    lateinit var binding: FragmentNwowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository =
            (requireActivity().application as NwowApplication).nwowRepository

        nwowViewModel = NwowViewModel(repository)
        progressDialog = Utility.initProgressDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_nwow,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeActivityName("Nwow")

        binding.search.setOnClickListener {
            Utility.hideKeyboard(requireActivity())
            val model = binding.searchModel.text.toString().trim()
            if (model.isEmpty()) {
                Utility.showSnackBarOnRelative(binding.nwowContainer, "Please provide model no")
                return@setOnClickListener
            }
            progressDialog.show()
            nwowViewModel.getResponse(binding.searchModel.text.toString())
            getNwowObserver()
        }

        binding.suggestedPrice.setOnClickListener {
            val model = binding.searchModel.text.toString().trim()
            val price = binding.price.text.toString().trim()
            val percentage = binding.percentage.text.toString().trim()

            if (model.isEmpty() || price.isEmpty() || percentage.isEmpty()) {
                Utility.showSnackBarOnRelative(
                    binding.nwowContainer,
                    "Please provide model no, price and percentage"
                )
                return@setOnClickListener
            }

            progressDialog.show()
            val suggestedPriceRequest = SuggestedPriceRequest(model, price, percentage)
            nwowViewModel.getSuggestedPrice(suggestedPriceRequest)
            getSuggestedPriceObserver()
        }
    }

    private fun getNwowObserver() {
        nwowViewModel.nwowResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandling.Success -> {
                    progressDialog.dismiss()

                    it.data?.Data?.let { data ->
                        binding.noDataFound.visibility = GONE
                        binding.cardLayout.visibility = VISIBLE
                        binding.lastUpdatedDate.visibility = VISIBLE
                        binding.lastUpdatedDate.text = "Last Updated Date: ${data.LAST_UPDATE_DATE}"
                        setHeaderData(data)
                        setSummaryData(data)
                        setSalesData(data)
                        setPurchaseData(data)
                        setReturnData(data)
                        setAdjustmentData(data)
                    } ?: run {
                        binding.cardLayout.visibility = GONE
                        binding.lastUpdatedDate.visibility = GONE
                        binding.noDataFound.visibility = VISIBLE
                        clearHeaderData()
                    }

                    nwowViewModel.flushVariables() // reset after handling
                }

                is ResponseHandling.Error -> {
                    progressDialog.dismiss()
                    Utility.showSnackBarOnRelative(binding.nwowContainer, it.errorMessage)

                    nwowViewModel.flushVariables() // reset after handling
                }
            }
        }
    }

    private fun getSuggestedPriceObserver() {
        nwowViewModel.suggestedPriceResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandling.Success -> {
                    progressDialog.dismiss()
                    binding.ebayPrice.text = "E-Bay: $ ${it.data?.ebay_price}"
                    binding.amzPrice.text = "Amazon: $${it.data?.amazon_price}"

                    nwowViewModel.flushSuggestedPriceVariables() // reset after handling
                }

                is ResponseHandling.Error -> {
                    progressDialog.dismiss()
                    Utility.showSnackBarOnRelative(binding.nwowContainer, it.errorMessage)
                    nwowViewModel.flushSuggestedPriceVariables() // reset after handling
                }
            }
        }
    }

    private fun clearHeaderData(){
        binding.balance.text = "Balance: 0"
        binding.inHand.text = "In Hand: 0"
        binding.fba.text = "FBA: 0"
        binding.transit.text = "Transit: 0"
    }

    private fun setHeaderData(nwowData: NwowData) {
        binding.balance.text = "Balance: ${nwowData.BALANCE}"
        binding.inHand.text = "In Hand: ${nwowData.IN_HAND}"
        binding.fba.text = "FBA: ${nwowData.IN_FBA}"
        binding.transit.text = "Transit: ${nwowData.TRANSIT}"
    }

    private fun setSummaryData(nwowData: NwowData) {
        binding.txtTotalSales.text = nwowData.SALE.toString()
        binding.txtTotalPurchase.text = nwowData.PURCHASE.toString()
        binding.txtAdjust.text = nwowData.ADJUSTMENT.toString()
        binding.txtReturn.text = nwowData.RETURN.toString()
        binding.txtValue.text = "$${nwowData.VALUE}"
        binding.txtSummaryNetProfit.text = "$${nwowData.NET_PROFIT}"
        nwowData.TOTAL_NET_PROFIT.let { profit ->
            binding.txtSummaryNetProfit.text = "$$profit"
            val colorRes = if (profit < 0) R.color.red else R.color.green
            binding.txtSummaryNetProfit.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
        binding.txtAvgPrice.text = "$${nwowData.AVG_PRICE}"
        binding.txtLastPurPrice.text = "$${nwowData.LAST_PURCHASE}"
        binding.txtRateToRateEbay.text = "$${nwowData.RATE_2_RATE_EBAY}"
        binding.txtRateToRateAmz.text = "$${nwowData.RATE_2_RATE_AMZ}"
        binding.txtAvgProfit.text = "$${nwowData.AVG_PROFIT}"
        binding.txtMinPurPrice.text = "$${nwowData.MIN_PURCHASE_PRICE}"
        binding.txtMaxSoldPrice.text = "$${nwowData.MAX_SOLD_PRICE}"
        nwowData.HSODATA.takeIf { it.isNotBlank() }?.let {
            binding.txtHighestSold.text = "$it"
        }
        binding.txtLastSold.text = nwowData.LAST_SOLD
        binding.txtSoldDays.text = nwowData.SOLD_DAYS.toString()
    }

    private fun setSalesData(nwowData: NwowData) {
        binding.txtSaleId.text = nwowData.SALE_ID.toString()
        binding.txtSoldDate.text = nwowData.LAST_SOLD_DATE
        binding.txtSoldType.text = nwowData.SOLD_TYPE
        binding.txtSoldPrice.text = "$${nwowData.LAST_SOLD_PRICE}"
        binding.txtShippingPrice.text = "$${nwowData.SHIPPING_PRICE}"
        binding.txtShippingPaid.text = "$${nwowData.SHIPPING_PAID}"
        binding.txtFbaSales.text = nwowData.FBA_SALE_QTY.toString()
        binding.txtAdFees.text = "$${nwowData.SPONSORD_AD_FEE}"
        binding.txtPortalCost.text = "$${nwowData.PORTAL_CHARGES}"
        nwowData.NET_PROFIT.let { profit ->
            binding.txtNetProfit.text = "$$profit"
            val colorRes = if (profit < 0) R.color.red else R.color.green
            binding.txtNetProfit.setTextColor(
                ContextCompat.getColor(binding.root.context, colorRes)
            )
        }
        binding.txtAccount.text = nwowData.PAYPAL_NAME
    }

    private fun setPurchaseData(nwowData: NwowData) {
        binding.txtPurInv.text = nwowData.INVOICE_NO
        binding.txtPurchaseDate.text = nwowData.PURCHASE_DATE
        binding.txtPurchaseQty.text = nwowData.LAST_PURCHASE_QTY.toString()
        binding.txtLastPurchase.text = nwowData.LAST_PURCHASE.toString()
        binding.txtPurchaseFrom.text = nwowData.PURCHASE_FROM
    }

    private fun setReturnData(nwowData: NwowData) {
        binding.txtReturnSaleId.text = nwowData.LAST_RETURN_SALE_ID.toString()
        binding.txtReturnDate.text = nwowData.LAST_RETURN_DATE
        binding.txtReturnSaleDate.text = nwowData.LAST_RETURN_SALE_DATE
        binding.txtFbaReturn.text = nwowData.FBA_RETURN_QTY.toString()
    }

    private fun setAdjustmentData(nwowData: NwowData) {
        binding.txtAdjustmentId.text = nwowData.LAST_ADJ_ID.toString()
        binding.txtAdjustmentDate.text = nwowData.LAST_ADJ_DATE
        binding.txtAdjustmentQty.text = nwowData.LAST_ADJ_QTY.toString()
        binding.txtComments.text = nwowData.LAST_ADJ_COMMENTS
    }

    override fun onDestroy() {
        super.onDestroy()
        nwowViewModel.flushVariables()
    }
}