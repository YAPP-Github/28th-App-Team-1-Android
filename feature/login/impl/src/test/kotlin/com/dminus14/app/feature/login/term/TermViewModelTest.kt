package com.dminus14.app.feature.login.term

import com.dminus14.app.core.common.event.GlobalAppEvent
import com.dminus14.app.core.common.event.GlobalErrorHandler
import com.dminus14.app.core.permission.AppPermission
import com.dminus14.app.core.permission.PermissionManager
import com.dminus14.app.domain.exception.ConsentVersionMismatchException
import com.dminus14.app.domain.exception.NetworkUnavailableException
import com.dminus14.app.domain.exception.RequiredConsentMissingException
import com.dminus14.app.domain.exception.UserNotFoundException
import com.dminus14.app.domain.model.ConsentDocument
import com.dminus14.app.domain.model.ConsentItem
import com.dminus14.app.domain.model.ConsentItemCode
import com.dminus14.app.domain.model.ConsentPendingStatus
import com.dminus14.app.domain.model.ConsentSubmission
import com.dminus14.app.domain.model.PendingConsentList
import com.dminus14.app.domain.model.UserProfile
import com.dminus14.app.domain.model.UserProfileUpdate
import com.dminus14.app.domain.repository.ConsentRepository
import com.dminus14.app.domain.repository.UserRepository
import com.dminus14.app.domain.usecase.CheckUserProfileUseCase
import com.dminus14.app.domain.usecase.GetConsentDocumentUseCase
import com.dminus14.app.domain.usecase.GetPendingConsentListUseCase
import com.dminus14.app.domain.usecase.SubmitConsentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TermViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUpMainDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDownMainDispatcher() {
        Dispatchers.resetMain()
    }

    // region TermState computed property

    @Test
    fun `terms가 비어 있으면 isAllChecked는 false다`() {
        val state = TermState()

        assertFalse(state.isAllChecked)
    }

    @Test
    fun `모든 term이 체크되면 isAllChecked는 true다`() {
        val state =
            TermState(
                terms = SampleTerms.map { it.copy(isChecked = true) },
            )

        assertTrue(state.isAllChecked)
    }

    @Test
    fun `하나라도 체크되지 않으면 isAllChecked는 false다`() {
        val state =
            TermState(
                terms =
                    SampleTerms.mapIndexed { index, term ->
                        term.copy(isChecked = index != 1)
                    },
            )

        assertFalse(state.isAllChecked)
    }

    @Test
    fun `전체 체크이고 로딩이 아니면 canSubmit은 true다`() {
        val state =
            TermState(
                terms = SampleTerms.map { it.copy(isChecked = true) },
            )

        assertTrue(state.canSubmit)
    }

    @Test
    fun `전체 체크여도 로딩 중이면 canSubmit은 false다`() {
        val state =
            TermState(
                terms = SampleTerms.map { it.copy(isChecked = true) },
                isLoading = true,
            )

        assertFalse(state.canSubmit)
    }

    @Test
    fun `일부만 체크되면 canSubmit은 false다`() {
        val state = TermState(terms = SampleTerms)

        assertFalse(state.canSubmit)
    }

    @Test
    fun `선택 약관이 체크되지 않아도 필수 약관이 모두 체크되면 canSubmit은 true다`() {
        val terms =
            SampleTerms.mapIndexed { index, term ->
                if (index == SampleTerms.lastIndex) {
                    term.copy(isRequired = false, isChecked = false)
                } else {
                    term.copy(isChecked = true)
                }
            }
        val state = TermState(terms = terms)

        assertTrue(state.canSubmit)
    }

    @Test
    fun `필수 약관이 하나라도 체크되지 않으면 선택 약관을 체크해도 canSubmit은 false다`() {
        val terms =
            SampleTerms.mapIndexed { index, term ->
                when (index) {
                    0 -> term.copy(isChecked = false)
                    SampleTerms.lastIndex -> term.copy(isRequired = false, isChecked = true)
                    else -> term.copy(isChecked = true)
                }
            }
        val state = TermState(terms = terms)

        assertFalse(state.canSubmit)
    }

    // endregion

    // region 초기 상태 & Load

    @Test
    fun `초기 상태는 terms가 비어 있고 canSubmit은 false다`() {
        val viewModel = createViewModel(terms = emptyList())

        assertEquals(emptyList<ConsentItem>(), viewModel.state.value.terms)
        assertNull(viewModel.state.value.visibleTermDetail)
        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isAllChecked)
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `Load Intent는 pending 목록을 서버에서 조회해 terms에 반영한다`() =
        runTest {
            val fakePending =
                PendingConsentList(
                    status = ConsentPendingStatus.NOT_SUBMITTED,
                    items =
                        listOf(
                            consentItem(
                                code = ConsentItemCode.TERMS_OF_SERVICE,
                                label = "서비스 이용약관",
                                hasDocument = true,
                            ),
                        ),
                )
            val viewModel =
                createViewModel(
                    terms = emptyList(),
                    deps =
                        TermViewModelTestDeps(
                            consentRepository = FakeConsentRepository(pending = fakePending),
                        ),
                )

            viewModel.onIntent(TermIntent.Load)
            advanceUntilIdle()

            val terms = viewModel.state.value.terms
            assertEquals(1, terms.size)
            assertEquals("서비스 이용약관", terms[0].label)
            assertEquals("TERMS_OF_SERVICE", terms[0].rawCode)
            assertTrue(terms[0].hasDocument)
            assertFalse(viewModel.state.value.isLoading)
        }

    // endregion

    // region 전체 동의

    @Test
    fun `아무것도 체크되지 않았을 때 ClickAllAgree하면 모든 term이 체크된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickAllAgree)

        assertTrue(
            viewModel.state.value.terms
                .all(ConsentItem::isChecked),
        )
        assertTrue(viewModel.state.value.isAllChecked)
        assertTrue(viewModel.state.value.canSubmit)
    }

    @Test
    fun `모두 체크된 상태에서 ClickAllAgree하면 모든 term이 해제된다`() {
        val viewModel =
            createViewModel(
                terms = SampleTerms.map { it.copy(isChecked = true) },
            )

        viewModel.onIntent(TermIntent.ClickAllAgree)

        assertTrue(
            viewModel.state.value.terms
                .none(ConsentItem::isChecked),
        )
        assertFalse(viewModel.state.value.isAllChecked)
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `일부만 체크된 상태에서 ClickAllAgree하면 모두 체크된다`() {
        val viewModel =
            createViewModel(
                terms =
                    SampleTerms.mapIndexed { index, term ->
                        term.copy(isChecked = index == 0)
                    },
            )

        viewModel.onIntent(TermIntent.ClickAllAgree)

        assertTrue(
            viewModel.state.value.terms
                .all(ConsentItem::isChecked),
        )
        assertTrue(viewModel.state.value.isAllChecked)
    }

    @Test
    fun `전체 동의 후 하나를 해제하면 isAllChecked는 false다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickAllAgree)
        viewModel.onIntent(TermIntent.ClickTerm(2))

        assertFalse(viewModel.state.value.isAllChecked)
        assertFalse(viewModel.state.value.canSubmit)
    }

    // endregion

    // region 개별 약관 토글

    @Test
    fun `개별 term 클릭 시 해당 term만 체크된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickTerm(1))

        assertFalse(
            viewModel.state.value.terms[0]
                .isChecked,
        )
        assertTrue(
            viewModel.state.value.terms[1]
                .isChecked,
        )
        assertFalse(
            viewModel.state.value.terms[2]
                .isChecked,
        )
    }

    @Test
    fun `이미 체크된 term을 클릭하면 해제된다`() {
        val viewModel =
            createViewModel(
                terms = SampleTerms.map { it.copy(isChecked = true) },
            )

        viewModel.onIntent(TermIntent.ClickTerm(1))

        assertTrue(
            viewModel.state.value.terms[0]
                .isChecked,
        )
        assertFalse(
            viewModel.state.value.terms[1]
                .isChecked,
        )
        assertTrue(
            viewModel.state.value.terms[2]
                .isChecked,
        )
    }

    @Test
    fun `모든 term을 개별 클릭으로 체크하면 isAllChecked는 true다`() {
        val viewModel = createViewModel()

        SampleTerms.indices.forEach { index ->
            viewModel.onIntent(TermIntent.ClickTerm(index))
        }

        assertTrue(viewModel.state.value.isAllChecked)
        assertTrue(viewModel.state.value.canSubmit)
    }

    @Test
    fun `범위 밖 index로 ClickTerm하면 상태가 변하지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickTerm(SampleTerms.size))

        assertEquals(SampleTerms, viewModel.state.value.terms)
    }

    // endregion

    // region 약관 상세 Bottom Sheet

    @Test
    fun `문서가 있는 term에서 ClickViewTerm하면 서버 문서로 상세 시트를 연다`() =
        runTest {
            val document =
                ConsentDocument(
                    code = ConsentItemCode.TERMS_OF_SERVICE,
                    rawCode = "TERMS_OF_SERVICE",
                    title = "서비스 이용약관",
                    version = 1,
                    contentMarkdown = "약관 본문",
                )
            val viewModel =
                createViewModel(
                    deps =
                        TermViewModelTestDeps(
                            consentRepository = FakeConsentRepository(document = document),
                        ),
                )

            viewModel.onIntent(TermIntent.ClickViewTerm(1))
            advanceUntilIdle()

            assertEquals(
                "서비스 이용약관",
                viewModel.state.value.visibleTermDetail
                    ?.title,
            )
            assertEquals(
                "약관 본문",
                viewModel.state.value.visibleTermDetail
                    ?.content,
            )
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `문서가 없는 term에서 ClickViewTerm하면 상세 시트를 열지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickViewTerm(0))

        assertNull(viewModel.state.value.visibleTermDetail)
    }

    @Test
    fun `범위 밖 index로 ClickViewTerm하면 상세 시트를 열지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickViewTerm(SampleTerms.size))

        assertNull(viewModel.state.value.visibleTermDetail)
    }

    @Test
    fun `DismissTermDetail하면 상세 시트가 닫힌다`() {
        val viewModel =
            createViewModel(
                visibleTermDetail = TermDetailContent(title = "제목", content = "본문"),
            )

        viewModel.onIntent(TermIntent.DismissTermDetail)

        assertNull(viewModel.state.value.visibleTermDetail)
    }

    @Test
    fun `BottomSheet가 열린 상태에서 term 체크 상태는 유지된다`() {
        val viewModel =
            createViewModel(
                visibleTermDetail = TermDetailContent(title = "제목", content = "본문"),
            )

        viewModel.onIntent(TermIntent.ClickTerm(1))

        assertNotNull(viewModel.state.value.visibleTermDetail)
        assertTrue(
            viewModel.state.value.terms[1]
                .isChecked,
        )
    }

    // endregion

    // region 제출 (ClickAgree)

    @Test
    fun `canSubmit이 false일 때 ClickAgree하면 제출도 Effect도 없다`() =
        runTest {
            val viewModel = createViewModel()
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(emptyList<TermEffect>(), receivedEffects)
        }

    @Test
    fun `로딩 중이면 전체 체크여도 ClickAgree 제출이 일어나지 않는다`() =
        runTest {
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    isLoading = true,
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(emptyList<TermEffect>(), receivedEffects)
        }

    @Test
    fun `제출 성공 후 권한이 없으면 체크 상태를 담아 보내고 DeniedPerm으로 이동한다`() =
        runTest {
            val fake = FakeConsentRepository(submitResult = Result.success(Unit))
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps =
                        TermViewModelTestDeps(
                            consentRepository = fake,
                            permissionManager = FakePermissionManager(granted = false),
                        ),
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            val submitted = fake.submittedSubmission
            assertEquals(SampleTerms.size, submitted?.items?.size)
            assertTrue(submitted?.items?.all { it.agreed } == true)
            assertEquals("AGE_OVER_14", submitted?.items?.first()?.rawCode)
            assertEquals(1, submitted?.items?.first()?.version)
            assertEquals(listOf(TermEffect.DeniedPerm), receivedEffects)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `제출 성공 후 권한이 있고 프로필이 있으면 ExistProfile로 이동한다`() =
        runTest {
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps =
                        TermViewModelTestDeps(
                            consentRepository =
                                FakeConsentRepository(
                                    submitResult = Result.success(Unit),
                                ),
                            userRepository =
                                FakeUserRepository(
                                    profileResult = Result.success(userProfile()),
                                ),
                            permissionManager = FakePermissionManager(granted = true),
                        ),
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(listOf(TermEffect.ExistProfile), receivedEffects)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `제출 성공 후 권한이 있고 프로필이 없으면 NonExistProfile로 이동한다`() =
        runTest {
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps =
                        TermViewModelTestDeps(
                            consentRepository =
                                FakeConsentRepository(
                                    submitResult = Result.success(Unit),
                                ),
                            userRepository =
                                FakeUserRepository(
                                    profileResult =
                                        Result.failure(
                                            UserNotFoundException(errCode = "USER_NOT_FOUND"),
                                        ),
                                ),
                            permissionManager = FakePermissionManager(granted = true),
                        ),
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(listOf(TermEffect.NonExistProfile), receivedEffects)
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `제출 성공 후 프로필 조회가 네트워크 오류면 전역 오류로 위임한다`() =
        runTest {
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps =
                        TermViewModelTestDeps(
                            consentRepository =
                                FakeConsentRepository(
                                    submitResult = Result.success(Unit),
                                ),
                            userRepository =
                                FakeUserRepository(
                                    profileResult =
                                        Result.failure(
                                            NetworkUnavailableException(
                                                errCode = "NETWORK_UNAVAILABLE",
                                            ),
                                        ),
                                ),
                            permissionManager = FakePermissionManager(granted = true),
                        ),
                )
            val globalEvents = collectGlobalEvents()
            val termEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(listOf(GlobalAppEvent.ShowNetworkErrorAndExit), globalEvents)
            assertEquals(emptyList<TermEffect>(), termEffects)
        }

    @Test
    fun `제출이 입력 오류로 실패하면 ShowToast로 안내한다`() =
        runTest {
            val fake =
                FakeConsentRepository(
                    submitResult =
                        Result.failure(
                            RequiredConsentMissingException(
                                errCode = "REQUIRED_CONSENT_MISSING",
                                message = "필수 동의 항목이 누락됐어요.",
                            ),
                        ),
                )
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps = TermViewModelTestDeps(consentRepository = fake),
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(
                listOf(TermEffect.ShowToast("필수 동의 항목이 누락됐어요.")),
                receivedEffects,
            )
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `제출이 버전 불일치로 실패하면 ShowToast 후 pending을 재조회한다`() =
        runTest {
            val reloaded =
                PendingConsentList(
                    status = ConsentPendingStatus.STALE,
                    items =
                        listOf(
                            consentItem(
                                code = ConsentItemCode.TERMS_OF_SERVICE,
                                label = "서비스 이용약관",
                                hasDocument = true,
                            ),
                        ),
                )
            val fake =
                FakeConsentRepository(
                    pending = reloaded,
                    submitResult =
                        Result.failure(
                            ConsentVersionMismatchException(
                                errCode = "CONSENT_VERSION_MISMATCH",
                                message = "동의 항목 버전이 최신이 아니에요.",
                            ),
                        ),
                )
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps = TermViewModelTestDeps(consentRepository = fake),
                )
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(
                listOf(TermEffect.ShowToast("동의 항목 버전이 최신이 아니에요.")),
                receivedEffects,
            )
            assertEquals(1, viewModel.state.value.terms.size)
            assertEquals(
                "TERMS_OF_SERVICE",
                viewModel.state.value.terms
                    .first()
                    .rawCode,
            )
            assertFalse(viewModel.state.value.isLoading)
        }

    @Test
    fun `제출이 네트워크 오류로 실패하면 전역 오류 이벤트로 위임한다`() =
        runTest {
            val fake =
                FakeConsentRepository(
                    submitResult =
                        Result.failure(
                            NetworkUnavailableException(errCode = "NETWORK_UNAVAILABLE"),
                        ),
                )
            val viewModel =
                createViewModel(
                    terms = SampleTerms.map { it.copy(isChecked = true) },
                    deps = TermViewModelTestDeps(consentRepository = fake),
                )
            val globalEvents = collectGlobalEvents()
            val termEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(listOf(GlobalAppEvent.ShowNetworkErrorAndExit), globalEvents)
            assertEquals(emptyList<TermEffect>(), termEffects)
            assertFalse(viewModel.state.value.isLoading)
        }

    // endregion

    // region 닫기

    @Test
    fun `ClickClose하면 Closed Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(TermIntent.ClickClose)

            assertEquals(TermEffect.Closed, effect.await())
        }

    // endregion

    private data class TermViewModelTestDeps(
        val consentRepository: ConsentRepository = NoopConsentRepository,
        val userRepository: UserRepository = NoopUserRepository,
        val permissionManager: PermissionManager = FakePermissionManager(granted = false),
    )

    private fun createViewModel(
        terms: List<ConsentItem> = SampleTerms,
        isLoading: Boolean = false,
        visibleTermDetail: TermDetailContent? = null,
        deps: TermViewModelTestDeps = TermViewModelTestDeps(),
    ): TermViewModel =
        TermViewModel(
            GetPendingConsentListUseCase(deps.consentRepository),
            GetConsentDocumentUseCase(deps.consentRepository),
            SubmitConsentUseCase(deps.consentRepository),
            CheckUserProfileUseCase(deps.userRepository),
            deps.permissionManager,
            TermState(
                terms = terms,
                isLoading = isLoading,
                visibleTermDetail = visibleTermDetail,
            ),
        )

    /** 카메라·마이크 권한 허용 여부를 [granted]로 고정하는 fake. */
    private class FakePermissionManager(
        private val granted: Boolean,
    ) : PermissionManager {
        override fun isGranted(permission: AppPermission): Boolean = granted

        override fun shouldShowRationale(
            permission: AppPermission,
            shouldShowRequestPermissionRationale: (manifestPermission: String) -> Boolean,
        ): Boolean = false
    }

    /** 프로필 조회 결과만 스텁하는 fake. 나머지는 AssertionError. */
    private class FakeUserRepository(
        private val profileResult: Result<UserProfile>? = null,
    ) : UserRepository {
        override suspend fun getUserProfile(): UserProfile =
            profileResult?.getOrThrow()
                ?: throw AssertionError("이 테스트에서 프로필 조회 스텁이 없습니다.")

        override suspend fun updateUserProfile(update: UserProfileUpdate) =
            throw AssertionError("이 테스트에서 프로필 수정은 일어나면 안 됩니다.")

        override suspend fun withdraw() = throw AssertionError("이 테스트에서 탈퇴는 일어나면 안 됩니다.")
    }

    /** 프로필 조회 미행사 테스트용. 호출되면 AssertionError. */
    private object NoopUserRepository : UserRepository {
        override suspend fun getUserProfile(): UserProfile =
            throw AssertionError("이 테스트에서 프로필 조회는 일어나면 안 됩니다.")

        override suspend fun updateUserProfile(update: UserProfileUpdate) =
            throw AssertionError("이 테스트에서 프로필 수정은 일어나면 안 됩니다.")

        override suspend fun withdraw() = throw AssertionError("이 테스트에서 탈퇴는 일어나면 안 됩니다.")
    }

    /** Load/ClickViewTerm/제출 검증용 fake. 필요한 것만 스텁하고 나머지는 AssertionError. */
    private class FakeConsentRepository(
        private val pending: PendingConsentList? = null,
        private val document: ConsentDocument? = null,
        private val submitResult: Result<Unit>? = null,
    ) : ConsentRepository {
        var submittedSubmission: ConsentSubmission? = null
            private set

        override suspend fun getPendingConsentList(): PendingConsentList =
            pending ?: throw AssertionError("이 테스트에서 pending 조회 스텁이 없습니다.")

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocument = document ?: throw AssertionError("이 테스트에서 문서 조회 스텁이 없습니다.")

        override suspend fun submitConsent(submission: ConsentSubmission) {
            submittedSubmission = submission
            submitResult?.getOrThrow()
                ?: throw AssertionError("이 테스트에서 제출 스텁이 없습니다.")
        }
    }

    /** 조회·제출 미행사 테스트용. 호출되면 AssertionError. */
    private object NoopConsentRepository : ConsentRepository {
        override suspend fun getPendingConsentList(): PendingConsentList =
            throw AssertionError("이 테스트에서 pending 조회는 일어나면 안 됩니다.")

        override suspend fun getConsentDocument(
            rawCode: String,
            version: Int,
        ): ConsentDocument = throw AssertionError("이 테스트에서 문서 조회는 일어나면 안 됩니다.")

        override suspend fun submitConsent(submission: ConsentSubmission) =
            throw AssertionError("이 테스트에서 제출은 일어나면 안 됩니다.")
    }

    private fun TestScope.collectEffects(viewModel: TermViewModel): MutableList<TermEffect> {
        val receivedEffects = mutableListOf<TermEffect>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.effect.collect(receivedEffects::add)
        }
        return receivedEffects
    }

    private fun TestScope.collectGlobalEvents(): MutableList<GlobalAppEvent> {
        val receivedEvents = mutableListOf<GlobalAppEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            GlobalErrorHandler.events.collect(receivedEvents::add)
        }
        return receivedEvents
    }

    private companion object {
        fun userProfile(): UserProfile =
            UserProfile(
                name = "테스터",
                email = null,
                provider = null,
                jobRole = "BACKEND",
                jobRoleLabel = "백엔드",
                careerYears = 3,
                remainingTicketCount = 3,
            )

        fun consentItem(
            code: ConsentItemCode,
            label: String,
            isRequired: Boolean = true,
            hasDocument: Boolean = false,
            version: Int = 1,
        ): ConsentItem =
            ConsentItem(
                code = code,
                rawCode = code.name,
                label = label,
                version = version,
                isRequired = isRequired,
                hasDocument = hasDocument,
            )

        val SampleTerms =
            listOf(
                consentItem(
                    code = ConsentItemCode.AGE_OVER_14,
                    label = "만 14세 이상입니다.",
                    hasDocument = false,
                ),
                consentItem(
                    code = ConsentItemCode.TERMS_OF_SERVICE,
                    label = "서비스 이용약관 동의",
                    hasDocument = true,
                ),
                consentItem(
                    code = ConsentItemCode.PERSONAL_INFO_COLLECTION,
                    label = "개인정보 수집·이용 동의",
                    hasDocument = true,
                ),
                consentItem(
                    code = ConsentItemCode.INTERVIEW_RECORDING,
                    label = "면접 영상·음성·촬영과 저장 동의",
                    hasDocument = true,
                ),
                consentItem(
                    code = ConsentItemCode.OVERSEAS_TRANSFER,
                    label = "개인정보 국외 이전 동의",
                    hasDocument = true,
                ),
            )
    }
}
