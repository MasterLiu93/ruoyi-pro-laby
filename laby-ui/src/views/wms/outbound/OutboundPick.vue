<template>
  <Dialog title="出库拣货" v-model="dialogVisible" width="1000px">
    <el-descriptions :column="2" border class="mb-20px">
      <el-descriptions-item label="出库单号">{{ outboundData.outboundNo }}</el-descriptions-item>
      <el-descriptions-item label="仓库">{{ outboundData.warehouseName }}</el-descriptions-item>
      <el-descriptions-item label="客户">{{ outboundData.customerName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag :type="DICT_TYPE.WMS_OUTBOUND_STATUS" :value="outboundData.status" />
      </el-descriptions-item>
    </el-descriptions>

    <h3>拣货明细</h3>
    <el-table :data="outboundData.items" border class="mt-10px">
      <el-table-column label="商品名称" prop="goodsName" width="150px" />
      <el-table-column label="SKU编码" prop="skuCode" width="150px" />
      <el-table-column label="批次号" prop="batchNo" width="120px">
        <template #default="scope">{{ scope.row.batchNo || '-' }}</template>
      </el-table-column>
      <el-table-column label="计划数量" prop="planQuantity" width="100px" />
      <el-table-column label="已拣货" prop="pickedQuantity" width="100px" />
      <el-table-column label="本次拣货" width="150px">
        <template #default="{ row }">
          <el-input-number
            v-model="row.currentPicked"
            :min="0"
            :max="row.planQuantity - row.pickedQuantity"
            :precision="2"
            size="small"
            class="!w-full"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120px" fixed="right">
        <template #default="{ row }">
          <el-button
            link
            type="primary"
            size="small"
            @click="handlePickItem(row)"
            :disabled="!row.currentPicked || row.currentPicked <= 0"
          >
            确认拣货
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { getOutbound, startPicking, completePicking } from '@/api/wms/outbound'

const message = useMessage()

const dialogVisible = ref(false)
const outboundData = ref({
  id: undefined,
  outboundNo: '',
  warehouseName: '',
  customerName: '',
  status: undefined,
  items: []
})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  
  // 加载出库单信息
  const data = await getOutbound(id)
  outboundData.value = data
  
  // 初始化拣货数据
  outboundData.value.items.forEach(item => {
    item.currentPicked = 0
  })
  
  // 如果是已审核状态，开始拣货
  if (data.status === 2) {
    await startPicking(id)
  }
}
defineExpose({ open })

/** 确认拣货单个明细 */
const emit = defineEmits(['success'])
const handlePickItem = async (row: any) => {
  try {
    await completePicking(outboundData.value.id, row.id, row.currentPicked)
    message.success('拣货成功')
    
    // 刷新数据
    const data = await getOutbound(outboundData.value.id)
    outboundData.value.items = data.items
    outboundData.value.items.forEach(item => {
      item.currentPicked = 0
    })
    
    // 如果状态变为待发货，关闭弹窗并刷新列表
    if (data.status === 4) {
      message.success('所有商品拣货完成，可以发货了')
      dialogVisible.value = false
      emit('success')
    }
  } catch {}
}
</script>

