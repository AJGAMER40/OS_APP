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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zerovault.app.threat.CyberLabViewModel
import com.zerovault.app.ui.theme.BorderSubtle
import com.zerovault.app.ui.theme.TextMuted
import com.zerovault.app.ui.theme.TextPrimary
import com.zerovault.app.ui.theme.TextSecondary
import com.zerovault.app.ui.theme.VaultBg
import com.zerovault.app.ui.theme.VaultCrimson
import com.zerovault.app.ui.theme.VaultCyan
import com.zerovault.app.ui.theme.VaultEmerald
import com.zerovault.app.ui.theme.VaultSurface
import com.zerovault.app.ui.theme.VaultSurfaceCard

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Lab Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(VaultCyan.copy(alpha = 0.15f))
                    .border(1.dp, VaultCyan, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Biotech,
                    contentDescription = null,
                    tint = VaultCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column {
                Text(
                    text = "CYBERSECURITY & FORENSICS LAB",
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Interactive Academic Penetration Suite",
                    fontSize = 12.sp,
                    color = VaultEmerald
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // DEMO 1: RAM HEAP SCRAPING & ZERO-FILL DEMO
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
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
                        tint = VaultCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "1. Cold-Boot Heap Forensics",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = heapState.addressTag,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = VaultCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Live Heap Buffer Payload: \"${heapState.cleartextPreview}\"",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = if (heapState.isSanitized) VaultEmerald else VaultCrimson
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Real-time Hex Dump Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(VaultSurface)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = heapState.hexDump,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if (heapState.isSanitized) VaultEmerald else VaultCyan,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.sanitizeHeapMemory() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VaultCrimson,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text("Sanitize RAM (0x00)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { viewModel.allocateSensitiveSecret() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VaultSurface,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text("Reallocate", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // DEMO 2: SILICON CRYPTO BENCHMARK
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
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
                        tint = VaultEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "2. Hardware AES-256 Benchmark",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Text(
                    text = "2MB Payload",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (isBenchmarking) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VaultEmerald, modifier = Modifier.size(28.dp))
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
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = VaultEmerald
                        )
                        Text("Hardware Throughput", fontSize = 10.sp, color = TextSecondary)
                    }

                    Column {
                        Text(
                            text = "${res.encryptionDurationMs} ms",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text("Encrypt Latency", fontSize = 10.sp, color = TextSecondary)
                    }

                    Column {
                        Text(
                            text = "${res.decryptionDurationMs} ms",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text("Decrypt Latency", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            } else {
                Text(
                    text = "Measures raw encryption throughput and latency using hardware-isolated Android KeyStore AES-GCM.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.runBenchmark() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VaultEmerald,
                    contentColor = VaultBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("Benchmark Silicon AES-256 Engine", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ==========================================
        // DEMO 3: LINUX KERNEL ANTI-TAMPER AUDIT
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceCard)
                .border(1.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .padding(16.dp)
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
                        tint = VaultCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "3. Kernel Anti-Tamper & PTrace",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                if (tamperResult != null) {
                    val score = tamperResult!!.threatScore
                    Text(
                        text = "Threat Score: $score/100",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (score > 20) VaultCrimson else VaultEmerald
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
                        label = "OS Firmware Tag",
                        detail = if (res.isTestKeysBuild) "Test-Keys Unofficial ROM" else "Official Google Release-Keys",
                        isDanger = res.isTestKeysBuild
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.runKernelScan() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = VaultCyan,
                    contentColor = VaultBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isScanning) {
                    CircularProgressIndicator(color = VaultBg, modifier = Modifier.size(16.dp))
                } else {
                    Text("Re-Probe Kernel State", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(72.dp)) // Space for bottom nav
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
            .background(VaultSurface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(detail, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
        }

        Icon(
            imageVector = if (isDanger) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (isDanger) VaultCrimson else VaultEmerald,
            modifier = Modifier.size(16.dp)
        )
    }
}
