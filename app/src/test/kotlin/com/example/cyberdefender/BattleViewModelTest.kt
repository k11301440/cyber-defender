package com.example.cyberdefender

import com.example.cyberdefender.model.*
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
        assertEquals(1, state.remainingActions)
        assertEquals(0, state.businessImpact)
        assertFalse(state.isThreatStunned)
    }

    @Test
    fun properLayerMitigationStunsThreatWithoutBusinessImpact() {
        // Stage 1 第1の攻撃 (USBワーム: Incident #1) の適正対策は L1_LOCK_SERVER_ROOM
        val l1Action = DefenseActionCatalog.allActions.first { it.id == ActionId.L1_LOCK_SERVER_ROOM }
        val initialThreatHp = viewModel.battleState.value.threat.currentHp
        val initialServerHp = viewModel.battleState.value.serverHp

        viewModel.executeAction(l1Action)

        val state = viewModel.battleState.value
        // 1アクションを消費したためendTurnが走り、スタンにより敵の反撃がスキップされて無傷でターン2へ進む
        assertEquals("敵の反撃がスタンで無力化されサーバHPが無傷であること", initialServerHp, state.serverHp)
        assertEquals("ターンが2に進んでいること", 2, state.turn)
        assertEquals("ピンポイント対策なので業務影響度は0%であること", 0, state.businessImpact)
        val expectedDamage = (l1Action.expectedDamage * 1.5).toInt()
        assertEquals(initialThreatHp - expectedDamage, state.threat.currentHp)
        assertTrue("スタンにより反撃不能になったログが存在すること", state.messages.any { it.message.contains("敵はスタン中のため反撃不能") })
    }

    @Test
    fun wrongLayerMitigationHalvesDamageWithoutStun() {
        // L1の攻撃に対して、L7のWAFを実行すると不適正で効果半減
        val l7Action = DefenseActionCatalog.allActions.first { it.id == ActionId.L7_WAF_PARAM_BIND }
        val initialThreatHp = viewModel.battleState.value.threat.currentHp

        viewModel.executeAction(l7Action)

        val state = viewModel.battleState.value
        assertFalse("不適正層なのでスタンしないこと", state.isThreatStunned)
        val expectedHalvedDamage = (l7Action.expectedDamage * 0.4).toInt()
        assertEquals(initialThreatHp - expectedHalvedDamage, state.threat.currentHp)
    }

    @Test
    fun tabooActionCausesInstantGameOver() {
        // 禁忌肢を実行すると即座にゲームオーバー
        val tabooAction = DefenseActionCatalog.allActions.first { it.id == ActionId.TABOO_RAW_SQL_STRING_CONCAT }

        viewModel.executeAction(tabooAction)

        val state = viewModel.battleState.value
        assertEquals("禁忌肢実行によりTABOO_GAME_OVERフェーズになること", BattlePhase.TABOO_GAME_OVER, state.phase)
        assertEquals("サーバHPが0になること", 0, state.serverHp)
        assertTrue("ランクがF-になること", state.resultRank.startsWith("F-"))
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

    @Test
    fun facilityUpgradeProgression() {
        // 設備投資の購入テスト
        val progressBefore = viewModel.gameProgress.value
        val initialSiemLevel = progressBefore.getLevel(FacilityType.SIEM_CONSOLE)
        assertEquals(0, initialSiemLevel)

        val success = viewModel.purchaseFacilityUpgrade(FacilityType.SIEM_CONSOLE)
        assertTrue("SIEMがLv1へ購入成功すること", success)

        val progressAfter = viewModel.gameProgress.value
        assertEquals(1, progressAfter.getLevel(FacilityType.SIEM_CONSOLE))
        assertTrue("SIEM所持フラグがtrueになること", progressAfter.isSiemOwned)
    }
}
