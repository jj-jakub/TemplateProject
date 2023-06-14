package com.jj.templateproject.data.utils

import java.io.IOException

data class NetworkException(val code: Int, override val message: String) : IOException(message)
