package com.qurba.android.network.models

import android.content.Context
import com.qurba.android.R
import com.qurba.android.network.models.response_models.DeliveryAreaResponse
import java.io.Serializable

open class AddAddressModel : Serializable {
    var area: DeliveryAreaResponse? = null
    var city: DeliveryAreaResponse? = null
    var country: DeliveryAreaResponse? = null
    var nearestLandmark: String? = null
    var flat: String? = null
    var floor: String? = null
    var building: String? = null
    var street: String? = null
    var label: String? = null
    private var _id: String? = null
    var case = 0
    var branchedStreet: String? = null

    fun getLabel(context: Context): String? {
        if (label == null) return ""
        if (label.equals("home", ignoreCase = true)) return context.getString(R.string.home_lbl)
        return if (label.equals("work", ignoreCase = true)) context.getString(R.string.work_lbl) else label
    }

    fun getId(): String? {
        return _id
    }

    fun setId(_id: String?) {
        this._id = _id
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is AddAddressModel) {
            if (obj.area?._id == null) {
                if (obj.area?.location?.coordinates?.toString()
                        == area?.location?.coordinates?.toString()) return true
            } else return if (obj.getId() != null) obj.getId().equals(getId(), ignoreCase = true) else obj.area?._id.equals(area?._id, ignoreCase = true)
        }
        return false
    }
}