package com.jpromi.spaceview.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.Calendar
import com.composables.icons.lucide.CircleCheck
import com.composables.icons.lucide.CircleX
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Paperclip
import com.composables.icons.lucide.Server
import com.composables.icons.lucide.Settings
import com.composables.icons.lucide.Shield
import com.jpromi.spaceview.AppTheme
import com.jpromi.spaceview.AppSettings
import com.jpromi.spaceview.controllers.LocalFullscreenController
import com.jpromi.spaceview.CalendarSettings
import com.jpromi.spaceview.elements.Expandable
import com.jpromi.spaceview.elements.LibrariesView
import com.jpromi.spaceview.elements.forms.SettingsButton
import com.jpromi.spaceview.elements.forms.SettingsDropdown
import com.jpromi.spaceview.elements.forms.SettingsNavigationButton
import com.jpromi.spaceview.elements.forms.SettingsSection
import com.jpromi.spaceview.elements.forms.SettingsSwitch
import com.jpromi.spaceview.elements.forms.SettingsTextInput
import com.jpromi.spaceview.elements.forms.TextInputRules
import com.jpromi.spaceview.enums.CalendarProviderENUM
import com.jpromi.spaceview.models.CalendarProvider
import com.jpromi.spaceview.models.Room
import com.jpromi.spaceview.network.ApiResult
import com.jpromi.spaceview.services.RoomService
import com.jpromi.spaceview.services.impl.DemoRoomService
import com.jpromi.spaceview.services.impl.RoomVoxRoomService
import kotlinx.coroutines.launch
import com.mikepenz.aboutlibraries.Libs
import spaceview.shared.generated.resources.Res
import kotlin.collections.getValue
import kotlin.collections.setValue
import kotlin.getValue
import kotlin.setValue

@Composable
fun ConfigurationScreen(
    onGoBack: () -> Unit,
    appSettings: AppSettings = remember { AppSettings() },
    calendarSettings: CalendarSettings = remember { CalendarSettings() },
) {
    var roomService by remember { mutableStateOf<RoomService>(DemoRoomService()) }
    val fullscreenController = LocalFullscreenController.current;

    var selectedProvider by remember {
        mutableStateOf(calendarSettings.calendarProvider ?: CalendarProviderENUM.DEMO)
    }

    // RoomVox
    var selectedRoomVoxServerUrl by remember {
        mutableStateOf(calendarSettings.roomVoxServerUrl)
    }
    var selectedRoomVoxToken by remember {
        mutableStateOf(calendarSettings.roomVoxAccessToken)
    }
    var isCheckingRoomVoxConnection by remember { mutableStateOf(false) }

    // UI
    var showAddEvent by remember { mutableStateOf(calendarSettings.showAddEvent) }
    var showLogo by remember { mutableStateOf(calendarSettings.showLogo) }

    var fullscreen by remember { mutableStateOf(appSettings.fullscreen) }

    // Admin
    var adminPinActive by remember { mutableStateOf(appSettings.adminPin.isNotEmpty()) }
    var adminPin by remember { mutableStateOf(appSettings.adminPin) }
    val adminPinRules = remember {
        TextInputRules(
            regex = Regex("\\d{4}"),
            maxLength = 4,
            allowEmpty = false,
            errorMessage = "PIN muss 4 Ziffern lang sein",
        )
    }

    // tmp
    val coroutineScope = rememberCoroutineScope()
    var remoteServerConnection by remember { mutableStateOf(false) }
    var remoteServerConnectionMessage by remember { mutableStateOf<String?>(null) }

    var loadedRooms by remember { mutableStateOf<List<Room>>(emptyList()) }
    var selectedRoomId by remember { mutableStateOf(calendarSettings.selectedRoomId) }

    var isLoadingRooms by remember { mutableStateOf(false) }
    var loadRoomsMessage by remember { mutableStateOf<String?>(null) }

    fun initRoomService() {
        when (selectedProvider) {
            CalendarProviderENUM.ROOMVOX -> {
                roomService = RoomVoxRoomService()
            }

            else -> {
                roomService = DemoRoomService()
            }
        }
    }

    fun loadRooms() {
        roomService.configure(
            serverUrl = selectedRoomVoxServerUrl,
            accessToken = selectedRoomVoxToken,
        )

        coroutineScope.launch {
            isLoadingRooms = true
            loadRoomsMessage = null

            when (val result = roomService.getRooms()) {
                is ApiResult.Success -> {
                    loadedRooms = result.data
                    if (loadedRooms.none { it.id == selectedRoomId }) {
                        selectedRoomId = loadedRooms.firstOrNull()?.id.orEmpty()
                    }
                    loadRoomsMessage = "${loadedRooms.size} Räume geladen"
                }

                else -> {
                    loadedRooms = emptyList()
                    loadRoomsMessage = "Räume konnten nicht geladen werden"
                }
            }

            isLoadingRooms = false
        }
    }

    fun checkConnection() {
        roomService.configure(
            serverUrl = selectedRoomVoxServerUrl,
            accessToken = selectedRoomVoxToken,
        )

        coroutineScope.launch {
            roomService.configure(
                serverUrl = selectedRoomVoxServerUrl,
                accessToken = selectedRoomVoxToken,
            )

            isCheckingRoomVoxConnection = true
            remoteServerConnectionMessage = null
            remoteServerConnection = false

            loadedRooms = emptyList()
            loadRoomsMessage = null

            remoteServerConnectionMessage = when (
                roomService.checkCredentials()
            ) {
                is ApiResult.Success -> {
                    remoteServerConnection = true
                    loadRooms()
                    "Verbunden"
                }

                is ApiResult.Unauthorized -> "Token falsch"
                is ApiResult.NetworkError -> "Network error"
                is ApiResult.NotFound -> "Not found"
                is ApiResult.Forbidden -> "Forbidden"
                else -> "Unknown error"
            }

            isCheckingRoomVoxConnection = false
        }
    }

    fun save() {
        // validator
        if (!(adminPinRules.isValid(adminPin) || !adminPinActive)) {
            return
        }

        // Calendar Settings
        calendarSettings.calendarProvider = selectedProvider

        // RoomVox
        if (selectedProvider == CalendarProviderENUM.ROOMVOX) {
            calendarSettings.roomVoxServerUrl = selectedRoomVoxServerUrl
            calendarSettings.roomVoxAccessToken = selectedRoomVoxToken
        } else {
            calendarSettings.roomVoxServerUrl = ""
            calendarSettings.roomVoxAccessToken = ""
        }

        // set Room ID
        if (
            selectedProvider in listOf(
                CalendarProviderENUM.ROOMVOX,
                CalendarProviderENUM.DEMO,
            )
        ) {
            calendarSettings.selectedRoomId = selectedRoomId
        } else {
            calendarSettings.selectedRoomId = ""
        }

        calendarSettings.showAddEvent = showAddEvent;
        calendarSettings.showLogo = showLogo

        // Admin
        if (adminPinActive) {
            appSettings.adminPin = adminPin
        } else {
            appSettings.adminPin = ""
        }

        if (appSettings.fullscreen != fullscreen)
            fullscreenController?.setFullscreen(fullscreen);

        appSettings.fullscreen = fullscreen;

        // leave
        onGoBack()
    }

    // on open
    LaunchedEffect(Unit) {
        initRoomService()
        checkConnection()
    }

    val scrollState = rememberLazyListState()

    // Navigation Scrolling
    val providerSectionIndex = 0
    val calendarSectionIndex = 1
    val applicationSectionIndex = 2
    val adminSectionIndex = 3
    val licenseSectionIndex = 4
    val sectionIndices = remember {
        listOf(
            providerSectionIndex,
            calendarSectionIndex,
            applicationSectionIndex,
            adminSectionIndex,
        )
    }
    val activeSectionIndex by remember {
        derivedStateOf {
            val layoutInfo = scrollState.layoutInfo
            val viewportAnchor = layoutInfo.viewportStartOffset + 96
            val visibleSections = layoutInfo.visibleItemsInfo
                .filter { item -> item.index in sectionIndices }

            visibleSections
                .lastOrNull { item -> item.offset <= viewportAnchor }
                ?.index
                ?: visibleSections.firstOrNull()?.index
                ?: providerSectionIndex
        }
    }

    fun scrollToSection(index: Int) {
        coroutineScope.launch {
            scrollState.animateScrollToItem(index)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.background),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Sidebar
        Column {
            // Title
            Text(
                text = "Einstellungen",
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp),
                color = AppTheme.textColor.copy(alpha = .75f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )

            // Navigation
            LazyColumn(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SettingsNavigationButton(
                        text = "Provider",
                        icon = Lucide.Server,
                        isActive = activeSectionIndex == providerSectionIndex,
                        onClick = { scrollToSection(providerSectionIndex) },
                    )
                }
                item {
                    SettingsNavigationButton(
                        text = "Kalender",
                        icon = Lucide.Calendar,
                        isActive = activeSectionIndex == calendarSectionIndex,
                        onClick = { scrollToSection(calendarSectionIndex) },
                    )
                }
                item {
                    SettingsNavigationButton(
                        text = "Applikation",
                        icon = Lucide.Settings,
                        isActive = activeSectionIndex == applicationSectionIndex,
                        onClick = { scrollToSection(applicationSectionIndex) },
                    )
                }
                item {
                    SettingsNavigationButton(
                        text = "Admin",
                        icon = Lucide.Shield,
                        isActive = activeSectionIndex == adminSectionIndex,
                        onClick = { scrollToSection(adminSectionIndex) },
                    )
                }
                item {
                    SettingsNavigationButton(
                        text = "Licenses",
                        icon = Lucide.Paperclip,
                        isActive = activeSectionIndex == adminSectionIndex,
                        onClick = { scrollToSection(licenseSectionIndex) },
                    )
                }
            }
        }

        VerticalDivider(
            color = AppTheme.borderSettings,
        )

        // Settings
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            scrollState.scrollBy(-delta)
                        }
                    },
                ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Provider
            item {
                SettingsSection(
                    title = "Provider"
                ) {

                    val providers: List<CalendarProvider> = listOf(
                        CalendarProvider(
                            id = CalendarProviderENUM.DEMO,
                            name = "Demo"
                        ),
                        CalendarProvider(
                            id = CalendarProviderENUM.ROOMVOX,
                            name = "RoomVox"
                        ),
                    )

                    SettingsDropdown(
                        label = "Provider auswaehlen",
                        options = providers,
                        selectedOption = providers.find { it.id == selectedProvider },
                        optionText = { it.name },
                        onOptionSelected = { provider ->
                            remoteServerConnectionMessage = null
                            remoteServerConnection = false
                            loadedRooms = emptyList()
                            selectedProvider = provider.id
                            initRoomService()

                            if (selectedProvider == CalendarProviderENUM.DEMO) {
                                checkConnection()
                            }
                        }
                    )

                    when (selectedProvider) {
                        CalendarProviderENUM.ROOMVOX -> {
                            SettingsSection(
                                title = "RoomVox Provider",
                                transparentBackground = true
                            ) {

                                SettingsTextInput(
                                    label = "Server",
                                    value = selectedRoomVoxServerUrl,
                                    onValueChange = {
                                        selectedRoomVoxServerUrl = it
                                        remoteServerConnectionMessage = null
                                        remoteServerConnection = false
                                        loadedRooms = emptyList()
                                    },
                                    keyboardType = KeyboardType.Uri
                                )

                                SettingsTextInput(
                                    label = "Token",
                                    value = selectedRoomVoxToken,
                                    onValueChange = {
                                        selectedRoomVoxToken = it
                                        remoteServerConnectionMessage = null
                                        remoteServerConnection = false
                                        loadedRooms = emptyList()
                                    },
                                    keyboardType = KeyboardType.Text,
                                    isPassword = true,
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (remoteServerConnectionMessage != null) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            if (remoteServerConnection) {
                                                Icon(
                                                    imageVector = Lucide.CircleCheck,
                                                    contentDescription = null,
                                                    tint = AppTheme.textColorGreen,
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Lucide.CircleX,
                                                    contentDescription = null,
                                                    tint = AppTheme.textColorRed,
                                                )
                                            }


                                            Text(
                                                text = remoteServerConnectionMessage!!,
                                                color =
                                                    if (remoteServerConnection) {
                                                        AppTheme.textColorGreen
                                                    } else {
                                                        AppTheme.textColorRed
                                                    },
                                            )
                                        }
                                    } else {
                                        Spacer(
                                            modifier = Modifier
                                        )
                                    }

                                    SettingsButton(
                                        text = if (isCheckingRoomVoxConnection) {
                                            "Prüfe..."
                                        } else {
                                            "Prüfen"
                                        },
                                        enabled = !isCheckingRoomVoxConnection,
                                        onClick = { checkConnection() },
                                        modifier = Modifier.width(200.dp),
                                    )
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }

            // Calendar
            item {
                SettingsSection(
                    title = "Kalender"
                ) {

                    SettingsDropdown(
                        label = "Raum auswählen",
                        options = loadedRooms,
                        selectedOption = loadedRooms.find { it.id == selectedRoomId }
                            ?: loadedRooms.firstOrNull(),
                        optionText = { room -> room.name },
                        onOptionSelected = { room ->
                            selectedRoomId = room.id
                        }
                    )

                    // allow edit
                }
            }

            // Application
            item {
                SettingsSection(title = "Applikation") {
                    SettingsSwitch(
                        checked = fullscreen,
                        onCheckedChange = {
                            fullscreen = it
                        },
                        text = "Fullscreen",
                    )
                }
            }

            // Admin
            item {

                SettingsSection(title = "Admin") {
                    SettingsSwitch(
                        checked = adminPinActive,
                        onCheckedChange = {
                            adminPinActive = it
                        },
                        text = "Admin PIN",
                    )

                    if (adminPinActive) {
                        SettingsTextInput(
                            label = "Admin PIN",
                            value = adminPin,
                            onValueChange = {
                                adminPin = it.filter(Char::isDigit).take(4)
                            },
                            keyboardType = KeyboardType.NumberPassword,
                            rules = adminPinRules,
                            isPassword = true,
                        )
                    }
                }

            }

            // Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                ) {
                    SettingsButton(
                        text = "Abbrechen",
                        onClick = { onGoBack() },
                        modifier = Modifier.width(200.dp),
                    )

                    SettingsButton(
                        text = "Speichern",
                        isPrimary = true,
                        enabled = remoteServerConnection && (!adminPinActive || adminPinRules.isValid(adminPin)),
                        onClick = { save() },
                        modifier = Modifier.width(200.dp),
                    )
                }
            }

            item {
                SettingsSection(title = "Licenses") {
                    LibrariesView()
                }
            }
        }
    }
}
