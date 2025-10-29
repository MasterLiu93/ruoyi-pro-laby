<template>
  <Dialog title="入库收货" v-model="dialogVisible" width="1000px">
    <el-descriptions :column="2" border class="mb-20px">
      <el-descriptions-item label="入库单号">{{ inboundData.inboundNo }}</el-descriptions-item>
      <el-descriptions-item label="仓库">{{ inboundData.warehouseName }}</el-descriptions-item>
      <el-descriptions-item label="供应商">{{ inboundData.supplierName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag :type="DICT_TYPE.WMS_INBOUND_STATUS" :value="inboundData.status" />
      </el-descriptions-item>
    </el-descriptions>

    <h3>收货明细</h3>
    <el-table :data="inboundData.items" border class="mt-10px">
      <el-table-column label="商品名称" prop="goodsName" width="150px" />
      <el-table-column label="SKU编码" prop="skuCode" width="150px" />
      <el-table-column label="批次号" prop="batchNo" width="120px">
        <template #default="scope">{{ scope.row.batchNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="计划数量" prop="planQuantity" width="100px" />
      <el-table-column label="已收货" prop="receivedQuantity" width="100px" />
      <el-table-column label="实收数量" width="120px">
        <template #default="{ row }">
          <el-input-number
            v-model="row.currentReceived"
            :min="0"
            :max="row.planQuantity - row.receivedQuantity"
            :precision="2"
            size="small"
            class="!w-full"
          />
        </template>
      </el-table-column>
      <el-table-column label="合格数量" width="120px">
        <template #default="{ row }">
          <el-input-number
            v-model="row.currentQualified"
            :min="0"
            :max="row.currentReceived || 0"
            :precision="2"
            size="small"
            class="!w-full"
          />
        </template>
      </el-table-column>
      <el-table-column label="不合格数量" width="130px">
        <template #default="{ row }">
          {{ ((row.currentReceived || 0) - (row.currentQualified || 0)).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120px" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleReceiveItem(row)"
            :disabled="!row.currentReceived || row.currentReceived <= 0"
          >
            确认收货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
      <el-button type="success" @click="handleComplete">完成入库</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { getInbound, startReceiving, completeReceiving, completeInbound } from '@/api/wms/inbound'

const message = useMessage()

const dialogVisible = ref(false)
const inboundData = ref({
  id: undefined,
  inboundNo: '',
  warehouseName: '',
  supplierName: '',
  status: undefined,
  items: []
})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  
  // 加载入库单信息
  const data = await getInbound(id)
  inboundData.value = data
  
  // 初始化收货数据
  inboundData.value.items.forEach(item => {
    item.currentReceived = 0
    item.currentQualified = 0
  })
  
  // 如果是已审核状态，开始收货
  if (data.status === 2) {
    await startReceiving(id)
  }
}
defineExpose({ open })

/** 确认收货单个明细 */
const handleReceiveItem = async (row: any) => {
  try {
    await completeReceiving(
      inboundData.value.id,
      row.id,
      row.currentReceived,
      row.currentQualified,
      row.currentReceived - row.currentQualified
    )
    message.success('收货成功')
    
    // 刷新数据
    const data = await getInbound(inboundData.value.id)
    inboundData.value.items = data.items
    inboundData.value.items.forEach(item => {
      item.currentReceived = 0
      item.currentQualified = 0
    })
  } catch {}
}

/** 完成入库 */
const emit = defineEmits(['success'])
const handleComplete = async () => {
  try {
    await message.confirm('确认完成入库吗？')
    // TODO: 获取当前用户信息
    await completeInbound(inboundData.value.id, 1, 'admin')
    message.success('入库完成')
    dialogVisible.value = false
    emit('success')
  } catch {}
}
</script>

