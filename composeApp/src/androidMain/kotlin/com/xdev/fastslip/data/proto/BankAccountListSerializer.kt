package com.xdev.fastslip.data.proto

import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object BankAccountListSerializer : Serializer<BankAccountProto.BankAccountList> {
    override val defaultValue: BankAccountProto.BankAccountList =
        BankAccountProto.BankAccountList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): BankAccountProto.BankAccountList {
        return try {
            BankAccountProto.BankAccountList.parseFrom(input)
        } catch (e: InvalidProtocolBufferException) {
            defaultValue
        }
    }

    override suspend fun writeTo(t: BankAccountProto.BankAccountList, output: OutputStream) =
        t.writeTo(output)
}
