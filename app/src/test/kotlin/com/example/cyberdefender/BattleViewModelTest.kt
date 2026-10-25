package com.example.cyberdefender

import com.example.cyberdefender.model.ActionId
import com.example.cyberdefender.model.DefenseActionCatalog
import com.example.cyberdefender.model.OsiLayer
import com.example.cyberdefender.model.ThreatType
import com.example.cyberdefender.viewmodel.BattleViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BattleViewModelTest {

    private lateinit var viewModel: BattleViewModel

    @Before
    fun setup() {
        viewModel = BattleViewModel()
    }

    @Test
    fun initialStageIsL1Physical() {
        val state = viewModel.battleState.value
        assertEquals(ThreatType.L1_USB_WORM, state.threat.type)
        assertEquals(OsiLayer.L1_PHYSICAL, state.threat.layer)
        assertEquals(100, state.serverHp)
        assertEquals(4, state.ap)
        assertEquals(0, state.businessImpact)
        assertFalse(state.isThreatStunned)
    }

    @Test
    fun properLayerMitigationStunsThreatWithoutBusinessImpact() {
        // Stage 1 (L1 Physical) の適正対策は L1_LOCK_SERVER_ROOM
        val l1Action = DefenseActionCatalog.allActions.first { it.id == ActionId.L1_LOCK_SERVER_ROOM }
        val initialThreatHp = viewModel.battleState.value.threat.currentHp

        viewModel.executeAction(l1Action)

        val state = viewModel.battleState.value
        assertTrue("適正層対策でスタンすること", state.isThreatStunned)
        assertEquals("ピンポイント対策なので業務影響度は0%であること", 0, state.businessImpact)
        val expectedDamage = (l1Action.baseDamage * 1.6).toInt()
        assertEquals(initialThreatHp - expectedDamage, state.threat.currentHp)
    }

    @Test
    fun overkillDefenseInstantlyWinsButIncursMassivePenalty() {
        // 過剰防衛: 全社光回線引っこ抜き
        val overkillAction = DefenseActionCatalog.allActions.first { it.id == ActionId.OVERKILL_CUT_MAIN_CABLE }

        viewModel.executeAction(overkillAction)

        val state = viewModel.battleState.value
        assertEquals("敵HPは0になること", 0, state.threat.currentHp)
        assertEquals("業務影響度は100%になること", 100, state.businessImpact)
        assertTrue("過剰防衛フラグが立つこと", state.isOverkillUsed)
        assertTrue("評価ランクがCになること", state.resultRank.startsWith("C"))
    }
}
