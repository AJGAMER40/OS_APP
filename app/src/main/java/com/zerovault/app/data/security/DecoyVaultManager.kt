package com.zerovault.app.data.security

data class DecoyFile(
    val id: String,
    val name: String,
    val category: String,
    val size: String,
    val date: String,
    val content: String
)

/**
 * Manages the Decoy Safe for Plausible Deniability.
 * Provides realistic non-sensitive dummy documents when under coercion or duress.
 */
object DecoyVaultManager {

    val decoyFiles = listOf(
        DecoyFile(
            id = "decoy_1",
            name = "Weekly_Grocery_List.txt",
            category = "Personal Notes",
            size = "1.2 KB",
            date = "Yesterday, 18:42",
            content = "WEEKLY GROCERY ESSENTIALS:\n- 1L Oat Milk (Unsweetened)\n- Organic Eggs (1 Dozen)\n- Sourdough Bread\n- Hass Avocados (x4)\n- Baby Spinach & Kale\n- Greek Yogurt\n- Dark Roast Coffee Beans\n- Himalayan Pink Salt"
        ),
        DecoyFile(
            id = "decoy_2",
            name = "Apartment_Lease_Agreement_Summary.txt",
            category = "Legal Documents",
            size = "4.8 KB",
            date = "Nov 02, 2026",
            content = "RESIDENTIAL LEASE SUMMARY:\nProperty: Apartment 4B, Maple Residency\nTenant: Primary Occupant\nMonthly Rent: \$1,450.00 (Due 1st of month)\nSecurity Deposit: \$1,450.00 (Held in escrow)\nTerm: 12 Months (Exp: Oct 31, 2027)\nUtilities Included: Water, Trash, Sewage\nEmergency Contact: Building Management Office"
        ),
        DecoyFile(
            id = "decoy_3",
            name = "Hypertrophy_Workout_Split.txt",
            category = "Health & Fitness",
            size = "2.1 KB",
            date = "Oct 28, 2026",
            content = "4-DAY UPPER/LOWER STRENGTH ROUTINE:\n\nDAY 1: UPPER BODY FOCUS\n- Barbell Bench Press: 4x6-8\n- Chest-Supported T-Bar Row: 4x8-10\n- Incline Dumbbell Press: 3x10-12\n- Lateral Cable Raises: 4x15\n- Tricep Rope Extensions: 3x12\n\nDAY 2: LOWER BODY FOCUS\n- Barbell Back Squats: 4x6\n- Romanian Deadlifts: 3x8-10\n- Bulgarian Split Squats: 3x10/leg\n- Standing Calf Raises: 4x15"
        ),
        DecoyFile(
            id = "decoy_4",
            name = "Home_WiFi_Setup_Notes.txt",
            category = "Tech Reference",
            size = "890 B",
            date = "Sep 15, 2026",
            content = "HOME ROUTER CONFIGURATION:\nRouter Model: TP-Link Archer AX55\nGateway IP: 192.168.1.1\nPrimary SSID: Skyline_5G\nGuest Network: Skyline_Guest\nDNS Primary: 1.1.1.1 (Cloudflare)\nDNS Secondary: 8.8.8.8 (Google)\nFirmware Version: 1.2.4 Build 20260812"
        )
    )
}
