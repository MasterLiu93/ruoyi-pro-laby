<template>
  <Dialog v-model="dialogVisible" title="拣货波次详情" width="1400px">
    <el-descriptions :column="3" border v-loading="loading">
      <el-descriptions-item label="波次号">
        {{ detailData.waveNo }}
      </el-descriptions-item>
      <el-descriptions-item label="仓库">
        {{ detailData.warehouseName }}
      </el-descriptions-item>
      <el-descriptions-item label="波次类型">
        <dict-tag :type="DICT_TYPE.WMS_WAVE_TYPE" :value="detailData.waveType" />
      </el-descriptions-item>

      <el-descriptions-item label="出库单数">
        {{ detailData.orderCount }}
      </el-descriptions-item>
      <el-descriptions-item label="明细行数">
        {{ detailData.itemCount }}
      </el-descriptions-item>
      <el-descriptions-item label="总数量">
        {{ detailData.totalQuantity }}
      </el-descriptions-item>

      <el-descriptions-item label="优先级">
        <el-tag v-if="detailData.priority === 3" type="danger">紧急</el-tag>
        <el-tag v-else-if="detailData.priority === 2" type="warning">重要</el-tag>
        <el-tag v-else type="info">普通</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="拣货员">
        {{ detailData.pickerName || '未分配' }}
      </el-descriptions-item>
      <el-descriptions-item label="波次状态">
        <dict-tag :type="DICT_TYPE.WMS_WAVE_STATUS" :value="detailData.status" />
      </el-descriptions-item>

      <el-descriptions-item label="预计时长">
        {{ detailData.estimatedTime ? `${detailData.estimatedTime} 分钟` : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="实际时长">
        {{ detailData.actualTime ? `${detailData.actualTime} 分钟` : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="开始时间">
        {{ formatDate(detailData.startTime) }}
      </el-descriptions-item>

      <el-descriptions-item label="结束时间">
        {{ formatDate(detailData.endTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="备注">
        {{ detailData.remark || '-' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-divider content-position="left">
      <span style="font-weight: bold">出库单列表</span>
    </el-divider>

    <el-table :data="outboundList" border max-height="400px" v-loading="loading">
      <el-table-column label="出库单号" prop="outboundNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="出库类型" prop="outboundType" min-width="100" align="center">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.WMS_OUTBOUND_TYPE" :value="row.outboundType" />
        </template>
      </el-table-column>
      <el-table-column label="客户" prop="customer" min-width="120" show-overflow-tooltip />
      <el-table-column label="总数量" prop="totalQuantity" min-width="90" align="center" />
      <el-table-column label="已拣货数量" prop="pickedQuantity" min-width="110" align="center">
        <template #default="{ row }">
          <span :style="{ color: row.pickedQuantity >= row.totalQuantity ? '#67c23a' : '#409eff' }">
            {{ row.pickedQuantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="总金额" prop="totalAmount" min-width="120" align="right">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold">
            {{ row.totalAmount ? `¥${row.totalAmount.toFixed(2)}` : '-' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="出库状态" prop="status" min-width="100" align="center">
        <template #default="{ row }">
          <dict-tag :type="DICT_TYPE.WMS_OUTBOUND_STATUS" :value="row.status" />
        </template>
      </el-table-column>
      <el-table-column
        label="预计发货时间"
        prop="expectedShipmentTime"
        min-width="160"
        :formatter="dateFormatter"
      />
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { DICT_TYPE } from '@/utils/dict'
import { dateFormatter } from '@/utils/formatTime'
import * as PickingWaveApi from '@/api/wms/pickingWave'
import * as OutboundApi from '@/api/wms/outbound'

/** 对话框 */
const dialogVisible = ref(false)
const loading = ref(false)

/** 详情数据 */
const detailData = ref({
  id: undefined,
  waveNo: '',
  warehouseName: '',
  waveType: '',
  orderCount: 0,
  itemCount: 0,
  totalQuantity: 0,
  priority: 1,
  pickerName: '',
  estimatedTime: 0,
  actualTime: 0,
  startTime: undefined,
  endTime: undefined,
  status: 1,
  remark: '',
  createTime: undefined,
  outboundIds: []
})

/** 出库单列表 */
const outboundList = ref([])

/** 格式化日期 */
const formatDate = (date: any) => {
  if (!date) return '-'
  return dateFormatter(null, null, date)
}

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  loading.value = true
  try {
    // 加载波次详情
    detailData.value = await PickingWaveApi.getPickingWave(id)

    // 加载出库单详情
    if (detailData.value.outboundIds && detailData.value.outboundIds.length > 0) {
      outboundList.value = []
      for (const outboundId of detailData.value.outboundIds) {
        const outbound = await OutboundApi.getOutbound(outboundId)
        outboundList.value.push(outbound)
      }
    }
  } finally {
    loading.value = false
  }
}

/** 暴露方法 */
defineExpose({ open })
</script>

