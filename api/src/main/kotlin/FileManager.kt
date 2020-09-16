package com.juul.mcumgr

import com.juul.mcumgr.command.File
import com.juul.mcumgr.transfer.FileDownloader
import com.juul.mcumgr.transfer.FileUploader
import com.juul.mcumgr.util.mapResponse

data class FileWriteResponse(
    val offset: Int
)

data class FileReadResponse(
    val data: ByteArray,
    val offset: Int,
    val length: Int? = null
)

class FileManager(val transport: Transport) {

    suspend fun fileWrite(
        fileName: String,
        data: ByteArray,
        offset: Int,
        length: Int? = null
    ): CommandResult<FileWriteResponse> {
        val request = File.WriteRequest(fileName, data, offset, length)
        return transport.send(
            request,
            File.WriteResponse::class.java
        ).mapResponse { response ->
            FileWriteResponse(response.offset)
        }
    }

    suspend fun fileRead(fileName: String, offset: Int): CommandResult<FileReadResponse> {
        val request = File.ReadRequest(fileName, offset)
        return transport.send(
            request,
            File.ReadResponse::class.java
        ).mapResponse { response ->
            FileReadResponse(response.data, response.offset, response.length)
        }
    }

    fun fileUploader(
        data: ByteArray,
        fileName: String,
        windowCapacity: Int = 1
    ) = FileUploader(fileName, data, transport, windowCapacity)

    fun fileDownloader(
        fileName: String,
        windowCapacity: Int = 1
    ) = FileDownloader(fileName, transport, windowCapacity)
}
