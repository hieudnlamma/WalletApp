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
import java.text.Normalizer
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
        val normalizedQuery = query?.toString().orEmpty().toSearchKey()
        countries = if (normalizedQuery.isBlank()) {
            allCountries
        } else {
            allCountries.filter { country ->
                country.countryName.toSearchKey().contains(normalizedQuery) ||
                    country.regionCode.toSearchKey().contains(normalizedQuery) ||
                    country.phoneCode.toSearchKey().contains(normalizedQuery)
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

    private val combiningMarksRegex = "\\p{M}+".toRegex()

    private fun CharSequence.toSearchKey(): String {
        return Normalizer
            .normalize(toString(), Normalizer.Form.NFD)
            .replace(combiningMarksRegex, "")
            // Đ và đ không bị tách dấu bởi Normalizer nên phải xử lý riêng.
            .replace('Đ', 'D')
            .replace('đ', 'd')
            .lowercase(Locale.ROOT)
            .trim()
    }

    private data class ViewHolder(
        val flag: ImageView,
        val name: TextView,
        val phoneCode: TextView,
    )
}
