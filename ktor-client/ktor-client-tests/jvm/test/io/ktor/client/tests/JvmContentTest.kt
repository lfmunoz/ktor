/*
 * Copyright 2014-2019 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

package io.ktor.client.tests

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.tests.utils.*
import io.ktor.http.content.*
import kotlinx.coroutines.*
import org.junit.*
import java.io.*

class JvmContentTest : ClientLoader() {
    private val testSize = listOf(
        0, 1, // small edge cases
        4 * 1024 - 1, 4 * 1024, 4 * 1024 + 1, // ByteChannel edge cases
        16 * 1024 * 1024 // big
    )

    @Test
    fun inputStreamTest() = clientTests {
        test { client ->
            testSize.forEach { size ->
                val content = makeArray(size)

                val responseData = client.echo<InputStream, ByteArray>(content) { response ->
                    response.readBytes()
                }

                assertArrayEquals("Test fail with size: $size", content, responseData)
            }
        }
    }

    @Test
    fun testChannelWriterContentShouldNotTimeout(): Unit = clientTests {
        config {
            install(HttpTimeout) {
                socketTimeoutMillis = 10_000
                connectTimeoutMillis = 10_000
            }
        }
        test { client ->
            repeat(5000) {
                val chunk = (0 until 10000).map { it.toByte() }.toByteArray()

                client.post<String>("$TEST_SERVER/content/consume") {
                    body = ChannelWriterContent(
                        body = {
                            writeFully(chunk, 0, chunk.size)
                        },
                        contentType = null,
                    )
                }
            }
        }
    }

    private suspend inline fun <reified Response : Any, T> HttpClient.echo(
        body: Any, crossinline block: (Response) -> T
    ): T = post<HttpStatement>("$TEST_SERVER/content/echo") {
        this.body = body
    }.receive<Response, T> {
        block(it)
    }
}
