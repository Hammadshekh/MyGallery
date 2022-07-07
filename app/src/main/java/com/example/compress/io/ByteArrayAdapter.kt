package com.example.compress.io

class ByteArrayAdapter(override val tag: String?) : ArrayAdapterInterface<ByteArray?> {
   override fun getArrayLength(array: ByteArray?): Int {
        return array!!.size
    }

    override fun newArray(length: Int): ByteArray {
        return ByteArray(length)
    }

    override val elementSizeInBytes: Int
        get() = 1
}
