package com.lmt.global.base.view.custom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.lmt.global.base.R
import java.util.Locale

internal data class CountryPhoneCode(
    val regionCode: String,
    val countryName: String,
    val phoneCode: String,
    @DrawableRes val flagResourceId: Int,
)

internal class CountryPhoneCodeAdapter(
    context: Context,
    private val allCountries: List<CountryPhoneCode>,
) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)
    private var countries = allCountries

    override fun getCount() = countries.size

    override fun getItem(position: Int) = countries[position]

    override fun getItemId(position: Int) = position.toLong()

    fun filter(query: CharSequence?) {
        val normalizedQuery = query.toString().trim().lowercase(Locale.getDefault())
        countries = if (normalizedQuery.isEmpty()) {
            allCountries
        } else {
            allCountries.filter { country ->
                country.countryName.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                    country.regionCode.lowercase(Locale.US).contains(normalizedQuery) ||
                    country.phoneCode.contains(normalizedQuery)
            }
        }
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_country_phone_code, parent, false)
            holder = ViewHolder(
                flag = view.findViewById(R.id.ivCountryFlag),
                name = view.findViewById(R.id.tvCountryName),
                phoneCode = view.findViewById(R.id.tvCountryPhoneCode),
            )
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        getItem(position).let { country ->
            holder.flag.setImageResource(country.flagResourceId)
            holder.name.text = country.countryName
            holder.phoneCode.text = country.phoneCode
        }
        return view
    }

    private data class ViewHolder(
        val flag: ImageView,
        val name: TextView,
        val phoneCode: TextView,
    )
}
