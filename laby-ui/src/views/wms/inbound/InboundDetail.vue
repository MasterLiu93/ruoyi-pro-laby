<template>
  <Dialog title="入库单详情" v-model="dialogVisible" width="1200px">
    <el-descriptions :column="2" border v-loading="loading">
      <el-descriptions-item label="入库单号">{{ detailData.inboundNo }}</el-descriptions-item>
      <el-descriptions-item label="入库类型">
        <dict-tag :type="DICT_TYPE.WMS_INBOUND_TYPE" :value="detailData.inboundType" />
      </el-descriptions-item>
      <el-descriptions-item label="仓库">{{ detailData.warehouseName }}</el-descriptions-item>
      <el-descriptions-item label="供应商">{{ detailData.supplierName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <dict-tag :type="DICT_TYPE.WMS_INBOUND_STATUS" :value="detailData.status" />
      </el-descriptions-item>
      <el-descriptions-item label="预计到货时间">
        {{ formatDate(detailData.expectedArrivalTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="实际到货时间">
        {{ formatDate(detailData.actualArrivalTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="总数量">{{ detailData.totalQuantity }}</el-descriptions-item>
      <el-descriptions-item label="已收货数量">{{ detailData.receivedQuantity }}</el-descriptions-item>
      <el-descriptions-item label="总金额">
        {{ detailData.totalAmount ? '¥' + detailData.totalAmount.toFixed(2) : '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="审核人">{{ detailData.auditByName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审核时间">
        {{ formatDate(detailData.auditTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="完成人">{{ detailData.completeByName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="完成时间">
        {{ formatDate(detailData.completeTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ formatDate(detailData.createTime) }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
    </el-descriptions>

    <!-- 明细列表 -->
    <div class="mt-20px">
      <h3>入库明细</h3>
      <el-table :data="detailData.items" border class="mt-10px">
        <el-table-column label="商品名称" prop="goodsName" min-width="150px" />
        <el-table-column label="SKU编码" prop="skuCode" width="150px" />
        <el-table-column label="单位" prop="goodsUnit" width="80px">
          <template #default="scope">
            <dict-tag :type="DICT_TYPE.WMS_GOODS_UNIT" :value="scope.row.goodsUnit" />
          </template>
        </el-table-column>
        <el-table-column label="批次号" prop="batchNo" width="150px">
          <template #default="scope">{{ scope.row.batchNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="库位" prop="locationCode" width="120px">
          <template #default="scope">{{ scope.row.locationCode || '-' }}</template>
        </el-table-column>
        <el-table-column label="计划数量" prop="planQuantity" width="100px" />
        <el-table-column label="实收数量" prop="receivedQuantity" width="100px" />
        <el-table-column label="合格数量" prop="qualifiedQuantity" width="100px" />
        <el-table-column label="不合格数量" prop="unqualifiedQuantity" width="110px" />
        <el-table-column label="单价" prop="price" width="100px">
          <template #default="scope">
            {{ scope.row.price ? '¥' + scope.row.price.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="金额" prop="amount" width="120px">
          <template #default="scope">
            {{ scope.row.amount ? '¥' + scope.row.amount.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" min-width="150px">
          <template #default="scope">{{ scope.row.remark || '-' }}</template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <el-button @click="dialogVisible = false">关 闭</el-button>
    </template>
  </Dialog>
</template>

<script setup lang="ts">
import { DICT_TYPE } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { getInbound } from '@/api/wms/inbound'

const dialogVisible = ref(false)
const loading = ref(false)
const detailData = ref({
  inboundNo: '',
  inboundType: undefined,
  warehouseName: '',
  supplierName: '',
  status: undefined,
  expectedArrivalTime: undefined,
  actualArrivalTime: undefined,
  totalQuantity: 0,
  receivedQuantity: 0,
  totalAmount: undefined,
  auditByName: '',
  auditTime: undefined,
  completeByName: '',
  completeTime: undefined,
  createTime: undefined,
  remark: '',
  items: []
})

/** 打开弹窗 */
const open = async (id: number) => {
  dialogVisible.value = true
  loading.value = true
  try {
    const data = await getInbound(id)
    detailData.value = data
  } finally {
    loading.value = false
  }
}
defineExpose({ open })
</script>

