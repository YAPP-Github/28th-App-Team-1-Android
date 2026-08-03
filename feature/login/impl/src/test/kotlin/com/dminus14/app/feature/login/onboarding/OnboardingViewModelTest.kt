package com.dminus14.app.feature.login.onboarding

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    @Test
    fun `초기 상태는 Naming 단계이고 계속하기 버튼이 비활성화된다`() {
        val viewModel = createViewModel()

        assertEquals(OnboardingStep.Naming, viewModel.state.value.step)
        assertEquals("", viewModel.state.value.name)
        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `초기 상태에서 직군과 연차 옵션 기본값이 채워져 있다`() {
        val viewModel = createViewModel()

        assertEquals(DefaultJobs, viewModel.state.value.jobs)
        assertEquals(DefaultExperienceOptions, viewModel.state.value.experienceOptions)
    }

    @Test
    fun `Load 시 jobs와 experienceOptions가 기본값으로 설정된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.Load)

        assertEquals(DefaultJobs, viewModel.state.value.jobs)
        assertEquals(DefaultExperienceOptions, viewModel.state.value.experienceOptions)
    }

    @Test
    fun `Load 시 selectedExperienceIndex가 0으로 reset된다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.ExperienceChange(5))
        assertEquals(5, viewModel.state.value.selectedExperienceIndex)

        viewModel.onIntent(OnboardingIntent.Load)

        assertEquals(0, viewModel.state.value.selectedExperienceIndex)
    }

    @Test
    fun `Load 시 step과 name과 selectedJobIndex는 유지된다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.JobClick(2))

        viewModel.onIntent(OnboardingIntent.Load)

        assertEquals(OnboardingStep.JobSelection, viewModel.state.value.step)
        assertEquals("재원", viewModel.state.value.name)
        assertEquals(2, viewModel.state.value.selectedJobIndex)
    }

    @Test
    fun `Load 후 Naming 단계면 name 유효성에 따라 isContinueEnabled가 결정된다`() {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.NameChange("재원"))

        viewModel.onIntent(OnboardingIntent.Load)

        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `빈 문자열 입력 시 name은 비어 있고 버튼이 비활성화된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange(""))

        assertEquals("", viewModel.state.value.name)
        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `공백만 입력하면 필터되어 버튼이 비활성화된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("   "))

        assertEquals("", viewModel.state.value.name)
        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `한글 1글자 입력 시 name이 저장되고 버튼이 활성화된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("재"))

        assertEquals("재", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `영문 1글자 입력 시 name이 저장되고 버튼이 활성화된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("a"))

        assertEquals("a", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `한글과 영문 혼용 입력이 허용된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("재won"))

        assertEquals("재won", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `5글자까지 입력이 허용된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("아아아아아"))

        assertEquals("아아아아아", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `6글자 입력 시 5글자까지만 저장된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("아아아아아아"))

        assertEquals("아아아아아", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `숫자는 필터되어 저장되지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("재원1"))

        assertEquals("재원", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `특수문자는 필터되어 저장되지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("재@원!"))

        assertEquals("재원", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `허용되지 않는 문자만 입력하면 name이 비어 버튼이 비활성화된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("123!@"))

        assertEquals("", viewModel.state.value.name)
        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `유효한 이름 입력 후 삭제하면 버튼이 다시 비활성화된다`() {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.NameChange("재원"))
        assertTrue(viewModel.state.value.isContinueEnabled)

        viewModel.onIntent(OnboardingIntent.NameChange(""))

        assertEquals("", viewModel.state.value.name)
        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `자모 입력이 허용된다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.NameChange("ㄱㅏ"))

        assertEquals("ㄱㅏ", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `이름이 유효하지 않을 때 ContinueClick은 step을 바꾸지 않는다`() {
        val viewModel = createViewModel()

        viewModel.onIntent(OnboardingIntent.ContinueClick)

        assertEquals(OnboardingStep.Naming, viewModel.state.value.step)
    }

    @Test
    fun `이름이 유효할 때 ContinueClick하면 JobSelection으로 이동한다`() {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.NameChange("재원"))

        viewModel.onIntent(OnboardingIntent.ContinueClick)

        assertEquals(OnboardingStep.JobSelection, viewModel.state.value.step)
    }

    @Test
    fun `JobSelection 진입 후 이름이 유지된다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        assertEquals("재원", viewModel.state.value.name)
    }

    @Test
    fun `JobSelection 진입 직후 isContinueEnabled는 false다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `Naming에서 PreviousClick하면 CloseRequested Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(OnboardingIntent.PreviousClick)

            assertEquals(OnboardingEffect.CloseRequested, effect.await())
        }

    @Test
    fun `Naming에서 CloseClick하면 CloseRequested Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(OnboardingIntent.CloseClick)

            assertEquals(OnboardingEffect.CloseRequested, effect.await())
        }

    @Test
    fun `직군 미선택 시 isContinueEnabled는 false다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        assertFalse(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `직군 선택 시 isContinueEnabled가 true가 된다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.JobClick(2))

        assertEquals(2, viewModel.state.value.selectedJobIndex)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `직군 선택 후 다른 직군으로 변경할 수 있다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.JobClick(2))

        viewModel.onIntent(OnboardingIntent.JobClick(4))

        assertEquals(4, viewModel.state.value.selectedJobIndex)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `직군 미선택 상태에서 ContinueClick은 step을 바꾸지 않는다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.ContinueClick)

        assertEquals(OnboardingStep.JobSelection, viewModel.state.value.step)
    }

    @Test
    fun `직군 선택 후 ContinueClick하면 ExperienceSelection으로 이동한다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.JobClick(2))

        viewModel.onIntent(OnboardingIntent.ContinueClick)

        assertEquals(OnboardingStep.ExperienceSelection, viewModel.state.value.step)
    }

    @Test
    fun `ExperienceSelection 진입 직후 isContinueEnabled는 true다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)

        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `JobSelection에서 PreviousClick하면 Naming으로 돌아간다`() {
        val viewModel = createViewModel()
        advanceToJobSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.PreviousClick)

        assertEquals(OnboardingStep.Naming, viewModel.state.value.step)
        assertEquals("재원", viewModel.state.value.name)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `기본 selectedExperienceIndex 0이면 isContinueEnabled는 true다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)

        assertEquals(0, viewModel.state.value.selectedExperienceIndex)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `ExperienceChange로 연차 선택이 변경된다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.ExperienceChange(5))

        assertEquals(5, viewModel.state.value.selectedExperienceIndex)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `유효한 index면 ContinueClick 후 RegisterDone으로 이동한다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.ContinueClick)

        assertEquals(OnboardingStep.RegisterDone, viewModel.state.value.step)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `ExperienceSelection에서 PreviousClick하면 JobSelection으로 돌아간다`() {
        val viewModel = createViewModel()
        advanceToExperienceSelection(viewModel)

        viewModel.onIntent(OnboardingIntent.PreviousClick)

        assertEquals(OnboardingStep.JobSelection, viewModel.state.value.step)
        assertEquals(2, viewModel.state.value.selectedJobIndex)
        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `RegisterDone에서 isContinueEnabled는 항상 true다`() {
        val viewModel = createViewModel()
        advanceToRegisterDone(viewModel)

        assertTrue(viewModel.state.value.isContinueEnabled)
    }

    @Test
    fun `RegisterDone에서 ContinueClick하면 Completed Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            advanceToRegisterDone(viewModel)
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(OnboardingIntent.ContinueClick)

            assertEquals(OnboardingEffect.Completed, effect.await())
        }

    @Test
    fun `RegisterDone에서 PreviousClick하면 ExperienceSelection으로 돌아간다`() {
        val viewModel = createViewModel()
        advanceToRegisterDone(viewModel)

        viewModel.onIntent(OnboardingIntent.PreviousClick)

        assertEquals(OnboardingStep.ExperienceSelection, viewModel.state.value.step)
    }

    @Test
    fun `이름 입력부터 RegisterDone까지 step과 입력값이 순서대로 유지된다`() {
        val viewModel = createViewModel()
        viewModel.onIntent(OnboardingIntent.NameChange("재won"))
        viewModel.onIntent(OnboardingIntent.ContinueClick)
        viewModel.onIntent(OnboardingIntent.JobClick(3))
        viewModel.onIntent(OnboardingIntent.ContinueClick)
        viewModel.onIntent(OnboardingIntent.ExperienceChange(4))
        viewModel.onIntent(OnboardingIntent.ContinueClick)

        with(viewModel.state.value) {
            assertEquals(OnboardingStep.RegisterDone, step)
            assertEquals("재won", name)
            assertEquals(3, selectedJobIndex)
            assertEquals(4, selectedExperienceIndex)
        }
    }

    @Test
    fun `RegisterDone까지 간 뒤 Previous로 Experience까지 되돌리고 다시 진행할 수 있다`() {
        val viewModel = createViewModel()
        advanceToRegisterDone(viewModel)

        viewModel.onIntent(OnboardingIntent.PreviousClick)
        assertEquals(OnboardingStep.ExperienceSelection, viewModel.state.value.step)

        viewModel.onIntent(OnboardingIntent.ContinueClick)
        assertEquals(OnboardingStep.RegisterDone, viewModel.state.value.step)
    }

    @Test
    fun `JobSelection에서 CloseClick하면 CloseRequested Effect를 발행한다`() =
        runTest {
            val viewModel = createViewModel()
            advanceToJobSelection(viewModel)
            val effect = async { viewModel.effect.first() }

            viewModel.onIntent(OnboardingIntent.CloseClick)

            assertEquals(OnboardingEffect.CloseRequested, effect.await())
            assertEquals(OnboardingStep.JobSelection, viewModel.state.value.step)
        }

    @Test
    fun `ContinueClick이 disabled일 때 Effect가 발행되지 않는다`() =
        runTest {
            val viewModel = createViewModel()
            val receivedEffects = mutableListOf<OnboardingEffect>()
            val collector = launch { viewModel.effect.collect(receivedEffects::add) }

            viewModel.onIntent(OnboardingIntent.ContinueClick)
            advanceUntilIdle()
            collector.cancel()

            assertEquals(emptyList<OnboardingEffect>(), receivedEffects)
        }

    private fun createViewModel(): OnboardingViewModel = OnboardingViewModel()

    private fun advanceToJobSelection(viewModel: OnboardingViewModel) {
        viewModel.onIntent(OnboardingIntent.NameChange("재원"))
        viewModel.onIntent(OnboardingIntent.ContinueClick)
    }

    private fun advanceToExperienceSelection(viewModel: OnboardingViewModel) {
        advanceToJobSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.JobClick(2))
        viewModel.onIntent(OnboardingIntent.ContinueClick)
    }

    private fun advanceToRegisterDone(viewModel: OnboardingViewModel) {
        advanceToExperienceSelection(viewModel)
        viewModel.onIntent(OnboardingIntent.ContinueClick)
    }
}
