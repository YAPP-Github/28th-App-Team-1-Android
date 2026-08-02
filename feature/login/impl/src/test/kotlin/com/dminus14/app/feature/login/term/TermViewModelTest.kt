package com.dminus14.app.feature.login.term

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TermViewModelTest {
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
    fun `visibleTermDetailIndex가 null이면 visibleTermDetail은 null이다`() {
        val state = TermState(terms = SampleTerms)

        assertNull(state.visibleTermDetail)
    }

    @Test
    fun `유효한 visibleTermDetailIndex면 해당 term을 반환한다`() {
        val state =
            TermState(
                terms = SampleTerms,
                visibleTermDetailIndex = 1,
            )

        assertEquals(SampleTerms[1], state.visibleTermDetail)
    }

    @Test
    fun `범위 밖 visibleTermDetailIndex면 visibleTermDetail은 null이다`() {
        val state =
            TermState(
                terms = SampleTerms,
                visibleTermDetailIndex = SampleTerms.size,
            )

        assertNull(state.visibleTermDetail)
    }

    @Test
    fun `body가 비어 있으면 hasContent는 false다`() {
        assertFalse(SampleTerms[0].hasContent())
    }

    @Test
    fun `body가 공백만이면 hasContent는 false다`() {
        val term = SampleTerms[0].copy(body = "   ")

        assertFalse(term.hasContent())
    }

    @Test
    fun `body가 있으면 hasContent는 true다`() {
        assertTrue(SampleTerms[1].hasContent())
    }

    // endregion

    // region 초기 상태 & Load

    @Test
    fun `초기 상태는 terms가 비어 있고 canSubmit은 false다`() {
        val viewModel = createViewModel(terms = emptyList())

        assertEquals(emptyList<TermDetailContent>(), viewModel.state.value.terms)
        assertNull(viewModel.state.value.visibleTermDetailIndex)
        assertFalse(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.isAllChecked)
        assertFalse(viewModel.state.value.canSubmit)
    }

    @Test
    fun `Load Intent는 상태를 변경하지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.Load)

        assertEquals(SampleTerms, viewModel.state.value.terms)
    }

    // endregion

    // region 전체 동의

    @Test
    fun `아무것도 체크되지 않았을 때 ClickAllAgree하면 모든 term이 체크된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickAllAgree)

        assertTrue(
            viewModel.state.value.terms
                .all(TermDetailContent::isChecked),
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
                .none(TermDetailContent::isChecked),
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
                .all(TermDetailContent::isChecked),
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
    fun `본문이 있는 term에서 ClickViewTerm하면 visibleTermDetailIndex가 설정된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickViewTerm(1))

        assertEquals(1, viewModel.state.value.visibleTermDetailIndex)
        assertEquals(SampleTerms[1], viewModel.state.value.visibleTermDetail)
    }

    @Test
    fun `본문이 없는 term에서 ClickViewTerm하면 visibleTermDetailIndex가 변하지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickViewTerm(0))

        assertNull(viewModel.state.value.visibleTermDetailIndex)
    }

    @Test
    fun `공백만 있는 본문 term에서 ClickViewTerm하면 visibleTermDetailIndex가 변하지 않는다`() {
        val viewModel =
            createViewModel(
                terms =
                    SampleTerms.mapIndexed { index, term ->
                        if (index == 1) term.copy(body = "   ") else term
                    },
            )

        viewModel.onIntent(TermIntent.ClickViewTerm(1))

        assertNull(viewModel.state.value.visibleTermDetailIndex)
    }

    @Test
    fun `범위 밖 index로 ClickViewTerm하면 visibleTermDetailIndex가 변하지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(TermIntent.ClickViewTerm(SampleTerms.size))

        assertNull(viewModel.state.value.visibleTermDetailIndex)
    }

    @Test
    fun `DismissTermDetail하면 visibleTermDetailIndex가 null이 된다`() {
        val viewModel =
            createViewModel(
                visibleTermDetailIndex = 1,
            )

        viewModel.onIntent(TermIntent.DismissTermDetail)

        assertNull(viewModel.state.value.visibleTermDetailIndex)
    }

    @Test
    fun `BottomSheet가 열린 상태에서 term 체크 상태는 유지된다`() {
        val viewModel =
            createViewModel(
                visibleTermDetailIndex = 1,
            )

        viewModel.onIntent(TermIntent.ClickTerm(1))

        assertEquals(1, viewModel.state.value.visibleTermDetailIndex)
        assertTrue(
            viewModel.state.value.terms[1]
                .isChecked,
        )
    }

    @Test
    fun `다른 term의 보기를 클릭하면 visibleTermDetailIndex가 변경된다`() {
        val viewModel =
            createViewModel(
                visibleTermDetailIndex = 1,
            )

        viewModel.onIntent(TermIntent.ClickViewTerm(2))

        assertEquals(2, viewModel.state.value.visibleTermDetailIndex)
        assertEquals(SampleTerms[2], viewModel.state.value.visibleTermDetail)
    }

    // endregion

    // region 하단 버튼 & 닫기

    @Test
    fun `canSubmit이 false일 때 ClickAgree하면 Effect가 발행되지 않는다`() =
        runTest {
            val viewModel = createViewModel()
            val receivedEffects = collectEffects(viewModel)

            viewModel.onIntent(TermIntent.ClickAgree)
            advanceUntilIdle()

            assertEquals(emptyList<TermEffect>(), receivedEffects)
        }

    @Test
    fun `로딩 중이면 전체 체크여도 ClickAgree Effect가 발행되지 않는다`() =
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
    fun `ClickClose하면 Closed Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(TermIntent.ClickClose)

            assertEquals(TermEffect.Closed, effect.await())
        }

    // endregion

    private fun createViewModel(
        terms: List<TermDetailContent> = SampleTerms,
        isLoading: Boolean = false,
        visibleTermDetailIndex: Int? = null,
    ): TermViewModel =
        TermViewModel(
            TermState(
                terms = terms,
                isLoading = isLoading,
                visibleTermDetailIndex = visibleTermDetailIndex,
            ),
        )

    private fun TestScope.collectEffects(viewModel: TermViewModel): MutableList<TermEffect> {
        val receivedEffects = mutableListOf<TermEffect>()
        backgroundScope.launch { viewModel.effect.collect(receivedEffects::add) }
        return receivedEffects
    }

    private companion object {
        val SampleTerms =
            listOf(
                TermDetailContent(
                    title = "(필수) 만 14세 이상입니다.",
                    body = "",
                    isEssential = true,
                ),
                TermDetailContent(
                    title = "(필수) 서비스 이용약관 동의",
                    body = "합성 예시 본문",
                    isEssential = true,
                ),
                TermDetailContent(
                    title = "(필수) 개인정보 수집·이용 동의",
                    body = "합성 예시 본문",
                    isEssential = true,
                ),
                TermDetailContent(
                    title = "(필수) 면접 영상·음성·촬영과 저장 동의",
                    body = "합성 예시 본문",
                    isEssential = true,
                ),
                TermDetailContent(
                    title = "(필수) 개인정보 국외 이전 동의",
                    body = "합성 예시 본문",
                    isEssential = true,
                ),
            )
    }
}
