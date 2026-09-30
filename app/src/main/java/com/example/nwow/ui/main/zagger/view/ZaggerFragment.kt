package com.example.nwow.ui.main.zagger.view

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.nwow.R
import com.example.nwow.databinding.FragmentZaggerBinding
import com.example.nwow.ui.main.MainActivity.Companion.changeActivityName
import com.example.nwow.ui.main.zagger.model.ZaggerData
import com.example.nwow.ui.main.zagger.view_model.ZaggerViewModel
import com.example.nwow.utils.DialogController
import com.example.nwow.utils.NwowApplication
import com.example.nwow.utils.ResponseHandling
import com.example.nwow.utils.Utility
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

class ZaggerFragment : Fragment() {

    private lateinit var zaggerViewModel: ZaggerViewModel
    private lateinit var progressDialog: ProgressDialog
    lateinit var binding: FragmentZaggerBinding
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository =
            (requireActivity().application as NwowApplication).zaggerRepository

        zaggerViewModel = ZaggerViewModel(repository)
        progressDialog = Utility.initProgressDialog(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_zagger,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeActivityName("Zagger")

        binding.search.setOnClickListener {
            Utility.hideKeyboard(requireActivity())
            progressDialog.show()
            zaggerViewModel.getResponse(binding.searchModel.text.toString())
            getZaggerObserver()
        }

        binding.placeOrder.setOnClickListener {
            progressDialog.show()
            zaggerViewModel.placeOrder(getStringBody(getJsonObject(binding.searchModel.text.toString()).toString())!!)
            placeOrderObserver()
        }
    }

    private fun getJsonObject(productNumber: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("product_number", productNumber)
        return jsonObject
    }

    private fun getStringBody(value: String): RequestBody? {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value)
    }

    private fun placeOrderObserver() {
        zaggerViewModel.placeOrderResponse.observe(requireActivity()) {
            when (it) {
                is ResponseHandling.Success -> {
                    progressDialog.dismiss()
                    zaggerViewModel.flushPlaceOrderVariables()
                    if (it.data!!.error!!.isEmpty())
                        showAlert(getString(R.string.order_placed), it.data.success!!)
                    else
                        showAlert("", it.data.error!![0]!!)
                }

                is ResponseHandling.Error -> {
                    zaggerViewModel.flushPlaceOrderVariables()
                    progressDialog.dismiss()
                    Utility.showSnackBar(
                        binding.zaggerContainer, it.errorMessage
                    )
                }
            }
        }
    }

    private fun showAlert(title: String, message: String) {
        alertDialog = DialogController.showDialogConfirmation(
            requireContext(),
            title,
            message,
            "",
            getString(R.string.dismiss),
            { })
        {
            alertDialog.dismiss()
        }
    }

    private fun getZaggerObserver() {
        zaggerViewModel.zaggerResponse.observe(requireActivity()) {
            when (it) {
                is ResponseHandling.Success -> {
                    progressDialog.dismiss()
                    zaggerViewModel.flushVariables()
                    if (it.data?.isEmpty()!!) {
                        binding.contentContainer.visibility = View.GONE
                        binding.placeOrder.visibility = View.GONE
                        Utility.showSnackBar(binding.zaggerContainer, "No Record Found!!")
                        zaggerViewModel.flushVariables()
                    } else
                        setData(it.data[0])
                }

                is ResponseHandling.Error -> {
                    zaggerViewModel.flushVariables()
                    binding.contentContainer.visibility = View.GONE
                    progressDialog.dismiss()
                    Utility.showSnackBar(
                        binding.zaggerContainer, it.errorMessage
                    )
                }
            }
        }
    }

    private fun setData(watchData: ZaggerData) {
        Picasso.get().load(watchData.media_url).into(binding.image)
        binding.title.text = watchData.title
        binding.desc.text = watchData.item_description
        binding.model.text = watchData.number
        binding.brand.text = watchData.brand
        binding.collection.text = watchData.collection
        binding.price.text = "$" + watchData.customer_price
        binding.color.text = watchData.dial_color
        binding.material.text = watchData.dial_material
        binding.bandColor.text = watchData.band_color
        binding.bandMaterial.text = watchData.band_material
        binding.caseTone.text = watchData.case_tone
        binding.caseDiameter.text = watchData.case_diameter
        binding.strapSize.text = watchData.strap_size
        binding.jewel.text = watchData.jewel
        binding.diamonds.text = watchData.no_of_diamonds
        binding.claspType.text = watchData.clasp_type
        binding.crownType.text = watchData.crown_type
        binding.waterResistant.text = watchData.water_resistant
        binding.movement.text = watchData.movement
        binding.origin.text = watchData.origin
        binding.packaging.text = watchData.special_packaging
        binding.upcCode.text = watchData.upc_code
        binding.stock.text = watchData.stock.toString()
        binding.contentContainer.visibility = View.VISIBLE
        binding.placeOrder.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        zaggerViewModel.flushVariables()
    }

}