package com.example.myapp.doctor_view

import android.os.Parcel
import android.os.Parcelable

class MyParcelableList : ArrayList<String>, Parcelable {
    constructor() : super()
    constructor(size: Int) : super(size)
    constructor(list: Collection<String>) : super(list)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(this)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MyParcelableList> {
        override fun createFromParcel(parcel: Parcel): MyParcelableList {
            return MyParcelableList(parcel.createStringArrayList() ?: emptyList())
        }

        override fun newArray(size: Int): Array<MyParcelableList?> {
            return arrayOfNulls(size)
        }
    }
}




