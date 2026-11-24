package com.example.util

/**
 * Pure-Kotlin ISO/IEC 18004 Byte-mode QR Code generator (EC Level L, Versions 1..6).
 * Generates genuinely scannable QR matrices for store links and contact info without external dependencies.
 */
object QrCodeGenerator {

    data class QrMatrix(
        val size: Int,
        val modules: BooleanArray
    ) {
        fun isDark(x: Int, y: Int): Boolean = modules[y * size + x]
    }

    private data class VersionSpec(
        val version: Int,
        val size: Int,
        val totalCodewords: Int,
        val ecCodewordsPerBlock: Int,
        val numBlocks: Int,
        val alignmentCenters: IntArray
    ) {
        val dataCodewords: Int get() = totalCodewords - ecCodewordsPerBlock * numBlocks
    }

    // Standard EC Level L parameters for QR Versions 1..6
    private val VERSION_SPECS = listOf(
        VersionSpec(1, 21, 26, 7, 1, intArrayOf()),
        VersionSpec(2, 25, 44, 10, 1, intArrayOf(6, 18)),
        VersionSpec(3, 29, 70, 15, 1, intArrayOf(6, 22)),
        VersionSpec(4, 33, 100, 20, 1, intArrayOf(6, 26)),
        VersionSpec(5, 37, 134, 26, 1, intArrayOf(6, 30)),
        VersionSpec(6, 41, 172, 18, 2, intArrayOf(6, 34))
    )

    fun generate(text: String): QrMatrix {
        val bytes = text.toByteArray(Charsets.UTF_8)
        val spec = VERSION_SPECS.firstOrNull { bytes.size + 2 <= it.dataCodewords }
            ?: VERSION_SPECS.last()
        val payloadBytes = if (bytes.size + 2 > spec.dataCodewords) {
            bytes.copyOf(spec.dataCodewords - 2)
        } else {
            bytes
        }

        val dataCodewords = encodeByteModeData(payloadBytes, spec.dataCodewords)
        val finalCodewords = interleaveWithEc(dataCodewords, spec)
        val bits = toBitList(finalCodewords, spec)

        val n = spec.size
        val modules = BooleanArray(n * n)
        val isFunction = BooleanArray(n * n)

        drawFunctionPatterns(n, spec, modules, isFunction)
        placeDataBits(n, bits, modules, isFunction)

        // Apply Mask 0 ((x + y) % 2 == 0) and write Format Info for EC Level L (01) + Mask 0 (000) -> 0x77C4
        applyMask(n, modules, isFunction, maskPattern = 0)
        drawFormatBits(n, modules, isFunction, formatBits = computeFormatBits(ecLevelBits = 1, mask = 0))

        return QrMatrix(n, modules)
    }

    private fun encodeByteModeData(data: ByteArray, capacityBytes: Int): IntArray {
        val bits = ArrayList<Int>(capacityBytes * 8)
        // Mode indicator: 0100 (Byte mode)
        appendBits(bits, 0b0100, 4)
        // Character count indicator (8 bits for Versions 1..9)
        appendBits(bits, data.size, 8)
        for (b in data) {
            appendBits(bits, b.toInt() and 0xFF, 8)
        }
        // Terminator (up to 4 zeros)
        val totalDataBits = capacityBytes * 8
        val terminatorLen = minOf(4, totalDataBits - bits.size)
        appendBits(bits, 0, terminatorLen)
        // Pad to byte boundary
        while (bits.size % 8 != 0) bits.add(0)
        // Pad codewords 0xEC, 0x11
        var padToggle = true
        while (bits.size < totalDataBits) {
            appendBits(bits, if (padToggle) 0xEC else 0x11, 8)
            padToggle = !padToggle
        }

        return IntArray(capacityBytes) { i ->
            var v = 0
            for (bit in 0 until 8) {
                v = (v shl 1) or bits[i * 8 + bit]
            }
            v
        }
    }

    private fun interleaveWithEc(data: IntArray, spec: VersionSpec): IntArray {
        val blockCount = spec.numBlocks
        val shortBlockLen = data.size / blockCount
        val longBlocks = data.size % blockCount
        val rsDivisor = reedSolomonDivisor(spec.ecCodewordsPerBlock)

        val dataBlocks = ArrayList<IntArray>(blockCount)
        val ecBlocks = ArrayList<IntArray>(blockCount)
        var offset = 0
        for (b in 0 until blockCount) {
            val len = shortBlockLen + if (b >= blockCount - longBlocks) 1 else 0
            val block = data.copyOfRange(offset, offset + len)
            offset += len
            dataBlocks.add(block)
            ecBlocks.add(reedSolomonRemainder(block, rsDivisor))
        }

        val result = ArrayList<Int>(spec.totalCodewords)
        val maxDataLen = dataBlocks.maxOf { it.size }
        for (i in 0 until maxDataLen) {
            for (block in dataBlocks) {
                if (i < block.size) result.add(block[i])
            }
        }
        for (i in 0 until spec.ecCodewordsPerBlock) {
            for (ec in ecBlocks) {
                result.add(ec[i])
            }
        }
        return result.toIntArray()
    }

    private fun toBitList(codewords: IntArray, spec: VersionSpec): BooleanArray {
        val remainderBits = when (spec.version) {
            1 -> 0
            in 2..6 -> 7
            else -> 0
        }
        val out = BooleanArray(codewords.size * 8 + remainderBits)
        var idx = 0
        for (cw in codewords) {
            for (bit in 7 downTo 0) {
                out[idx++] = ((cw ushr bit) and 1) != 0
            }
        }
        return out
    }

    private fun drawFunctionPatterns(
        n: Int,
        spec: VersionSpec,
        modules: BooleanArray,
        isFunction: BooleanArray
    ) {
        fun setFunc(x: Int, y: Int, dark: Boolean) {
            if (x in 0 until n && y in 0 until n) {
                modules[y * n + x] = dark
                isFunction[y * n + x] = true
            }
        }

        // Finder patterns + separators
        fun drawFinder(cx: Int, cy: Int) {
            for (dy in -4..4) {
                for (dx in -4..4) {
                    val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
                    val x = cx + dx
                    val y = cy + dy
                    if (x in 0 until n && y in 0 until n) {
                        val dark = dist != 2 && dist != 4
                        setFunc(x, y, dark)
                    }
                }
            }
        }
        drawFinder(3, 3)
        drawFinder(n - 4, 3)
        drawFinder(3, n - 4)

        // Alignment patterns
        val centers = spec.alignmentCenters
        for (cy in centers) {
            for (cx in centers) {
                if ((cx <= 8 && cy <= 8) || (cx >= n - 9 && cy <= 8) || (cx <= 8 && cy >= n - 9)) {
                    continue
                }
                for (dy in -2..2) {
                    for (dx in -2..2) {
                        val dist = maxOf(kotlin.math.abs(dx), kotlin.math.abs(dy))
                        setFunc(cx + dx, cy + dy, dist != 1)
                    }
                }
            }
        }

        // Timing patterns
        for (i in 8 until n - 8) {
            val dark = (i % 2 == 0)
            if (!isFunction[6 * n + i]) setFunc(i, 6, dark)
            if (!isFunction[i * n + 6]) setFunc(6, i, dark)
        }

        // Reserve format info modules
        for (i in 0..8) {
            if (i != 6) {
                isFunction[8 * n + i] = true
                isFunction[i * n + 8] = true
            }
        }
        for (i in 0..7) {
            isFunction[8 * n + (n - 1 - i)] = true
            isFunction[(n - 1 - i) * n + 8] = true
        }
        // Dark module
        setFunc(8, n - 8, true)
    }

    private fun placeDataBits(
        n: Int,
        bits: BooleanArray,
        modules: BooleanArray,
        isFunction: BooleanArray
    ) {
        var bitIdx = 0
        var upward = true
        var right = n - 1
        while (right >= 1) {
            if (right == 6) right--
            for (vert in 0 until n) {
                val y = if (upward) n - 1 - vert else vert
                for (dx in 0..1) {
                    val x = right - dx
                    if (!isFunction[y * n + x]) {
                        val dark = if (bitIdx < bits.size) bits[bitIdx++] else false
                        modules[y * n + x] = dark
                    }
                }
            }
            upward = !upward
            right -= 2
        }
    }

    private fun applyMask(n: Int, modules: BooleanArray, isFunction: BooleanArray, maskPattern: Int) {
        for (y in 0 until n) {
            for (x in 0 until n) {
                if (!isFunction[y * n + x]) {
                    val invert = when (maskPattern) {
                        0 -> (x + y) % 2 == 0
                        else -> false
                    }
                    if (invert) {
                        modules[y * n + x] = !modules[y * n + x]
                    }
                }
            }
        }
    }

    private fun computeFormatBits(ecLevelBits: Int, mask: Int): Int {
        val data = (ecLevelBits shl 3) or mask
        var rem = data
        for (i in 0 until 10) {
            rem = (rem shl 1) xor ((rem ushr 9) * 0x537)
        }
        return ((data shl 10) or rem) xor 0x5412
    }

    private fun drawFormatBits(
        n: Int,
        modules: BooleanArray,
        isFunction: BooleanArray,
        formatBits: Int
    ) {
        for (i in 0..5) {
            val dark = ((formatBits ushr i) and 1) != 0
            modules[8 * n + i] = dark
            isFunction[8 * n + i] = true
        }
        modules[8 * n + 7] = ((formatBits ushr 6) and 1) != 0
        modules[8 * n + 8] = ((formatBits ushr 7) and 1) != 0
        modules[7 * n + 8] = ((formatBits ushr 8) and 1) != 0
        for (i in 9..14) {
            val dark = ((formatBits ushr i) and 1) != 0
            modules[(14 - i) * n + 8] = dark
        }

        for (i in 0..7) {
            val dark = ((formatBits ushr i) and 1) != 0
            modules[(n - 1 - i) * n + 8] = dark
        }
        for (i in 8..14) {
            val dark = ((formatBits ushr i) and 1) != 0
            modules[8 * n + (n - 15 + i)] = dark
        }
        modules[(n - 8) * n + 8] = true
    }

    private fun appendBits(out: MutableList<Int>, value: Int, len: Int) {
        for (i in len - 1 downTo 0) {
            out.add((value ushr i) and 1)
        }
    }

    private fun reedSolomonDivisor(degree: Int): IntArray {
        val result = IntArray(degree)
        result[degree - 1] = 1
        var root = 1
        for (i in 0 until degree) {
            for (j in 0 until degree) {
                result[j] = gfMultiply(result[j], root)
                if (j + 1 < degree) {
                    result[j] = result[j] xor result[j + 1]
                }
            }
            root = gfMultiply(root, 0x02)
        }
        return result
    }

    private fun reedSolomonRemainder(data: IntArray, divisor: IntArray): IntArray {
        val result = IntArray(divisor.size)
        for (b in data) {
            val factor = b xor result[0]
            System.arraycopy(result, 1, result, 0, result.size - 1)
            result[result.size - 1] = 0
            for (i in divisor.indices) {
                result[i] = result[i] xor gfMultiply(divisor[i], factor)
            }
        }
        return result
    }

    private fun gfMultiply(a: Int, b: Int): Int {
        var x = a
        var y = b
        var z = 0
        for (i in 7 downTo 0) {
            z = (z shl 1) xor ((z ushr 7) * 0x11D)
            z = z xor (((y ushr i) and 1) * x)
        }
        return z
    }
}
