package vn.spamshield

import android.app.role.RoleManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import vn.spamshield.data.SpamRepository
import vn.spamshield.ui.SpamShieldApp
import vn.spamshield.ui.theme.SpamShieldTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: SpamRepository
    private var roleHeld by mutableStateOf(false)
    private var roleAvailable by mutableStateOf(false)
    private var refreshKey by mutableIntStateOf(0)

    private val roleLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        refreshRoleState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = SpamRepository(applicationContext)
        refreshRoleState()
        setContent {
            SpamShieldTheme {
                SpamShieldApp(
                    repository = repository,
                    roleHeld = roleHeld,
                    roleAvailable = roleAvailable,
                    refreshKey = refreshKey,
                    onRequestRole = ::requestScreeningRole,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshRoleState()
        refreshKey++
    }

    private fun refreshRoleState() {
        val roleManager = getSystemService(RoleManager::class.java)
        roleAvailable = roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true
        roleHeld = roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
    }

    private fun requestScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java) ?: return
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            roleLauncher.launch(roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING))
        }
    }
}
