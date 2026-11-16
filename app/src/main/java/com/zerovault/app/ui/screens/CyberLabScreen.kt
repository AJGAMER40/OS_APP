package com.zerovault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.threat.CyberLabViewModel
import com.zerovault.app.ui.components.SanctuaryButton
import com.zerovault.app.ui.components.SanctuaryCard
import com.zerovault.app.ui.components.SanctuaryDestructiveButton
import com.zerovault.app.ui.components.SanctuaryOutlinedButton
import com.zerovault.app.ui.components.SanctuaryScaffold
import com.zerovault.app.ui.components.SanctuaryStatusChip
import com.zerovault.app.ui.components.SanctuaryStatusLevel
import com.zerovault.app.ui.theme.SanctuaryAmberLight
import com.zerovault.app.ui.theme.SanctuaryBorderSubtle
import com.zerovault.app.ui.theme.SanctuaryCrimsonLight
import com.zerovault.app.ui.theme.SanctuaryEmeraldContainer
import com.zerovault.app.ui.theme.SanctuaryEmeraldLight
import com.zerovault.app.ui.theme.SanctuaryEyebrowStyle
import com.zerovault.app.ui.theme.SanctuaryGold
import com.zerovault.app.ui.theme.SanctuaryGoldLight
import com.zerovault.app.ui.theme.SanctuaryMuted
import com.zerovault.app.ui.theme.SanctuaryPine
import com.zerovault.app.ui.theme.SanctuaryPineBorder
import com.zerovault.app.ui.theme.SanctuaryPineSurface
import com.zerovault.app.ui.theme.SanctuaryStone
import com.zerovault.app.ui.theme.SanctuaryTheme
import com.zerovault.app.ui.theme.SanctuaryTypography
import com.zerovault.app.ui.theme.SanctuaryWhite

/**
 * CyberLabScreen provides interactive penetration testing and forensic analysis tooling.
 *
 * Integrity & Technical Realism:
 * - Denser information layout for inspectable forensic data
 * - Distinguishes synthetic benchmark/simulation results from absolute real-world guarantees
 * - Clearly explains Android heap memory lifecycle: byte scrubbing vs JVM GC compaction
 * - Explains kernel probe heuristics without creating false certainty against low-level rootkits
 */
@Composable
fun CyberLabScreen(
    viewModel: CyberLabViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val heapState by viewModel.heapState.collectAsState()
    val benchmarkResult by viewModel.benchmarkResult.collectAsState()
    val isBenchmarking by viewModel.isBenchmarking.collectAsState()
    val tamperResult by viewModel.tamperResult.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    SanctuaryScaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lab Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "FORENSICS SUITE",
                        style = SanctuaryEyebrowStyle,
                        color = SanctuaryGoldLight,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Cybersecurity Lab",
                        style = SanctuaryTypography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = SanctuaryWhite
                    )
                }

                SanctuaryStatusChip(
                    label = "DIAGNOSTIC MODE",
                    statusLevel = SanctuaryStatusLevel.NEUTRAL
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // DEMO 1: RAM HEAP FORENSICS & MEMORY SANITIZATION
            // ==========================================
            SanctuaryCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = null,
                                tint = SanctuaryGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "1. Volatile Memory Sanitization",
                                style = SanctuaryTypography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = heapState.addressTag,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Live Heap Preview: \"${heapState.cleartextPreview}\"",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (heapState.isSanitized) SanctuaryEmeraldLight else SanctuaryCrimsonLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Real-time Hex Dump Window
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SanctuaryPine)
                            .border(1.dp, SanctuaryBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = heapState.hexDump,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (heapState.isSanitized) SanctuaryEmeraldLight else SanctuaryGoldLight,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Note: In-process byte wiping (Arrays.fill(0)) scrubs the active buffer. Due to JVM heap copying and compaction, unreferenced remnants may remain in garbage collector segments until full GC.",
                        style = SanctuaryTypography.bodySmall,
                        color = SanctuaryMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SanctuaryDestructiveButton(
                            text = "Sanitize Buffer (0x00)",
                            onClick = { viewModel.sanitizeHeapMemory() },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(40.dp)
                        )

                        SanctuaryOutlinedButton(
                            text = "Reallocate",
                            leadingIcon = Icons.Default.Refresh,
                            onClick = { viewModel.allocateSensitiveSecret() },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // DEMO 2: SILICON CRYPTO BENCHMARK
            // ==========================================
            SanctuaryCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = SanctuaryGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "2. Silicon AES-256-GCM Benchmark",
                                style = SanctuaryTypography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "2 MB Payload",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SanctuaryStone
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (isBenchmarking) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = SanctuaryGold, modifier = Modifier.size(26.dp))
                        }
                    } else if (benchmarkResult != null) {
                        val res = benchmarkResult!!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = String.format("%.1f MB/s", res.throughputMBs),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = SanctuaryEmeraldLight
                                )
                                Text("In-Memory Throughput", style = SanctuaryTypography.bodySmall, fontSize = 10.sp, color = SanctuaryMuted)
                            }

                            Column {
                                Text(
                                    text = "${res.encryptionDurationMs} ms",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SanctuaryWhite
                                )
                                Text("Encrypt Latency", style = SanctuaryTypography.bodySmall, fontSize = 10.sp, color = SanctuaryMuted)
                            }

                            Column {
                                Text(
                                    text = "${res.decryptionDurationMs} ms",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SanctuaryWhite
                                )
                                Text("Decrypt Latency", style = SanctuaryTypography.bodySmall, fontSize = 10.sp, color = SanctuaryMuted)
                            }
                        }
                    } else {
                        Text(
                            text = "Synthetic benchmark measuring in-memory hardware cryptographic execution. Real file I/O operations are constrained by flash storage bus throughput.",
                            style = SanctuaryTypography.bodySmall,
                            color = SanctuaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SanctuaryButton(
                        text = "Run Silicon Benchmark",
                        leadingIcon = Icons.Default.PlayArrow,
                        onClick = { viewModel.runBenchmark() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // DEMO 3: LINUX KERNEL INTEGRITY & TAMPER AUDIT
            // ==========================================
            SanctuaryCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = SanctuaryGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "3. Kernel Integrity & Anti-Tamper",
                                style = SanctuaryTypography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SanctuaryWhite,
                                fontSize = 13.sp
                            )
                        }

                        if (tamperResult != null) {
                            val score = tamperResult!!.threatScore
                            Text(
                                text = "Score: $score/100",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (score > 20) SanctuaryCrimsonLight else SanctuaryEmeraldLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (tamperResult != null) {
                        val res = tamperResult!!
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            TamperRow(
                                label = "PTrace Debugger Attached",
                                detail = "TracerPid: ${res.tracerPid} (/proc/self/status)",
                                isDanger = res.isPTraceDetected
                            )
                            TamperRow(
                                label = "Frida Dynamic Hooking",
                                detail = if (res.isFridaHookingDetected) "Injected Gum-JS libraries found" else "Zero memory hooking detected (/proc/self/maps)",
                                isDanger = res.isFridaHookingDetected
                            )
                            TamperRow(
                                label = "Root Privilege Escalation",
                                detail = if (res.isRootBinaryDetected) "SU binary present on system" else "System partition clean, sandboxed",
                                isDanger = res.isRootBinaryDetected
                            )
                            TamperRow(
                                label = "OS Firmware Build Tag",
                                detail = if (res.isTestKeysBuild) "Test-Keys Unofficial ROM" else "Official Google Release-Keys",
                                isDanger = res.isTestKeysBuild
                            )
                        }
                    } else {
                        Text(
                            text = "Heuristic user-space environment audit. Note: Advanced rootkits with kernel-level hooking (e.g. KernelSU / Shamiko) can hide artifacts from unprivileged processes.",
                            style = SanctuaryTypography.bodySmall,
                            color = SanctuaryMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SanctuaryOutlinedButton(
                        text = if (isScanning) "Probing..." else "Re-Probe Kernel State",
                        onClick = { viewModel.runKernelScan() },
                        enabled = !isScanning,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(84.dp)) // Space for bottom navigation
        }
    }
}

@Composable
private fun TamperRow(
    label: String,
    detail: String,
    isDanger: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SanctuaryPine)
            .border(1.dp, SanctuaryBorderSubtle, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = SanctuaryTypography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = SanctuaryWhite,
                fontSize = 11.sp
            )
            Text(
                text = detail,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = SanctuaryMuted
            )
        }

        Icon(
            imageVector = if (isDanger) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isDanger) SanctuaryCrimsonLight else SanctuaryEmeraldLight,
            modifier = Modifier.size(16.dp)
        )
    }
}

// =========================================================================
// PREVIEWS
// =========================================================================

@Preview(name = "CyberLabScreen - Diagnostic Suite", showBackground = true, backgroundColor = 0xFF0C1B17)
@Composable
private fun CyberLabScreenPreview() {
    SanctuaryTheme(darkTheme = true) {
        CyberLabScreen(
            viewModel = CyberLabViewModel()
        )
    }
}