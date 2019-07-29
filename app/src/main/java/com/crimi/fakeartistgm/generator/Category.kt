package com.crimi.fakeartistgm.generator

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.crimi.fakeartistgm.BR
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel
data class Category @ParcelConstructor constructor(val name: String, val defaultWeight: Double = 1.0) : BaseObservable() {
    @Bindable
    var weight = defaultWeight
        set(value) {
            field = value
            notifyPropertyChanged(BR.weight)
        }
}